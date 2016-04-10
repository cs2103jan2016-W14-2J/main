//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.shade.org.apache.commons.lang.time.DateUtils;

import Task.*;

public class AddParser {
	
	// Prepositions
	private final String KEYWORD_FROM = "from";
	private final String KEYWORD_TO = "to";
	private final String KEYWORD_ON = "on";
	private final String KEYWORD_BY = " by ";
	private final String KEYWORD_BY_1 = "by";
	private final String KEYWORD_AT = "at";
	private final String KEYWORD_BEFORE = "before";
	private final String KEYWORD_IN = "in";
	private final String KEYWORD_DUE = "due";
	
	// Positions of prepositions in a string
	private int LAST_POSITION_OF_FROM = -1;
	private int LAST_POSITION_OF_TO = -1;
	private int LAST_POSITION_OF_ON = -1;
	private int LAST_POSITION_OF_AT = -1;
	private int LAST_POSITION_OF_BEFORE = -1;
	private int LAST_POSITION_OF_BY = -1;
	private int LAST_POSITION_OF_IN = -1;
	
	// Constants
	private String STRING_SPLITTER = "\\s+";
	private String STRING_MIDNIGHT_TIME = "0000hrs";
	private String STRING_SPACING = " ";
	private String STRING_EMPTY = "";
	
	private int currentPosition = 0;
	private String contentOfPreposition = "";
	private String confirmTaskName = "";
	private String taskName = "";
	private String contentToAnalyse;
	private boolean hasTimeDateElement;
	private TASK_TYPE taskType;
	private Date date;
	private DateTimeParser dt;
	
	private ArrayList<String> taskItems;
	private ArrayList<String> preposition;
	private ArrayList<String> inputElements;
	private ArrayList<String> str;
	
	public AddParser() {
		
		date = new Date();
		dt = new DateTimeParser();
		str = new ArrayList<String>();
		preposition = new ArrayList<>(Arrays.asList("on", "at", "by", "before", "in"));
	
	}
	
	/*
	 * This method analyses the task type of an input and extract the
	 * information of the input.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils} with attributes of the add command set to 
	 * CommandUtils object.
	 * 
	 */
	
	protected CommandUtils executeAddParser(CommandUtils commandUtil, String userInput) {
		
		assert (userInput != null);
		
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		taskName = dt.checkForAbbreviation(taskItems);
		taskName = dt.checkAndConvertDateElement(taskItems);
		
		commandUtil = determineTaskType(commandUtil, taskItems, taskName);
		taskType = commandUtil.getType();
		
		return analyseTaskType(commandUtil);
	}

	/*
	 * This method extracts the information of the input according to its 
	 * task type.
	 * 
	 * @param commandUtil {@code CommandUtils}
	 * 
	 * @return {@code CommandUtils} with attributes of the add command set to 
	 * CommandUtils object.
	 * 
	 */
	
