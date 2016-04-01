package Command;

/* @@author: Lu Yang */

import Parser.*;
import Task.*;

import java.util.ArrayList;
import java.util.Date;

import GUI.UI_TAB;


public class Add extends Command {	
	public Add(Parser parser, ArrayList<ArrayList<Task>> data, ArrayList<String> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		TASK_TYPE type = parser.getType();
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
		String name = parser.getName();
		ArrayList<String> tags = parser.getTag();
		//ArrayList<String> tags = null;
		this.updateCategories(tags);
		
		Task task = new Task(name, tags);
		this.floatingTasks.add(task);
		this.UIStatus = UI_TAB.FLOATING;
		return "Floating task \"" + name + "\" added to floatingTasks.";
	}
	
	private String addDeadlinedTasks() {
		String name = parser.getName(); // cannot be null
		ArrayList<String> tags = parser.getTag();
		//ArrayList<String> tags = null;
		Date endDateTime = parser.getEndTime();
		this.updateCategories(tags);
		
		Task task = new Task(name, tags, endDateTime);
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
		//ArrayList<String> tags = parser.getTags();
		ArrayList<String> tags = null;
		Date startDateTime = parser.getStartTime();
		Date endDateTime = parser.getEndTime();
		this.updateCategories(tags);
		
		Task task = new Task(name, tags, startDateTime, endDateTime);
		
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
}