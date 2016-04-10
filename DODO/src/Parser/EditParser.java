//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import Command.*;
import Logger.LoggerFile;
import Task.*;

public class EditParser {
	
	private static Logger logger;
	
	// Constants
	private static final String KEYWORD_TO = " to ";
	private static final String KEYWORD_FROM = " from ";
	private static final String KEYWORD_AT = " at ";
	private static final String KEYWORD_BEFORE = " before ";
	private static final String KEYWORD_ON = " on ";
	private static final String KEYWORD_BY = " by ";
	private static final String STRING_HASH_TAG = "#";
	private static final String PUNCTUATION_REMOVER = "[:-]";
	private static final String STRING_SPLITTER = "\\s+";
	private static final String STRING_EMPTY = "";
	private static final String STRING_SPACING = " ";
	private static final int NUM_NO_DATE_ELEMENT = 0;
	private static final int NUM_FIRST_ELEMENT = 0;

	// Logging messages for processing methods.
	private static final String LOGGER_MESSAGE_PROCESS_EDIT_PARSER = "EditParser class: Entering EditParser class.";
	private static final String LOGGER_MESSAGE_EXIT_EDIT_PARSER = "EditParser class: Exiting EditParser class.";
	private static final String LOGGER_MESSAGE_PROCESS_EDIT_TYPE = "EditParser class: Analysing edit type.";
	private static final String LOGGER_MESSAGE_PROCESS_TASK_NAME_TYPE = "EditParser class: Processing edit task name type.";
	private static final String LOGGER_MESSAGE_PROCESS_EVENT_TIME = "EditParser class: Processing edit event type.";
	private static final String LOGGER_MESSAGE_PROCESS_START_TIME = "EditParser class: Processing edit start time.";
	private static final String LOGGER_MESSAGE_PROCESS_DEADLINED = "EditParser class: Processing edit deadline.";
	private static final String LOGGER_MESSAGE_PROCESS_END_TIME = "EditParser class: Processing edit end time.";
	private static final String LOGGER_MESSAGE_PROCESS_TAG_TYPE = "EditParser class: Processing edit tag type.";
	private static final String LOGGER_MESSAGE_PROCESS_INVALID = "EditParser class: Invalid edit type.";
	private static final String LOGGER_MESSAGE_INDEX_OUT_OF_BOUND = "WARNING! Method may throw index out of bound.";
	private static final String LOGGER_MESSAGE_PROCESS_EDIT_TAG_METHOD = "EditParser class: Processing editTag method.";
	private static final String LOGGER_MESSAGE_PROCESS_EXTRACT_TASK_ID = "EditParser class: Extracting task id.";
	
	private String newTaskName = "";
	private int INDEX_OF_LAST_TO = -1;
	private int INDEX_OF_LAST_FROM = -1;
	
	private EDIT_TYPE editType;
	private ArrayList<String> editTaskElements;
	private ArrayList<String> newTag;
	
	private Date date;
	private DateTimeParser dt;
	private AddParser ap;
	
	public EditParser() {
		
		newTag = new ArrayList<String>();
		dt = new DateTimeParser();
		date = new Date();
		logger = LoggerFile.getLogger();
		ap = new AddParser();
	
	}
	
