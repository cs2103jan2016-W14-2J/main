package Logic;

import Command.*;
import Parser.*;
import Storage.*;
import Task.*;
import dodo.Parser;
import GUI.*;

import java.util.*;

/*
 * @author: Lu Yang
 * description: 
 */

public class Logic {
	private Storage storage;
	private ArrayList<Task> ongoingTasks;
	private ArrayList<Task> completedTasks;
	private ArrayList<Task> overdueTasks;
	private ArrayList<Task> floatingTasks;
	private ArrayList<Task> results;
	
	// UI: you will call this when user starts the programme
	public Logic(String directory) {
		storage = new Storage(directory);
		ongoingTasks = storage.read(TASK_STATUS.ONGOING);
		completedTasks = storage.read(TASK_STATUS.COMPLETED);
		overdueTasks = storage.read(TASK_STATUS.OVERDUE);
		floatingTasks = storage.read(TASK_STATUS.FLOATING);
	}
	
	// UI: you will call this to run logic
	public String run(String input) {
		return processCommand(input);
	}
	
	public String processCommand(String input) {
		Parser parser = new Parser(input);
		COMMAND_TYPE command = parser.getCommandType();
		
		switch (command) {
		case ADD:
			return add(parser);
		case UNDO:
			return undo();
		}
		return "Invalid COMMAND_TYPE.";
	}
	
	public String add(Parser parser) {
		Add add = new Add(parser, this, COMMAND_TYPE.ADD);
		return add.execute();
	}
	
	public String undo() {
		return "Undo successful";
	}
	
	// UI: you will call this when user is quiting the programme
	public String save() {
		storage.save(TASK_STATUS.ONGOING, ongoingTasks);
		storage.save(TASK_STATUS.COMPLETED, completedTasks);
		storage.save(TASK_STATUS.FLOATING, floatingTasks);
		storage.save(TASK_STATUS.OVERDUE, overdueTasks);
		return "Saved successfully";
	}
	
	/***********************************MUTATORS***********************************************/
	public void setOngoingTasks(ArrayList<Task> tasks) {
		this.ongoingTasks = tasks;
	}
	
	public void setFloatingTasks(ArrayList<Task> tasks) {
		this.floatingTasks = tasks;
	}
	
	public void setCompletedTasks(ArrayList<Task> tasks) {
		this.completedTasks = tasks;
	}
	
	public void setOverdueTasks(ArrayList<Task> tasks) {
		this.overdueTasks = tasks;
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
}
