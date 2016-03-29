package Command;

/* @@author: Lu Yang */

import java.util.*;
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

	private String edit(int index, TASK_STATUS status, EDIT_TYPE edit_type) {
		try {
			ArrayList<Task> tasks = getTasks(status);
			Task task = tasks.get(index);
			switch (edit_type) {
			case TASK_NAME:
				return editName(task, index);
			case DEADLINED:
				return editDeadlined((DeadlinedTask)task, index);
			case START_TIME:
				return editStartTime((Event) task, index);
			case END_TIME:
				return editEndTime((Event) task, index);
			case EVENT_TIME:
				task = (Event) task;
				editStartTime((Event) task, index);
				editEndTime((Event) task, index);
				return "Task " + (index+INDEX_ADJUSTMENT) + " has been changed to from \"" + ((Event) task).getStartDateTime()
						+ "\" to \"" + ((Event)task).getEndDateTime() + "\".";
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

	private String editStartTime(Event task, int index) {
		Date newStartTime = parser.getStartTime();
		task.setStartDateTime(newStartTime);
		return "Task " + index + " has been changed to " + "\"" + task.getStartDateTime() + "\".";
	}
	
	private String editEndTime(Event task, int index) {
		Date newEndTime = parser.getEndTime();
		task.setEndDateTime(newEndTime);
		return "Task " + index + " has been changed to " + "\"" + task.getEndDateTime() + "\".";
	}

	private String editDeadlined(DeadlinedTask task, int index) {
		Date newDeadline = parser.getEndTime();
		System.out.println("=========EDIT========== newDeadline" + newDeadline);
		task.setEndDateTime(newDeadline);
		return "Task " + index + " has been changed to " + "\"" + task.getEndDateTime() + "\".";
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