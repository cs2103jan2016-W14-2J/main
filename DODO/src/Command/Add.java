package Command;

import Parser.*;
import Task.*;

import java.util.ArrayList;
import java.util.Date;


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
		
		Task task = new Task(TASK_TYPE.FLOATING, name, tag);
		floatingTasks.add(task);
		return "Floating task \"" + name + "\" added to floatingTasks.";
	}
	
	private String addDeadlinedTasks() {
		String name = parser.getName(); // cannot be null
		String tag = parser.getTag(); // may be null
		Date endDateTime = parser.getEndTime(); // cannot be null
		
		DeadlinedTask task = new DeadlinedTask(TASK_TYPE.DEADLINED, name, tag, endDateTime);
		
		if (task.getIsOverdue()){
			overdueTasks.add(task);
			return "Deadlined task \"" + name + "\" added to overdueTasks.";
		}
		else {
			ongoingTasks.add(task);
			return "Deadlined task \"" + name + "\" added to ongoingTasks.";
		}
	}
	
	private String addEvent() {
		String name = parser.getName(); // cannot be null
		String tag = parser.getTag(); // may be null
		Date startDateTime = parser.getStartTime();
		Date endDateTime = parser.getEndTime();
		
		Event task = new Event(TASK_TYPE.EVENT, name, tag, startDateTime, endDateTime);
		
		if (task.getIsOverdue()) {
			overdueTasks.add(task);
			return "Event \"" + name + "\" added to overdueTasks.";
		}
		else {
			ongoingTasks.add(task);
			return "Event \"" + name + "\" added to ongoingTasks.";
		}
	}

	@Override
	public String undo() {
		//TODO
		return null;
	}
}