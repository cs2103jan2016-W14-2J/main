package Command;

import java.util.ArrayList;

import GUI.*;
import Logic.*;
import Parser.*;
import Storage.*;
import Task.*;


public abstract class Command {
	protected COMMAND_TYPE command_type;
	protected Parser parser;
	protected ArrayList<Task> ongoingTasks;
	protected ArrayList<Task> completedTasks;
	protected ArrayList<Task> overdueTasks;
	protected ArrayList<Task> floatingTasks;
	protected TASK_STATUS UIStatus;
	
	public Command(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		this.UIStatus = UIRightBox.getCurrentTab();
		this.parser = parser;
		this.floatingTasks = data.get(0);
		this.ongoingTasks = data.get(1);
		this.completedTasks = data.get(2);
		this.overdueTasks = data.get(3);
		this.command_type = command_type;
	}
	
	public COMMAND_TYPE getCommandType() {
		return this.command_type;
	}
	
	public ArrayList<ArrayList<Task>> getData() {
		return compress();
	}
	
	public abstract String execute();
	
	public abstract String undo();
	
	private ArrayList<ArrayList<Task>> compress() {
		ArrayList<ArrayList<Task>> tasks = new ArrayList<ArrayList<Task>>();
		tasks.add(floatingTasks);
		tasks.add(ongoingTasks);
		tasks.add(completedTasks);
		tasks.add(overdueTasks);
		return tasks;
	}
}
