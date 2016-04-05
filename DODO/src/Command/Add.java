package Command;

/* @@author: Lu Yang */

import Parser.*;
import Task.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import GUI.UI_TAB;


public class Add extends Command {
	private static final String MESSAGE_INVALID_ADD = "Please enter a valid add command.";
	private static final String MESSAGE_SUCCESSFUL_ADD = "Task \"%1$s\" is added to %2$s.";

	public Add(Parser parser, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		super(parser, floatingTasks, ongoingTasks, completedTasks, overdueTasks, results, categories);
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
			return MESSAGE_INVALID_ADD;
		}
	}
	
	private String addFloatingTasks() {
		String name = parser.getName();
		ArrayList<String> tags = parser.getTag();
		
		Task task = new Task(name);
		for (String tagString: tags) {
			this.tagTask(tagString, task);
		}
		
		this.floatingTasks.add(task);
		this.UIStatus = UI_TAB.FLOATING;
		
		System.out.println("=====add===== tasks: " + task.getCategories());
		return String.format(MESSAGE_SUCCESSFUL_ADD, task, this.UIStatus);
	}
	
	private String addDeadlinedTasks() {
		String name = parser.getName();
		ArrayList<String> tags = parser.getTag();
		Date endDateTime = parser.getEndTime();
		
		Task task = new Task(name, endDateTime);
		for (String tagString: tags) {
			this.tagTask(tagString, task);
		}
		
		checkOverdueTask(task);
		return String.format(MESSAGE_SUCCESSFUL_ADD, task, this.UIStatus);
	}
	
	private String addEvent() {
		String name = parser.getName();
		ArrayList<String> tags = parser.getTag();
		Date startDateTime = parser.getStartTime();
		Date endDateTime = parser.getEndTime();
		if (startDateTime.after(endDateTime)) {
			return MESSAGE_INVALID_EVENT_TIME;
		}
		
		Task task = new Task(name, startDateTime, endDateTime);
		for (String tagString: tags) {
			this.tagTask(tagString, task);
		}
		
		checkOverdueTask(task);	
		return String.format(MESSAGE_SUCCESSFUL_ADD, task, this.UIStatus);
	}
	
	private void checkOverdueTask(Task task) {
		if (task.getStatus()==TASK_STATUS.OVERDUE) {
			overdueTasks.add(task);
			this.UIStatus = UI_TAB.OVERDUE;
		}
		else if (task.getStatus()==TASK_STATUS.ONGOING) {
			ongoingTasks.add(task);
			this.UIStatus = UI_TAB.ONGOING;
		}
	}
}