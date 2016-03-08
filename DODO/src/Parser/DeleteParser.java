package Parser;

import java.util.ArrayList;
import Command.*;

public class DeleteParser {
	private String userTask;
	private DELETE_TYPE deleteType;
	private ArrayList<Integer> taskToDelete; 
	
	private static String MESSAGE_WRONG_DELETE_COMMAND = "Oops. Please enter a valid range.";
	
	
	public DeleteParser(String userTask) {
		this.userTask = userTask;
		this.taskToDelete = new ArrayList<Integer>();
		executeDeleteParser();
	}

	private void executeDeleteParser() {
		String[] str = userTask.replaceAll("[:.-]", "").toLowerCase().split("\\s+");
		
		for (int i = 0; i < str.length; i++) {
			System.out.println(str[i]);
		}
		
		switch (detemineDeleteType(userTask.toLowerCase())) {
		
			case SINGLE:
				parseSingleDelete(str);
				break;
			case MULTIPLE:
				parseMultipleDelete(str);
				break;
			case RANGE:
				parseRangeDelete(str);
				break;
			case ALL:
				break;
			default:
				System.out.println(MESSAGE_WRONG_DELETE_COMMAND);
				break;
		}
	}

	private void parseRangeDelete(String[] str) {
		
		// Example: user enters "delete 1 - 5"
		if (str.length == 2) {
			for (int i = Integer.parseInt(str[0]); i < Integer.parseInt(str[1]) + 1; i++) {
				taskToDelete.add(i);
			}
		}
		// Example: user enters "delete 1 to 5"
		else if (str.length == 3) {
			for (int i = Integer.parseInt(str[0]); i < Integer.parseInt(str[2]) + 1; i++) {
				taskToDelete.add(i);
			}
		}
		
		setTaskToDelete(taskToDelete);
	}

	private void parseMultipleDelete(String[] str) {
		for (int i = 0; i < str.length; i++) {
			taskToDelete.add(i);
		}
		setTaskToDelete(taskToDelete);
	}

	private void parseSingleDelete(String[] str) {
		taskToDelete.add(Integer.parseInt(str[0]));
		setTaskToDelete(taskToDelete);
	}

	private DELETE_TYPE detemineDeleteType(String userTask) {
		System.out.print("DEBUG @line 34: " + userTask.length());
		
		if (checkIfDeleteSingle(userTask)) {
			setDeleteType(DELETE_TYPE.SINGLE);
			return DELETE_TYPE.SINGLE;
		}
		else if (checkIfDeleteRange(userTask)) {
			System.out.print("DEBUG @line 43: range");
			setDeleteType(DELETE_TYPE.RANGE);
			return DELETE_TYPE.RANGE;
		}
		else if (checkIfDeleteMultiple(userTask)) {
			System.out.print("DEBUG @line 47: multiple");
			setDeleteType(DELETE_TYPE.MULTIPLE);
			return DELETE_TYPE.MULTIPLE;
		}
		else if (checkIfDeleteAll(userTask)) {
			System.out.print("DEBUG @line 51: all");
			setDeleteType(DELETE_TYPE.ALL);
			return DELETE_TYPE.ALL;
		}
		else {
			System.out.print("DEBUG @line 55: wrong input");
			return null;
		}
	}

	private boolean checkIfDeleteAll(String userTask) {
		if (userTask.contains("all")) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean checkIfDeleteRange(String userTask) {
		System.out.print("DEBUG @line 102: checkIfDeleteRange");
		if (userTask.contains("-") || (userTask.contains("to"))) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean checkIfDeleteMultiple(String userTask) {
		
		String[] str = userTask.toLowerCase().split("\\s+");
		
		if (str.length > 1 && !checkIfDeleteRange(userTask)) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean checkIfDeleteSingle(String userTask) {
		
		if (userTask.length() == 1 && !userTask.contains("all")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void setDeleteType(DELETE_TYPE deleteType) {
		this.deleteType = deleteType;
	}
	
	public DELETE_TYPE getDeleteType() {
		return this.deleteType;
	}
	
	private void setTaskToDelete(ArrayList<Integer> taskToDelete) {
		this.taskToDelete = taskToDelete;
	}
	
	public ArrayList<Integer> getTaskToDelete() {
		return this.taskToDelete;
	}
}
