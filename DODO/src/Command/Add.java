package Command;

/* @@author: Lu Yang */

import Parser.*;
import Task.*;

import java.util.ArrayList;
import java.util.Date;

import GUI.UI_TAB;


public class Add extends Command {

	public Add(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		super(parser, data, command_type);
	}

	@Override
	public String execute() {
		TASK_TYPE type = parser.getType();
		System.out.println("[DEBUG/ADD] command type: " + parser.getCommandType());
		System.out.println("[DEBUG/ADD] type: " + type);
		switch (type) {
		case FLOATING:
			return addFloatingTasks();
		case DEADLINED:
			return addDeadlinedTasks();
		case EVENT:
			return addEvent();
		default:
			return "Invalid type of task.";
		}
	}
	
	private String addFloatingTasks() {
		String name = parser.getName(); // cannot be null
		String tag = parser.getTag(); // may be null
		
		Task task = new Task(name, tag);
		floatingTasks.add(task);
		this.UIStatus = UI_TAB.FLOATING;
		return "Floating task \"" + name + "\" added to floatingTasks.";
	}
	
	private String addDeadlinedTasks() {
		String name = parser.getName(); // cannot be null
		String tag = parser.getTag(); // may be null
		Date endDateTime = parser.getEndTime(); // cannot be null
		
		Task task = new Task(name, tag, endDateTime);
		
		if (task.getIsOverdue()){
			overdueTasks.add(task);
			this.UIStatus = UI_TAB.OVERDUE;
			return "Deadlined task \"" + name + "\" added to overdueTasks.";
		}
		else {
			ongoingTasks.add(task);
			this.UIStatus = UI_TAB.ONGOING;
			return "Deadlined task \"" + name + "\" added to ongoingTasks.";
		}
	}
	
	private String addEvent() {
		String name = parser.getName(); // cannot be null
		String tag = parser.getTag(); // may be null
		Date startDateTime = parser.getStartTime();
		Date endDateTime = parser.getEndTime();
		
		Task task = new Task(name, tag, startDateTime, endDateTime);
		
		if (task.getIsOverdue()) {
			overdueTasks.add(task);
			this.UIStatus = UI_TAB.OVERDUE;
			return "Event \"" + name + "\" added to overdueTasks.";
		}
		else {
			ongoingTasks.add(task);
			this.UIStatus = UI_TAB.ONGOING;
			return "Event \"" + name + "\" added to ongoingTasks.";
		}
	}

	@Override
	public String undo() {
		//TODO
		return null;
	}
}