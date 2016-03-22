package Parser;

import java.util.ArrayList;
import Command.*;

public class DeleteParser {
	private String userTask;
	private DELETE_TYPE deleteType;
	private ArrayList<String> tagToDelete; 
	private ArrayList<Integer> indexToDelete;
	
	private String STRING_CHECKER_ALL = "all";
	private String STRING_CHECKER_TO = "to";
	private String STRING_CHECKER_HYPHEN = "-";
	private String STRING_CHECKER_OPEN_PARENTHESES = "<";
	private String STRING_CHECKER_CLOSE_PARENTHESES = ">";
	private String STRING_CHECKER_TAG = "tag";
	private String STRING_CHECKER_CATEGORY = "category";
	
	public DeleteParser(String userTask) {
		this.userTask = userTask;
		this.tagToDelete = new ArrayList<String>();
		this.indexToDelete = new ArrayList<Integer>();
		executeDeleteParser();
	}

	private void executeDeleteParser() {
		boolean isInteger = false;
		String[] str = userTask.replaceAll("[:.,]", "").toLowerCase().split("\\s+");
		isInteger = isTaskIndex(str);
		
		switch (detemineDeleteType(userTask.toLowerCase(), isInteger)) {
		
			case SINGLE_INDEX:
				parseSingleDeleteIndex(str);
				break;
			case SINGLE_TAG:
				parseSingleDeleteTag(str);
				break;
			case MULTIPLE_INDEXES:
				parseMultipleDeleteIndexes(str);
				break;
			case MULTIPLE_TAGS:
				parseMutipleDeleteTags(str);
				break;
			case RANGE_INDEXES:
				parseRangeDelete(str);
				break;
			case ALL_INDEXES:
				break;
			case ALL_TAGS:
				break;
			default:
				break;
		}
	}


	private boolean isTaskIndex(String[] str) {
		if (str[0].startsWith(STRING_CHECKER_OPEN_PARENTHESES)) {
			return false;
		}
		else {
			return true;
		}
	}

	private void parseRangeDelete(String[] str) {
		// Example: user enters "delete 1 to 5"
		if (str.length == 3) {
			for (int i = Integer.parseInt(str[0]); i < Integer.parseInt(str[2]) + 1; i++) {
				indexToDelete.add(i);
			}
		}
	}

	private void parseMultipleDeleteIndexes(String[] str) {
	
		for (int i = 0; i < str.length; i++) {
			indexToDelete.add(Integer.parseInt(str[i]));
		}
	}
	
	private void parseMutipleDeleteTags(String[] str) {
		
		for (int i = 0; i < str.length; i++) {
			tagToDelete.add(str[i].substring(1, str[0].length() - 1));
		}
	}

	
	private void parseSingleDeleteTag(String[] str) {
		tagToDelete.add(str[0].substring(1, str[0].length() - 1));
	}
	
	private void parseSingleDeleteIndex(String[] str) {
		indexToDelete.add(Integer.parseInt(str[0]));
	}

	private DELETE_TYPE detemineDeleteType(String userTask, boolean isInteger) {

		if (checkIfDeleteAll(userTask)) {
			setDeleteType(DELETE_TYPE.ALL_INDEXES);
			return DELETE_TYPE.ALL_INDEXES;
		}
		else if (checkIfDeleteAllTag(userTask)) {
			setDeleteType(DELETE_TYPE.ALL_TAGS);
			return DELETE_TYPE.ALL_TAGS;
		}
		else if (checkIfDeleteSingleIndex(userTask) && isInteger == true){
			setDeleteType(DELETE_TYPE.SINGLE_INDEX);
			return DELETE_TYPE.SINGLE_INDEX;
		}
		else if (checkIfDeleteSingleTag(userTask) && isInteger == false) {
			setDeleteType(DELETE_TYPE.SINGLE_TAG);
			return DELETE_TYPE.SINGLE_TAG;	
		}
		else if (checkIfDeleteRange(userTask)) {
			setDeleteType(DELETE_TYPE.RANGE_INDEXES);
			return DELETE_TYPE.RANGE_INDEXES;
		}
		else if (checkIfDeleteMultiple(userTask) && isInteger == true) {
			setDeleteType(DELETE_TYPE.MULTIPLE_INDEXES);
			return DELETE_TYPE.MULTIPLE_INDEXES;
		}
		else if (checkIfDeleteMultiple(userTask) && isInteger == false) {
			setDeleteType(DELETE_TYPE.MULTIPLE_TAGS);
			return DELETE_TYPE.MULTIPLE_TAGS;
		}
		else {
			return null;
		}
	}

	private boolean checkIfDeleteAllTag(String userTask) {
		if (userTask.contains(STRING_CHECKER_ALL) && (userTask.contains(STRING_CHECKER_TAG) 
			|| userTask.contains(STRING_CHECKER_CATEGORY))) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean checkIfDeleteAll(String userTask) {
		if (userTask.contains(STRING_CHECKER_ALL) && !(userTask.contains(STRING_CHECKER_TAG) 
			|| userTask.contains(STRING_CHECKER_CATEGORY))) {
			
			return true;
		}
		else {
			return false;
		}
	}

	private boolean checkIfDeleteRange(String userTask) {
		if (userTask.contains(STRING_CHECKER_HYPHEN) || (userTask.contains(STRING_CHECKER_TO))) {
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

	private boolean checkIfDeleteSingleIndex(String userTask) {
		
		if (userTask.length() == 1 && !userTask.contains(STRING_CHECKER_ALL)) {
			return true;
		}
		else {
			return false;
		}
	}
	
private boolean checkIfDeleteSingleTag(String userTask) {
		String[] temp = userTask.split(" ");
		
		if (temp.length == 1 && temp[0].startsWith(STRING_CHECKER_OPEN_PARENTHESES) && temp[0].endsWith(STRING_CHECKER_CLOSE_PARENTHESES)) {
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
	
	public ArrayList<String> getTagToDelete() {
		return this.tagToDelete;
	}
	
	public ArrayList<Integer> getIndexToDelete() {
		return this.indexToDelete;
	}
}
