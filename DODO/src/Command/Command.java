package Command;

/* @@author: Lu Yang */

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
	protected UI_TAB UIStatus;
	
	public Command(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		this.UIStatus = UIRightBox.getCurrentTab();
		this.parser = parser;
		this.floatingTasks = data.get(0);
		this.ongoingTasks = data.get(1);
		this.completedTasks = data.get(2);
		this.overdueTasks = data.get(3);
		this.command_type = command_type;
	}
	
	public abstract String execute();
	
	public abstract String undo();
	
	public ArrayList<ArrayList<Task>> getData() {
		return this.compress();
	}
	
	public COMMAND_TYPE getCommandType() {
		return this.command_type;
	}
	
	public UI_TAB getStatus() {
		return this.UIStatus;
	}
	
	/*********************************INTERNAL METHODS***************************************/
	protected ArrayList<ArrayList<Task>> compress() {
		ArrayList<ArrayList<Task>> tasks = new ArrayList<ArrayList<Task>>();
		tasks.add(floatingTasks);
		tasks.add(ongoingTasks);
		tasks.add(completedTasks);
		tasks.add(overdueTasks);
		return tasks;
	}
	
	protected ArrayList<Task> combine(ArrayList<ArrayList<Task>> all) {
		ArrayList<Task> temp = new ArrayList<Task>();
		for (ArrayList<Task> element: all) {
			temp.addAll(element);
		}
		return temp;
	}
	
	protected ArrayList<Task> getTasks(UI_TAB status) {
		switch (status) {
		case ONGOING:
			return this.ongoingTasks;
		case FLOATING:
			return this.floatingTasks;
		case COMPLETED:
			return this.completedTasks;
		case OVERDUE:
			return this.overdueTasks;
		case ALL:
			return combine(compress());
		default:
			return null;
		}
	}

	protected void setTasks(UI_TAB status, ArrayList<Task> list) {
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
		case ALL:
			split(list);
		}
	}
	
	private void split(ArrayList<Task> all) {
		this.floatingTasks.clear();
		this.ongoingTasks.clear();
		this.overdueTasks.clear();
		this.completedTasks.clear();
		for (Task task: all) {
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
}