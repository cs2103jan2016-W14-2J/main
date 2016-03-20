package Command;

import java.util.ArrayList;

import Parser.Parser;
import Task.Task;

public class Complete extends Command {

	public Complete(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		super(parser, data, command_type);
	}

	@Override
	public String execute() {
		int index = parser.getTaskID()-INDEX_ADJUSTMENT;
		switch (this.UIStatus) {
		case FLOATING:
			return completeTask(index, this.floatingTasks);
		case ONGOING:
			return completeTask(index, this.ongoingTasks);
		case COMPLETED:
			return "Your task have been completed.";
		case OVERDUE:
			return completeTask(index, this.overdueTasks);
		default:
			return "Invalid command.";
		} 
	}

	private String completeTask(int index, ArrayList<Task> tasks) {
		try {
		Task task = tasks.get(index);
		if (task.getComplete()) {
			return "Task " + index + " has been completed before.";
		}
		task.complete(true);
		return "Congratulation! Task " + (index+INDEX_ADJUSTMENT) + " is completed.";
		}
		catch (IndexOutOfBoundsException e) {
			return "Task " + (index+INDEX_ADJUSTMENT) + " is absent.";
		}
	}

	@Override
	public String undo() {
		// TODO Auto-generated method stub
		return null;
	}

}
