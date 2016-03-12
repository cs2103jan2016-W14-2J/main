package Command;

import java.util.ArrayList;
import Logic.*;
import Parser.*;
import Command.*;
import Task.*;
import GUI.*;

public class Delete extends Command{
	
	public Delete(Parser parser, Logic logic, COMMAND_TYPE command_type) {
		super(parser, logic, command_type);
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
	
	private String deleteAll() {
		logic.setFloatingTasks(new ArrayList<Task>());
		return "All deleted";
	}
	
	private String delete(ArrayList<Integer> indexes) {
		TASK_STATUS status = UIRightBox.getCurrentTab();
		ArrayList<Task> tasks = getTasks(status);
		
		System.out.println("[DEBUG] status: " + status);
		System.out.println("[DEBUG] task: " + tasks);
		System.out.println("[DEBUG] indexes: " + indexes);
		
		if (tasks.size()==0) {
			return "Empty task list in " + status;
		}
		
		for (int i=0; i<indexes.size(); i++) {
			System.out.println("[DEBUG/Delete]" + tasks.remove(indexes.get(i)-1-i));
		}
		setTasks(status, tasks);
		return "Deletion completed.";
	}
	
	private ArrayList<Task> getTasks(TASK_STATUS status) {
		switch (status) {
		case ONGOING:
			return logic.getOngoingTasks();
		case FLOATING:
			return logic.getFloatingTasks();
		case COMPLETED:
			return logic.getCompletedTasks();
		case OVERDUE:
			return logic.getOverdueTasks();
		default:
			return null;
		}
	}
	
	private void setTasks(TASK_STATUS status, ArrayList<Task> list) {
		switch(status) {
		case ONGOING:
			logic.setOngoingTasks(list);
			break;
		case FLOATING:
			logic.setFloatingTasks(list);
			break;
		case COMPLETED:
			logic.setCompletedTasks(list);
			break;
		case OVERDUE:
			logic.setOverdueTasks(list);
			break;
		}
	}
	
	public String undo() {
		return "Undone";
	}
}
