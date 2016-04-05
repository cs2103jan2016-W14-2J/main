//@@author: Hao Jie
package Parser;

import java.util.ArrayList;
import Command.*;

public class FlagAndCompleteParser {
	private FLAGANDCOMPLETE_TYPE _type;
	private ArrayList<Integer> taskIndex; 
	
	
	public FlagAndCompleteParser() {
		this.taskIndex = new ArrayList<Integer>();
	}
	
	protected CommandUtils executeFlagCompleteParser(CommandUtils commandUtil, String userTaskIndex) {
	
		String[] str = userTaskIndex.trim().replaceAll("[:.,]", " ").toLowerCase().split("\\s+");
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
		
		if (userTaskIndex.contains("-")) {
			userTaskIndex = userTaskIndex.replace("-", " ");
		}
		else if (userTaskIndex.contains(" to ")) {
			userTaskIndex = userTaskIndex.replace(" to ", " ");
		}
		
		String[] temp = userTaskIndex.split("\\s+");
		
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

	private CommandUtils detemineDeleteType(CommandUtils commandUtil, String usertaskIndex) {

		if (checkIfSingle(usertaskIndex)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.SINGLE);
		}
		else if (checkIfRange(usertaskIndex)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.RANGE);
		}
		else if (checkIfMultiple(usertaskIndex)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.MULTIPLE);
		}
		else if (checkIfAll(usertaskIndex)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.ALL);
		}
		else {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.INVALID);
		}
		return commandUtil;
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
}
