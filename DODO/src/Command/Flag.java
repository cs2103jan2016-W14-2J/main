package Command;

/* @@author: Lu Yang */

import java.util.ArrayList;
import java.util.TreeMap;

import Parser.*;
import Task.Category;
import Task.Task;

public class Flag extends Command {
	private static final String MESSAGE_INVALID_FLAG = "Please enter a valid flag command.";
	private boolean toFlag;

	public Flag(CommandUtils cu, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories, boolean toFlag) {
		super(cu, floatingTasks, ongoingTasks, completedTasks, overdueTasks, results, categories);
		this.toFlag = toFlag;
	}

	@Override
	public String execute() {
		ArrayList<Integer> indexes = cu.getTaskToFlagAndMark();
		FLAGANDCOMPLETE_TYPE type = cu.getFlagAndCompleteType();
		switch (type) {
		case SINGLE:
		case RANGE:
		case MULTIPLE:
			return flag(indexes);
		case ALL:
			return flagAll();
		default:
			return MESSAGE_INVALID_FLAG;
		}
	}

	private String flagAll() {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		ArrayList<Integer> indexes = new ArrayList<Integer>();

		for (int i=1; i<=tasks.size(); i++) {
			indexes.add(i);
		}
		return flag(indexes);
	}

	private String flag(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		String status = "";
		int index = 0;
		for (int i=indexes.size()-1; i>=0; i--) {
			try {
				index = indexes.get(i)-1;
				Task task = tasks.get(index);
				if (toFlag) {
					if (task.getFlag()) {
						status += "Task " + (index + INDEX_ADJUSTMENT) + " is already flagged.\n";
					}
					else {
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
				this.lastModifiedIndex = index;
			} catch (IndexOutOfBoundsException e) {
				status += "Task " + (index + INDEX_ADJUSTMENT) + " is absent.\n";
			}
		}
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
}
