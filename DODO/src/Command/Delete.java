package Command;

import java.util.ArrayList;
import Logic.*;
import Parser.*;
import Command.*;
import Task.*;

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
		//STUB
		logic.setFloatingTasks(new ArrayList<Task>());
		return "All deleted";
	}
	
	private String delete(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = logic.getFloatingTasks();
		System.out.println("[DEBUG] indexes: " + indexes);
		for (int i=0; i<indexes.size(); i++) {
			System.out.println("[DEBUG/Delete]" + tasks.remove(indexes.get(i)-1));
		}
		logic.setFloatingTasks(tasks);
		return "Deletion completed.";
	}
	
	public String undo() {
		return "Undone";
	}
}
