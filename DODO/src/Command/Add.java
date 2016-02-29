package Command;

import GUI.*;
import Logic.*;
import Parser.*;
import Storage.*;
import Task.*;
import java.util.ArrayList;
import java.util.Date;


public class Add extends Command {
	
	// for use in logic class
	public Add(Parser parser, Logic logic, COMMAND_TYPE command_type) {
		super(parser, logic, command_type);
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
		
		Task task = new Task(TASK_TYPE.FLOATING, name, tag);
		ArrayList<Task> floatingTasks = logic.getFloatingTasks();
		floatingTasks.add(task);
		logic.setFloatingTasks(floatingTasks);
		return "Floating task \"" + name + "\" added to floatingTasks.";
	}
	
	private String addDeadlinedTasks() {
		String name = parser.getName(); // cannot be null
		String tag = parser.getTag(); // may be null
		Date endDateTime = null; // cannot be null
		
		DeadlinedTask task = new DeadlinedTask(TASK_TYPE.DEADLINED, name, tag, endDateTime);
		
		if (task.getIsOverdue()){
			ArrayList<Task> overdueTasks = logic.getOverdueTasks();
			overdueTasks.add(task);
			logic.setOverdueTasks(overdueTasks);
			return "Deadlined task \"" + name + "\"} added to overdueTasks.";
		}
		else {
			ArrayList<Task> ongoingTasks = logic.getOngoingTasks();
			ongoingTasks.add(task);
			logic.setOngoingTasks(ongoingTasks);
			return "Deadlined task \"" + name + "\" added to ongoingTasks.";
		}
	}
	
	private String addEvent() {
		String name = parser.getName(); // cannot be null
		String tag = parser.getTag(); // may be null
		Date startDateTime = null;
		Date endDateTime = null;
		
		Event task = new Event(TASK_TYPE.EVENT, name, tag, startDateTime, endDateTime);
		
		if (task.getIsOverdue()) {
			ArrayList<Task> overdueTasks = logic.getOverdueTasks();
			overdueTasks.add(task);
			logic.setOverdueTasks(overdueTasks);
			return "Event \"" + name + "\" added to overdueTasks.";
		}
		else {
			ArrayList<Task> ongoingTasks = logic.getOngoingTasks();
			ongoingTasks.add(task);
			logic.setOngoingTasks(ongoingTasks);
			return "Event \"" + name + "\" added to ongoingTasks.";
		}
	}

	@Override
	public String undo() {
		//TODO
		return null;
	}
}