package Command;

/* @@author: Lu Yang */

import java.util.ArrayList;
import java.util.TreeMap;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import GUI.UI_TAB;
import Parser.*;
import Task.*;

public class Complete extends Command {
	private static final String MESSAGE_INVALID_COMPLETE = "Please enter a valid command.";
	private static final String MESSAGE_SUCCESSFUL_COMPLETE = 
			"Congratulation! Task(s) at \"%1$s\" is/are successfully completed. ";
	private static final String MESSAGE_UNSUCCESSFUL_COMPLETE = 
			"Task(s) at \"%1$s\" failed to complete due to its/their invalid index or already completed status. ";

	public Complete(CommandUtils cu, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		super(cu, floatingTasks, ongoingTasks, completedTasks, overdueTasks, results, categories);
	}

	@Override
	public String execute() {
		ArrayList<Integer> indexes= cu.getTaskToFlagAndMark();
		FLAGANDCOMPLETE_TYPE type = cu.getFlagAndCompleteType();
		switch (type) {
		case SINGLE:
		case RANGE:
		case MULTIPLE:
			return complete(indexes);
		case ALL:
			return completeAll();
		default:
			return MESSAGE_INVALID_COMPLETE;
		}
	}

	private String completeAll() {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for (int i=0; i<=tasks.size(); i++) {
			indexes.add(i);
		}
		return complete(indexes);
	}

	private String complete(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		ArrayList<Integer> unsuccessfulComplete = new ArrayList<Integer>();
		ArrayList<Integer> successfulComplete = new ArrayList<Integer>();
		
		int index;
		for (int i=indexes.size()-1; i>=0; i--) {
			index = indexes.get(i)-INDEX_ADJUSTMENT;
			try {
				Task task = tasks.get(index);
				if (task.getStatus()==TASK_STATUS.COMPLETED) {
					unsuccessfulComplete.add(index + INDEX_ADJUSTMENT);
				}
				else {
					tasks.remove(task);
					if (this.UIStatus==UI_TAB.ALL) {
						this.deleteTaskFromOtherTab(task);
					}
					this.completedTasks.add(task);
					task.setComplete();
					successfulComplete.add(index + INDEX_ADJUSTMENT);
				}
				this.lastModifiedIndex = index;
			} catch (IndexOutOfBoundsException e) {
				unsuccessfulComplete.add(index + INDEX_ADJUSTMENT);
			}
		}
		this.UIStatus = UI_TAB.COMPLETED;
		Collections.sort(successfulComplete);
		Collections.sort(unsuccessfulComplete);
		
		if (unsuccessfulComplete.size()==0) {
			return String.format(MESSAGE_SUCCESSFUL_COMPLETE, successfulComplete);
		}
		if (successfulComplete.size()==0) {
			return String.format(MESSAGE_UNSUCCESSFUL_COMPLETE, unsuccessfulComplete);
		}
		return String.format(MESSAGE_SUCCESSFUL_COMPLETE, successfulComplete) + "\n " + 
				String.format(MESSAGE_UNSUCCESSFUL_COMPLETE, unsuccessfulComplete);
	}
}
