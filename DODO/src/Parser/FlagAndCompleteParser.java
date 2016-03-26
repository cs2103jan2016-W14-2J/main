package Parser;

import java.util.ArrayList;
import Command.*;

public class FlagAndCompleteParser {
	private String userTaskIndex;
	private FLAGANDCOMPLETE_TYPE _type;
	private ArrayList<Integer> taskIndex; 
	
	private static String MESSAGE_WRONG_DELETE_COMMAND = "Oops. Please enter a valid range.";
	
	
	public FlagAndCompleteParser(String userTaskIndex) {
		this.userTaskIndex = userTaskIndex;
		this.taskIndex = new ArrayList<Integer>();
		executeDeleteParser(userTaskIndex);
	}

	private void executeDeleteParser(String userTaskIndex) {
		
		String[] str = userTaskIndex.trim().replaceAll("[:.,]", " ").toLowerCase().split("\\s+");
		
		switch (detemineDeleteType(userTaskIndex.toLowerCase())) {
		
			case SINGLE:
				parseSingle(str);
				break;
			case MULTIPLE:
				System.out.print("DEBUG @line 33: MULTIPLE");
				parseMultiple(str);
				break;
			case RANGE:
				parseRange();
				break;
			case ALL:
				break;
			default:
				System.out.println(MESSAGE_WRONG_DELETE_COMMAND);
				break;
		}
	}

	private void parseRange() {
		
		if (userTaskIndex.contains("-")) {
			userTaskIndex = userTaskIndex.replace("-", " ");
		}
		else if (userTaskIndex.contains("to")) {
			userTaskIndex = userTaskIndex.replace("to", " ");
		}
		
		String[] temp = userTaskIndex.split("\\s+");
		
		if (temp.length == 2) {
			for (int i = Integer.parseInt(temp[0]); i < Integer.parseInt(temp[1]) + 1; i++) {
				taskIndex.add(i);
			}
			setTaskIndex(taskIndex);
		}
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

		if (checkIfSingle(usertaskIndex)) {
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.SINGLE);
			return FLAGANDCOMPLETE_TYPE.SINGLE;
		}
		else if (checkIfRange(usertaskIndex)) {
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.RANGE);
			return FLAGANDCOMPLETE_TYPE.RANGE;
		}
		else if (checkIfMultiple(usertaskIndex)) {
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.MULTIPLE);
			return FLAGANDCOMPLETE_TYPE.MULTIPLE;
		}
		else if (checkIfAll(usertaskIndex)) {
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.ALL);
			return FLAGANDCOMPLETE_TYPE.ALL;
		}
		else {
			return null;
		}
	}

	private boolean checkIfAll(String usertaskIndex) {
		return (usertaskIndex.contains("all")) ? true: false;
	}

	private boolean checkIfRange(String usertaskIndex) {
		return (usertaskIndex.contains("-") || (usertaskIndex.contains("to"))) ? true : false;
	}

	private boolean checkIfMultiple(String usertaskIndex) {
		String[] str = usertaskIndex.replaceAll("[,]", " ").toLowerCase().split("\\s+");
		return (str.length > 1 && !checkIfRange(usertaskIndex)) ? true : false;
	}

	private boolean checkIfSingle(String usertaskIndex) {
		return (!checkIfRange(usertaskIndex) && !checkIfMultiple(usertaskIndex)
				&& !usertaskIndex.contains("all")) ? true : false;
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
