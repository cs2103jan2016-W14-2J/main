//@@author: A0125552L

package Parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import Command.*;
import Logger.*;

public class SortParser {
	
	private static Logger logger;
	private static final String ALPHABETICAL_ORDER = "abc";
	private static final String NUMERICAL_ORDER = "123";
	private static final String REVERSE_ALPHABETICAL_ORDER = "cba";
	private static final String REVERSE_NUMERICAL_ORDER = "321";
	private static final String DATE_ORDER = "date";
	private static final String PREPOSITION_BY = "by";
	private static final int POSITION_OF_INPUT  = 0;
	private static final String STRING_SPLITTER = "\\s+";
	private static final String STRING_EMPTY = "";
	
	// Variable
	private String[] stringSplit;
	
	// Logging messages for methods
	private static final String LOGGER_MESSAGE_DETERMINE_SORT_TYPE = "SortParser Class: Processing user input in determineSortType method.";
	private static final String LOGGER_MESSAGE_IS_REVERSE_ALPHABETICAL = "SortParser Class: Processing user input in isSortByReverseAlphabetical method.";
	private static final String LOGGER_MESSAGE_IS_ALPHABETICAL = "SortParser Class: Processing user input in isSortByAlphabetical method.";
	private static final String LOGGER_MESSAGE_IS_BY_DATE = "SortParser Class: Processing user input in isSortByDate method.";
	private static final String LOGGER_MESSAGE_REMOVE_BY = "SortParser Class: Processing user input in removeBy method.";
	private static final String LOGGER_MESSAGE_IS_SORT_DATE = "SortParser Class: Sort type is BY_DATE.";
	private static final String LOGGER_MESSAGE_IS_SORT_ASCENDING = "SortParser Class: Sort type is BY_ASCENDING.";
	private static final String LOGGER_MESSAGE_IS_SORT_DESCENDING = "SortParser Class: Sort type is BY_DESCENDING.";
	private static final String LOGGER_MESSAGE_IS_INVALID_SORT = "SortParser Class: Sort type is INVALID.";
	
			
	public SortParser() {
		logger = LoggerFile.getLogger();
	}
	
	/*
	 * This method process the Sort command.
	 * 
	 * @param commandUtil {@code CommandUtils} and userTask {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
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
	
	/*
	 * This method checks if it is a sort by descending order type.
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean isSortByReverseAlphabetical(String userTask) {
		
		assert(userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_IS_REVERSE_ALPHABETICAL);
		
		stringSplit = userTask.toLowerCase().split(STRING_SPLITTER);
		
		return (stringSplit[POSITION_OF_INPUT].trim().contains(REVERSE_ALPHABETICAL_ORDER) ||
				stringSplit[POSITION_OF_INPUT].trim().contains(REVERSE_NUMERICAL_ORDER)) ? true : false;
	
	}
	
	/*
	 * This method checks if it is a sort by ascending order type.
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean isSortByAlphabetical(String userTask) {
		
		assert(userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_IS_ALPHABETICAL);
		
		stringSplit = userTask.toLowerCase().split(STRING_SPLITTER);
		
		return (stringSplit[POSITION_OF_INPUT].trim().contains(ALPHABETICAL_ORDER) ||
				stringSplit[POSITION_OF_INPUT].trim().contains(NUMERICAL_ORDER)) ? true : false;
	
	}

	/*
	 * This method checks if it is a sort by date type.
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean isSortByDate(String userTask) {
		
		assert(userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_IS_BY_DATE);
		
		stringSplit = userTask.toLowerCase().split(STRING_SPLITTER);
		
		return (stringSplit[POSITION_OF_INPUT].trim().contains(DATE_ORDER)) ? true : false;
	
	}
	
	/*
	 * This method removes the "by" after sort command
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code String} with the "by after sort command removed.
	 * 			
	 * 
	 */
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
