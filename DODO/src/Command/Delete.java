package Command;

/* @@author: Lu Yang */

import java.util.*;

import GUI.UI_TAB;
import Parser.*;
import Task.*;

public class Delete extends Command {
	private static final String MESSAGE_SUCCESSFUL_DELETE = "Task(s) at \"%1$s\" is/are successfully deleted. ";
	private static final String MESSAGE_SUCCESSFUL_DELETE_TAG = "Tag(s) \"%1$s\" are successfully deleted. ";
	private static final String MESSAGE_UNSUCCESSFUL_DELETE_TAG = "Tag(s) \"%1$s\" are not successfully deleted. ";
	
	public Delete(Parser parser, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		super(parser, floatingTasks, ongoingTasks, completedTasks, overdueTasks, results, categories);
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
				if (this.UIStatus==UI_TAB.SEARCH) {
					delete(task);
				}
				this.deleteTaskInCategory(task);
				System.out.println("=====DELTE===== categories: " + this.categories);
			}
		} catch (IndexOutOfBoundsException e) {
			return MESSAGE_INDEXOUTOFBOUND;
		} catch (NullPointerException e) {
			return MESSAGE_EMPTY_LIST;
		}
		return String.format(MESSAGE_SUCCESSFUL_DELETE, indexes);
	}
	
	private void delete(Task taskToDelete) {
		TASK_STATUS status = taskToDelete.getStatus();
		switch (status) {
		case ONGOING:
			this.ongoingTasks.remove(taskToDelete);
			break;
		case FLOATING:
			this.floatingTasks.remove(taskToDelete);
			break;
		case COMPLETED:
			this.completedTasks.remove(taskToDelete);
			break;
		case OVERDUE:
			this.overdueTasks.remove(taskToDelete);
			break;
		}
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
		return "Start Time of tasks at " + indexes + " has been removed.";
	}
	
	private String convertToFloating(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus); 
		for (Integer index: indexes) {
			Task task = tasks.get(index - INDEX_ADJUSTMENT);
			if (this.UIStatus==UI_TAB.SEARCH) {
				delete(task);
			}
			task.setEnd(null);
			this.floatingTasks.add(task);
			tasks.remove(index- INDEX_ADJUSTMENT);
		}
		this.UIStatus = UI_TAB.FLOATING;
		return "Deadline of tasks at " + indexes + " has been removed.";
	}
}
