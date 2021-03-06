package Logic;

import Command.*;
import GUI.UI_TAB;
import Parser.*;
import Storage.*;
import Task.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/* @@author: A0130684H */

public class Logic {
	private static final String MESSAGE_SUCCESSFUL_UNDO = "Undo successful.";
	private static final String MESSAGE_SUCCESSFUL_REDO = "Redo successful.";
	private static final String MESSAGE_SUCCESSFUL_SAVE = "Save successful.";
	private static final String MESSAGE_UNSUCCESSFUL_UNDO = "Undo not successful. There is nothing to undo";
	private static final String MESSAGE_UNSUCCESSFUL_REDO = "Redo not successful. There is nothing to redo";
	private static final String MESSAGE_SWITCH_VIEW = "Successfully switch to %1$s.";
	private static final String MESSAGE_INVALID_COMMAND = "Please enter a valid command.";
	private static Logic theOne; //singleton
	private Storage storage;
	private static Logger logger = Logger.getLogger("Logic");

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
	private int lastModifiedIndex;

	/*************************************PUBLIC METHODS******************************************/
	/**
	 * This method constructs an instance of Logic.
	 * It implements Singleton Pattern so that only one instance of Logic is allowed to be constructed at all time.
	 * It initializes appropriate attributes such as Storage, History and ArrayLists.
	 * It must be called before using any other methods of Logic.
	 * 
	 * @return {@code Logic theOne}
	 */
	public static Logic getInstance() {
		if (theOne==null) {
			theOne = new Logic();
		}
		return theOne;
	}
	
	/**
	 * This method constructs an instance of Parser.
	 * It processes the user input and update its attributes correspondingly.
	 * 
	 * @param  {@code String input}
	 * @return {@code String feedback}
	 */
	public String run(String input) {
		this.previous = input;
		CommandUtils cu = new CommandUtils();
		Parser parser = new Parser();
		try {
			cu = parser.executeCommand(cu, input);
		} catch (Exception e) {
			return MESSAGE_INVALID_COMMAND;
		}
		String message;
		try {
			message = processCommand(cu);
		} catch (Exception e) {
			logger.log(Level.WARNING, "processing error", e);
		}
		
		return message;
	}
	
	/**
	 * This method saves all its memory, or its attributes to Storage.
	 * 
	 * @return {@code String feedback}
	 */
	public String save() {
		storage.save(TASK_STATUS.ONGOING, ongoingTasks);
		storage.save(TASK_STATUS.COMPLETED, completedTasks);
		storage.save(TASK_STATUS.FLOATING, floatingTasks);
		storage.save(TASK_STATUS.OVERDUE, overdueTasks);
		storage.saveCategories(this.categories);
		return MESSAGE_SUCCESSFUL_SAVE;
	}
	
	/**
	 * This method converts categories of type ArrayList of String,
	 * to type of ArrayList of Category,
	 * @param categoriesString
	 * @return {@code ArrayList<Category> categories}
	 */
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
	
