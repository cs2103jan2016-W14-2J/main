package Logic;

import Command.*;
import Parser.*;
import Storage.*;
import Task.*;
import java.util.*;

/*
 * @author: Lu Yang
 * description: 
 */

public class Logic {
	private static Logic theOne; //singleton
	private Storage storage;
	private ArrayList<Task> ongoingTasks;
	private ArrayList<Task> completedTasks;
	private ArrayList<Task> overdueTasks;
	private ArrayList<Task> floatingTasks;
	private TreeMap<String, Category> categories;
	private ArrayList<Task> results;
	private History history;

	/*************************************PUBLIC METHODS******************************************/
	public static Logic getInstance(String directory) {
		if (theOne==null) {
			theOne = new Logic(directory);
		}
		return theOne;
	}

	public String run(String input) {
		return processCommand(input);
	}

	public String save() {
		String test = storage.save(TASK_STATUS.ONGOING, ongoingTasks);
		System.out.println("[DEBUG/Logic/save] save: " + test);
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

	public TreeMap<String, Category> getCategories() {
		return this.categories;
	}

	/***********************************PRIVATE METHODS***********************************************/
	private Logic(String directory) {
		storage = new Storage(directory);
		ongoingTasks = storage.read(TASK_STATUS.ONGOING);
		completedTasks = storage.read(TASK_STATUS.COMPLETED);
		overdueTasks = storage.read(TASK_STATUS.OVERDUE);
		floatingTasks = storage.read(TASK_STATUS.FLOATING);
		history = new History();
	}

	private String processCommand(String input) {
		String status = "";
		Parser parser = new Parser(input);
		COMMAND_TYPE command = parser.getCommandType();
		ArrayList<ArrayList<Task>> data = compress();
		
		

		switch (command) {
		case ADD:
			Add add = new Add(parser, data, COMMAND_TYPE.ADD);
			status = execute(add, data);
			break;
		case DELETE:
			Delete delete = new Delete(parser, data, COMMAND_TYPE.DELETE, categories);
			status = execute(delete, data);
			categories = delete.getCategories();
			break;
		case EDIT:
			Edit edit = new Edit(parser, data, COMMAND_TYPE.EDIT);
			status = execute(edit, data);
			break;
		case COMPLETE:
			Complete complete = new Complete(parser, data, COMMAND_TYPE.COMPLETE);
			status = execute(complete, data);
			break;
		case TAG:
			Tag tag = new Tag(parser, data, COMMAND_TYPE.TAG, categories);
			status = execute(tag, data);
			categories = tag.getCategories();
			break;
		case FLAG:
			Flag flag = new Flag(parser, data, COMMAND_TYPE.FLAG);
			status = execute(flag, data);
			break;
		case UNDO:
			try {
				data = history.undo(data);
				update(data);
				status = "Undone successfully.";
			} catch (EmptyStackException e) {
				status = "There is nothing to be undone.";
			}
			break;
		case REDO:
			try {
				data = history.redo();
				update(data);
				status = "Redone successfully.";
			} catch (EmptyStackException e) {
				status = "There is nothing to be redone.";
			}
			break;
		default:
			status = "Invalid Command.";
		}
		return status;
	}

	private String execute(Command command, ArrayList<ArrayList<Task>> data) {
		history.save(cloneData(data));
		String status = command.execute();
		this.update(command.getData());
		return status;
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
}
