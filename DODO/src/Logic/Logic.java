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
	private TreeMap<String, Category> categories;
	private ArrayList<Task> results;
	private ArrayList<Task> all;
	private History history;
	private UI_TAB status;

	/*************************************PUBLIC METHODS******************************************/
	public static Logic getInstance(String directory) {
		if (theOne==null) {
			theOne = new Logic(directory);
		}
		return theOne;
	}

	public String run(String input) {
		Parser parser;
		try {
			parser = new Parser(input);
		} catch (NumberFormatException e) {
			return "ERROR FROM PARSER";
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

	public ArrayList<Category> getCategories() {
		ArrayList<Category> list = new ArrayList<Category>(this.categories.values());
		return list;
	}
	
	public UI_TAB getStatus() {
		return this.status;
	}
	
	public ArrayList<Task> getSearchResults() {
		return this.results;
	}

	/***********************************PRIVATE METHODS***********************************************/
	private Logic(String directory) {
		storage = new Storage(directory);
		ongoingTasks = storage.read(TASK_STATUS.ONGOING);
		completedTasks = storage.read(TASK_STATUS.COMPLETED);
		overdueTasks = storage.read(TASK_STATUS.OVERDUE);
		floatingTasks = storage.read(TASK_STATUS.FLOATING);
		categories = reinitialiseCategories();
		results = new ArrayList<Task>();
		history = new History();
	}
	
	// for testing
	protected Logic() {
		ongoingTasks = new ArrayList<Task>();
		completedTasks = new ArrayList<Task>();
		overdueTasks = new ArrayList<Task>();
		floatingTasks = new ArrayList<Task>();
		categories = new TreeMap<String, Category>();
		results = new ArrayList<Task>();
		history = new History();
	}

	protected String processCommand(Parser parser) {
		String message = "";
		
		COMMAND_TYPE command = parser.getCommandType();
		ArrayList<ArrayList<Task>> data = compress();

		switch (command) {
		case ADD:
			Add add = new Add(parser, data, COMMAND_TYPE.ADD);
			message = execute(add, data);
			break;
		case DELETE:
			Delete delete = new Delete(parser, data, COMMAND_TYPE.DELETE, categories);
			message = execute(delete, data);
			categories = delete.getCategories();
			break;
		case EDIT:
			Edit edit = new Edit(parser, data, COMMAND_TYPE.EDIT);
			message = execute(edit, data);
			break;
		case COMPLETE:
			Complete complete = new Complete(parser, data, COMMAND_TYPE.COMPLETE);
			message = execute(complete, data);
			this.status = UI_TAB.COMPLETED;
			break;
		case TAG:
			Tag tag = new Tag(parser, data, COMMAND_TYPE.TAG, categories);
			message = execute(tag, data);
			categories = tag.getCategories();
			break;
		case FLAG:
			Flag flag = new Flag(parser, data, COMMAND_TYPE.FLAG, true);
			message = execute(flag, data);
			break;
		case UNFLAG:
			flag = new Flag(parser, data, COMMAND_TYPE.FLAG, false);
			message = execute(flag, data);
			break;
		case UNDO:
			try {
				data = history.undo(data);
				update(data);
				message = "Undone successfully.";
			} catch (EmptyStackException e) {
				message = "There is nothing to be undone.";
			}
			break;
		case REDO:
			try {
				data = history.redo();
				update(data);
				message = "Redone successfully.";
			} catch (EmptyStackException e) {
				message = "There is nothing to be redone.";
			}
			break;
		case SEARCH:
			Search search = new Search(parser, data, COMMAND_TYPE.SEARCH, categories);
			history.save(cloneData(data));
			message = search.execute();
			this.results = search.getSearchResults();
			this.status = UI_TAB.SEARCH;
			break;
		case SORT:
			Sort sort = new Sort(parser, data, COMMAND_TYPE.SORT);
			message = execute(sort, data);
			break;
		default:
			message = "Invalid Command.";
		}
		return message;
	}

	private String execute(Command command, ArrayList<ArrayList<Task>> data) {
		history.save(cloneData(data));
		String message = command.execute();
		this.update(command.getData());
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

	private String update(ArrayList<ArrayList<Task>> data) {
		this.floatingTasks = data.get(0);
		this.ongoingTasks = data.get(1);
		this.completedTasks = data.get(2);
		this.overdueTasks = data.get(3);
		return "Data updated.";
	}
	
	private TreeMap<String, Category> reinitialiseCategories() {
		TreeMap<String, Category> categories = new TreeMap<String, Category>();
		categories = readCategories(floatingTasks, categories);
		categories = readCategories(ongoingTasks, categories);
		categories = readCategories(completedTasks, categories);
		categories = readCategories(overdueTasks, categories);
		return categories;
	}

	private TreeMap<String, Category> readCategories(ArrayList<Task> tasks,
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
	}
}
