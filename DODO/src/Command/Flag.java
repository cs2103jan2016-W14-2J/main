package Command;

/* @@author: Lu Yang */

import java.util.ArrayList;
import java.util.TreeMap;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import Parser.*;
import Task.Category;
import Task.Task;

public class Flag extends Command {
	private static final String MESSAGE_INVALID_FLAG = "Please enter a valid flag command.";
	private static final String MESSAGE_SUCCESSFUL_FLAG = 
			"Task(s) at \"%1$s\" is/are successfully flagged. ";
	private static final String MESSAGE_SUCCESSFUL_UNFLAG =
			"Task(s) at \"%1$s\" is/are successfully unflagged. ";
	private static final String MESSAGE_UNSUCCESSFUL_FLAG = 
			"Task(s) at \"%1$s\" failed to flag due to its/their invalid index or already flagged/unflagged status. ";
	private static final String MESSAGE_UNSUCCESSFUL_UNFLAG = 
			"Task(s) at \"%1$s\" failed to unflag due to its/their invalid index or already flagged/unflagged status. ";
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
		ArrayList<Integer> successfulUnflag = new ArrayList<Integer>();
		ArrayList<Integer> unsuccessfulUnflag = new ArrayList<Integer>();
		ArrayList<Integer> successfulFlag = new ArrayList<Integer>();
		ArrayList<Integer> unsuccessfulFlag = new ArrayList<Integer>();

		int index = 0;
		for (int i=indexes.size()-1; i>=0; i--) {
			index = indexes.get(i)-1;
			try {
				Task task = tasks.get(index);
				if (toFlag) {
					// user wants to flags
					if (task.getFlag()) {
						unsuccessfulFlag.add(index + INDEX_ADJUSTMENT);
					}
					else {
						successfulFlag.add(index + INDEX_ADJUSTMENT);
					}
				}
				else {
					if (!task.getFlag()) {
						unsuccessfulUnflag.add(index + INDEX_ADJUSTMENT);
					}
					else {
						successfulUnflag.add(index + INDEX_ADJUSTMENT);
					}
				}
				task.setFlag(toFlag);
				this.lastModifiedIndex = index;
			} catch (IndexOutOfBoundsException e) {
				unsuccessfulFlag.add(index + INDEX_ADJUSTMENT);
			}
		}

		Collections.sort(successfulFlag);
		Collections.sort(unsuccessfulFlag);
		Collections.sort(successfulUnflag);
		Collections.sort(unsuccessfulUnflag);

		if (this.toFlag) {
			if (unsuccessfulFlag.size()==0) {
				return String.format(MESSAGE_SUCCESSFUL_FLAG, successfulFlag);
			}
			if (successfulFlag.size()==0) {
				return String.format(MESSAGE_UNSUCCESSFUL_FLAG, unsuccessfulFlag);
			}
			return String.format(MESSAGE_SUCCESSFUL_FLAG, successfulFlag) + "\n " + 
			String.format(MESSAGE_UNSUCCESSFUL_FLAG, unsuccessfulFlag);
		}
		else {
			if (unsuccessfulUnflag.size()==0) {
				return String.format(MESSAGE_SUCCESSFUL_UNFLAG, successfulUnflag);
			}
			if (successfulUnflag.size()==0) {
				return String.format(MESSAGE_UNSUCCESSFUL_UNFLAG, unsuccessfulUnflag);
			}
			return String.format(MESSAGE_SUCCESSFUL_UNFLAG, successfulUnflag) + "\n " + 
			String.format(MESSAGE_UNSUCCESSFUL_UNFLAG, unsuccessfulUnflag);
		}
	}
}
