package Command;

import java.util.ArrayList;
import Logic.*;
import Parser.*;
import Command.*;
import Task.*;
import GUI.*;

public class Delete extends Command{
	
	public Delete(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		super(parser, data, command_type);
	}
	
	public String execute() {
		DELETE_TYPE type = parser.getDeleteType();
		ArrayList<Integer> indexes = parser.getTaskToDelete();
		switch (type) {
		case ALL:
			return deleteAll();
		case MULTIPLE:
		case RANGE:
		case SINGLE:
			return delete(indexes);
		default:
			return "Invalid Delete Type.";
		}
	}
	
	public String undo() {
		return "Undone";
	}
	
	private String deleteAll() {
		setTasks(this.UIStatus, new ArrayList<Task>());
		return "All tasks in " + this.UIStatus + "are deleted.";
	}
	
	private String delete(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = getTasks(this.UIStatus);
		if (tasks.size()==0) {
			return this.UIStatus + " is empty. There is nothing to delete.";
		}
		for (int i=0; i<indexes.size(); i++) {
			tasks.remove(indexes.get(i)-1-i);
		}
		setTasks(this.UIStatus, tasks);
		return "Deletion completed.";
	}
	
	private ArrayList<Task> getTasks(TASK_STATUS status) {
		switch (status) {
		case ONGOING:
			return this.ongoingTasks;
		case FLOATING:
			return this.floatingTasks;
		case COMPLETED:
			return this.completedTasks;
		case OVERDUE:
			return this.overdueTasks;
		default:
			return null;
		}
	}
	
	private void setTasks(TASK_STATUS status, ArrayList<Task> list) {
		switch(status) {
		case ONGOING:
			this.ongoingTasks = list;
			break;
		case FLOATING:
			this.floatingTasks = list;
			break;
		case COMPLETED:
			this.completedTasks = list;
			break;
		case OVERDUE:
			this.overdueTasks = list;
			break;
		}
	}
}
