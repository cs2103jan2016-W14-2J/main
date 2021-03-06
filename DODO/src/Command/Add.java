package Command;

/* @@author: A0130684H */

import Parser.*;
import Task.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import GUI.UI_TAB;


public class Add extends Command {
	private static final String MESSAGE_INVALID_ADD = "Please enter a valid add command.";
	private static final String MESSAGE_SUCCESSFUL_ADD = "Task \"%1$s\" is added to %2$s.";

	public Add(CommandUtils cu, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		super(cu, floatingTasks, ongoingTasks, completedTasks, overdueTasks, results, categories);
	}

	@Override
	public String execute() {
		TASK_TYPE type = cu.getType();
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
		String name = cu.getName();
		ArrayList<String> tags = cu.getTag();
		boolean important = cu.getImportance();
		System.out.println("====ADD==== tags: " + tags);
		System.out.println("====ADD==== size of arraylist: " + tags.size());
		
		Task task = new Task(name);
		task.setFlag(important);
		if (tags!=null) {
			for (String tagString: tags) {
				System.out.println("====ADD==== tag: " + tagString);
				if (tagString!=null) this.tagTask(tagString, task);
			}
		}

		this.floatingTasks.add(task);
		this.UIStatus = UI_TAB.FLOATING;
		this.lastModifiedIndex = this.floatingTasks.size()-1;

		System.out.println("=====add===== tasks: " + task.getCategories());
		return String.format(MESSAGE_SUCCESSFUL_ADD, task, this.UIStatus);
	}

	private String addDeadlinedTasks() {
		String name = cu.getName();
		ArrayList<String> tags = cu.getTag();
		Date endDateTime = cu.getEndTime();
		boolean important = cu.getImportance();
		System.out.println("====ADD==== tags: " + tags);

		Task task = new Task(name, endDateTime);
		task.setFlag(important);
		if (tags!=null) {
			for (String tagString: tags) {
				if (tagString!=null) this.tagTask(tagString, task);
			}
		}

		checkOverdueTask(task);
		return String.format(MESSAGE_SUCCESSFUL_ADD, task, this.UIStatus);
	}

	private String addEvent() {
		String name = cu.getName();
		ArrayList<String> tags = cu.getTag();
		Date startDateTime = cu.getStartTime();
		Date endDateTime = cu.getEndTime();
		boolean important = cu.getImportance();

		if (startDateTime.after(endDateTime)) {
			return MESSAGE_INVALID_EVENT_TIME;
		}

		Task task = new Task(name, startDateTime, endDateTime);
		task.setFlag(important);
		if (tags!=null) {
			for (String tagString: tags) {
				if (tagString!=null) this.tagTask(tagString, task);
			}
		}

		checkOverdueTask(task);	
		return String.format(MESSAGE_SUCCESSFUL_ADD, task, this.UIStatus);
	}

	private void checkOverdueTask(Task task) {
		if (task.getStatus()==TASK_STATUS.OVERDUE) {
			overdueTasks.add(task);
			this.UIStatus = UI_TAB.OVERDUE;
			this.lastModifiedIndex = this.overdueTasks.size()-1;
		}
		else if (task.getStatus()==TASK_STATUS.ONGOING) {
			ongoingTasks.add(task);
			this.UIStatus = UI_TAB.ONGOING;
			this.lastModifiedIndex = this.ongoingTasks.size()-1;
		}
	}
}