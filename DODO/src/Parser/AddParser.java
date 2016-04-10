//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.shade.org.apache.commons.lang.time.DateUtils;

import Task.*;
import Logger.*;

public class AddParser {
	
	private static Logger logger;
	
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
	
	// Logging for processing task types.
	private static final String LOGGER_MESSAGE_DEADLINE_TYPE = "Processing deadline task.";
	private static final String LOGGER_MESSAGE_FLOATING_TYPE = "Processing floating task.";
	private static final String LOGGER_MESSAGE_EVENT_TYPE = "Processing event task.";
	
	// Logging for processing methods.
	private static final String LOGGER_MESSAGE_EXECUTE_ADD_PARSER = "AddParser Class: Processing executeAddParser method.";
	private static final String LOGGER_MESSAGE_EXIT_CLASS = "AddParser Class: Exiting AddParser class.";
	private static final String LOGGER_MESSAGE_DETERMINE_TASK_TYPE = "AddParser Class: Determining task type.";
	private static final String LOGGER_MESSAGE_CHECK_IF_EVENT_TASK = "AddParser Class: Checking if input is event task.";
	private static final String LOGGER_MESSAGE_CHECK_IF_FLOATING_TASK = "AddParser Class: Checking if input is floating task.";
	private static final String LOGGER_MESSAGE_CHECK_IF_DEADLINE_TASK = "AddParser Class: Checking if input is deadline task.";
	private static final String LOGGER_MESSAGE_PARSE_EVENT = "AddParser Class: Processing event task.";
	private static final String LOGGER_MESSAGE_PARSE_DEADLINED = "AddParser Class: Processing deadline task.";
	private static final String LOGGER_MESSAGE_PARSE_FLOATING = "AddParser Class: Processing floating task.";
	private static final String LOGGER_MESSAGE_GET_KEYWORD_POSITION = "AddParser Class: Getting position of preposition.";
	private static final String LOGGER_MESSAGE_CONVERT_ARRAYLIST_TO_LOWERCASE = "AddParser Class: Converting arraylist to lowercase.";
	private static final String LOGGER_MESSAGE_SET_EVENT_TIME = "AddParser Class: Setting time elements for event task.";
	private static final String LOGGER_MESSAGE_SET_DEADLINE = "AddParser Class: Setting time element for deadline task.";
	private static final String LOGGER_MESSAGE_EXTRACT_DATE_FROM_TASK = "AddParser Class: Extracting date elements from task name";
	private static final String LOGGER_MESSAGE_EXTRACT_PREPOSITION = "AddParser Class: Extracting preposition.";
	private static final String LOGGER_MESSAGE_GET_TASK_NAME = "AddParser Class: Extracting task name.";
	private static final String LOGGER_MESSAGE_FINAL_VERIFICATION = "AddParser Class: Checking if task name is really floating task.";
	private static final String LOGGER_MESSAGE_EXTRACT_LAST_PREPOSITION = "AddParser Class: Extracting last string.";
	private static final String LOGGER_MESSAGE_HAS_LAST_WORD_AS_PREPOSITION = "AddParser Class: Checking if last string is a preposition";
	
	public AddParser() {
		
		date = new Date();
		dt = new DateTimeParser();
		str = new ArrayList<String>();
		logger = LoggerFile.getLogger();
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
		logger.log(Level.INFO, LOGGER_MESSAGE_EXECUTE_ADD_PARSER);
		
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		taskName = dt.checkForAbbreviation(taskItems);
		taskName = dt.checkAndConvertDateElement(taskItems);
		
		commandUtil = determineTaskType(commandUtil, taskItems, taskName);
		taskType = commandUtil.getType();
		
		logger.log(Level.INFO, LOGGER_MESSAGE_EXIT_CLASS);
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
				logger.log(Level.INFO, LOGGER_MESSAGE_DEADLINE_TYPE);
				commandUtil = parseDEADLINED(commandUtil, taskName);
				return parseFloating(taskItems, commandUtil.getName(), commandUtil);
			
			case FLOATING:
				logger.log(Level.INFO, LOGGER_MESSAGE_FLOATING_TYPE);
				return parseFloating(taskItems, taskName, commandUtil);
			
			case EVENT:
				logger.log(Level.INFO, LOGGER_MESSAGE_EVENT_TYPE);
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
	 * @param commandUtil {@code CommandUtils}, taskItems {@code List<E>}
	 * 		  and taskName {@code String}
	 * 
	 * @return {@code CommandUtils} with the task type set to CommandUtils object.
	 * 
	 */
	
	public CommandUtils determineTaskType(CommandUtils commandUtil, ArrayList<String> taskItems, String taskName) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_DETERMINE_TASK_TYPE);
		
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
		
		logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_IF_EVENT_TASK);
		
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
	 * @param taskItems {@code List<E>} and taskName {@code String}
	 * 
	 * @return {@code boolean} 
	 * 
	 */
	private boolean checkIfFloatingTask(ArrayList<String> taskItems, String taskName) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_IF_FLOATING_TASK);
		return (!checkIfEventTask(taskName) && !checkIfDeadlinedTask(taskItems, taskName)) ? true : false;
	
	}
	
	/*
	 * This method analyses and verifies if a user input is a deadlined task.
	 * 
	 * @param taskItems {@code List<E>} and taskName {@code String}
	 * 
	 * @return {@code boolean} 
	 * 
	 */
	
	private boolean checkIfDeadlinedTask(ArrayList<String> taskItems, String taskName) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_IF_DEADLINE_TASK);
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
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PARSE_EVENT);
		
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

		logger.log(Level.INFO, LOGGER_MESSAGE_PARSE_DEADLINED);
		
		String[] elements = taskName.split(STRING_SPLITTER);
		inputElements = new ArrayList<String>(Arrays.asList(elements));
		
		confirmTaskName = getPossibleTaskName(currentPosition, confirmTaskName, inputElements);
		confirmTaskName = dt.extractDate(confirmTaskName) + STRING_SPACING;
		contentToAnalyse = dt.getDateElements() + STRING_SPACING;
		System.out.println("parseDeadline : " + contentToAnalyse);
		str = extractInputWithPreposition(currentPosition, contentOfPreposition, str);
		
		extractDateFromTask();
		return setDeadLine(commandUtil, confirmTaskName, date);
	}
	
	/*
	 * This method verifies if the user input contains end time and extract
	 * the task name accordingly.
	 * 
	 * @param taskItems {@code List<E>}, commandUtil {@code CommandUtils} 
	 * 		  and taskName {@code String}
	 * 
	 * @return {@code CommandUtils} 
	 * 
	 */
	private CommandUtils parseFloating(ArrayList<String> taskItems, String taskName, CommandUtils commandUtil) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PARSE_FLOATING);
		
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
			
			// Example: Hackathon today to 05.05.16
			else if (dates.size() > 1 && hasTimeDateElement == true) {
				
				commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(1), date));
				commandUtil.setStartTime(dates.get(0));
				commandUtil.setTaskType(TASK_TYPE.EVENT);
			}
			
			// Meet Hannah at Hackathon
			else if (hasTimeDateElement == false) {
				commandUtil.setTaskName(taskName);	
			}
		}
		
		return commandUtil;
	}
	
	/*
	 * This method identifies the position of the prepositions in a user input
	 * 
	 * @param taskName {@code String}
	 * 
	 * @return {} 
	 * 
	 */
	
	private void getKeywordPosition(String taskName) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_GET_KEYWORD_POSITION);
		ArrayList<String> temp = new ArrayList<String>(convertArrayListToLowerCase(taskName));
		
		LAST_POSITION_OF_AT = temp.lastIndexOf(KEYWORD_AT);
		LAST_POSITION_OF_ON = temp.lastIndexOf(KEYWORD_ON);
		LAST_POSITION_OF_BEFORE = temp.lastIndexOf(KEYWORD_BEFORE);
		LAST_POSITION_OF_BY = temp.lastIndexOf(KEYWORD_BY_1);
		LAST_POSITION_OF_FROM = temp.lastIndexOf(KEYWORD_FROM);
		LAST_POSITION_OF_TO = temp.lastIndexOf(KEYWORD_TO);
		LAST_POSITION_OF_IN = temp.lastIndexOf(KEYWORD_IN);
	
	}
	
	/*
	 * This method converts elements in an arraylist to lowercase.
	 * 
	 * @param taskName {@code String}
	 * 
	 * @return {@code List<E>} with all string in lowercase. 
	 * 
	 */
	private ArrayList<String> convertArrayListToLowerCase(String taskName) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_CONVERT_ARRAYLIST_TO_LOWERCASE);
		String[] str = taskName.toLowerCase().split(STRING_SPLITTER);
		ArrayList<String> temp = new ArrayList<String>(Arrays.asList(str));
		
		return temp;
	}
	
	/*
	 * This method extracts the time elements from an event task and set to CommandUtils
	 * object.
	 * 
	 * @param commandUtil {@code CommandUtils} , taskName {@code String} 
	 * 		  and date {@code Date}
	 * 
	 * @return {@code CommandUtils} 
	 * 
	 */
	
	private CommandUtils setEventTime(CommandUtils commandUtil, String taskName, Date date) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_SET_EVENT_TIME);
		
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
		}
		
		return commandUtil;
	
	}
	
	/*
	 * This method extracts the time elements from an event task and set to CommandUtils
	 * object.
	 * 
	 * @param commandUtil {@code CommandUtils} , confirmTaskName {@code String} 
	 * 		  and date {@code Date}
	 * 
	 * @return {@code CommandUtils} 
	 * 
	 */

	private CommandUtils setDeadLine(CommandUtils commandUtil, String confirmTaskName, Date date) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_SET_DEADLINE);
		
		List<Date> confirmDate = new PrettyTimeParser().parse(contentToAnalyse.replace(KEYWORD_BY, STRING_SPACING));
		
		// Example: Meeting with boss at 6.30pm
		if (confirmDate.size() == 1 && !contentToAnalyse.contains(STRING_MIDNIGHT_TIME)) {
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(confirmDate.get(0), date));
		}
		// Example: submit proposal to boss at 2400hrs.
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
	
	/*
	 * This method extracts the time elements from a task 
	 * 
	 * @param {}
	 * 
	 * @return {} 
	 * 
	 */
	private void extractDateFromTask() {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_EXTRACT_DATE_FROM_TASK);
		
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
	
	/*
	 * This method extracts string starting from a preposition to the string 
	 * before the next preposition or the end of the user input.
	 * 
	 * @param currentPosition {@code int} , contentOfPreposition {@code String} 
	 * 		  and str {@code List<E>}
	 * 
	 * @return {@code List<E>}  
	 * 
	 */
	private ArrayList<String> extractInputWithPreposition(int currentPosition, String contentOfPreposition,
			ArrayList<String> str) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_EXTRACT_PREPOSITION);
		
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
	
	/*
	 * This method extracts possible task name from the user input
	 * object.
	 * 
	 * @param currentPosition {@code int} , confirmTaskName {@code String} 
	 * 		  and inputElements {@code List<E>}
	 * 
	 * @return {@code String} with preposition and possible date elements removed. 
	 * 
	 */

	private String getPossibleTaskName(int currentPosition, String confirmTaskName, ArrayList<String> inputElements) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_GET_TASK_NAME);
		
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

	/*
	 * This method verifies if a floating task consists of date element.
	 * Example: tomorrow meet boss for meeting.
	 * 
	 * @param userTask {@code String} and commandUtil {@code CommandUtils}
	 * 
	 * @return {@code CommandUtils} 
	 * 
	 */
	
	private CommandUtils finalVerification(String userTask, CommandUtils commandUtil) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_FINAL_VERIFICATION);
		
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
	
	/*
	 * This method checks if the last string in the task name consists of
	 * preposition and removes it if there is.
	 * 
	 * @param finalTaskName {@code String} 
	 * 
	 * @return {@code CommandUtils} 
	 * 
	 */

	private String extractLastPreposition(String finalTaskName) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_EXTRACT_LAST_PREPOSITION);
		
		String[] str = finalTaskName.split(STRING_SPLITTER);
		String newStr = STRING_EMPTY;
		int lastWordPosition = str.length - 1;
		
		if (isLastWordPreposition(str, lastWordPosition)) {
			str[lastWordPosition] = STRING_EMPTY;
		}

		return toStringArray(str, newStr).trim();
	}

	/*
	 * This method concatenates string array into string.
	 * 
	 * @param str {@code String[]} and newStr {@code String} 
	 * 
	 * @return {@code CommandUtils} 
	 * 
	 */

	private String toStringArray(String[] str, String newStr) {
		
		for (int i = 0; i < str.length; i++) {
			newStr += str[i] + STRING_SPACING;
		}
		
		return newStr;
	}
	
	/*
	 * This method checks if a string contains a preposition word 
	 * as its last word.
	 * 
	 * @param str {@code String[]} and lastWordPosition {@code int} 
	 * 
	 * @return {@code boolean} 
	 * 
	 */
	
	private boolean isLastWordPreposition (String[] str, int lastWordPosition) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_HAS_LAST_WORD_AS_PREPOSITION);
		
		return (str[lastWordPosition].toLowerCase().contains(KEYWORD_AT) || 
				str[lastWordPosition].toLowerCase().contains(KEYWORD_BY) ||
				str[lastWordPosition].toLowerCase().contains(KEYWORD_ON) ||
				str[lastWordPosition].toLowerCase().contains(KEYWORD_DUE) ||
				str[lastWordPosition].toLowerCase().contains(KEYWORD_TO)) 
				? true : false;
	}
	
}