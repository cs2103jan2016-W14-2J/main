package Command;

/* @@author: A0130684H */

import java.util.*;

import GUI.UI_TAB;
import Parser.*;
import Task.*;

public class Delete extends Command {
	private static final String MESSAGE_INVALID_DELETE = "Please enter a valid delete command.";
	private static final String MESSAGE_SUCCESSFUL_DELETE = "Task(s) at \"%1$s\" is/are successfully deleted. ";
	private static final String MESSAGE_SUCCESSFUL_DELETE_TAG = "Tag(s) \"%1$s\" are successfully deleted. ";
	private static final String MESSAGE_UNSUCCESSFUL_DELETE_TAG = "Tag(s) \"%1$s\" are not successfully deleted. ";
	private static final String MESSAGE_DELETE_ALL = "All tasks and tags are deleted.";
	
	public Delete(CommandUtils cu, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		super(cu, floatingTasks, ongoingTasks, completedTasks, overdueTasks, results, categories);
	}

	public String execute() {
		DELETE_TYPE type = cu.getDeleteType();
		ArrayList<String> tags = cu.getTagToDelete();
		ArrayList<Integer> indexes = cu.getIndexToDelete();

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
			return MESSAGE_INVALID_DELETE;
		}
	}
	
	private String deleteTask(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		System.out.println("====DELETE====" + tasks);
		if (tasks==null || tasks.size()==0) {
			return MESSAGE_EMPTY_LIST;
		}
		
		try {
			for (int i=indexes.size()-1; i>=0; i--) {
				int index = indexes.get(i) - INDEX_ADJUSTMENT;
				Task task = tasks.remove(index);
				if (this.UIStatus==UI_TAB.SEARCH || this.UIStatus==UI_TAB.ALL) {
					deleteTaskFromOtherTab(task);
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
	
	private String deleteAllTasks() {
		String message;
		if (this.UIStatus!=UI_TAB.ALL) {
			ArrayList<Task> tasks = retrieve(this.UIStatus);
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			for (int i=1; i<=tasks.size(); i++) {
				indexes.add(i);
			}
			message = deleteTask(indexes);
		}
		else {
			this.floatingTasks.clear();
			this.ongoingTasks.clear();
			this.completedTasks.clear();
			this.overdueTasks.clear();
			this.results.clear();
			this.categories.clear();
			message = MESSAGE_DELETE_ALL;
		}
		this.UIStatus = UI_TAB.ALL;
		return message;
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
		this.lastModifiedIndex = indexes.get(indexes.size()-1) - INDEX_ADJUSTMENT;
		return "Start Time of tasks at " + indexes + " has been removed.";
	}
	
	private String convertToFloating(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus); 
		for (Integer index: indexes) {
			Task task = tasks.get(index - INDEX_ADJUSTMENT);
			task.setEnd(null);
			this.floatingTasks.add(task);
			tasks.remove(index - INDEX_ADJUSTMENT);
			if (this.UIStatus == UI_TAB.SEARCH || this.UIStatus == UI_TAB.ALL) {
				this.deleteTaskFromOtherTab(task);
			}
		}
		this.UIStatus = UI_TAB.FLOATING;
		this.lastModifiedIndex = this.floatingTasks.size()-1;
		return "Deadline of tasks at " + indexes + " has been removed.";
	}
}