	private CommandUtils analyseTaskType(CommandUtils commandUtil) {
		
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

	/*
	 * This method analyses and determines the task type of a user input.
	 * 
	 * @param commandUtil {@code CommandUtils}, taskItems {@code ArrayList<String>}
	 * 		  and taskName {@code String}
	 * 
	 * @return {@code CommandUtils} with the task type set to CommandUtils object.
	 * 
	 */
	
	public CommandUtils determineTaskType(CommandUtils commandUtil, ArrayList<String> taskItems, String taskName) {
		
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
	
	/*
	 * This method analyses and verifies if a user input is an event task.
	 * 
	 * @param taskName {@code String}
	 * 
	 * @return {@code boolean} 
	 * 
	 */
	private boolean checkIfEventTask(String taskName) {
		
		LAST_POSITION_OF_FROM = taskName.toLowerCase().lastIndexOf(KEYWORD_FROM);
		LAST_POSITION_OF_TO = taskName.toLowerCase().lastIndexOf(KEYWORD_TO);

		// Example: study from <startDate> to <endDate>
		if (LAST_POSITION_OF_FROM < LAST_POSITION_OF_TO && LAST_POSITION_OF_FROM != -1) {
			return (dt.checkForDateAndTime(taskName, LAST_POSITION_OF_FROM, LAST_POSITION_OF_TO)) 
					? true : false;
		}
		// Example: play DOTA from 7pm
		else if (LAST_POSITION_OF_FROM > LAST_POSITION_OF_TO) {
			return (dt.checkForDateAndTime(taskName, LAST_POSITION_OF_FROM, taskName.length())) 
					? true : false;
		}
		else {
			return false;
		}
	}
	
	/*
	 * This method analyses and verifies if a user input is a floating task.
	 * 
	 * @param taskItems {@code ArrayList<String>} and taskName {@code String}
	 * 
	 * @return {@code boolean} 
	 * 
	 */
	private boolean checkIfFloatingTask(ArrayList<String> taskItems, String taskName) {
		
		return (!checkIfEventTask(taskName) && !checkIfDeadlinedTask(taskItems, taskName)) ? true : false;
	
	}
	
	/*
	 * This method analyses and verifies if a user input is a deadlined task.
	 * 
	 * @param taskItems {@code ArrayList<String>} and taskName {@code String}
	 * 
	 * @return {@code boolean} 
	 * 
	 */
	
	private boolean checkIfDeadlinedTask(ArrayList<String> taskItems, String taskName) {
		
		getKeywordPosition(taskName);
		
		return ((LAST_POSITION_OF_AT != -1 || LAST_POSITION_OF_ON != -1 || 
				 LAST_POSITION_OF_BEFORE != -1 || LAST_POSITION_OF_BY != -1 || 
				 LAST_POSITION_OF_IN != -1) && (LAST_POSITION_OF_FROM == -1 && 
				 LAST_POSITION_OF_TO == -1)) ? true : false;
	}
	
	/*
	 * This method extracts the start time, end time and task name from an
	 * event task.
	 * 
	 * @param commandUtil {@code CommandUtils} and taskName {@code String}
	 * 
	 * @return {@code CommandUtils} 
	 * 
	 */
	
	private CommandUtils parseEVENT(CommandUtils commandUtil, String taskName) {
	
		// Add prepositions for determining an event task to ArrayList<String> preposition.
		String[] elements = taskName.split(STRING_SPLITTER);
		preposition.add(KEYWORD_FROM);
		preposition.add(KEYWORD_TO);
		
		// Store string split taskName into ArrayList<String>
		inputElements = new ArrayList<String>(Arrays.asList(elements));
		
		// Separate task name from date elements.
		confirmTaskName = getPossibleTaskName(currentPosition, confirmTaskName, inputElements);
		confirmTaskName = dt.extractDate(confirmTaskName) + STRING_SPACING;
		contentToAnalyse = dt.getDateElements() + STRING_SPACING;
		str = extractInputWithPreposition(currentPosition, contentOfPreposition, str);
		
		extractDateFromTask();
		return setEventTime(commandUtil, taskName, date);
	}
	
	/*
	 * This method extracts the end time and task name from a deadline
	 * task.
	 * 
	 * @param commandUtil {@code CommandUtils} and taskName {@code String}
	 * 
	 * @return {@code CommandUtils} 
	 * 
	 */
	
	private CommandUtils parseDEADLINED(CommandUtils commandUtil, String taskName) {

		String[] elements = taskName.split(STRING_SPLITTER);
		inputElements = new ArrayList<String>(Arrays.asList(elements));
		
		confirmTaskName = getPossibleTaskName(currentPosition, confirmTaskName, inputElements);
		confirmTaskName = dt.extractDate(confirmTaskName) + STRING_SPACING;
		contentToAnalyse = dt.getDateElements() + STRING_SPACING;
		str = extractInputWithPreposition(currentPosition, contentOfPreposition, str);
		
		extractDateFromTask();
		return setDeadLine(commandUtil, confirmTaskName, date);
	}
	
	/*
	 * This method verifies if the user input contains end time and extract
	 * the task name.
	 * 
	 * @param taskItems {@code ArrayList<String>}, commandUtil {@code CommandUtils} 
	 * 		  and taskName {@code String}
	 * 
	 * @return {@code CommandUtils} 
	 * 
	 */
	private CommandUtils parseFloating(ArrayList<String> taskItems, String taskName, CommandUtils commandUtil) {
		
		hasTimeDateElement = dt.hasDateAndTimeElements(taskName);
		List<Date> dates = new PrettyTimeParser().parse(taskName);
		
		if (dates.size() == 0 && dt.getDateElements() == null) {
			
			commandUtil.setTaskName(taskName);
		}
		else {
			
			// Example: Meet Hannah Tomorrow. (no preposition)
			if (dates.size() == 1 && hasTimeDateElement == true) {		
			
				commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
				commandUtil = finalVerification(taskName, commandUtil);	
			}
			// Example: 
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
	
	private void getKeywordPosition(String taskName) {
		
		ArrayList<String> temp = new ArrayList<String>(convertArrayListToLowerCase(taskName));
		
		LAST_POSITION_OF_AT = temp.lastIndexOf(KEYWORD_AT);
		LAST_POSITION_OF_ON = temp.lastIndexOf(KEYWORD_ON);
		LAST_POSITION_OF_BEFORE = temp.lastIndexOf(KEYWORD_BEFORE);
		LAST_POSITION_OF_BY = temp.lastIndexOf(KEYWORD_BY_1);
		LAST_POSITION_OF_FROM = temp.lastIndexOf(KEYWORD_FROM);
		LAST_POSITION_OF_TO = temp.lastIndexOf(KEYWORD_TO);
		LAST_POSITION_OF_IN = temp.lastIndexOf(KEYWORD_IN);
	
	}

	private ArrayList<String> convertArrayListToLowerCase(String taskName) {
		
		String[] str = taskName.toLowerCase().split(STRING_SPLITTER);
		ArrayList<String> temp = new ArrayList<String>(Arrays.asList(str));
		
		return temp;
	}


	private CommandUtils setEventTime(CommandUtils commandUtil, String taskName, Date date) {
		
		List<Date> confirmDate = new PrettyTimeParser().parse(contentToAnalyse.replace(KEYWORD_FROM, STRING_SPACING));
		
		if (confirmDate.size() == 2) {
		
			commandUtil.setStartTime(dt.checkAndSetDefaultStartTime(confirmDate.get(0), date));
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(confirmDate.get(1), date));
			commandUtil.setTaskName(confirmTaskName.trim());
		}
		else if (confirmDate.size() == 1) {
			
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(confirmDate.get(0), date));
			commandUtil.setTaskName(confirmTaskName.trim());
			commandUtil.setTaskType(TASK_TYPE.DEADLINED);
		}
		else {
		
			commandUtil.setTaskType(TASK_TYPE.FLOATING);
			commandUtil.setTaskType(TASK_TYPE.FLOATING);
		}
		
		return commandUtil;
	
	}


	private CommandUtils setDeadLine(CommandUtils commandUtil, String confirmTaskName, Date date) {
		
		System.out.println("setDeadLine : " + contentToAnalyse);
		List<Date> confirmDate = new PrettyTimeParser().parse(contentToAnalyse.replace(KEYWORD_BY, STRING_SPACING));
		
		if (confirmDate.size() == 1 && !contentToAnalyse.contains(STRING_MIDNIGHT_TIME)) {
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(confirmDate.get(0), date));
		}
		else if (confirmDate.size() == 1 && contentToAnalyse.contains(STRING_MIDNIGHT_TIME)) {
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(DateUtils.addDays(confirmDate.get(0), 1), date));
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
				contentToAnalyse += str.get(i) + STRING_SPACING;
			}
			else {
				System.out.println("Test str size fail : " + str.get(i));
				confirmTaskName += str.get(i) + STRING_SPACING;
			}
		}
	}

	private ArrayList<String> extractInputWithPreposition(int currentPosition, String contentOfPreposition,
			ArrayList<String> str) {
		
		for (int i = 0; i < inputElements.size(); ) {
			
			contentOfPreposition += inputElements.get(currentPosition) + STRING_SPACING;
			inputElements.remove(currentPosition);
			
			while (inputElements.size() != 0) {
				
				if (!preposition.contains(inputElements.get(currentPosition))) {
					contentOfPreposition += inputElements.get(currentPosition) + STRING_SPACING;
					inputElements.remove(currentPosition);
				}
				else {
					break;
				}
			}
			
			str.add(contentOfPreposition.trim());
			contentOfPreposition = STRING_EMPTY;
		
		}
		
		return str;
	}

	private String getPossibleTaskName(int currentPosition, String confirmTaskName, ArrayList<String> inputElements) {
		
		for (int i = 0; i < inputElements.size(); i++) {
			
			if (!preposition.contains(inputElements.get(currentPosition))) {
				confirmTaskName += inputElements.get(currentPosition) + STRING_SPACING; 
				inputElements.remove(currentPosition);
			}
			else {
				break;
			}
		}
		return confirmTaskName;
	}


	
	private CommandUtils finalVerification(String userTask, CommandUtils commandUtil) {

		String temp = dt.extractDate(userTask);
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
		
		String[] str = temp.split(STRING_SPLITTER);
		String newStr = STRING_EMPTY;
		int lastWordPosition = str.length - 1;
		
		if (isLastWordPreposition(str, lastWordPosition)) {
			str[lastWordPosition] = STRING_EMPTY;
		}

		return toStringArray(str, newStr).trim();
	}

	private String toStringArray(String[] str, String newStr) {
		
		for (int i = 0; i < str.length; i++) {
			newStr += str[i] + STRING_SPACING;
		}
		
		return newStr;
	}
	
	private boolean isLastWordPreposition (String[] str, int lastWordPosition) {
		
		return (str[lastWordPosition].toLowerCase().contains(KEYWORD_AT) || 
				str[lastWordPosition].toLowerCase().contains(KEYWORD_BY) ||
				str[lastWordPosition].toLowerCase().contains(KEYWORD_ON) ||
				str[lastWordPosition].toLowerCase().contains(KEYWORD_DUE) ||
				str[lastWordPosition].toLowerCase().contains(KEYWORD_TO)) 
				? true : false;
	}
	
}