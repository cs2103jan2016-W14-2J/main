package Command;

/* @@author: Lu Yang */

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

	public Command(Parser parser, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		this.UIStatus = UIRightBox.getCurrentTab();
		this.parser = parser;
		this.floatingTasks = floatingTasks;
		this.ongoingTasks = ongoingTasks;
		this.completedTasks = completedTasks;
		this.overdueTasks = overdueTasks;
		this.results = results;
		this.categories = categories;
	}

	public abstract String execute();

	/*******************************************ACCESSOR*********************************************/

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
			return this.results;
		case ALL:
			return combine(floatingTasks, ongoingTasks, completedTasks, overdueTasks);
		default:
			return null;
		}
	}

	/*//protected void modify(UI_TAB status, ArrayList<Task> newList) {
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
			modifyOnSearch(newList);
			break;
		case ALL:
			splitAllAndUpdate(newList);
			break;
		default:
			break;
		}
	}*/
	
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
	protected boolean tagTask(String categoryStr, Task task) {
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
	private ArrayList<Task> combine(ArrayList<Task> ongoingTasks, ArrayList<Task> completedTasks,
			ArrayList<Task> floatingTasks, ArrayList<Task> overdueTasks) {
		ArrayList<Task> combined = new ArrayList<Task>();
		combined.addAll(floatingTasks);
		combined.addAll(ongoingTasks);
		combined.addAll(completedTasks);
		combined.addAll(overdueTasks);
		return combined;
	}
	
	private void modifyOnSearch(ArrayList<Task> newList) {
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
	
	private ArrayList<Task> copyList(ArrayList<Task> original) {
		ArrayList<Task> newList = new ArrayList<Task>();
		for (Task task: original) {
			newList.add(task);
		}
		return newList;
	}
}
