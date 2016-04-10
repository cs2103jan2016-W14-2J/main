//@@author: A0125552L
package Parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import Command.*;
import Logger.*;

public class SortParser {
	
	private static Logger logger;
	private String ALPHABETICAL_ORDER = "abc";
	private String NUMERICAL_ORDER = "123";
	private String REVERSE_ALPHABETICAL_ORDER = "cba";
	private String REVERSE_NUMERICAL_ORDER = "321";
	private String DATE_ORDER = "date";
	private String PREPOSITION_BY = "by";
	private int POSITION_OF_INPUT  = 0;
	
	private String STRING_SPLITTER = "\\s+";
	private String STRING_EMPTY = "";
	private String[] stringSplit;
	
	// Logging messages for methods
	private final String LOGGER_MESSAGE_DETERMINE_SORT_TYPE = "SortParser Class: Processing user input in determineSortType method.";
	private final String LOGGER_MESSAGE_IS_REVERSE_ALPHABETICAL = "SortParser Class: Processing user input in isSortByReverseAlphabetical method.";
	private final String LOGGER_MESSAGE_IS_ALPHABETICAL = "SortParser Class: Processing user input in isSortByAlphabetical method.";
	private final String LOGGER_MESSAGE_IS_BY_DATE = "SortParser Class: Processing user input in isSortByDate method.";
	private final String LOGGER_MESSAGE_REMOVE_BY = "SortParser Class: Processing user input in removeBy method.";
	private final String LOGGER_MESSAGE_IS_SORT_DATE = "SortParser Class: Sort type is BY_DATE.";
	private final String LOGGER_MESSAGE_IS_SORT_ASCENDING = "SortParser Class: Sort type is BY_ASCENDING.";
	private final String LOGGER_MESSAGE_IS_SORT_DESCENDING = "SortParser Class: Sort type is BY_DESCENDING.";
	private final String LOGGER_MESSAGE_IS_INVALID_SORT = "SortParser Class: Sort type is INVALID.";
	
			
	public SortParser() {
		logger = LoggerFile.getLogger();
	}
	
	protected CommandUtils determineSortType(CommandUtils commandUtil, String userTask) {
		
		assert (userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_DETERMINE_SORT_TYPE);
		
		userTask = removeBy(userTask.toLowerCase());
		
		if (isSortByDate(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_IS_SORT_DATE);
			commandUtil.setSortType(SORT_TYPE.BY_DATE);
		}
		else if (isSortByAlphabetical(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_IS_SORT_ASCENDING);
			commandUtil.setSortType(SORT_TYPE.BY_ASCENDING);
		}
		else if (isSortByReverseAlphabetical(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_IS_SORT_DESCENDING);
			commandUtil.setSortType(SORT_TYPE.BY_DESCENDING);
		}
		else {
			logger.log(Level.INFO, LOGGER_MESSAGE_IS_INVALID_SORT);
			commandUtil.setSortType(SORT_TYPE.INVALID);
		}
		return commandUtil;
	}

	private boolean isSortByReverseAlphabetical(String userTask) {
		
		assert(userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_IS_REVERSE_ALPHABETICAL);
		
		stringSplit = userTask.toLowerCase().split(STRING_SPLITTER);
		
		return (stringSplit[POSITION_OF_INPUT].trim().contains(REVERSE_ALPHABETICAL_ORDER) ||
				stringSplit[POSITION_OF_INPUT].trim().contains(REVERSE_NUMERICAL_ORDER)) ? true : false;
	
	}

	private boolean isSortByAlphabetical(String userTask) {
		
		assert(userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_IS_ALPHABETICAL);
		
		stringSplit = userTask.toLowerCase().split(STRING_SPLITTER);
		
		return (stringSplit[POSITION_OF_INPUT].trim().contains(ALPHABETICAL_ORDER) ||
				stringSplit[POSITION_OF_INPUT].trim().contains(NUMERICAL_ORDER)) ? true : false;
	
	}

	private boolean isSortByDate(String userTask) {
		
		assert(userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_IS_BY_DATE);
		
		stringSplit = userTask.toLowerCase().split(STRING_SPLITTER);
		
		return (stringSplit[POSITION_OF_INPUT].trim().contains(DATE_ORDER)) ? true : false;
	
	}

	private String removeBy(String userTask) {
		
		assert(userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_BY);
		
		stringSplit = userTask.toLowerCase().split(STRING_SPLITTER);
		
		if (stringSplit[POSITION_OF_INPUT].contains(PREPOSITION_BY)) {
			userTask = userTask.replace(PREPOSITION_BY, STRING_EMPTY);
		}
		
		return userTask.trim();
	}
}
