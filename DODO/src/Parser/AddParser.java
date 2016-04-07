//@@author: Hao Jie
package Parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;


import Command.*;
import Task.*;

public class AddParser {
	private final String KEYWORD_FROM = "from";
	private final String KEYWORD_TO = "to";
	private final String KEYWORD_ON = " on ";
	private final String KEYWORD_BY = " by ";
	private final String KEYWORD_AT = " at ";
	private final String KEYWORD_BEFORE = " before ";
	private final String KEYWORD_IN = " in ";

	private int LAST_POSITION_OF_FROM = -1;
	private int LAST_POSITION_OF_TO = -1;
	private int LAST_POSITION_OF_ON = -1;
	private int LAST_POSITION_OF_AT = -1;
	private int LAST_POSITION_OF_BEFORE = -1;
	private int LAST_POSITION_OF_BY = -1;
	private int LAST_POSITION_OF_IN = -1;
	
	private String confirmTaskName = "";
	private String taskName = "";
	private String contentToAnalyse;
	private TASK_TYPE taskType;
	private DateTimeParser dt;
	private ArrayList<String> taskItems;
	private ArrayList<String> preposition;
	private ArrayList<String> inputElements;
	private ArrayList<String> str;

	public AddParser() {
		dt = new DateTimeParser();
		preposition = new ArrayList<>(Arrays.asList("on", "at", "by", "before", "in"));
	}
	
	protected CommandUtils executeAddParser(CommandUtils commandUtil, String userTask) {
		String[] str = userTask.split("\\s+");
		taskItems = new ArrayList<String>(Arrays.asList(str));
		taskName = dt.checkForAbbreviation(taskItems);
		taskName = dt.checkAndConvertDateElement(taskItems);
		commandUtil = determineTaskType(commandUtil, taskItems, taskName);
		taskType = commandUtil.getType();
		switch(taskType) {
			case DEADLINED:
				commandUtil = parseDEADLINED(commandUtil, taskName);
				return parseFloating(taskItems, commandUtil.getName(), commandUtil);
			case FLOATING:
				return parseFloating(taskItems, taskName, commandUtil);
			case EVENT:
				commandUtil = parseEVENT(commandUtil, taskName);
				return parseFloating(taskItems, commandUtil.getName(), commandUtil);
			default:
				break;
		}
		return commandUtil;
	}


	public CommandUtils determineTaskType(CommandUtils commandUtil, ArrayList<String> taskItems, 
			String taskName) {
		
		if (checkIfDeadlinedTask(taskItems, taskName)) {
			commandUtil.setTaskType(TASK_TYPE.DEADLINED);
		}
		else if (checkIfEventTask(taskName)) {
			commandUtil.setTaskType(TASK_TYPE.EVENT);
		}
		else if (checkIfFloatingTask(taskItems, taskName)) {
			commandUtil.setTaskType(TASK_TYPE.FLOATING);
		}
		else {
			commandUtil.setTaskType(TASK_TYPE.INVALID);
		}
		return commandUtil;
	}

	private boolean checkIfEventTask(String taskName) {
		LAST_POSITION_OF_FROM = taskName.toLowerCase().lastIndexOf(KEYWORD_FROM);
		LAST_POSITION_OF_TO = taskName.toLowerCase().lastIndexOf(KEYWORD_TO);

		// add study from <startDate> to <endDate>
		if (LAST_POSITION_OF_FROM < LAST_POSITION_OF_TO && LAST_POSITION_OF_FROM != -1) {
			return (dt.checkForDateAndTime(taskName, LAST_POSITION_OF_FROM, LAST_POSITION_OF_TO)) 
					? true : false;
		}
		else if (LAST_POSITION_OF_FROM > LAST_POSITION_OF_TO) {
			return (dt.checkForDateAndTime(taskName, LAST_POSITION_OF_FROM, taskName.length())) 
					? true : false;
		}
		else {
			return false;
		}
	}

