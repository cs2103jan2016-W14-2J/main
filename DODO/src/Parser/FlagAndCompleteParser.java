package Parser;

import java.util.ArrayList;
import Command.*;

public class FlagAndCompleteParser {
	private String usertaskIndex;
	private FLAGANDCOMPLETE_TYPE _type;
	private ArrayList<Integer> taskIndex; 
	
	private static String MESSAGE_WRONG_DELETE_COMMAND = "Oops. Please enter a valid range.";
	
	
	public FlagAndCompleteParser(String usertaskIndex) {
		this.usertaskIndex = usertaskIndex;
		this.taskIndex = new ArrayList<Integer>();
		executeDeleteParser();
	}

	private void executeDeleteParser() {
		String[] str = usertaskIndex.replaceAll("[:.,]", "").toLowerCase().split("\\s+");
		
		for (int i = 0; i < str.length; i++) {
			System.out.println(str[i]);
		}
		
		switch (detemineDeleteType(usertaskIndex.toLowerCase())) {
		
			case SINGLE:
				parseSingle(str);
				break;
			case MULTIPLE:
				System.out.print("DEBUG @line 33: MULTIPLE");
				parseMultiple(str);
				break;
			case RANGE:
				parseRange(str);
				break;
			case ALL:
				break;

			default:
				System.out.println(MESSAGE_WRONG_DELETE_COMMAND);
				break;
		}
	}

	private void parseRange(String[] str) {
		System.out.println("DEBUG :" + str[2]);
		if (str.length == 3) {
			for (int i = Integer.parseInt(str[0]); i < Integer.parseInt(str[2]) + 1; i++) {
				taskIndex.add(i);
			}
		}
		
		setTaskIndex(taskIndex);
	}

	private void parseMultiple(String[] str) {
	
		for (int i = 0; i < str.length; i++) {
			taskIndex.add(Integer.parseInt(str[i]));
		}
		setTaskIndex(taskIndex);
	}

	private void parseSingle(String[] str) {
		taskIndex.add(Integer.parseInt(str[0]));
		setTaskIndex(taskIndex);
	}

	private FLAGANDCOMPLETE_TYPE detemineDeleteType(String usertaskIndex) {
//		System.out.println("DEBUG @line 34: " + usertaskIndex.length());
		
		if (checkIfSingle(usertaskIndex)) {
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.SINGLE);
			return FLAGANDCOMPLETE_TYPE.SINGLE;
		}
		else if (checkIfRange(usertaskIndex)) {
			System.out.print("DEBUG @line 43: range");
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.RANGE);
			return FLAGANDCOMPLETE_TYPE.RANGE;
		}
		else if (checkIfMultiple(usertaskIndex)) {
			System.out.print("DEBUG @line 47: multiple");
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.MULTIPLE);
			return FLAGANDCOMPLETE_TYPE.MULTIPLE;
		}
		else if (checkIfAll(usertaskIndex)) {
			System.out.print("DEBUG @line 51: all");
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.ALL);
			return FLAGANDCOMPLETE_TYPE.ALL;
		}
		
		else {
			System.out.print("DEBUG @line 55: wrong input");
			return null;
		}
	}


	private boolean checkIfAll(String usertaskIndex) {
		if (usertaskIndex.contains("all")) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean checkIfRange(String usertaskIndex) {
		System.out.print("DEBUG @line 102: checkIfDeleteRange");
		if (usertaskIndex.contains("-") || (usertaskIndex.contains("to"))) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean checkIfMultiple(String usertaskIndex) {
		System.out.print("DEBUG @line 139: checkIfDeleteMultiple");
		String[] str = usertaskIndex.toLowerCase().split("\\s+");
		
		if (str.length > 1 && !checkIfRange(usertaskIndex)) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean checkIfSingle(String usertaskIndex) {
		
		if (usertaskIndex.length() == 1 && !usertaskIndex.contains("all")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void setFlagCompleteType(FLAGANDCOMPLETE_TYPE _type) {
		this._type = _type;
	}
	
	public FLAGANDCOMPLETE_TYPE getFlagCompleteType() {
		return this._type;
	}
	
	private void setTaskIndex(ArrayList<Integer> taskIndex) {
		this.taskIndex = taskIndex;
	}
	
	public ArrayList<Integer> getTaskIndex() {
		return this.taskIndex;
	}
}
