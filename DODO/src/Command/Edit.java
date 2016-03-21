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
		
		return edit(index, this.UIStatus, edit_type);
	}

	private String edit(int index, TASK_STATUS status, EDIT_TYPE edit_type) {
		ArrayList<Task> tasks = getTasks(status);
		Task task = tasks.get(index);
		switch (edit_type) {
		case TASK_NAME:
			task = editName(index, task);
			setTasks(status, tasks);
			return "Task " + index + " has been renamed as " + "\"" + task.getName() + "\"";
		default:
			return "ERROR";
		}
		
	}
	
	private Task editName(int index, Task task) {
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