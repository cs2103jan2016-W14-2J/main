//@@author: Hao Jie
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
	private String STRING_HASH_TAG = "#";
	private String STRING_CHECKER_TAG = "tag";
	private String STRING_CHECKER_CATEGORY = "category";
	private String STRING_START_DATE = "start date";
	private String STRING_END_DATE = "end date";
	private String STRING_START_INDICATOR = "start";
	private String STRING_END_INDICATOR = "end";
	
	public DeleteParser(String userTask) {
		this.userTask = userTask;
		this.tagToDelete = new ArrayList<String>();
		this.indexToDelete = new ArrayList<Integer>();
		executeDeleteParser();
	}

	private void executeDeleteParser() {
		boolean isInteger = false;
		String[] str = userTask.replaceAll("[:.,]", "").split("\\s+");
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
			case START_DATE:
				parseDeleteStartDate(str);
				break;
			case END_DATE:
				parseDeleteEndDate(str);
				break;
			default:
				setDeleteType(DELETE_TYPE.INVALID);
				break;
		}
	}

	private boolean isTaskIndex(String[] str) {
		return (str[0].startsWith(STRING_HASH_TAG)) ? false : true;
	}

	private void parseDeleteEndDate(String[] str) {
		if (!str[0].contains(STRING_END_INDICATOR)) {
			indexToDelete.add(Integer.parseInt(str[0]));
		}
		else {
			setDeleteType(DELETE_TYPE.INVALID);
		}
	}

	private void parseDeleteStartDate(String[] str) {
		if (!str[0].contains(STRING_START_INDICATOR)) {
			indexToDelete.add(Integer.parseInt(str[0]));
		}
		else {
			setDeleteType(DELETE_TYPE.INVALID);
		}
	}

	private void parseRangeDelete(String[] str) {
		// Example: delete 1 to 5 / delete 1 - 5
		if (str.length == 3) {
			for (int i = Integer.parseInt(str[0]); i < Integer.parseInt(str[2]) + 1; i++) {
				indexToDelete.add(i);
			}
		}
	}

	private void parseMultipleDeleteIndexes(String[] str) {
		// Example: delete 1 4 9 14
		for (int i = 0; i < str.length; i++) {
			indexToDelete.add(Integer.parseInt(str[i]));
		}
	}
	
	private void parseMutipleDeleteTags(String[] str) {
		// Example: delete #nus #soc #singapore
		for (int i = 0; i < str.length; i++) {
			tagToDelete.add(str[i].substring(1, str[i].length()));
		}
	}

	
	private void parseSingleDeleteTag(String[] str) {
		// Example: delete #nus
		tagToDelete.add(str[0].substring(1, str[0].length()));
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
		else if (checkIfDeleteRange(userTask) && isInteger == true) {
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
		else if(checkIfDeleteStartDate(userTask)) {
			setDeleteType(DELETE_TYPE.START_DATE);
			return DELETE_TYPE.START_DATE;
		}
		else if(checkIfDeleteEndDate(userTask)) {
			setDeleteType(DELETE_TYPE.END_DATE);
			return DELETE_TYPE.END_DATE;
		}
		else {
			setDeleteType(DELETE_TYPE.INVALID);
			return DELETE_TYPE.INVALID;
		}
	}

	private boolean checkIfDeleteEndDate(String userTask) {
		return (userTask.contains(STRING_END_DATE)) ? true : false;
	}

	private boolean checkIfDeleteStartDate(String userTask) {
		return (userTask.contains(STRING_START_DATE)) ? true : false;
	}

	private boolean checkIfDeleteAllTag(String userTask) {
		return (userTask.contains(STRING_CHECKER_ALL) && (userTask.contains(STRING_CHECKER_TAG) 
				|| userTask.contains(STRING_CHECKER_CATEGORY))) ? true : false;
	}

	private boolean checkIfDeleteAll(String userTask) {
		return (userTask.contains(STRING_CHECKER_ALL) && !(userTask.contains(STRING_CHECKER_TAG) 
				|| userTask.contains(STRING_CHECKER_CATEGORY))) ? true : false;
	}

	private boolean checkIfDeleteRange(String userTask) {
		return (userTask.contains(STRING_CHECKER_HYPHEN) || (userTask.contains(STRING_CHECKER_TO))) 
				? true : false;
	}

	private boolean checkIfDeleteMultiple(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");	
		return (str.length > 1 && !checkIfDeleteRange(userTask) && 
				!checkIfDeleteEndDate(userTask) &&
				!checkIfDeleteStartDate(userTask)) ? true : false;
	}

	private boolean checkIfDeleteSingleIndex(String userTask) {
		return (userTask.length() == 1 && !userTask.contains(STRING_CHECKER_ALL)) ? true : false;
	}
	
	private boolean checkIfDeleteSingleTag(String userTask) {
		String[] temp = userTask.split(" ");
		return (temp.length == 1 && temp[0].startsWith(STRING_HASH_TAG)) ? true : false;
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
