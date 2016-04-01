package Logic;

import Command.*;
import GUI.UI_TAB;
import Parser.*;
import Storage.*;
import Task.*;
import java.util.*;

/* @@author: Lu Yang */

public class Logic {
	private static Logic theOne; //singleton
	private Storage storage;
	private ArrayList<Task> ongoingTasks;
	private ArrayList<Task> completedTasks;
	private ArrayList<Task> overdueTasks;
	private ArrayList<Task> floatingTasks;
	private ArrayList<Task> results;
	private ArrayList<Task> all;
	private History history;
	private UI_TAB status;
	private ArrayList<String> categories;
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
			e.printStackTrace();
			return "Invalid Command.";
		}
		return processCommand(parser);
	}
	
	public String save() {
		storage.save(TASK_STATUS.ONGOING, ongoingTasks);
		storage.save(TASK_STATUS.COMPLETED, completedTasks);
		storage.save(TASK_STATUS.FLOATING, floatingTasks);
		storage.save(TASK_STATUS.OVERDUE, overdueTasks);
		return "Saved successfully";
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

	public ArrayList<String> getCategories() {
		return this.categories;
	}
	
	public ArrayList<Task> getAll() {
		ArrayList<Task> temp = new ArrayList<Task>();
		temp.addAll(this.overdueTasks);
		temp.addAll(this.ongoingTasks);
		temp.addAll(this.floatingTasks);
		temp.addAll(this.completedTasks);
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
	private Logic(String directory) {
		storage = new Storage(directory);
		ongoingTasks = storage.read(TASK_STATUS.ONGOING);
		completedTasks = storage.read(TASK_STATUS.COMPLETED);
		overdueTasks = storage.read(TASK_STATUS.OVERDUE);
		floatingTasks = storage.read(TASK_STATUS.FLOATING);
		categories = initialiseCategories();
		results = new ArrayList<Task>();
		history = new History();
	}
	
	// for testing
	/*protected Logic() {
		ongoingTasks = new ArrayList<Task>();
		completedTasks = new ArrayList<Task>();
		overdueTasks = new ArrayList<Task>();
		floatingTasks = new ArrayList<Task>();
		categories = new TreeMap<String, Category>();
		results = new ArrayList<Task>();
		history = new History();
	}*/

	protected String processCommand(Parser parser) {
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
			categories = delete.getCategories();
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
				data = history.undo(data);
				update(data, this.categories);
				message = "Undone successfully.";
			} catch (EmptyStackException e) {
				message = "There is nothing to be undone.";
			}
			break;
		case REDO:
			try {
				data = history.redo();
				update(data, this.categories);
				message = "Redone successfully.";
			} catch (EmptyStackException e) {
				message = "There is nothing to be redone.";
			}
			break;
		case SEARCH:
			Search search = new Search(parser, data, categories);
			history.save(cloneData(data));
			message = search.execute();
			this.results = search.getSearchResults();
			this.status = search.getStatus();
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
		String message = command.execute();
		this.update(command.getData(), command.getCategories());
		this.status = command.getStatus();
		return message;
	}

	private ArrayList<ArrayList<Task>> compress() {
		ArrayList<ArrayList<Task>> tasks = new ArrayList<ArrayList<Task>>();
		tasks.add(floatingTasks);
		tasks.add(ongoingTasks);
		tasks.add(completedTasks);
		tasks.add(overdueTasks);
		return tasks;
	}

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

	private String update(ArrayList<ArrayList<Task>> data, ArrayList<String> categories) {
		this.floatingTasks = data.get(0);
		this.ongoingTasks = data.get(1);
		this.completedTasks = data.get(2);
		this.overdueTasks = data.get(3);
		this.categories = categories;
		return "Data updated.";
	}
	
	private ArrayList<String> initialiseCategories() {
		ArrayList<String> categories = new ArrayList<String>();
		categories = readCategories(floatingTasks, categories);
		categories = readCategories(ongoingTasks, categories);
		categories = readCategories(completedTasks, categories);
		categories = readCategories(overdueTasks, categories);
		return categories;
	}
	
	private ArrayList<String> readCategories(ArrayList<Task> tasks, ArrayList<String> categories) {
		for (Task task: tasks) {
			ArrayList<String> tags = task.getTags();
			for (String tag: tags) {
				if (!hasTag(categories, tag)) {
					categories.add(tag);
				}
			}
		}
		return categories;
	}
	
	/*private TreeMap<String, Category> readCategories(ArrayList<Task> tasks,
			TreeMap<String, Category> categories) {
		for (Task task: tasks) {
			if (task.getTag()!=null) {
				// task is specifically tagged
				String key = task.getTag();
				Category category = categories.get(key);
				if (category!=null) {
					// category already present
					category.addTask(task);
				}
				else {
					// new category needs to be created
					TreeMap<String, Task> tasksUnderCategory = new TreeMap<String, Task>();
					category = new Category(key, tasksUnderCategory);
				}
				categories.put(key, category);
			}
		}
		return categories;
	}*/
	
	private boolean hasTag(ArrayList<String> categories, String key) {
		for (String tag: categories) {
			if (key.equalsIgnoreCase(tag)) {
				return true;
			}
		}
		return false;
	}
}
