package Command;

/* @@author: Lu Yang */

import java.util.*;

import GUI.UI_TAB;
import Task.*;
import Parser.*;

public class Edit extends Command {
	private static final String MESSAGE_INTERNAL_ERROR = "[INTERNAL ERROR] edit/edit.";
	
	public Edit(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		super(parser, data, command_type);
	}

	@Override
	public String execute() {
		int index = parser.getTaskID()-INDEX_ADJUSTMENT;
		EDIT_TYPE edit_type = parser.getEditType(); // parser.getEditType();
		return edit(index, this.UIStatus, edit_type);
	}

	private String edit(int index, UI_TAB status, EDIT_TYPE edit_type) {
		try {
			ArrayList<Task> tasks = getTasks(status);
			Task task = tasks.get(index);
			switch (edit_type) {
			case TASK_NAME:
				return editName(task, index);
			case DEADLINED:
				return editDeadlined(task, index);
			case START_TIME:
				return editStartTime(task, index);
			case END_TIME:
				return editEndTime(task, index);
			case EVENT_TIME:
				editStartTime(task, index);
				editEndTime(task, index);
				return "Task " + (index+INDEX_ADJUSTMENT) + " has been changed to from \"" + task.getStart()
						+ "\" to \"" + task.getEnd() + "\".";
			default:
				return MESSAGE_INTERNAL_ERROR;
			}
		} catch (IndexOutOfBoundsException e) {
			return "Please key in a valid index.";
		} catch (NullPointerException e) {
			return MESSAGE_INTERNAL_ERROR;
		} catch (ClassCastException e) {
			return "Please key in a valid edit command.";
		}
	}

	private String editStartTime(Task task, int index) {
		Date newStartTime = parser.getStartTime();
		task.setStart(newStartTime);
		return "Task " + index + " has been changed to " + "\"" + task.getStart() + "\".";
	}
	
	private String editEndTime(Task task, int index) {
		Date newEndTime = parser.getEndTime();
		task.setEnd(newEndTime);
		return "Task " + index + " has been changed to " + "\"" + task.getEnd() + "\".";
	}

	private String editDeadlined(Task task, int index) {
		Date newDeadline = parser.getEndTime();
		task.setEnd(newDeadline);
		return "Task " + index + " has been changed to " + "\"" + task.getEnd() + "\".";
	}

	private String editName(Task task, int index) {
		String newName = parser.getName();
		task.setName(newName);
		return  "Task " + index + " has been renamed as " + "\"" + task.getName() + "\".";
	}

	@Override
	public String undo() {
		// TODO Auto-generated method stub
		return null;
	}

}