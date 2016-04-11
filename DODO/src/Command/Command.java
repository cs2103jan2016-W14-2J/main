package Command;

/* @@author: A0130684H*/

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
	protected CommandUtils cu;
	protected ArrayList<Task> ongoingTasks;
	protected ArrayList<Task> completedTasks;
	protected ArrayList<Task> overdueTasks;
	protected ArrayList<Task> floatingTasks;
	protected ArrayList<Task> results;
	protected TreeMap<String, Category> categories;
	protected int lastModifiedIndex;
	
	/**
	 * This method constructs an instance of Command.
	 * @param cu
	 * @param floatingTasks
	 * @param ongoingTasks
	 * @param completedTasks
	 * @param overdueTasks
	 * @param results
	 * @param categories
	 */
	public Command(CommandUtils cu, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		this.UIStatus = UIRightBox.getCurrentTab();
		this.cu = cu;
		this.floatingTasks = floatingTasks;
		this.ongoingTasks = ongoingTasks;
		this.completedTasks = completedTasks;
		this.overdueTasks = overdueTasks;
		this.results = results;
		this.categories = categories;
	}
	
	/**
	 * This method executes a user input and updates its memeory.
	 * @return {@code String feedback}
	 */
	public abstract String execute();

	/*******************************************ACCESSOR*********************************************/

	public TreeMap<String, Category> getCategories() {
		return this.categories;
	}

	public UI_TAB getStatus() {
		return this.UIStatus;
	}
	
	public int getLastModifiedIndex() {
		return this.lastModifiedIndex;
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
			return combine(overdueTasks, ongoingTasks, floatingTasks, completedTasks);
		default:
			return null;
		}
	}

	protected void deleteTaskFromOtherTab(Task taskToDelete) {
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

	/*************************************CATEGORY MANIPULATION********************************/
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
		boolean flag = category.addTask(task);
		System.out.println("[DEBUG Command/tagTask] category has " + flag + " been added to this task.");
		return flag;
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
				throw new Error("IMPOSSIBLE, this tag must have been added to this task previously.");
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
		ArrayList<Task> taggedTasks = category.getTasks();
		for (Task task: taggedTasks) {
			boolean indicator = task.deleteCategory(oldTag);
			if (indicator==false) {
				throw new Error("IMPOSSIBLE, this tag must have been added to this task previously.");
			}
			task.addCategory(newTag);
		}
		
		this.categories.remove(oldTag.toLowerCase());
		this.categories.put(newTag.toLowerCase(), category);
		return true;
	}

	// this method detaches reference of all categories under this task from this taskd
	protected boolean deleteTaskInCategory(Task task) {
		ArrayList<String> categoriesString = task.getCategories();
		for (String categoryString: categoriesString) {
			Category category = this.categories.get(categoryString.toLowerCase());
			boolean flag = category.deleteTask(task);
			if (flag==false) {
				throw new Error("IMPOSSIBLE, this tag must have been added to this task previously.");
			}
			if (category.getTasks().size()==0) {
				deleteCategory(categoryString);
			}
		}
		return true;
	}

	/*****************************************PRIVATE METHODS***************************************/
	
	private ArrayList<Task> combine(ArrayList<Task> overdueTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> floatingTasks, ArrayList<Task> completedTasks) {
		ArrayList<Task> all = new ArrayList<Task>();
		all.addAll(overdueTasks);
		all.addAll(ongoingTasks);
		all.addAll(floatingTasks);
		all.addAll(completedTasks);
		return all;
	}
}