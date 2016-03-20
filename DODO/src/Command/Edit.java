package Command;

import java.util.*;
import Task.*;
import Parser.*;

public class Edit extends Command {
	public Edit(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		super(parser, data, command_type);
	}

	@Override
	public String execute() {
		int index = parser.getTaskID()-INDEX_ADJUSTMENT;
		EDIT_TYPE edit_type = EDIT_TYPE.TASK_NAME; // parser.getEditType();

		switch (this.UIStatus) {
		case FLOATING:
			return edit(index, this.floatingTasks, edit_type);
		case ONGOING:
			return edit(index, this.ongoingTasks, edit_type);
		case COMPLETED:
			return edit(index, this.completedTasks, edit_type);
		case OVERDUE:
			return edit(index, this.overdueTasks, edit_type);
		default:
			return "Invalid type of task.";
		}
	}

	private String edit(int index, ArrayList<Task> tasks, EDIT_TYPE edit_type) {
		Task task = tasks.get(index);
		switch (edit_type) {
		case TASK_NAME:
			task = editName(index, task);
			return "Task " + index + " has been renamed as " + "\"" + task.getName() + "\"";
		default:
			return "ERROR";
		}
	}
	
	private Task editName(int index, Task task) {
		// Java passes by value instead of reference
		String newName = parser.getName();
		task.setName(newName);
		return task;
	}

	@Override
	public String undo() {
		// TODO Auto-generated method stub
		return null;
	}

}