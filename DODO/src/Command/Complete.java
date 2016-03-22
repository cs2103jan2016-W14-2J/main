package Command;

import java.util.ArrayList;

import Parser.*;
import Task.*;

public class Complete extends Command {

	public Complete(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		super(parser, data, command_type
		
		);
	}

	@Override
	public String execute() {
		int index = parser.getTaskID();
		switch (this.UIStatus) {
		case FLOATING:
			return complete(this.UIStatus, index);
		case ONGOING:
			return complete(this.UIStatus, index);
		case COMPLETED:
			return "Your task have been completed.";
		case OVERDUE:
			return complete(this.UIStatus, index);
		default:
			return "Invalid command.";
		}
	}

	private String complete(TASK_STATUS status, int index) {
		System.out.println(status + ", " + index);
		ArrayList<Task> tasks = getTasks(status);
		try {
			Task task = tasks.get(index);
			if (task.getComplete()) {
				return "Task " + index + " has been completed before.";
			}
			task.complete(true);
			tasks.remove(index);
			completedTasks.add(task);
			setTasks(status, tasks);
			return "Congratulation! Task " + (index+INDEX_ADJUSTMENT) + " is completed.";
		} catch (IndexOutOfBoundsException e) {
			return "Task " + (index+INDEX_ADJUSTMENT) + " is absent.";
		}
	}

	@Override
	public String undo() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