	private boolean checkIfDeadlinedTask(ArrayList<String> taskItems, String taskName) {
		getKeywordPosition(taskName);
		return ((LAST_POSITION_OF_AT != -1 || LAST_POSITION_OF_ON != -1 || 
			LAST_POSITION_OF_BEFORE != -1 || LAST_POSITION_OF_BY != -1
			|| LAST_POSITION_OF_IN != -1) && (LAST_POSITION_OF_FROM == -1
			&& LAST_POSITION_OF_TO == -1)) ? true : false;
	}

	private void getKeywordPosition(String taskName) {
		ArrayList<String> temp = new ArrayList<String>(convertArrayListToLowerCase(taskName));
		LAST_POSITION_OF_AT = temp.lastIndexOf("at");
		LAST_POSITION_OF_ON = temp.lastIndexOf("on");
		LAST_POSITION_OF_BEFORE = temp.lastIndexOf("before");
		LAST_POSITION_OF_BY = temp.lastIndexOf("by");
		LAST_POSITION_OF_FROM = temp.lastIndexOf("from");
		LAST_POSITION_OF_TO = temp.lastIndexOf("to");
		LAST_POSITION_OF_IN = temp.lastIndexOf("in");
	}

	private ArrayList<String> convertArrayListToLowerCase(String taskName) {
		String[] str = taskName.toLowerCase().split("\\s+");
		ArrayList<String> temp = new ArrayList<String>(Arrays.asList(str));
		return temp;
	}

	private boolean checkIfFloatingTask(ArrayList<String> taskItems, String taskName) {
		return (!checkIfEventTask(taskName) && !checkIfDeadlinedTask(taskItems, taskName)) ? true : false;
	}
	

	private CommandUtils parseEVENT(CommandUtils commandUtil, String taskName) {
		Date date = new Date();
		int currentPosition = 0;
		String contentOfPreposition = "";
		str = new ArrayList<String>();
		String[] elements = taskName.split("[\\s+]");
		inputElements = new ArrayList<String>(Arrays.asList(elements));
		confirmTaskName = getPossibleTaskName(currentPosition, confirmTaskName, inputElements);
		confirmTaskName = dt.extractDate(confirmTaskName) + " ";
		contentToAnalyse = dt.getDateElements() + " ";
		preposition.add(KEYWORD_FROM);
		preposition.add(KEYWORD_TO);
		str = extractInputWithPreposition(currentPosition, contentOfPreposition, str);
		extractDateFromTask();
		return setEventTime(commandUtil, taskName, date);
	}

