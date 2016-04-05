//@@author: Hao Jie
package Parser;

import java.util.ArrayList;
import Command.*;

public class DeleteParser {
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
	
	public DeleteParser() {
		this.tagToDelete = new ArrayList<String>();
		this.indexToDelete = new ArrayList<Integer>();
	}

	protected CommandUtils executeDeleteParser(CommandUtils commandUtil, String userTask) {
		boolean isInteger = false;
		String[] str = userTask.replaceAll("[:.,]", "").split("\\s+");
		isInteger = isTaskIndex(str);
		commandUtil = detemineDeleteType(commandUtil, userTask.toLowerCase(), isInteger);
		deleteType = commandUtil.getDeleteType();
		
		switch (deleteType) {
		
			case SINGLE_INDEX:
				return parseSingleDeleteIndex(commandUtil,str);
			case SINGLE_TAG:
				return parseSingleDeleteTag(commandUtil,str);
			case MULTIPLE_INDEXES:
				return parseMultipleDeleteIndexes(commandUtil,str);
			case MULTIPLE_TAGS:
				return parseMutipleDeleteTags(commandUtil,str);
			case RANGE_INDEXES:
				return parseRangeDelete(commandUtil, str);
			case ALL_INDEXES:
				break;
			case ALL_TAGS:
				break;
			case START_DATE:
				return parseDeleteStartDate(commandUtil, str);
			case END_DATE:
				return parseDeleteEndDate(commandUtil, str);
			default:
				break;
		}
		return commandUtil;
	}

	private boolean isTaskIndex(String[] str) {
		return (str[0].startsWith(STRING_HASH_TAG)) ? false : true;
	}

	private CommandUtils parseDeleteEndDate(CommandUtils commandUtil, String[] str) {
		if (!str[0].contains(STRING_END_INDICATOR)) {
			indexToDelete.add(Integer.parseInt(str[0]));
			commandUtil.setIndexToDelete(indexToDelete);
		}
		else {
			commandUtil.setDeleteType(DELETE_TYPE.INVALID);
		}
		return commandUtil;
	}

	private CommandUtils parseDeleteStartDate(CommandUtils commandUtil, String[] str) {
		if (!str[0].contains(STRING_START_INDICATOR)) {
			indexToDelete.add(Integer.parseInt(str[0]));
			commandUtil.setIndexToDelete(indexToDelete);
		}
		else {
			commandUtil.setDeleteType(DELETE_TYPE.INVALID);
		}
		return commandUtil;
	}

	private CommandUtils parseRangeDelete(CommandUtils commandUtil, String[] str) {
		// Example: delete 1 to 5 / delete 1 - 5
		if (str.length == 3) {
			for (int i = Integer.parseInt(str[0]); i < Integer.parseInt(str[2]) + 1; i++) {
				indexToDelete.add(i);
			}
		}
		commandUtil.setIndexToDelete(indexToDelete);
		return commandUtil;
	}

	private CommandUtils parseMultipleDeleteIndexes(CommandUtils commandUtil, String[] str) {
		// Example: delete 1 4 9 14
		for (int i = 0; i < str.length; i++) {
			indexToDelete.add(Integer.parseInt(str[i]));
		}
		commandUtil.setIndexToDelete(indexToDelete);
		return commandUtil;
	}
	
	private CommandUtils parseMutipleDeleteTags(CommandUtils commandUtil, String[] str) {
		// Example: delete #nus #soc #singapore
		for (int i = 0; i < str.length; i++) {
			tagToDelete.add(str[i].substring(1, str[i].length()));
		}
		commandUtil.setTagToDelete(tagToDelete);
		return commandUtil;
	}

	
	private CommandUtils parseSingleDeleteTag(CommandUtils commandUtil, String[] str) {
		// Example: delete #nus
		tagToDelete.add(str[0].substring(1, str[0].length()));
		commandUtil.setTagToDelete(tagToDelete);
		return commandUtil;
	}
	
	private CommandUtils parseSingleDeleteIndex(CommandUtils commandUtil, String[] str) {
		indexToDelete.add(Integer.parseInt(str[0]));
		commandUtil.setIndexToDelete(indexToDelete);
		return commandUtil;
	}

	private CommandUtils detemineDeleteType(CommandUtils commandUtil, String userTask, boolean isInteger) {

		if (checkIfDeleteAll(userTask)) {
			commandUtil.setDeleteType(DELETE_TYPE.ALL_INDEXES);
		}
		else if (checkIfDeleteAllTag(userTask)) {
			commandUtil.setDeleteType(DELETE_TYPE.ALL_TAGS);
		}
		else if (checkIfDeleteSingleIndex(userTask) && isInteger == true){
			commandUtil.setDeleteType(DELETE_TYPE.SINGLE_INDEX);
		}
		else if (checkIfDeleteSingleTag(userTask) && isInteger == false) {
			commandUtil.setDeleteType(DELETE_TYPE.SINGLE_TAG);	
		}
		else if (checkIfDeleteRange(userTask) && isInteger == true) {
			commandUtil.setDeleteType(DELETE_TYPE.RANGE_INDEXES);
		}
		else if (checkIfDeleteMultiple(userTask) && isInteger == true) {
			commandUtil.setDeleteType(DELETE_TYPE.MULTIPLE_INDEXES);
		}
		else if (checkIfDeleteMultiple(userTask) && isInteger == false) {
			commandUtil.setDeleteType(DELETE_TYPE.MULTIPLE_TAGS);
		}
		else if(checkIfDeleteStartDate(userTask)) {
			commandUtil.setDeleteType(DELETE_TYPE.START_DATE);
		}
		else if(checkIfDeleteEndDate(userTask)) {
			commandUtil.setDeleteType(DELETE_TYPE.END_DATE);
		}
		else {
			commandUtil.setDeleteType(DELETE_TYPE.INVALID);
		}
		return commandUtil;
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

}
