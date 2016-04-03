package Command;

/* @@author: Lu Yang */

import java.util.ArrayList;
import java.util.TreeMap;

import Parser.*;
import Task.*;

public class Complete extends Command {

	public Complete(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		ArrayList<Integer> indexes= parser.getTaskToFlagAndMark();
		FLAGANDCOMPLETE_TYPE type = parser.getFlagAndCompleteType();
		switch (type) {
		case SINGLE:
		case RANGE:
		case MULTIPLE:
			return complete(indexes);
		case ALL:
			return completeAll();
		default:
			return "Invalid command.";
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
		String status = "";
		int index = 0;
		for (int i=indexes.size()-1; i>=0; i--) {
			try {
				index = indexes.get(i)-1;
				Task task = tasks.get(index);
				if (task.getStatus()==TASK_STATUS.COMPLETED) {
					status += "Task " + (index + INDEX_ADJUSTMENT) + " has been completed before.\n";
				}
				else {
					task.setComplete();;
					tasks.remove(index);
					this.completedTasks.add(task);
					this.modify(this.UIStatus, tasks);
					status += "Congratulation! Task " + (index+INDEX_ADJUSTMENT) + " is completed.\n";
				}
			} catch (IndexOutOfBoundsException e) {
				status += "Task " + (index+INDEX_ADJUSTMENT) + " is absent.\n";
			}
		}
		return status;
	}
}
