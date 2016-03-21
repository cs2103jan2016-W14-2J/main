package Command;

import java.util.ArrayList;

import GUI.*;
import Parser.*;
import Task.*;


public abstract class Command {
	protected static final int INDEX_ADJUSTMENT = 1;
	
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
	
	protected ArrayList<Task> getTasks(TASK_STATUS status) {
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

	protected void setTasks(TASK_STATUS status, ArrayList<Task> list) {
		switch(status) {
		case ONGOING:
			this.ongoingTasks = list;
			break;
		case FLOATING:
			this.floatingTasks = list;
			break;
		case COMPLETED:
			this.completedTasks = list;
			break;
		case OVERDUE:
			this.overdueTasks = list;
			break;
		}
	}
}
