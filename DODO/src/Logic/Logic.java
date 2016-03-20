package Logic;

import Command.*;
import Parser.*;
import Storage.*;
import Task.*;
import GUI.*;

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
	private ArrayList<Task> results;
	
	/*************************************CONSTRUCTOR******************************************/
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
	
	
	/***********************************PRIVATE METHODS***********************************************/
	private Logic(String directory) {
		System.out.println("[Logic/init] directory: " + directory);
		storage = new Storage(directory);
		ongoingTasks = storage.read(TASK_STATUS.ONGOING);
		completedTasks = storage.read(TASK_STATUS.COMPLETED);
		overdueTasks = storage.read(TASK_STATUS.OVERDUE);
		floatingTasks = storage.read(TASK_STATUS.FLOATING);
	}

	private String processCommand(String input) {
		Parser parser = new Parser(input);
		COMMAND_TYPE command = parser.getCommandType();
		ArrayList<ArrayList<Task>> data = compress();

		switch (command) {
		case ADD:
			return add(parser, data);
		case DELETE:
			return delete(parser, data);
		case UNDO:
			return undo();
		}
		return "Invalid COMMAND_TYPE.";
	}

	private String add(Parser parser, ArrayList<ArrayList<Task>> data) {
		Add add = new Add(parser, data, COMMAND_TYPE.ADD);
		String status = add.execute();
		data = add.getData();
		update(data);
		return status;
	}
	
	private String delete(Parser parser, ArrayList<ArrayList<Task>> data) {
		Delete delete = new Delete(parser, data, COMMAND_TYPE.DELETE);
		String status = delete.execute();
		data = delete.getData();
		update(data);
		return status;
	}

	private String undo() {
		return "Undo successful";
	}

	private ArrayList<ArrayList<Task>> compress() {
		ArrayList<ArrayList<Task>> tasks = new ArrayList<ArrayList<Task>>();
		tasks.add(floatingTasks);
		tasks.add(ongoingTasks);
		tasks.add(completedTasks);
		tasks.add(overdueTasks);
		return tasks;
	}
	
	private String update(ArrayList<ArrayList<Task>> data) {
		this.floatingTasks = data.get(0);
		this.ongoingTasks = data.get(1);
		this.completedTasks = data.get(2);
		this.overdueTasks = data.get(3);
		return "Data updated.";
	}
}
