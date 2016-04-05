package Command;

/* @@author: Lu Yang */

import java.util.*;

import GUI.UI_TAB;
import Task.*;
import Parser.*;

public class Edit extends Command {
	private static final String MESSAGE_INVALID_EDIT = 
			"Please enter a valid edit.";
	private static final String MESSAGE_SUCCESSFUL_EDIT_NAME = 
			"Task name has been successfully edited to \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_START = 
			"Task start time has been successfully edited to \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_END = 
			"Task end time has been successfully edited to \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_EVENT = 
			"Task event time has been successfully edited to start from \"%1$s\" to \"%2$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_TAG = 
			"Tag name \"%1$s\" has been successfully edit to \"%2$s\".";

	public Edit(Parser parser, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		super(parser, floatingTasks, ongoingTasks, completedTasks, overdueTasks, results, categories);
	}

	@Override
	public String execute() {
		EDIT_TYPE edit_type = parser.getEditType();
		int index = parser.getTaskID();
		ArrayList<Task> tasks = this.retrieve(this.UIStatus);
		System.out.println("=====EDIT===== edit_type: " + edit_type);
		try {
			switch (edit_type) {
			case TASK_NAME:
				Task task = tasks.get(index-INDEX_ADJUSTMENT);
				return editName(task);
			case START_TIME:
				task = tasks.get(index-INDEX_ADJUSTMENT);
				return editStartTime(task);
			case DEADLINED:
			case END_TIME:
				task = tasks.get(index-INDEX_ADJUSTMENT);
				return editEndTime(task);
			case EVENT_TIME:
				task = tasks.get(index-INDEX_ADJUSTMENT);
				return editStartAndEndTime(task);
			case TAG:
				return editTag();
			default:
				return MESSAGE_INVALID_EDIT;
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return MESSAGE_INDEXOUTOFBOUND;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return MESSAGE_EMPTY_LIST;
		}
	}
	
	private String editTag() {
		String oldTag = parser.getOldTag();
		String newTag = parser.getTag().get(0);
		
		if (this.editCategory(oldTag, newTag)) {
			return String.format(MESSAGE_SUCCESSFUL_EDIT_TAG, oldTag, newTag);
		}
		else {
			return String.format(MESSAGE_UNSUCCESSFUL_SEARCH_TAG, oldTag);
		}
	}

	private String editName(Task task) {
		String newName = parser.getName();
		task.setName(newName);
		return String.format(MESSAGE_SUCCESSFUL_EDIT_NAME, task);
	}
	
	private String editStartTime(Task task) {
		TASK_TYPE oldType = task.getType();
		Date newStartTime = parser.getStartTime();
		
		if (newStartTime.after(task.getEnd())) {
			return MESSAGE_INVALID_START_TIME;
		}
		
		task.setStart(newStartTime);
		if (oldType==TASK_TYPE.FLOATING) {
			convertFromFloating(task);
		}
		return String.format(MESSAGE_SUCCESSFUL_EDIT_START, task.getStartString());
	}
	
	private String editEndTime(Task task) {
		TASK_TYPE oldType = task.getType();
		Date newEndTime = parser.getEndTime();
		
		if (task.getType()==TASK_TYPE.EVENT && newEndTime.before(task.getStart())) {
			return MESSAGE_INVALID_START_TIME;
		}
		
		task.setEnd(newEndTime);
		if (oldType==TASK_TYPE.FLOATING) {
			convertFromFloating(task);
		}
		
		return String.format(MESSAGE_SUCCESSFUL_EDIT_END, task.getEndString());
	}
	
	private String editStartAndEndTime(Task task) {
		TASK_TYPE type = task.getType();
		Date newStartTime = parser.getStartTime();
		Date newEndTime = parser.getEndTime();
		
		if (newStartTime.after(newEndTime)) {
			return MESSAGE_INVALID_EVENT_TIME;
		}
		
		task.setStart(newStartTime);
		task.setEnd(newEndTime);
		if (type==TASK_TYPE.FLOATING) {
			convertFromFloating(task);
		}
		
		return String.format(MESSAGE_SUCCESSFUL_EDIT_EVENT, task.getStartString(), task.getEndString());
	}
	
	private void convertFromFloating(Task task) {
		this.floatingTasks.remove(task);	
		if (task.getStatus()==TASK_STATUS.OVERDUE) {
			this.overdueTasks.add(task);
			this.UIStatus = UI_TAB.OVERDUE;
		}
		else if (task.getStatus()==TASK_STATUS.ONGOING) {
			this.ongoingTasks.add(task);
			this.UIStatus = UI_TAB.ONGOING;
		}
	}
}
