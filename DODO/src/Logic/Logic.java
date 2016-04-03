package Logic;

import Command.*;
import GUI.UI_TAB;
import Parser.*;
import Storage.*;
import Task.*;
import java.util.*;
/* @@author: Lu Yang */

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