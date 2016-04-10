//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import Command.*;
import Logger.LoggerFile;

public class EditParser {
	
	private static Logger logger;
	
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
	private static final int NUM_MAX_DATE_ELEMENTS = 2;
	private static final int NUM_NO_DATE_ELEMENT = 0;
	
	private String newTaskName = "";
	private int INDEX_OF_LAST_TO = -1;
	private int INDEX_OF_LAST_AT = -1;
	private int INDEX_OF_LAST_BEFORE = -1;
	private int INDEX_OF_LAST_ON = -1;
	private int INDEX_OF_LAST_BY = -1;
	private int INDEX_OF_LAST_FROM = -1;
	
	private EDIT_TYPE editType;
	private ArrayList<String> editTaskElements;
	private ArrayList<String> newTag;
	
	private Date date;
	private DateTimeParser dt;
	
	public EditParser() {
		
		newTag = new ArrayList<String>();
		dt = new DateTimeParser();
		date = new Date();
		logger = LoggerFile.getLogger();
	
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
		
		String[] editElements = userInput.replaceAll(PUNCTUATION_REMOVER, STRING_EMPTY).split(STRING_SPLITTER);
		editTaskElements = new ArrayList<String>(Arrays.asList(editElements));
		newTaskName = dt.checkForAbbreviation(editTaskElements);
		
		String[] updatedElements = newTaskName.replaceAll(PUNCTUATION_REMOVER, STRING_EMPTY).split(STRING_SPLITTER);
		editTaskElements = new ArrayList<String>(Arrays.asList(updatedElements));
		
		userInput = extractTaskID(commandUtil, editTaskElements);
		commandUtil = determineEditType(commandUtil, userInput);
		editType = commandUtil.getEditType();
		
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
		
		switch(editType) {
		
		case TASK_NAME:
			return parseEditTaskName(commandUtil, userInput);
			
		case EVENT_TIME:
			return parseEditEventTime(commandUtil, userInput);
			
		case START_TIME:
			return parseEditStartTime(commandUtil, userInput);
			
		case DEADLINED:
			return parseEditDeadLined(commandUtil, userInput);
			
		case END_TIME:
			return parseEditEndTime(commandUtil, userInput);
			
		case TAG:
			return parserEditTag(commandUtil, userInput);
			
		case INVALID:
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
		
		if (str[0].startsWith(STRING_HASH_TAG) && str[2].startsWith(STRING_HASH_TAG)) {
			commandUtil.setOldTag(str[0].substring(1, str[0].length()));
			newTag.add(str[2].substring(1, str[0].length()));
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
		commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
		
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
		commandUtil.setStartTime(dates.get(0));
		
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
	
		String temp = "";
		INDEX_OF_LAST_ON = userInput.lastIndexOf(KEYWORD_ON);
		INDEX_OF_LAST_AT = userInput.lastIndexOf(KEYWORD_AT);
		
		if (userInput.lastIndexOf(KEYWORD_BY) != -1 && userInput.lastIndexOf(KEYWORD_AT) == -1) {
		
			INDEX_OF_LAST_BY = userInput.lastIndexOf(KEYWORD_BY);
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_BY)));
			temp = userInput.substring(INDEX_OF_LAST_BY, userInput.length());
		
		}
		else if (userInput.lastIndexOf(KEYWORD_ON) != -1 && userInput.lastIndexOf(KEYWORD_AT) == -1) {
		
			INDEX_OF_LAST_ON = userInput.lastIndexOf(KEYWORD_ON);
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_ON)));
			temp = userInput.substring(INDEX_OF_LAST_ON, userInput.length());
		
		}
		else if (userInput.lastIndexOf(KEYWORD_AT) != -1 && userInput.lastIndexOf(KEYWORD_ON) == -1) {
		
			INDEX_OF_LAST_AT = userInput.lastIndexOf(KEYWORD_AT);
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_AT)));
			temp = userInput.substring(INDEX_OF_LAST_AT, userInput.length());
		
		}
		
		else if (INDEX_OF_LAST_ON != -1 && INDEX_OF_LAST_ON < INDEX_OF_LAST_AT) {
			
			commandUtil.setTaskName(userInput.substring(0, INDEX_OF_LAST_ON));
			temp = userInput.substring(INDEX_OF_LAST_ON, INDEX_OF_LAST_AT);
			String temp2 = userInput.substring(INDEX_OF_LAST_AT, userInput.length());
			
			List<Date> dateTemp = new PrettyTimeParser().parse(temp);
			List<Date> dateTemp2 = new PrettyTimeParser().parse(temp2);
			
			if (dateTemp.size() == NUM_NO_DATE_ELEMENT && dateTemp2.size() != NUM_NO_DATE_ELEMENT) {
			
				commandUtil.setTaskName(newTaskName + STRING_SPACING + temp.trim());
				temp = temp2;
			
			}
			else if (dateTemp.size() != NUM_NO_DATE_ELEMENT && dateTemp2.size() == NUM_NO_DATE_ELEMENT) {
				commandUtil.setTaskName(newTaskName + STRING_SPACING + temp2.trim());
			}
			
		}
		else if (INDEX_OF_LAST_AT != -1 && INDEX_OF_LAST_ON > INDEX_OF_LAST_AT) {
			
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_AT)));
			temp = userInput.substring(INDEX_OF_LAST_AT, INDEX_OF_LAST_ON);
			String temp2 = userInput.substring(INDEX_OF_LAST_ON, userInput.length());
			
			List<Date> dateTemp = new PrettyTimeParser().parse(temp);
			List<Date> dateTemp2 = new PrettyTimeParser().parse(temp2);
			
			if (dateTemp.size() == NUM_NO_DATE_ELEMENT && dateTemp2.size() != NUM_NO_DATE_ELEMENT) {
			
				commandUtil.setTaskName(newTaskName + STRING_SPACING + temp.trim());
				temp = temp2;
			
			}
			else if (dateTemp.size() != 0 && dateTemp2.size() == 0) {
				commandUtil.setTaskName(newTaskName + STRING_SPACING + temp2.trim());
			}
			
		}
			
		else if (userInput.lastIndexOf(KEYWORD_BEFORE) != -1) {
			
			INDEX_OF_LAST_BEFORE = userInput.lastIndexOf(KEYWORD_BEFORE);
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_BEFORE)));
			temp = userInput.substring(INDEX_OF_LAST_BEFORE, userInput.length());
		}
		
		List<Date> dates = new PrettyTimeParser().parse(temp);
		
		if (dates.size() != NUM_NO_DATE_ELEMENT) {
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
		}
		
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
	
		INDEX_OF_LAST_FROM = userInput.lastIndexOf(KEYWORD_FROM);
		commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_FROM)));
		String temp = userInput.substring(INDEX_OF_LAST_FROM, userInput.length());
		List<Date> dates = new PrettyTimeParser().parse(temp);

		if (dates.size() == NUM_MAX_DATE_ELEMENTS) {
			commandUtil.setStartTime(dates.get(0));
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(1), date));
		}
		return commandUtil;
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
	private CommandUtils parseEditTaskName(CommandUtils commandUtil, String userTask) {
		
		commandUtil.setTaskName(userTask.trim());
		
		return commandUtil;
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
		
		if (hasTaskName(userInput) && !userInput.trim().startsWith(STRING_HASH_TAG)) {
			/*
			 * @Description: edits the content of the task name. 
			 * Example: edit 1 buy sweet
			 */
			if (!hasDeadLined(userInput) && !hasStartTime(userInput) && !hasEndTime(userInput)) {
				commandUtil.setEditType(EDIT_TYPE.TASK_NAME);
			}
			/*
			 * @ Description: edits the end time of an event
			 *  Example: edit 1 revise CS2103t from today to 24.07.2016
			 */
			else if (!hasDeadLined(userInput) && hasStartTime(userInput) && hasEndTime(userInput)) {
				commandUtil.setEditType(EDIT_TYPE.EVENT_TIME);
			}
			/*
			 * @Description: edits the starting time of an event
			 * Example: edit 1 buy sweet by tuesday
			 */
			else if (hasDeadLined(userInput) && !hasStartTime(userInput) && !hasEndTime(userInput)) {
				commandUtil.setEditType(EDIT_TYPE.DEADLINED);
			}
		}
		/*
		 * @Description: edits the deadline of task 
		 * Example: edit 1 by tomorrow
		 */
		else if (hasDeadLined(userInput) && !hasEndTime(userInput) && !hasStartTime(userInput)
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
		else if (!hasDeadLined(userInput) && hasStartTime(userInput) && hasEndTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.EVENT_TIME);
		}
		/*
		 * @ Description: edits a tag to a new name
		 *  Example: edit 1 #soc to #nus
		 */
		else if (userInput.trim().startsWith(STRING_HASH_TAG)) {
			
			commandUtil.setEditType(EDIT_TYPE.TAG);
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
	 * This method checks if input contains task name.
	 * 
	 * @param userInput {@code String}
	 * 
	 * @return {@code boolean} 
	 * 			
	 * 
	 */
	private boolean hasTaskName(String userInput) {
		
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		DateTimeParser dt = new DateTimeParser();
		boolean hasDateTime = dt.hasDateAndTimeElements(userInput);
		
		return (dates.size() == NUM_NO_DATE_ELEMENT || hasDateTime == false) ? true : false;
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
		
		String temp = "";
		
		if (editTaskElements.size() < 2) {
			return temp;
		}
		else if (!editTaskElements.get(0).startsWith(STRING_HASH_TAG)){
		
			commandUtil.setTaskID(editTaskElements.get(0));
			editTaskElements.remove(0);
			temp = toStringTaskElements(editTaskElements);
		}
		else if (editTaskElements.get(0).startsWith(STRING_HASH_TAG)) {
			
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