	/*
	 * This method process the edit command.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	
	protected CommandUtils executeEditParser(CommandUtils commandUtil, String userInput) {
		
		assert (userInput != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_EDIT_PARSER);
		
		String[] editElements = userInput.replaceAll(PUNCTUATION_REMOVER, STRING_EMPTY).split(STRING_SPLITTER);
		editTaskElements = new ArrayList<String>(Arrays.asList(editElements));
		newTaskName = dt.checkForAbbreviation(editTaskElements);
		
		String[] updatedElements = newTaskName.replaceAll(PUNCTUATION_REMOVER, STRING_EMPTY).split(STRING_SPLITTER);
		editTaskElements = new ArrayList<String>(Arrays.asList(updatedElements));
		
		userInput = extractTaskID(commandUtil, editTaskElements);
		commandUtil = ap.executeAddParser(commandUtil, userInput);
		commandUtil = determineEditType(commandUtil, userInput);
		editType = commandUtil.getEditType();
		
		logger.log(Level.INFO, LOGGER_MESSAGE_EXIT_EDIT_PARSER);
		return processEditType(commandUtil, userInput);
	}

	/*
	 * This method process the edit type".
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils processEditType(CommandUtils commandUtil, String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_EDIT_TYPE);
		
		switch(editType) {
		
		case TASK_NAME:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_TASK_NAME_TYPE);
			return parseEditTaskName(commandUtil, userInput);
			
		case EVENT_TIME:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_EVENT_TIME);
			return parseEditEventTime(commandUtil, userInput);
			
		case START_TIME:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_START_TIME);
			return parseEditStartTime(commandUtil, userInput);
			
		case DEADLINED:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_DEADLINED);
			return parseEditDeadLined(commandUtil, userInput);
			
		case END_TIME:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_END_TIME);
			return parseEditEndTime(commandUtil, userInput);
			
		case TAG:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_TAG_TYPE);
			return parserEditTag(commandUtil, userInput);
			
		case INVALID:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_INVALID);
			break;
		}
		return commandUtil;
	}
	
	/*
	 * This method process the edit of tag.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parserEditTag(CommandUtils commandUtil, String userInput) {
		
		String[] str = userInput.trim().split(STRING_SPLITTER);
		assert (str.length <= 3 && str.length > 0);
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_EDIT_TAG_METHOD);
		logger.log(Level.WARNING, LOGGER_MESSAGE_INDEX_OUT_OF_BOUND);
		
		if (str[NUM_FIRST_ELEMENT].startsWith(STRING_HASH_TAG) && str[2].startsWith(STRING_HASH_TAG)) {
			
			commandUtil.setOldTag(str[NUM_FIRST_ELEMENT].substring(1, str[NUM_FIRST_ELEMENT].length()));
			newTag.add(str[2].substring(1, str[NUM_FIRST_ELEMENT].length()));
			commandUtil.setTaskTag(newTag);	
		}
		else {
			commandUtil.setEditType(EDIT_TYPE.INVALID);
		}
		return commandUtil;
	}

	/*
	 * This method process the edit of end time.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parseEditEndTime(CommandUtils commandUtil, String userInput) {
		
		INDEX_OF_LAST_TO = userInput.lastIndexOf(KEYWORD_TO);
		String temp = userInput.substring(INDEX_OF_LAST_TO, userInput.length());
		
		commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_TO)));
		List<Date> dates = new PrettyTimeParser().parse(temp);
		commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(NUM_FIRST_ELEMENT), date));
		
		return commandUtil;
	}

	/*
	 * This method process the edit of start time.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	
	private CommandUtils parseEditStartTime(CommandUtils commandUtil, String userInput) {
		
		INDEX_OF_LAST_FROM = userInput.lastIndexOf(KEYWORD_FROM);
		String temp = userInput.substring(INDEX_OF_LAST_FROM, userInput.length());
		
		commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_FROM)));
		List<Date> dates = new PrettyTimeParser().parse(temp);
		commandUtil.setStartTime(dates.get(NUM_FIRST_ELEMENT));
		
		return commandUtil;
	}

	/*
	 * This method process the edit of deadline.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parseEditDeadLined(CommandUtils commandUtil, String userInput) {
		AddParser ap = new AddParser();
		commandUtil = ap.executeAddParser(commandUtil, userInput);
		return commandUtil;
	}

	/*
	 * This method process the edit of event time.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parseEditEventTime(CommandUtils commandUtil, String userInput) {
		AddParser ap = new AddParser();
		return ap.executeAddParser(commandUtil, userInput);
	}
	
	/*
	 * This method process the edit of task name".
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parseEditTaskName(CommandUtils commandUtil, String userInput) {
		AddParser ap = new AddParser();
		return ap.executeAddParser(commandUtil, userInput);
	}

	/*
	 * This method determines the type of edit.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils determineEditType(CommandUtils commandUtil, String userInput) {
		
		if (commandUtil.getType() == TASK_TYPE.FLOATING && !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.TASK_NAME);
		}
		/*
		 * @Description: edits the deadline of task 
		 * Example: edit 1 by tomorrow
		 */
		else if (commandUtil.getType() == TASK_TYPE.DEADLINED && !hasEndTime(userInput) && !hasStartTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.DEADLINED);
		}
		/*
		 * @ Description: edits the end time of an event
		 *  Example: edit 1 to 24.07.2016
		 */
		else if (!hasDeadLined(userInput) && hasEndTime(userInput) && !hasStartTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.END_TIME);
		}
		/*
		 * @ Description: edits the start time of an event
		 *  Example: edit 1 from 24.07.2016
		 */
		else if (!hasDeadLined(userInput) && !hasEndTime(userInput) && hasStartTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.START_TIME);
		}
		/*
		 * @ Description: edits the start and end time of an event
		 *  Example: edit 1 from 24.07.2016 to 28.07.2016
		 */
		else if (commandUtil.getType() == TASK_TYPE.EVENT && !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.EVENT_TIME);
		}
		/*
		 * @ Description: edits a tag to a new name
		 *  Example: edit 1 #soc to #nus
		 */
		else if (userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.TAG);
		}
		else if (commandUtil.getType() == TASK_TYPE.DEADLINED && !hasDeadLined(userInput) && hasEndTime(userInput) 
				&& !hasStartTime(userInput) && !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.DEADLINED);
		}
		else {
			commandUtil.setEditType(EDIT_TYPE.INVALID);
		} 
		return commandUtil;
	}

	/*
	 * This method checks if input contains deadline.
	 * 
	 * @param userInput {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean hasDeadLined(String userInput) {
		
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		
		if (userInput.contains(KEYWORD_BEFORE) || userInput.contains(KEYWORD_BY) 
			|| userInput.contains(KEYWORD_ON) || userInput.contains(KEYWORD_AT)) {
			
			return (dates.size() != NUM_NO_DATE_ELEMENT) ? true : false;
		
		}
		else {
			return false;
		}
	}
	
	/*
	 * This method checks if input contains "start time".
	 * 
	 * @param userInput {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean hasStartTime(String userInput) {
	
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		
		return (userInput.contains(KEYWORD_FROM) && dates.size() != NUM_NO_DATE_ELEMENT) ? true : false;
	}

	/*
	 * This method checks if input contains "end time".
	 * 
	 * @param userInput {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean hasEndTime(String userInput) {
		
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		
		return (userInput.contains(KEYWORD_TO) && dates.size() != NUM_NO_DATE_ELEMENT
				&& !userInput.contains(STRING_HASH_TAG)) ? true : false;
	}

	/*
	 * This method extracts the task index.
	 * 
	 * @param commandUtil {@code CommandUtils} and editTaskElements {@code ArrayList<String>}
	 * 
	 * @return {@code String} with task index removed.
	 * 			
	 * 
	 */
	private String extractTaskID(CommandUtils commandUtil, ArrayList<String> editTaskElements) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_EXTRACT_TASK_ID);
		String temp = "";
		
		if (editTaskElements.size() < 2) {
			return temp;
		}
		else if (!editTaskElements.get(NUM_FIRST_ELEMENT).startsWith(STRING_HASH_TAG)){
		
			commandUtil.setTaskID(editTaskElements.get(NUM_FIRST_ELEMENT));
			editTaskElements.remove(NUM_FIRST_ELEMENT);
			temp = toStringTaskElements(editTaskElements);
		}
		else if (editTaskElements.get(NUM_FIRST_ELEMENT).startsWith(STRING_HASH_TAG)) {
			
			temp = toStringTaskElements(editTaskElements);
		}
		
		return temp;
	}
	
	
	private String toStringTaskElements(ArrayList<String> taskNameArrayList) {
	
		String name = "";
		
		for (int i = 0; i < taskNameArrayList.size(); i++) {
			name += STRING_SPACING + taskNameArrayList.get(i); 
		}
		
		return name;
	}

}