	/**
	 * This method updates all its memory with the latest System time.
	 *
	 */
	public void update() {
		this.updateList(this.ongoingTasks);
		this.updateList(this.overdueTasks);
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
	
	public int getLastModifiedIndex() {
		return this.lastModifiedIndex;
	}

	/***********************************PRIVATE METHODS***********************************************/
	private String processCommand(CommandUtils cu) {
		String message = "";
		COMMAND_TYPE command = cu.getCommandType();
		System.out.println("=====COMMAND===== command: " + command);
		System.out.println(command);

		switch (command) {
		case ADD:
			Add add = new Add(cu, this.floatingTasks, this.ongoingTasks, 
					this.completedTasks, this.overdueTasks, this.results, this.categories);
			message = execute(add);
			break;
		case DELETE:
			Delete delete = new Delete(cu, this.floatingTasks, this.ongoingTasks, 
					this.completedTasks, this.overdueTasks, this.results, this.categories);
			message = execute(delete);
			break;
		case EDIT:
			Edit edit = new Edit(cu, this.floatingTasks, this.ongoingTasks, 
					this.completedTasks, this.overdueTasks, this.results, this.categories);
			message = execute(edit);
			break;
		case COMPLETE:
			Complete complete = new Complete(cu, this.floatingTasks, this.ongoingTasks, 
					this.completedTasks, this.overdueTasks, this.results, this.categories);
			message = execute(complete);
			break;
		case TAG:
			Tag tag = new Tag(cu, this.floatingTasks, this.ongoingTasks, 
					this.completedTasks, this.overdueTasks, this.results, this.categories);
			message = execute(tag);
			categories = tag.getCategories();
			break;
		case FLAG:
			Flag flag = new Flag(cu, this.floatingTasks, this.ongoingTasks, 
					this.completedTasks, this.overdueTasks, this.results, this.categories, true);
			message = execute(flag);
			break;
		case UNFLAG:
			flag = new Flag(cu, this.floatingTasks, this.ongoingTasks, 
					this.completedTasks, this.overdueTasks, this.results, this.categories, false);
			message = execute(flag);
			break;
		case UNDO:
			try {
				this.update(this.history.undoData(this.compress()), this.history.undoCategories(categories));
				message = MESSAGE_SUCCESSFUL_UNDO;
			} catch (EmptyStackException e) {
				message = MESSAGE_UNSUCCESSFUL_UNDO;
			}
			break;
		case REDO:
			try {
				this.update(this.history.redoData(), this.history.redoCategories());
				message = MESSAGE_SUCCESSFUL_REDO;
			} catch (EmptyStackException e) {
				message = MESSAGE_UNSUCCESSFUL_REDO;
			}
			break;
		case SEARCH:
			this.results.clear();
			Search search = new Search(cu, this.floatingTasks, this.ongoingTasks, 
					this.completedTasks, this.overdueTasks, this.results, this.categories);
			message = execute(search);
			break;
		case SORT:
			Sort sort = new Sort(cu, this.floatingTasks, this.ongoingTasks, 
					this.completedTasks, this.overdueTasks, this.results, this.categories);
			message = execute(sort);
			break;
		case CHANGE_DIRECTORY:
			message = this.redirect();
			break;
		case INVALID:
		default:
			message = MESSAGE_INVALID_COMMAND;
		}
		return message;
	}

	private String execute(Command command) {
		history.save(compress(), cloneCategories(this.categories));
		String message = command.execute();
		this.status = command.getStatus();
		this.lastModifiedIndex = command.getLastModifiedIndex();
		return message;
	}

	private void updateList(ArrayList<Task> list) {
		for (Task task: list) {
			boolean updated = task.update();
			if (updated) {
				TASK_STATUS newStatus = task.getStatus();
				list.remove(task);
				ArrayList<Task> newList = retrieve(newStatus);
				newList.add(task);
				System.out.println("=====LOGIC===== update: " + updated);
			}
		}
	}
	
	private String redirect() {
		this.storage.redirect();
		return this.save();
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
		data.add(cloneList(this.floatingTasks));
		data.add(cloneList(this.ongoingTasks));
		data.add(cloneList(this.completedTasks));
		data.add(cloneList(this.overdueTasks));
		data.add(cloneList(this.results));
		return data;
	}

	private ArrayList<Task> retrieve(TASK_STATUS status) {
		switch (status) {
		case ONGOING:
			return this.ongoingTasks;
		case FLOATING:
			return this.floatingTasks;
		case COMPLETED:
			return this.completedTasks;
		case OVERDUE:
			return this.overdueTasks;
		default:
			return null;
		}
	}

	/**********************************FOR UNDO/REDO**********************************************************/
	private ArrayList<Task> cloneList(ArrayList<Task> original) {
		ArrayList<Task> newList = new ArrayList<Task>();
		for (int i=0; i<original.size(); i++) {
			Task task = new Task(original.get(i));
			newList.add(task);
		}
		return newList;
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
	private Logic() {
		storage = Storage.getInstance();
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