	private CommandUtils setEventTime(CommandUtils commandUtil, String taskName, Date date) {
		List<Date> confirmDate = new PrettyTimeParser().parse(contentToAnalyse.replace(KEYWORD_FROM, " "));
		if (confirmDate.size() == 2) {
			commandUtil.setStartTime(confirmDate.get(0));
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(confirmDate.get(1), date));
			commandUtil.setTaskName(confirmTaskName.trim());
		}
		else if (confirmDate.size() == 1) {
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(confirmDate.get(0), date));
			commandUtil.setTaskName(confirmTaskName.trim());
		}
		else {
			commandUtil.setTaskType(TASK_TYPE.FLOATING);
			commandUtil.setTaskType(TASK_TYPE.FLOATING);
		}
		return commandUtil;
	}

	private CommandUtils parseDEADLINED(CommandUtils commandUtil, String taskName) {
		System.out.println("Test new deadline parsing");
		Date date = new Date();
		int currentPosition = 0;
		String contentOfPreposition = "";
		str = new ArrayList<String>();
		String[] elements = taskName.split("[\\s+]");
		inputElements = new ArrayList<String>(Arrays.asList(elements));
		
		confirmTaskName = getPossibleTaskName(currentPosition, confirmTaskName, inputElements);
		confirmTaskName = dt.extractDate(confirmTaskName) + " ";
		contentToAnalyse = dt.getDateElements() + " ";
		str = extractInputWithPreposition(currentPosition, contentOfPreposition, str);
		extractDateFromTask();
		return setDeadLine(commandUtil, confirmTaskName, date);
	}

	private CommandUtils setDeadLine(CommandUtils commandUtil, String confirmTaskName, Date date) {
		System.out.println("setDeadLine : " + confirmTaskName);
		List<Date> confirmDate = new PrettyTimeParser().parse(contentToAnalyse.replace(KEYWORD_BY, " "));
		if (confirmDate.size() == 1) {
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(confirmDate.get(0), date));
		}
		else if (confirmDate.size() == 2) {
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(confirmDate.get(1), date));
		}
		commandUtil.setTaskName(confirmTaskName.trim());
		if (confirmTaskName.trim().equals(taskName)) {
			commandUtil.setTaskType(TASK_TYPE.FLOATING);
		}
		return commandUtil;
	}

	private void extractDateFromTask() {
		for (int i = 0; i < str.size(); i++) {
			List<Date> dates = new PrettyTimeParser().parse(str.get(i));
			if (dates.size() != 0 && dt.hasDateAndTimeElements(str.get(i)) == true) {
		//	if (dates.size() != 0) {
				System.out.println("Test str size : " + str.get(i));
				contentToAnalyse += str.get(i) + " ";
			}
			else {
				System.out.println("Test str size fail : " + str.get(i));
				confirmTaskName += str.get(i) + " ";
			}
		}
	}

	private ArrayList<String> extractInputWithPreposition(int currentPosition, String contentOfPreposition,
			ArrayList<String> str) {
		for (int i = 0; i < inputElements.size(); ) {
			contentOfPreposition += inputElements.get(currentPosition) + " ";
			inputElements.remove(currentPosition);
			while (inputElements.size() != 0) {
				if (!preposition.contains(inputElements.get(currentPosition))) {
					contentOfPreposition += inputElements.get(currentPosition) + " ";
					inputElements.remove(currentPosition);
				}
				else {
					break;
				}
			}
			str.add(contentOfPreposition.trim());
			contentOfPreposition = "";
		}
		return str;
	}

	private String getPossibleTaskName(int currentPosition, String confirmTaskName, ArrayList<String> inputElements) {
		for (int i = 0; i < inputElements.size(); i++) {
			if (!preposition.contains(inputElements.get(currentPosition))) {
				confirmTaskName += inputElements.get(currentPosition) + " "; 
				inputElements.remove(currentPosition);
			}
			else {
				break;
			}
		}
		return confirmTaskName;
	}


	private CommandUtils parseFloating(ArrayList<String> taskItems, String taskName, CommandUtils commandUtil) {
		boolean hasTimeDateElement = dt.hasDateAndTimeElements(taskName);
		Date date = new Date();
		List<Date> dates = new PrettyTimeParser().parse(taskName);
		System.out.println("TEST parseFloating 123:" + dt.getDateElements());
		
		if (dates.size() == 0 && dt.getDateElements() == null) {
			commandUtil.setTaskName(taskName);
		}
		else {
			if (dates.size() == 1 && hasTimeDateElement == true) {
				commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
				commandUtil = finalVerification(taskName, commandUtil);
			}
			else if (dates.size() > 1 && hasTimeDateElement == true) {
				commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(1), date));
				commandUtil.setStartTime(dates.get(0));
				commandUtil = finalVerification(taskName, commandUtil);
			}
			else if (hasTimeDateElement == false) {
				commandUtil.setTaskName(taskName);
			}
		}
		return commandUtil;
	
	}
	
	private CommandUtils finalVerification(String userTask, CommandUtils commandUtil) {
		String temp = "";
		temp = dt.extractDate(userTask);
		commandUtil.setTaskName(extractLastPreposition(temp));
		if (temp.trim().equals(userTask.trim())) {
			commandUtil.setTaskType(TASK_TYPE.FLOATING);
		}
		else {
			commandUtil.setTaskType(TASK_TYPE.DEADLINED);
		}
		return commandUtil;
	}
	
	private String extractLastPreposition(String temp) {
		String[] str = temp.split("[\\s+]");
		String newStr = "";
		int lastWordPosition = str.length - 1;
		
		if (str[lastWordPosition].toLowerCase().contains(KEYWORD_AT) || 
			str[lastWordPosition].toLowerCase().contains(KEYWORD_BY) ||
			str[lastWordPosition].toLowerCase().contains(KEYWORD_ON)) {
			str[lastWordPosition] = "";
		}
		for (int i = 0; i < str.length; i++) {
			newStr += str[i] + " ";
		}
		return newStr.trim();
	}
	
}