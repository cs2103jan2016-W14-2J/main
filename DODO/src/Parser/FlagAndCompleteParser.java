//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import Command.*;

public class FlagAndCompleteParser {
	private FLAGANDCOMPLETE_TYPE _type;
	private ArrayList<Integer> taskIndex; 
	private String STRING_SPLITTER = "\\s+";
	private String KEYWORD_ALL = "all";
	private String KEYWORD_TO = " to ";
	private String KEYWORD_DASH = " - ";
	private String KEYWORD_SPACE = " "; 
	private String PUNCTUATION_REMOVER = "[:.,]";
	private int SINGLE_INDEX = 1;
	
	public FlagAndCompleteParser() {
		this.taskIndex = new ArrayList<Integer>();
	}
	
	protected CommandUtils executeFlagCompleteParser(CommandUtils commandUtil, String userTaskIndex) {
	
		String[] str = userTaskIndex.trim().replaceAll(PUNCTUATION_REMOVER, KEYWORD_SPACE).toLowerCase().split(STRING_SPLITTER);
		System.out.println("FLAG2: " + userTaskIndex);
		commandUtil = detemineDeleteType(commandUtil, userTaskIndex.toLowerCase());
		_type = commandUtil.getFlagAndCompleteType();
		switch (_type) {
		
			case SINGLE:
				return parseSingle(commandUtil, str);
			case MULTIPLE:
				return parseMultiple(commandUtil, str);
			case RANGE:
				return parseRange(commandUtil, userTaskIndex);
			case ALL:
				break;
			default:
				break;
		}
		return commandUtil;
	}

	private CommandUtils parseRange(CommandUtils commandUtil, String userTaskIndex) {
		
		if (userTaskIndex.contains(KEYWORD_DASH)) {
			userTaskIndex = userTaskIndex.replace(KEYWORD_DASH, KEYWORD_SPACE);
		}
		else if (userTaskIndex.contains(KEYWORD_TO)) {
			userTaskIndex = userTaskIndex.replace(KEYWORD_TO, KEYWORD_SPACE);
		}
		
		String[] temp = userTaskIndex.split(STRING_SPLITTER);
		
		if (temp.length == 2) {
			for (int i = Integer.parseInt(temp[0]); i < Integer.parseInt(temp[1]) + 1; i++) {
				taskIndex.add(i);
			}
		}
		commandUtil.setTaskToFlagAndMark(taskIndex);
		return commandUtil;
	}

	private CommandUtils parseMultiple(CommandUtils commandUtil, String[] str) {
		for (int i = 0; i < str.length; i++) {
			taskIndex.add(Integer.parseInt(str[i]));
		}
		commandUtil.setTaskToFlagAndMark(taskIndex);
		return commandUtil;
	}

	private CommandUtils parseSingle(CommandUtils commandUtil, String[] str) {
		taskIndex.add(Integer.parseInt(str[0]));
		commandUtil.setTaskToFlagAndMark(taskIndex);
		return commandUtil;
	}

	private CommandUtils detemineDeleteType(CommandUtils commandUtil, String userTaskIndex) {
		if (checkIfSingle(userTaskIndex)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.SINGLE);
		}
		else if (checkIfRange(userTaskIndex)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.RANGE);
		}
		else if (checkIfMultiple(userTaskIndex)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.MULTIPLE);
		}
		else if (checkIfAll(userTaskIndex)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.ALL);
		}
		else {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.INVALID);
		}
		return commandUtil;
	}

	private boolean checkIfAll(String userTaskIndex) {
		String[] temp = userTaskIndex.split("\\s+");
		return (userTaskIndex.toLowerCase().contains(KEYWORD_ALL) && temp.length == 1) ? true: false;
	}

	private boolean checkIfRange(String userTaskIndex) {
		return (userTaskIndex.contains(KEYWORD_DASH) || (userTaskIndex.contains(KEYWORD_TO))) ? true : false;
	}

	private boolean checkIfMultiple(String userTaskIndex) {
		String[] str = userTaskIndex.replaceAll(PUNCTUATION_REMOVER, KEYWORD_SPACE).toLowerCase().split(STRING_SPLITTER);
		return (str.length > SINGLE_INDEX && !checkIfRange(userTaskIndex)) ? true : false;
	}

	private boolean checkIfSingle(String userTaskIndex) {
		String[] temp = userTaskIndex.split("\\s+");
		return (temp.length == 1 && !temp[0].toLowerCase().contains(KEYWORD_ALL)) ? true : false;
	}
}
