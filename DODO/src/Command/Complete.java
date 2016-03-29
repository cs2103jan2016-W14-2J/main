package Command;

/* @@author: Lu Yang */

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
		ArrayList<Task> tasks = getTasks(this.UIStatus);
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for (int i=0; i<tasks.size(); i++) {
			indexes.add(i);
		}
		return complete(indexes);
	}

	private String complete(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = getTasks(this.UIStatus);
		String status = "";
		int index = 0;
		for (int i=indexes.size()-1; i>=0; i--) {
			try {
				index = indexes.get(i)-1;
				Task task = tasks.get(index);
				if (task.getComplete()) {
					status += "Task " + (index + INDEX_ADJUSTMENT) + " has been completed before.\n";
				}
				else {
					task.complete(true);
					tasks.remove(index);
					completedTasks.add(task);
					setTasks(this.UIStatus, tasks);
					status += "Congratulation! Task " + (index+INDEX_ADJUSTMENT) + " is completed.\n";
				}
			} catch (IndexOutOfBoundsException e) {
				status += "Task " + (index+INDEX_ADJUSTMENT) + " is absent.\n";
			}
		}
		return status;
	}

	@Override
	public String undo() {
		// TODO Auto-generated method stub
		return null;
	}



}
