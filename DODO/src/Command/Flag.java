package Command;

import java.util.ArrayList;

import Parser.Parser;
import Task.Task;

public class Flag extends Command {
	private boolean toFlag;

	public Flag(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type, boolean toFlag) {
		super(parser, data, command_type);
		this.toFlag = toFlag;
	}

	@Override
	public String execute() {
		ArrayList<Integer> indexes = parser.getTaskToFlagAndMark();
		FLAGANDCOMPLETE_TYPE type = parser.getFlagAndCompleteType();
		switch (type) {
		case SINGLE:
		case RANGE:
		case MULTIPLE:
			return flag(indexes);
		case ALL:
			return flagAll();
		default:
			return "Invalid command.";
		}
	}

	private String flagAll() {
		ArrayList<Task> tasks = getTasks(this.UIStatus);
		ArrayList<Integer> indexes = new ArrayList<Integer>();

		for (int i=1; i<=tasks.size(); i++) {
			indexes.add(i);
		}
		return flag(indexes);
	}

	private String flag(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = getTasks(this.UIStatus);
		String status = "";
		int index = 0;
		for (int i=indexes.size()-1; i>=0; i--) {
			try {
				index = indexes.get(i)-1;
				Task task = tasks.get(index);
				boolean existingFlag = task.getFlag();
				if (toFlag) {
					if (task.getFlag()) {
						status += "Task " + (index + INDEX_ADJUSTMENT) + " is already flagged.\n";
					}
					else {
						task.setFlag(toFlag);
						status += "Task " + (index + INDEX_ADJUSTMENT) + " is flagged.\n";
					}
				}
				else {
					if (!task.getFlag()) {
						status += "Task " + (index + INDEX_ADJUSTMENT) + " is already unflagged.\n";
					}
					else {
						status += "Task " + (index + INDEX_ADJUSTMENT) + " is unflagged.\n";
					}
				}
				task.setFlag(toFlag);
			} catch (IndexOutOfBoundsException e) {
				status += "Task " + (index + INDEX_ADJUSTMENT) + " is absent.\n";
			}
		}
		setTasks(this.UIStatus, tasks);
		return status;
	}

	/*private String flag(int index, ArrayList<Task> tasks) {
		Task task = tasks.get(index);
		if (task.getFlag()) {
			return "Task " + index + " is already flagged.";
		}
		else {
			task.setFlag(true);
			return "Task " + index + " is flagged.";
		}
	}*/

	@Override
	public String undo() {
		// TODO Auto-generated method stub
		return null;
	}

}
