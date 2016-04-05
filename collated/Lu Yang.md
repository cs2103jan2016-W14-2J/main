# Lu Yang
###### DODO\src\Command\Add.java
``` java

import Parser.*;
import Task.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import GUI.UI_TAB;


public class Add extends Command {
	private static final String MESSAGE_INVALID_ADD = "Please enter a valid add command.";
	private static final String MESSAGE_SUCCESSFUL_ADD = "Task \"%1$s\" is added to %2$s.";
	
	public Add(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		TASK_TYPE type = parser.getType();
		switch (type) {
		case FLOATING:
			return addFloatingTasks();
		case DEADLINED:
			return addDeadlinedTasks();
		case EVENT:
			return addEvent();
		default:
			return MESSAGE_INVALID_ADD;
		}
	}
	
	private String addFloatingTasks() {
		String name = parser.getName();
		ArrayList<String> tags = parser.getTag();
		
		Task task = new Task(name);
		for (String tagString: tags) {
			this.addCategory(tagString, task);
		}
		
		this.floatingTasks.add(task);
		this.UIStatus = UI_TAB.FLOATING;
		
		System.out.println("=====add===== tasks: " + task.getCategories());
		return String.format(MESSAGE_SUCCESSFUL_ADD, task, this.UIStatus);
	}
	
	private String addDeadlinedTasks() {
		String name = parser.getName();
		ArrayList<String> tags = parser.getTag();
		Date endDateTime = parser.getEndTime();
		
		Task task = new Task(name, endDateTime);
		for (String tagString: tags) {
			this.addCategory(tagString, task);
		}
		
		checkOverdueTask(task);
		return String.format(MESSAGE_SUCCESSFUL_ADD, task, this.UIStatus);
	}
	
	private String addEvent() {
		String name = parser.getName();
		ArrayList<String> tags = parser.getTag();
		Date startDateTime = parser.getStartTime();
		Date endDateTime = parser.getEndTime();
		if (startDateTime.after(endDateTime)) {
			return MESSAGE_INVALID_EVENT_TIME;
		}
		
		Task task = new Task(name, startDateTime, endDateTime);
		for (String tagString: tags) {
			this.addCategory(tagString, task);
		}
		
		checkOverdueTask(task);	
		return String.format(MESSAGE_SUCCESSFUL_ADD, task, this.UIStatus);
	}
	
	private void checkOverdueTask(Task task) {
		if (task.getIsOverdue()) {
			overdueTasks.add(task);
			this.UIStatus = UI_TAB.OVERDUE;
		}
		else {
			ongoingTasks.add(task);
			this.UIStatus = UI_TAB.ONGOING;
		}
	}
}
```
###### DODO\src\Command\Command.java
``` java

import java.util.*;
import GUI.*;
import Parser.*;
import Task.*; 

public abstract class Command {
	protected static final int INDEX_ADJUSTMENT = 1;
	protected static final String MESSAGE_INVALID_START_TIME = "Please enter a valid start time of the event.";
	protected static final String MESSAGE_INVALID_END_TIME = "Please enter a valid end time of the task.";
	protected static final String MESSAGE_INVALID_EVENT_TIME = "Please enter a valid start and end time of the event.";
	protected static final String MESSAGE_INDEXOUTOFBOUND = "Please enter a valid index.";
	protected static final String MESSAGE_EMPTY_LIST = "There is no task in this tab. Please add more tasks.";
	protected static final String MESSAGE_UNSUCCESSFUL_SEARCH_TAG = "There is no tag named \"%1$s\".";
	
	protected UI_TAB UIStatus;
	protected Parser parser;
	protected ArrayList<Task> ongoingTasks;
	protected ArrayList<Task> completedTasks;
	protected ArrayList<Task> overdueTasks;
	protected ArrayList<Task> floatingTasks;
	protected ArrayList<Task> results;
	protected TreeMap<String, Category> categories;

	public Command(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		this.UIStatus = UIRightBox.getCurrentTab();
		System.out.println("=====COMMAND===== constructor: " + this.UIStatus);
		this.parser = parser;
		this.floatingTasks = data.get(0);
		this.ongoingTasks = data.get(1);
		this.completedTasks = data.get(2);
		this.overdueTasks = data.get(3);
		this.results = data.get(4);
		this.categories = categories;
	}

	public abstract String execute();

	/*******************************************ACCESSOR*********************************************/
	public ArrayList<ArrayList<Task>> getData() {
		return this.compress(floatingTasks, ongoingTasks, completedTasks, overdueTasks, results);
	}

	public TreeMap<String, Category> getCategories() {
		return this.categories;
	}

	public UI_TAB getStatus() {
		return this.UIStatus;
	}

	/******************************************INHERIENT METHODS*************************************/
	protected ArrayList<Task> retrieve(UI_TAB status) {
		switch (status) {
		case ONGOING:
			return this.ongoingTasks;
		case FLOATING:
			return this.floatingTasks;
		case COMPLETED:
			return this.completedTasks;
		case OVERDUE:
			return this.overdueTasks;
		case SEARCH:
			return (ArrayList<Task>) this.results.clone();
		case ALL:
			return combine(floatingTasks, ongoingTasks, completedTasks, overdueTasks);
		default:
			return null;
		}
	}

	protected void modify(UI_TAB status, ArrayList<Task> newList) {
		switch (status) {
		case ONGOING:
			this.ongoingTasks = newList;
			break;
		case FLOATING:
			this.floatingTasks = newList;
			break;
		case COMPLETED:
			this.completedTasks = newList;
			break;
		case OVERDUE:
			this.overdueTasks = newList;
			break;
		case SEARCH:
			compareWithSearchAndUpdate(newList);
			break;
		case ALL:
			splitAllAndUpdate(newList);
			break;
		default:
			break;
		}
	}
	
	/*************************************CATEGORY MANIPULATION********************************/
	// IDLE
	/*protected ArrayList<Category> retrieveCategories(ArrayList<String> tags, Task task) {
		ArrayList<Category> categories = new ArrayList<Category>();
		for (String tagStr: tags) {
			Category category = this.categories.get(tagStr.toLowerCase());
			
			if (category==null) {
				//tagStr is not present in this.categories
				category = new Category(tagStr);
				this.categories.put(tagStr.toLowerCase(), category);
			}
			category.addTask(task);
			categories.add(category);
		}
		return categories;
	}*/
	
	protected Category findCategory(String categoryString) {
		Category category = this.categories.get(categoryString.toLowerCase());
		return category;
	}
	
	
	// add task into category
	protected boolean addCategory(String categoryStr, Task task) {
		Category category = this.categories.get(categoryStr.toLowerCase());
		if (category==null) {
			category = new Category(categoryStr);
			this.categories.put(categoryStr.toLowerCase(), category);
		}
		category.addTask(task);
		if (!task.addCategory(category.getName())) {
			return false;
		}
		return true;
	}
	
	//delete an entire category
	protected boolean deleteCategory(String categoryString) {
		Category category = this.categories.get(categoryString.toLowerCase());
		if (category==null) {
			return false;
		}
		ArrayList<Task> taggedTasks = category.getTasks();
		for (Task task: taggedTasks) {
			boolean indicator = task.deleteCategory(category.getName());
			if (indicator==false) {
				throw new Error("IMPOSSIBLE");
			}
		}
		this.categories.remove(categoryString.toLowerCase());
		return true;
	}
	
	//edit an entire category
	protected boolean editCategory(String oldTag, String newTag) {
		Category category = this.categories.get(oldTag.toLowerCase());
		if (category==null) {
			return false;
		}
		category.setName(newTag);
		this.categories.remove(oldTag.toLowerCase());
		this.categories.put(newTag.toLowerCase(), category);
		return true;
	}
	
	protected boolean deleteTaskInCategory(Task task) {
		ArrayList<String> categoriesString = task.getCategories();
		for (String categoryString: categoriesString) {
			Category category = this.categories.get(categoryString.toLowerCase());
			boolean flag = category.deleteTask(task);
			System.out.println("====COMMAND/DELETETASKINCATEGORY==== CRITICAL LOGIC ERROR IF FALSE: " + flag);
			if (category.getTasks().size()==0) {
				deleteCategory(categoryString);
			}
		}
		return true;
	}
	
	/*****************************************PRIVATE METHODS***************************************/
	private ArrayList<ArrayList<Task>> compress(ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results) {
		ArrayList<ArrayList<Task>> data = new ArrayList<ArrayList<Task>> ();
		data.add(floatingTasks);
		data.add(ongoingTasks);
		data.add(completedTasks);
		data.add(overdueTasks);
		data.add(results);
		return data;
	}

	private ArrayList<Task> combine(ArrayList<Task> ongoingTasks, ArrayList<Task> completedTasks,
			ArrayList<Task> floatingTasks, ArrayList<Task> overdueTasks) {
		ArrayList<Task> combined = new ArrayList<Task>();
		combined.addAll(floatingTasks);
		combined.addAll(ongoingTasks);
		combined.addAll(completedTasks);
		combined.addAll(overdueTasks);
		return combined;
	}
	
	private void compareWithSearchAndUpdate(ArrayList<Task> newList) {
		for (Task task: this.results) {
			if (!newList.contains(task)) {
				// this task has  been deleted
				TASK_STATUS taskStatus = task.getStatus();
				this.results.remove(task);
				switch (taskStatus) {
				case ONGOING:
					this.ongoingTasks.remove(task);
					break;
				case FLOATING:
					this.floatingTasks.remove(task);
					break;
				case COMPLETED:
					this.completedTasks.remove(task);
					break;
				case OVERDUE:
					this.overdueTasks.remove(task);
					break;
				}
			}
		}
	}

	private void splitAllAndUpdate(ArrayList<Task> newList) {
		this.floatingTasks.clear();
		this.ongoingTasks.clear();
		this.overdueTasks.clear();
		this.completedTasks.clear();
		for (Task task: newList) {
			if (task.getStatus()==TASK_STATUS.FLOATING) {
				this.floatingTasks.add(task);
			}
			else if (task.getStatus()==TASK_STATUS.ONGOING) {
				this.ongoingTasks.add(task);
			}
			else if (task.getStatus()==TASK_STATUS.COMPLETED) {
				this.completedTasks.add(task);
			}
			else if (task.getStatus()==TASK_STATUS.OVERDUE) {
				this.overdueTasks.add(task);
			}
		}
	}
	
	private ArrayList<Task> cloneList(ArrayList<Task> original) {
		ArrayList<Task> newList = new ArrayList<Task>();
		for (int i=0; i<original.size(); i++) {
			Task task = new Task(original.get(i));
			newList.add(task);
		}
		return newList;
	}
}
```
###### DODO\src\Command\COMMAND_TYPE.java
``` java

public enum COMMAND_TYPE {
	ADD, DELETE, EDIT, UNDO, REDO, SEARCH, COMPLETE, TAG, UNTAG, FLAG, UNFLAG, SORT, 
	HELP, EXIT, CHANGE_DIRECTORY, INVALID;
}
```
###### DODO\src\Command\Complete.java
``` java

import java.util.ArrayList;
import java.util.TreeMap;

import Parser.*;
import Task.*;

public class Complete extends Command {

	public Complete(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		ArrayList<Integer> indexes= parser.getTaskToFlagAndMark();
		FLAGANDCOMPLETE_TYPE type = parser.getFlagAndCompleteType();
		switch (type) {
		case SINGLE:
		case RANGE:
		case MULTIPLE:
			return complete(indexes);
		case ALL:
			return completeAll();
		default:
			return "Invalid command.";
		}
	}

	private String completeAll() {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for (int i=0; i<=tasks.size(); i++) {
			indexes.add(i);
		}
		return complete(indexes);
	}

	private String complete(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		String status = "";
		int index = 0;
		for (int i=indexes.size()-1; i>=0; i--) {
			try {
				index = indexes.get(i)-1;
				Task task = tasks.get(index);
				if (task.getStatus()==TASK_STATUS.COMPLETED) {
					status += "Task " + (index + INDEX_ADJUSTMENT) + " has been completed before.\n";
				}
				else {
					task.setComplete();;
					tasks.remove(index);
					this.completedTasks.add(task);
					this.modify(this.UIStatus, tasks);
					status += "Congratulation! Task " + (index+INDEX_ADJUSTMENT) + " is completed.\n";
				}
			} catch (IndexOutOfBoundsException e) {
				status += "Task " + (index+INDEX_ADJUSTMENT) + " is absent.\n";
			}
		}
		return status;
	}
}
```
###### DODO\src\Command\Delete.java
``` java

import java.util.*;

import GUI.UI_TAB;
import Parser.*;
import Task.*;

public class Delete extends Command {
	private static final String MESSAGE_SUCCESSFUL_DELETE = "Task(s) at \"%1$s\" is/are successfully deleted. ";
	private static final String MESSAGE_SUCCESSFUL_DELETE_TAG = "Tag(s) \"%1$s\" are successfully deleted. ";
	private static final String MESSAGE_UNSUCCESSFUL_DELETE_TAG = "Tag(s) \"%1$s\" are not successfully deleted. ";
	
	public Delete(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	public String execute() {
		DELETE_TYPE type = parser.getDeleteType();
		ArrayList<String> tags = parser.getTagToDelete();
		ArrayList<Integer> indexes = parser.getIndexToDelete();

		// SINGLE_INDEX, SINGLE_TAG, MULTIPLE_INDEXES, MULTIPLE_TAGS, RANGE_INDEXES, ALL_INDEXES, ALL_TAGS;
		switch (type) {
		case SINGLE_INDEX:
		case RANGE_INDEXES:
		case MULTIPLE_INDEXES:
			return deleteTask(indexes);
		case ALL_INDEXES:
			return deleteAllTasks();
		case SINGLE_TAG:
		case MULTIPLE_TAGS:
			return deleteCategories(tags);
		case ALL_TAGS:
			return deleteAllTags();
		case START_DATE:
			return convertToDeadlined(indexes);
		case END_DATE:
			return convertToFloating(indexes);
		default:
			return "Please enter a valid delete command.";
		}
	}
	
	private String deleteTask(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		
		if (tasks==null || tasks.size()==0) {
			return MESSAGE_EMPTY_LIST;
		}
		
		try {
			for (int i=indexes.size()-1; i>=0; i--) {
				int index = indexes.get(i) - INDEX_ADJUSTMENT;
				Task task = tasks.remove(index);
				this.deleteTaskInCategory(task);
				System.out.println("=====DELTE===== categories: " + this.categories);
			}
		} catch (IndexOutOfBoundsException e) {
			return MESSAGE_INDEXOUTOFBOUND;
		} catch (NullPointerException e) {
			return MESSAGE_EMPTY_LIST;
		}
		
		this.modify(UIStatus, tasks);
		return String.format(MESSAGE_SUCCESSFUL_DELETE, indexes);
	}
	
	
	private String deleteAllTasks() {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		this.UIStatus = UI_TAB.ALL;
		for (int i=1; i<=tasks.size(); i++) {
			indexes.add(i);
		}
		return deleteTask(indexes);
	}
	
	
	private String deleteAllTags() {
		for (Category category: new ArrayList<Category>(this.categories.values())) {
			boolean flag = this.deleteCategory(category.getName());
		}
		return  String.format(MESSAGE_SUCCESSFUL_DELETE_TAG, "ALL");
	}
	
	private String deleteCategories(ArrayList<String> categoriesStr) {
		ArrayList<String> unsuccessfulDeletions = new ArrayList<String>();
		ArrayList<String> successfulDeletions = new ArrayList<String>();
		
		for (String categoryStr: categoriesStr) {
			boolean flag = this.deleteCategory(categoryStr);
			if (flag) successfulDeletions.add(categoryStr);
			else unsuccessfulDeletions.add(categoryStr);
			System.out.println("====DELETE/TAG==== categoryStr: " + categoryStr + ", flag: " + flag);
		}
		
		if (unsuccessfulDeletions.size()==0) {
			return String.format(MESSAGE_SUCCESSFUL_DELETE_TAG, successfulDeletions);
		}
		if (successfulDeletions.size()==0) {
			return String.format(MESSAGE_UNSUCCESSFUL_DELETE_TAG, unsuccessfulDeletions);
		}
		return String.format(MESSAGE_SUCCESSFUL_DELETE_TAG, successfulDeletions) + 
				String.format(MESSAGE_UNSUCCESSFUL_DELETE_TAG, unsuccessfulDeletions);
	}
	
	private String convertToDeadlined(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus); 
		for (Integer index: indexes) {
			Task task = tasks.get(index- INDEX_ADJUSTMENT);
			task.setStart(null);
		}
		this.modify(this.UIStatus, tasks);
		return "Start Time of tasks at " + indexes + " has been removed.";
	}
	
	private String convertToFloating(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus); 
		for (Integer index: indexes) {
			Task task = tasks.get(index - INDEX_ADJUSTMENT);
			task.setEnd(null);
			this.floatingTasks.add(task);
			tasks.remove(index- INDEX_ADJUSTMENT);
		}
		this.modify(this.UIStatus, tasks);
		this.UIStatus = UI_TAB.FLOATING;
		return "Deadline of tasks at " + indexes + " has been removed.";
	}
}
```
###### DODO\src\Command\Edit.java
``` java

import java.util.*;

import GUI.UI_TAB;
import Task.*;
import Parser.*;

public class Edit extends Command {
	private static final String MESSAGE_INVALID_EDIT = 
			"Please enter a valid edit.";
	private static final String MESSAGE_SUCCESSFUL_EDIT_NAME = 
			"Task name has been successfully edited to \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_START = 
			"Task start time has been successfully edited to \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_END = 
			"Task end time has been successfully edited to \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_EVENT = 
			"Task event time has been successfully edited to start from \"%1$s\" to \"%2$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_TAG = 
			"Tag name \"%1$s\" has been successfully edit to \"%2$s\".";

	public Edit(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		EDIT_TYPE edit_type = parser.getEditType();
		int index = parser.getTaskID();
		ArrayList<Task> tasks = this.retrieve(this.UIStatus);
		System.out.println("=====EDIT===== edit_type: " + edit_type);
		try {
			switch (edit_type) {
			case TASK_NAME:
				Task task = tasks.get(index-INDEX_ADJUSTMENT);
				return editName(task);
			case START_TIME:
				task = tasks.get(index-INDEX_ADJUSTMENT);
				return editStartTime(task);
			case DEADLINED:
			case END_TIME:
				task = tasks.get(index-INDEX_ADJUSTMENT);
				return editEndTime(task);
			case EVENT_TIME:
				task = tasks.get(index-INDEX_ADJUSTMENT);
				return editStartAndEndTime(task);
			case TAG:
				return editTag();
			default:
				return MESSAGE_INVALID_EDIT;
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return MESSAGE_INDEXOUTOFBOUND;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return MESSAGE_EMPTY_LIST;
		}
	}
	
	private String editTag() {
		String oldTag = parser.getOldTag();
		String newTag = parser.getTag().get(0);
		
		if (this.editCategory(oldTag, newTag)) {
			return String.format(MESSAGE_SUCCESSFUL_EDIT_TAG, oldTag, newTag);
		}
		else {
			return String.format(MESSAGE_UNSUCCESSFUL_SEARCH_TAG, oldTag);
		}
	}

	private String editName(Task task) {
		String newName = parser.getName();
		task.setName(newName);
		return String.format(MESSAGE_SUCCESSFUL_EDIT_NAME, task);
	}
	
	private String editStartTime(Task task) {
		TASK_TYPE oldType = task.getType();
		Date newStartTime = parser.getStartTime();
		
		if (newStartTime.after(task.getEnd())) {
			return MESSAGE_INVALID_START_TIME;
		}
		
		task.setStart(newStartTime);
		if (oldType==TASK_TYPE.FLOATING) {
			convertFromFloating(task);
		}
		return String.format(MESSAGE_SUCCESSFUL_EDIT_START, task.getStartString());
	}
	
	private String editEndTime(Task task) {
		TASK_TYPE oldType = task.getType();
		Date newEndTime = parser.getEndTime();
		
		if (task.getType()==TASK_TYPE.EVENT && newEndTime.before(task.getStart())) {
			return MESSAGE_INVALID_START_TIME;
		}
		
		task.setEnd(newEndTime);
		if (oldType==TASK_TYPE.FLOATING) {
			convertFromFloating(task);
		}
		
		return String.format(MESSAGE_SUCCESSFUL_EDIT_END, task.getEndString());
	}
	
	private String editStartAndEndTime(Task task) {
		TASK_TYPE type = task.getType();
		Date newStartTime = parser.getStartTime();
		Date newEndTime = parser.getEndTime();
		
		if (newStartTime.after(newEndTime)) {
			return MESSAGE_INVALID_EVENT_TIME;
		}
		
		task.setStart(newStartTime);
		task.setEnd(newEndTime);
		if (type==TASK_TYPE.FLOATING) {
			convertFromFloating(task);
		}
		
		return String.format(MESSAGE_SUCCESSFUL_EDIT_EVENT, task.getStartString(), task.getEndString());
	}
	
	private void convertFromFloating(Task task) {
		this.floatingTasks.remove(task);	
		if (task.getIsOverdue()==true) {
			this.overdueTasks.add(task);
			this.UIStatus = UI_TAB.OVERDUE;
		}
		else {
			this.ongoingTasks.add(task);
			this.UIStatus = UI_TAB.ONGOING;
		}
	}
}
```
###### DODO\src\Command\Flag.java
``` java

import java.util.ArrayList;
import java.util.TreeMap;

import Parser.Parser;
import Task.Category;
import Task.Task;

public class Flag extends Command {
	private boolean toFlag;

	public Flag(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories, boolean toFlag) {
		super(parser, data, categories);
		this.toFlag = toFlag;
	}

	@Override
	public String execute() {
		ArrayList<Integer> indexes = parser.getTaskToFlagAndMark();
		FLAGANDCOMPLETE_TYPE type = parser.getFlagAndCompleteType();
		switch (type) {
		case SINGLE:
		case RANGE:
		case MULTIPLE:
			return flag(indexes);
		case ALL:
			return flagAll();
		default:
			return "Invalid command.";
		}
	}

	private String flagAll() {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		ArrayList<Integer> indexes = new ArrayList<Integer>();

		for (int i=1; i<=tasks.size(); i++) {
			indexes.add(i);
		}
		return flag(indexes);
	}

	private String flag(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		String status = "";
		int index = 0;
		for (int i=indexes.size()-1; i>=0; i--) {
			try {
				index = indexes.get(i)-1;
				Task task = tasks.get(index);
				boolean existingFlag = task.getFlag();
				if (toFlag) {
					if (task.getFlag()) {
						status += "Task " + (index + INDEX_ADJUSTMENT) + " is already flagged.\n";
					}
					else {
						status += "Task " + (index + INDEX_ADJUSTMENT) + " is flagged.\n";
					}
				}
				else {
					if (!task.getFlag()) {
						status += "Task " + (index + INDEX_ADJUSTMENT) + " is already unflagged.\n";
					}
					else {
						status += "Task " + (index + INDEX_ADJUSTMENT) + " is unflagged.\n";
					}
				}
				task.setFlag(toFlag);
			} catch (IndexOutOfBoundsException e) {
				status += "Task " + (index + INDEX_ADJUSTMENT) + " is absent.\n";
			}
		}
		return status;
	}

	/*private String flag(int index, ArrayList<Task> tasks) {
		Task task = tasks.get(index);
		if (task.getFlag()) {
			return "Task " + index + " is already flagged.";
		}
		else {
			task.setFlag(true);
			return "Task " + index + " is flagged.";
		}
	}*/
}
```
###### DODO\src\Command\History.java
``` java

import java.util.*;
import Task.*;

public class History {
	private static Stack<ArrayList<ArrayList<Task>>> undo_data;
	private static Stack<ArrayList<ArrayList<Task>>> redo_data;
	private static Stack<TreeMap<String, Category>> undo_categories;
	private static Stack<TreeMap<String, Category>> redo_categories;
	
	public History() {
		undo_data = new Stack<ArrayList<ArrayList<Task>>>();
		redo_data = new Stack<ArrayList<ArrayList<Task>>>();
		undo_categories = new Stack<TreeMap<String, Category>>();
		redo_categories = new Stack<TreeMap<String, Category>>();
	}

	public String save(ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		undo_data.push(data);
		undo_categories.push(categories);
		redo_data = new Stack<ArrayList<ArrayList<Task>>>();
		redo_categories = new Stack<TreeMap<String, Category>>();
		return "[INTERNAL MESSAGE/HISTORY]: data saved.";
	}
	
	public ArrayList<ArrayList<Task>> undoData(ArrayList<ArrayList<Task>> data) {
		redo_data.push(data);
		return undo_data.pop();
	}
	
	public TreeMap<String, Category> undoCategories(TreeMap<String, Category> categories) {
		redo_categories.push(categories);
		return redo_categories.pop();
	}
	
	public ArrayList<ArrayList<Task>> redoData() {
		return redo_data.pop();
	}
	
	public TreeMap<String, Category> redoCategories() {
		return redo_categories.pop();
	}
}
```
###### DODO\src\Command\Search.java
``` java

import java.util.*;

import GUI.UI_TAB;
import Parser.Parser;
import Task.*;

public class Search extends Command {
	private static final String MESSAGE_INVALID_SEARCH = "Please enter a valid search command.";
	private static final String MESSAGE_SUCCESSFUL_SEARCH_KEYWORD = "Search keyword \"%1$s\" is successful.";
	private static final String MESSAGE_SUCCESSFUL_SEARCH_TAG = "Search tag \"%1$s\" is successful.";
	private static final String MESSAGE_UNSUCCESSFUL_SEARCH_KEYWORD = "There is no task named \"%1$s\".";
	
	
	
	public Search(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		SEARCH_TYPE type = parser.getSearchType();
		switch (type) {
		case BY_TASK:
			String keyword = parser.getSearchByTask();
			return searchByKeyword(keyword);
			/*Date searchDate = parser.getSearchByDate();
			System.out.println("============SEARCH=========== dt: " + searchDate);
			return "[SEARCH] UNDER DEVELOPMENT";*/
		case BY_TAG:
			String searchTag = parser.getSearchByTag();
			return searchByTag(searchTag);
		case BY_DATE:
			/*Date searchDate = parser.getSearchByDate();
			System.out.println("============SEARCH=========== dt: " + searchDate);
			return "[SEARCH] UNDER DEVELOPMENT";*/
		case INVALID:
		default:
			return MESSAGE_INVALID_SEARCH;
		}
	}
	
	/**********************************INTERNAL METHODS***************************************/
	
	/*private String searchByDate(DateTime searchDate) {
		ArrayList<Task> floatingResults = searchTasksByDate(floatingTasks, searchDate);
		return null;
	}*/
	
	/*private ArrayList<Task> searchTasksByDate(ArrayList<Task> tasks, DateTime searchDate) {
		System.out.println(searchDate);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		ArrayList<Task> results = new ArrayList<Task>();
		for (int i=0; i<tasks.size(); i++) {
			Task task = tasks.get(i);
			if (task.getStatus()==TASK_TYPE.DEADLINED) {
				DateTime endDate = new DateTime(((DeadlinedTask) task).getEndDateTime());
			}
			else if (task instanceof Event) {
				
			}
		}
		return null;
	}*/
	
	private String searchByTag(String searchTag) {
		Category category = this.findCategory(searchTag);
		if (category==null) {
			return String.format(MESSAGE_UNSUCCESSFUL_SEARCH_TAG, searchTag);
		}
		results = category.getTasks();
		this.UIStatus = UI_TAB.SEARCH;
		return String.format(MESSAGE_SUCCESSFUL_SEARCH_TAG, searchTag);
	}

	private String searchByKeyword(String keyword) {
		if (keyword.length()<2) {
			return MESSAGE_INVALID_SEARCH;
		}
		
		ArrayList<Task> floatingResults = searchTasksByKeyword(floatingTasks, keyword);
		ArrayList<Task> ongoingResults = searchTasksByKeyword(ongoingTasks, keyword);
		ArrayList<Task> completedResults = searchTasksByKeyword(completedTasks, keyword);
		ArrayList<Task> overdueResults = searchTasksByKeyword(overdueTasks, keyword);
		this.results.addAll(floatingResults);
		this.results.addAll(ongoingResults);
		this.results.addAll(completedResults);
		this.results.addAll(overdueResults);
		this.UIStatus = UI_TAB.SEARCH;
		
		if (this.results.size()==0) {
			return String.format(MESSAGE_UNSUCCESSFUL_SEARCH_KEYWORD, keyword);
		}
		return String.format(MESSAGE_SUCCESSFUL_SEARCH_KEYWORD, keyword);
	}

	private ArrayList<Task> searchTasksByKeyword(ArrayList<Task> tasks, String searchStr) {
		searchStr = searchStr.toLowerCase();
		String[] fragments = searchStr.split(" ");
		
		ArrayList<Task> results = new ArrayList<Task>();
		for (Task task: tasks) {
			String name = task.getName().toLowerCase();
			
			boolean search = true;
			for (String fragment: fragments) {
				if (!name.contains(fragment)) search = false;
			}
			if (search==true) results.add(task);
		}
		return results;
	}
}
```
###### DODO\src\Command\Sort.java
``` java

import java.util.*;

import Parser.Parser;
import Task.*;

public class Sort extends Command {
	private static final String INVALID_SORT = "Please enter a valid sort command.";
	private static final DescendingAlphabeticalComparator decendingAlphabeticalComparator = new DescendingAlphabeticalComparator();
	private static final AscendingAlphaticalComparator ascendingAlphabeticalComparator = new AscendingAlphaticalComparator();
	private static final DateComparator dateComparator = new DateComparator();

	public Sort(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		SORT_TYPE type = parser.getSortType();
		switch (type) {
		case BY_ASCENDING:
		case BY_DESCENDING:
			return sortByAlphabet(type);
		case BY_DATE:
			return sortByDate();
		default: 
			return INVALID_SORT;
		}
	}

	public String sortByAlphabet(SORT_TYPE type) {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		if (tasks.size()==0) {
			return MESSAGE_EMPTY_LIST;
		}
		if (type==SORT_TYPE.BY_DESCENDING) {
			Collections.sort(tasks, decendingAlphabeticalComparator);
		}
		else {
			Collections.sort(tasks, ascendingAlphabeticalComparator);
		}
		return "Sorted by Alphabets.";
	}
	
	public String sortByDate() {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		if (tasks.size()==0) {
			return MESSAGE_EMPTY_LIST;
		}
		Collections.sort(tasks, dateComparator);
		this.modify(UIStatus, tasks);
		return "Sorted by Dates.";
	}


	private static class AscendingAlphaticalComparator implements Comparator<Task> {
		@Override
		public int compare(Task a, Task b) {
			String nameA = a.getName();
			String nameB = b.getName();
			int comp = nameA.compareToIgnoreCase(nameB);
			return comp;
		}
	}
	
	private static class DescendingAlphabeticalComparator implements Comparator<Task> {
		@Override
		public int compare(Task a, Task b) {
			String nameA = a.getName();
			String nameB = b.getName();
			int comp = nameB.compareToIgnoreCase(nameA);
			return comp;
		}
	}
	
	private static class DateComparator implements Comparator<Task> {
		@Override
		public int compare(Task a, Task b) {
			Date aDate = a.getEnd();
			Date bDate = b.getEnd();
			if (aDate==null && bDate==null) return -1;
			if (aDate==null) return 1;
			if (bDate==null) return -1;
			if (aDate.after(bDate)) {
				return 1;
			}
			else if (aDate.before(bDate)) {
				return -1;
			}
			else return 0;
		}
	}
}
```
###### DODO\src\Command\Tag.java
``` java

import java.util.*;

import Parser.Parser;
import Task.Category;
import Task.Task;

public class Tag extends Command {
	private static final String MESSAGE_SUCCESSFUL_TAG = 
			"Task(s) \"%1$s\" is/are successfully tagged by \"%2$s\". ";
	private static final String MESSAGE_UNSUCCESSFUL_TAG = 
			"Task(s) \"%1$s\" is/are not successfully tagged by \"%2$s\". ";
	
	public Tag(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		int index = parser.getTaskID();
		ArrayList<String> tags = parser.getTag();
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		
		Task task;
		try {
			task = tasks.get(index-INDEX_ADJUSTMENT);
		} catch (IndexOutOfBoundsException e) {
			return MESSAGE_INDEXOUTOFBOUND;
		} catch (NullPointerException e) {
			return MESSAGE_EMPTY_LIST;
		}

		return tag(tags, task);
	}

	private String tag(ArrayList<String> tags, Task task) {
		ArrayList<String> unsuccessfulTags = new ArrayList<String>();
		ArrayList<String> successfulTags = new ArrayList<String>();
		
		for (String tag: tags) {
			Category category = this.findCategory(tag);
			if (category==null) {
				this.addCategory(tag, task);
				category = this.findCategory(tag);
			}
			// task interchangably add category in class Category
			if (category.addTask(task)) {
				successfulTags.add(tag);
			}
			else {
				// already tagged
				unsuccessfulTags.add(tag);
			}
		}
		/*if (categories.containsKey(tag)) {
				Category category = categories.get(tag);
				category.addTask(task);
				return "Task " + (index+INDEX_ADJUSTMENT) + " is tagged under a new Tag "  + "\"" + tag + "\"";
			}
			else {
				TreeMap<String, Task> tasksInCategory = new TreeMap<String, Task>();
				Category category = new Category(tag, tasksInCategory);
				categories.put(tag, category);
				return "Task " + (index+INDEX_ADJUSTMENT) + " is tagged under the existing Tag "  + "\"" + tag + "\"";
			}*/
		if (unsuccessfulTags.size()==0) {
			return String.format(MESSAGE_SUCCESSFUL_TAG, task, successfulTags);
		}
		if (successfulTags.size()==0) {
			return String.format(MESSAGE_UNSUCCESSFUL_TAG, task, unsuccessfulTags);
		}
		return String.format(MESSAGE_SUCCESSFUL_TAG, task, successfulTags) + 
				String.format(MESSAGE_UNSUCCESSFUL_TAG, task, unsuccessfulTags);
		
	}
}
```
###### DODO\src\Logic\Logic.java
``` java

public class Logic {
	private static final String MESSAGE_SUCCESSFUL_UNDO = "Undo successful.";
	private static final String MESSAGE_SUCCESSFUL_REDO = "Redo successful.";
	private static final String MESSAGE_UNSUCCESSFUL_UNDO = "Undo not successful. There is nothing to undo";
	private static final String MESSAGE_UNSUCCESSFUL_REDO = "Redo not successful. There is nothing to redo";
	private static Logic theOne; //singleton
	private Storage storage;
	
	/*************************************MEMORY*************************************************/
	private ArrayList<Task> ongoingTasks;
	private ArrayList<Task> completedTasks;
	private ArrayList<Task> overdueTasks;
	private ArrayList<Task> floatingTasks;
	private ArrayList<Task> results;
	private TreeMap<String, Category> categories;
	private History history;
	private UI_TAB status;
	private String previous;

	/*************************************PUBLIC METHODS******************************************/
	public static Logic getInstance(String directory) {
		if (theOne==null) {
			theOne = new Logic(directory);
		}
		return theOne;
	}

	public String run(String input) {
		this.previous = input;
		Parser parser;
		try {
			parser = new Parser(input);
		} catch (Exception e) {
			return "Invalid Command.";
		}
		return processCommand(parser);
	}
	
	public String save() {
		storage.save(TASK_STATUS.ONGOING, ongoingTasks);
		storage.save(TASK_STATUS.COMPLETED, completedTasks);
		storage.save(TASK_STATUS.FLOATING, floatingTasks);
		storage.save(TASK_STATUS.OVERDUE, overdueTasks);
		storage.saveCategories(this.categories);
		return "Saved successfully";
	}
	
	public ArrayList<Category> mapCategories(ArrayList<String> categoriesString) {
		ArrayList<Category> categories = new ArrayList<Category>();
		for (String categoryString: categoriesString) {
			Category category = this.categories.get(categoryString.toLowerCase());
			if (category!=null) {
				categories.add(category);
			}
		}
		return categories;
	}
	/***********************************ACCESSORS***********************************************/
	public ArrayList<Task> getOngoingTasks() {
		return this.ongoingTasks;
	}

	public ArrayList<Task> getFloatingTasks() {
		return this.floatingTasks;
	}

	public ArrayList<Task> getCompletedTasks() {
		return this.completedTasks;
	}

	public ArrayList<Task> getOverdueTasks() {
		return this.overdueTasks;
	}

	public ArrayList<Category> getCategories() {
		ArrayList<Category> categories = new ArrayList<Category>(this.categories.values());
		return categories;
	}
	
	public ArrayList<Task> getAll() {
		ArrayList<Task> temp = new ArrayList<Task>();
		temp.addAll(this.floatingTasks);
		temp.addAll(this.ongoingTasks);
		temp.addAll(this.completedTasks);
		temp.addAll(this.overdueTasks);
		return temp;
	}
	
	public UI_TAB getStatus() {
		return this.status;
	}
	
	public ArrayList<Task> getSearchResults() {
		return this.results;
	}
	
	public String getPreviousCommand() {
		if (this.previous==null) {
			return "";
		}
		return this.previous;
	}
	
	/***********************************PRIVATE METHODS***********************************************/
	private String processCommand(Parser parser) {
		String message = "";
		COMMAND_TYPE command = parser.getCommandType();
		ArrayList<ArrayList<Task>> data = compress();
		
		switch (command) {
		case ADD:
			Add add = new Add(parser, data, categories);
			message = execute(add, data);
			break;
		case DELETE:
			Delete delete = new Delete(parser, data, categories);
			message = execute(delete, data);
			break;
		case EDIT:
			Edit edit = new Edit(parser, data, categories);
			message = execute(edit, data);
			break;
		case COMPLETE:
			Complete complete = new Complete(parser, data, categories);
			message = execute(complete, data);
			this.status = UI_TAB.COMPLETED;
			break;
		case TAG:
			Tag tag = new Tag(parser, data, categories);
			message = execute(tag, data);
			categories = tag.getCategories();
			System.out.println(categories);
			break;
		case FLAG:
			Flag flag = new Flag(parser, data, categories, true);
			message = execute(flag, data);
			break;
		case UNFLAG:
			flag = new Flag(parser, data, categories, false);
			message = execute(flag, data);
			break;
		case UNDO:
			try {
				update(history.undoData(data), history.undoCategories(this.categories));
				message = MESSAGE_SUCCESSFUL_UNDO;
			} catch (EmptyStackException e) {
				message = MESSAGE_UNSUCCESSFUL_UNDO;
			}
			break;
		case REDO:
			try {
				update(history.redoData(), history.redoCategories());
				message = MESSAGE_SUCCESSFUL_REDO;
			} catch (EmptyStackException e) {
				message = MESSAGE_UNSUCCESSFUL_REDO;
			}
			break;
		case SEARCH:
			Search search = new Search(parser, data, categories);
			message = execute(search, data);
			break;
		case SORT:
			Sort sort = new Sort(parser, data, categories);
			message = execute(sort, data);
			break;
		default:
			message = "Invalid Command.";
		}
		return message;
	}
	
	private String execute(Command command, ArrayList<ArrayList<Task>> data) {
		history.save(cloneData(data), cloneCategories(this.categories));
		String message = command.execute();
		this.update(command.getData(), command.getCategories());
		this.status = command.getStatus();
		return message;
	}
	
	/***************************************DATA MANIPULATION***********************************/
	private String update(ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		this.floatingTasks = data.get(0);
		this.ongoingTasks = data.get(1);
		this.completedTasks = data.get(2);
		this.overdueTasks = data.get(3);
		this.results = data.get(4);
		this.categories = categories;
		return "Data updated.";
	}

	private ArrayList<ArrayList<Task>> compress() {
		ArrayList<ArrayList<Task>> data = new ArrayList<ArrayList<Task>> ();
		data.add(floatingTasks);
		data.add(ongoingTasks);
		data.add(completedTasks);
		data.add(overdueTasks);
		data.add(results);
		return data;
	}
	
	
	/**********************************CLONE**********************************************************/
	
	private ArrayList<Task> cloneList(ArrayList<Task> original) {
		ArrayList<Task> newList = new ArrayList<Task>();
		for (int i=0; i<original.size(); i++) {
			Task task = new Task(original.get(i));
			newList.add(task);
		}
		return newList;
	}

	private ArrayList<ArrayList<Task>> cloneData(ArrayList<ArrayList<Task>> data) {
		ArrayList<ArrayList<Task>> newData = new ArrayList<ArrayList<Task>>();
		for (int i=0; i<data.size(); i++) {
			ArrayList<Task> copy = cloneList(data.get(i));
			newData.add(copy);
		}
		return newData;
	}
	
	private TreeMap<String, Category> cloneCategories(TreeMap<String, Category> original) {
		TreeMap<String, Category> newCategories = new TreeMap<String, Category>();
		ArrayList<Category> categories = new ArrayList<Category>(original.values());
		for (Category category: categories) {
			Category newCategory = new Category(category);
			newCategories.put(category.getName().toLowerCase(), newCategory);
		}
		return newCategories;
	}
	
	/***********************************INITALISE*****************************************************/
	private Logic(String directory) {
		storage = Storage.getInstance(directory);
		results = new ArrayList<Task>();
		history = new History();
		ongoingTasks = storage.read(TASK_STATUS.ONGOING);
		completedTasks = storage.read(TASK_STATUS.COMPLETED);
		overdueTasks = storage.read(TASK_STATUS.OVERDUE);
		floatingTasks = storage.read(TASK_STATUS.FLOATING);
		categories = storage.readCategories();
	}
	
	/*private TreeMap<String, Category> initialiseCategories(ArrayList<Task> ongoingTasks, ArrayList<Task> completedTasks,
			ArrayList<Task> overdueTasks, ArrayList<Task> floatingTasks) {
		TreeMap<String, Category> categories = new TreeMap<String, Category>();
		categories = loadCategoriesFromList(ongoingTasks, categories);
		categories = loadCategoriesFromList(floatingTasks, categories);
		categories = loadCategoriesFromList(completedTasks, categories);
		categories = loadCategoriesFromList(overdueTasks, categories);
		return categories;
	}

	private TreeMap<String, Category> loadCategoriesFromList(ArrayList<Task> tasks,
			TreeMap<String, Category> categories) {
		for (Task task: tasks) {
			ArrayList<String> taskCategories = task.getCategories();
			for (String category: taskCategories) {
				if (!categories.containsKey(category.toLowerCase())) {
					categories.put(category.toLowerCase(), category);
				}
			}
		}
		return categories;
	}*/
}
```
###### DODO\src\Storage\Storage.java
``` java

import Command.*;
import GUI.*;
import Logic.*;
import Parser.*;
import Task.*;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class Storage {
	private static Storage theOne;
	private static final String FILENAME_ONGOING_TASKS = "OngoingTasks.txt";
	private static final String FILENAME_COMPLETED_TASKS = "CompletedTasks.txt";
	private static final String FILENAME_FLOATING_TASKS = "FloatingTasks.txt";
	private static final String FILENAME_OVERDUE_TASKS = "OverdueTasks.txt";
	private static final String FILENAME_CATEGORIES = "Categories.txt";

	private PrintWriter pw;
	private BufferedReader br;
	private String ongoingDirectory;
	private String completedDirectory;
	private String floatingDirectory;
	private String overdueDirectory;
	private String categoriesDirectory;
	
	public static Storage getInstance(String directory) {
		if (theOne==null) {
			theOne = new Storage(directory);
		}
		return theOne;
	}
	
	private Storage(String directory) {
		// it cannot create a file in a specific directory for now
		// it only creates a file in the same directory as the programme
		ongoingDirectory = directory  + FILENAME_ONGOING_TASKS;
		completedDirectory = directory + FILENAME_COMPLETED_TASKS;
		floatingDirectory = directory + FILENAME_FLOATING_TASKS;
		overdueDirectory = directory + FILENAME_OVERDUE_TASKS;
		categoriesDirectory = directory + FILENAME_CATEGORIES;
		if (!fileExists(categoriesDirectory)) initialiseFile(categoriesDirectory);
		if (!fileExists(ongoingDirectory)) initialiseFile(ongoingDirectory);
		if (!fileExists(completedDirectory)) initialiseFile(completedDirectory);
		if (!fileExists(floatingDirectory)) initialiseFile(floatingDirectory);
		if (!fileExists(overdueDirectory)) initialiseFile(overdueDirectory);
	}
	
	public ArrayList<Task> read(TASK_STATUS task_status) {
		switch (task_status) {
		case ONGOING:
			return readFromFile(ongoingDirectory);
		case COMPLETED:
			return readFromFile(completedDirectory);
		case FLOATING:
			return readFromFile(floatingDirectory);
		case OVERDUE:
			return readFromFile(overdueDirectory);
		default:
			return null;
		}
	}
	
	public TreeMap<String, Category> readCategories() {
		 TreeMap<String, Category> categories = new TreeMap<String, Category>();
		try {
			br = new BufferedReader(new FileReader(this.categoriesDirectory));
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss");
			Gson gson = gsonBuilder.create();
			categories = gson.fromJson(br, new TypeToken<TreeMap<String, Category>>() {}.getType());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (categories==null) {
			System.out.println("[DEBUG/Storage] null");
			return new TreeMap<String, Category>();
		}
		return categories;
	}
	
	public String saveCategories(TreeMap<String, Category> categories)  {
		clearFile(this.categoriesDirectory);
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(this.categoriesDirectory), "UTF-8")) {
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").setPrettyPrinting();
			Gson gson = gsonBuilder.create();
			gson.toJson(categories, writer);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "Saving " + this.categoriesDirectory + " is successful. :)";
	}

	public String save(TASK_STATUS task_status, ArrayList<Task> tasks) {
		switch (task_status) {
		case ONGOING:
			return printToFile(ongoingDirectory, tasks);
		case COMPLETED:
			return printToFile(completedDirectory, tasks);
		case FLOATING:
			return printToFile(floatingDirectory, tasks);
		case OVERDUE:
			return printToFile(overdueDirectory, tasks);
		default:
			return "Invalid type of tasks.";
		}
	}
	
	/******************************INTERNAL***********************************************/

	private boolean fileExists(String directory) {
		File file = new File(directory);
		if (file.exists()) return true;
		else return false; 
	}

	private String printToFile(String filename, ArrayList<Task> tasks)  {
		clearFile(filename);
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")) {
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").setPrettyPrinting();
			Gson gson = gsonBuilder.create();
			gson.toJson(tasks, writer);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "Saving " + filename + " is successful. :)";
	}

	private ArrayList<Task> readFromFile(String filename) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		try {
			br = new BufferedReader(new FileReader(filename));
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss");
			Gson gson = gsonBuilder.create();
			tasks = gson.fromJson(br, new TypeToken<ArrayList<Task>>() {}.getType());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[DEBUG/Storage.java] readFromFile: " + filename);
		if (tasks==null) {
			System.out.println("[DEBUG/Storage] null");
			return new ArrayList<Task>();
		}
		return tasks;
	}

	private void initialiseFile(String filename) {
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
			pw.print("");
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void clearFile(String filename) {
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, false)));
			pw.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*public class DateSerializer implements JsonSerializer<Date>{
		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			String str = formatter.format(src);
			return new JsonPrimitive(str);
		}
	}

	public class DateDeserializer implements JsonDeserializer<Date> {
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException	 {
			String date = json.getAsString();
			return new Date(json.getAsJsonPrimitive().getAsString());
		}
	}*/
	
	/*public class CustomSerializer implements JsonSerializer<ArrayList<Task>> {
		private static TreeMap<String, Class> map = new TreeMap<String, Class>();
		
		@Override 
		public JsonElement serialize(ArrayList<Task> src, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray ja = new JsonArray();
			for (Task task: src) {
				
			}
		}
	}*/
	
	/*public class CustomDeserializer implements JsonDeserializer<ArrayList<Task>> {
		@Override
		public ArrayList<Task> deserialize(JsonElement json, Type typeofT, JsonDeserializationContext context)
				throws JsonParseException {
			ArrayList<Task> list = new ArrayList<Task>();
			JsonArray ja = json.getAsJsonArray();
			for (JsonElement je: ja) {
				String type = je.getAsJsonObject().get("type").getAsString();
				System.out.println("=====CustomDeserializer=====" + type);
				if (type.equals("DEADLINED")) {
					JsonObject jsonObj = je.getAsJsonObject();
					String endDateTimeStr = jsonObj.get("endDateTime").getAsString();
					TASK_TYPE task_type = TASK_TYPE.EVENT;
					String name = jsonObj.get("name").getAsString();
					boolean flag = jsonObj.get("flag").getAsBoolean();
					boolean isComplete = jsonObj.get("isComplete").getAsBoolean();
					String tag = null;
					try {
						tag = jsonObj.get("tag").getAsString();
					} catch (NullPointerException e) {
					}
					
					Date endDateTime = null;
					try {
						endDateTime = formatter.parse(endDateTimeStr);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					DeadlinedTask task = new DeadlinedTask(task_type, name, tag, endDateTime);
					list.add(task);
				}
				else if (type.equals("EVENT")) {
					JsonObject jsonObj = je.getAsJsonObject();
					String startDateTimeStr = jsonObj.get("startDateTime").getAsString();
					String endDateTimeStr = jsonObj.get("endDateTime").getAsString();
					TASK_TYPE task_type = TASK_TYPE.EVENT;
					String name = jsonObj.get("name").getAsString();
					boolean flag = jsonObj.get("flag").getAsBoolean();
					boolean isComplete = jsonObj.get("isComplete").getAsBoolean();
					String tag = null;
					try {
						tag = jsonObj.get("tag").getAsString();
					} catch (NullPointerException e) {
					}
					Date startDateTime = null;
					Date endDateTime = null;
					try {
						startDateTime = formatter.parse(startDateTimeStr);
						endDateTime = formatter.parse(endDateTimeStr);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Event event = new Event(task_type, name, tag, startDateTime, endDateTime);
					list.add(event);
				}
				else {
					list.add(context.deserialize(je, Task.class));
				}
			}
			return list;
		}
	}*/
}
```
###### DODO\src\Task\Category.java
``` java

import java.util.*;

public class Category {
	private String name;
	private ArrayList<Task> tasks;
	
	public Category() {
		this("");
	}
	
	public Category(String name) {
		this.name = name;
		this.tasks = new ArrayList<Task>();
	}
	
	public Category(Category category) {
		this.name = category.getName();
		this.tasks = category.getTasks();
	}
	
	/****************************MUTATORS******************************/
	public boolean addTask(Task task) {
		if (!tasks.contains(task)) {
			tasks.add(task);
			return task.addCategory(this.name);
		}
		return false;
	}
	
	public boolean deleteTask(Task task) {
		if (tasks.contains(task)) {
			tasks.remove(task);
			return true;
		}
		return false;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/****************************ACCESSORS*****************************/
	public String getName() {
		return this.name;
	}
	
	public ArrayList<Task> getTasks() {
		return this.tasks;
	}
	
	@Override
	public String toString() {
		return "#" + name;
	}
}
```
###### DODO\src\Task\Task.java
``` java

public class Task {
	private static final SimpleDateFormat datetimeFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
	private TASK_TYPE type;
	private TASK_STATUS status;
	private String name;
	private Date start;
	private Date end;
	private boolean flag;
	private boolean isOverdue;
	private ArrayList<String> categories;

	// Default Constructor
	public Task() {
		this("");
	}

	// Constructor of Floating Tasks
	public Task(String name) {
		this.type = TASK_TYPE.FLOATING;
		this.status = TASK_STATUS.FLOATING;
		this.name = name; // must have

		this.categories = new ArrayList<String>();
		this.flag = false;
		this.isOverdue = false;
	}

	// Constructor of Deadlined Tasks
	public Task(String name, Date end) {
		this.type = TASK_TYPE.DEADLINED;
		this.status =TASK_STATUS.ONGOING;
		this.name = name;
		/*this.end = formatter.format(end);*/
		this.end = end;

		this.categories = new ArrayList<String>();
		this.flag = false;;
		this.isOverdue = checkOverdue(end);
	}

	// Constructor of Event
	public Task(String name, Date start, Date end) {
		this.type = TASK_TYPE.EVENT;
		this.status =TASK_STATUS.ONGOING;
		this.name = name;
		this.end = end;
		this.start = start;

		this.categories = new ArrayList<String>();
		this.flag = false;
		this.isOverdue = checkOverdue(end);
	}

	// copy constructor
	public Task(Task original) {
		this.type = original.type;
		this.status = original.status;
		this.name = original.name;
		this.end = original.end;
		this.start = original.start;
		this.flag = original.flag;
		this.isOverdue = original.isOverdue;
		this.categories = original.categories;
	}

	/**************************************ACCESSORS**************************/
	public TASK_TYPE getType() {
		return this.type;
	}

	public TASK_STATUS getStatus() {
		return this.status;
	}

	public boolean getFlag() {
		return this.flag;
	}

	public String getName() {
		return this.name;
	}

	public ArrayList<String> getCategories() {
		return this.categories;
	}

	public boolean getIsOverdue() {
		return this.isOverdue;
	}

	public Date getStart() {
		return this.start;
	}

	public String getStartString() {
		return convertToString(this.start);
	}
	
	public String getEndString() {
		return convertToString(this.end);
	}

	public Date getEnd() {
		return this.end;
	}

	/***********************************MUTATORS*****************************/
	public void setFlag(boolean flag) {	
		this.flag = flag; 
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setComplete() {
		this.status = TASK_STATUS.COMPLETED;
	}

	public void setStart(Date start) {
		if (start==null) {
			// convert to deadlined
			this.type = TASK_TYPE.DEADLINED;
			this.start = null;
		}
		else {
			if (this.start==null) {
				// convert to Event from Deadlined/Floating
				this.type = TASK_TYPE.EVENT;
			}
			this.start = start;
		}
	}

	public void setEnd(Date end) {
		if (end==null) {
			// convert to floating
			this.type = TASK_TYPE.FLOATING;
			this.end = null;
		}
		else {
			if (this.type==TASK_TYPE.FLOATING) {
				// if the task is a floating task
				this.type = TASK_TYPE.DEADLINED;
			}
			this.end = end;
			this.isOverdue = checkOverdue(end);
		}
	}

	public void setCategory(ArrayList<String> categories) {
		this.categories = categories;
	}
	
	public boolean deleteCategory(String category) {
		for (String string: this.categories) {
			if (string.equalsIgnoreCase(category)) {
				this.categories.remove(string);
				return true;
			}
		}
		return false;
	}
	
	public boolean addCategory(String category) {
		for (String string: this.categories) {
			if (string.equalsIgnoreCase(category)) {
				return false;
			}
		}
		this.categories.add(category);
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task task = (Task) obj;
			return task==this;
		}
		else return false;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	
	/********************************************INTERNAL*************************************/
	private boolean checkOverdue(Date end) {
		Date current = new Date();
		if  (current.after(end)) {
			this.status = TASK_STATUS.OVERDUE;
			return true;
		}
		else {
			this.status = TASK_STATUS.ONGOING;
			return false;
		}
	}

	private String convertToString(Date date) {
		if (date==null) {
			return null;
		}
		
		String timeStr = timeFormatter.format(date);
		if (timeStr.equals("23:59:59")) {
			return dateFormater.format(date);
		}
		else {
			return datetimeFormater.format(date);
		}
	}
}
```
###### DODO\src\Task\TASK_STATUS.java
``` java

public enum TASK_STATUS {
	ONGOING, FLOATING, COMPLETED, OVERDUE;
}
```
###### DODO\src\Task\TASK_TYPE.java
``` java

public enum TASK_TYPE {
	DEADLINED, EVENT, FLOATING, INVALID;
}
```
