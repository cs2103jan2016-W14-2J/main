package Command;

import java.util.ArrayList;

import Parser.Parser;
import Task.Task;

public class Flag extends Command {

	public Flag(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		super(parser, data, command_type);
	}

	@Override
	public String execute() {
		int index = parser.getTaskID() - INDEX_ADJUSTMENT;
		switch (this.UIStatus) {
		case FLOATING:
			return flag(index, this.floatingTasks);
		case ONGOING:
			return flag(index, this.ongoingTasks);
		case COMPLETED:
			return flag(index, this.completedTasks);
		case OVERDUE:
			return flag(index, this.overdueTasks);
		default:
			return "ERROR";
		}
	}

	private String flag(int index, ArrayList<Task> tasks) {
		Task task = tasks.get(index);
		if (task.getFlag()) {
			return "Task " + index + " is already flagged.";
		}
		else {
			task.setFlag(true);
			return "Task " + index + " is flagged.";
		}
	}

	@Override
	public String undo() {
		// TODO Auto-generated method stub
		return null;
	}

}
