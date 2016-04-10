//@@author: A0125552L
package Parser;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Command.*;
import Logger.LoggerFile;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class SearchParser {
	
	private String tag;
	private SEARCH_TYPE searchType;
	private static Logger logger;
	
	// Constants
	private static final String PREPOSITION_BY = "by";
	private static final String PREPOSITION_BY_WITH_SPACE = "by";
	private static final String STRING_HASH_TAG = "#";
	private static final String STRING_SPLITTER = "\\s+";
	private static final String STRING_EMPTY = "";
	
	// Logging Message for processes
	private static final String LOGGER_MESSAGE_PROCESS_SEARCH_COMMAND = "SearchParser Class: Processing Search Command.";
	private static final String LOGGER_MESSAGE_EXIT_SEARCH_PARSER_CLASS = "SearchParser Class: Exiting SearchParser class." ;
	private static final String LOGGER_MESSAGE_ANALYSING_SEARCH_TYPE = "SearchParser Class: Analysing search type.";
	private static final String LOGGER_MESSAGE_PROCESS_SEARCH_DATE = "SearchParser Class: Processing search date.";
	private static final String LOGGER_MESSAGE_PROCESS_SEARCH_TAG = "SearchParser Class: Processing search tag.";
	private static final String LOGGER_MESSAGE_PROCESS_INVALID = "SearchParser Class: Process is invalid.";
	private static final String LOGGER_MESSAGE_PROCESS_DETERMINE_SEARCH_TYPE = "SearchParser Class: Determining serch type.";
	private static final String LOGGER_MESSAGE_PROCESS_IS_SEARCH_TASK = "SearchParser Class: Checking if search task type.";
	private static final String LOGGER_MESSAGE_PROCESS_IS_SEARCH_DATE = "SearchParser Class: Checking if search date type.";
	private static final String LOGGER_MESSAGE_PROCESS_IS_SEARCH_TAG = "SearchParser Class: Checking if search tag type.";
	private static final String LOGGER_MESSAGE_PROCESS_REMOVE_BY = "SearchParser Class: Removing \"by\".";

	public SearchParser() {
		logger = LoggerFile.getLogger();
	}

	/*
	 * This method process the Search command.
	 * 
	 * @param commandUtil {@code CommandUtils} and userTask {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	protected CommandUtils executeSearchParser(CommandUtils commandUtil, String userTask) {
	
		assert (userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_SEARCH_COMMAND);
		
		userTask = removeBy(userTask);
		commandUtil = determineSearchType(commandUtil, userTask);
		searchType = commandUtil.getSearchType();
		
		logger.log(Level.INFO, LOGGER_MESSAGE_EXIT_SEARCH_PARSER_CLASS);
		return analyseSearchType(commandUtil, userTask);
	}
	
	/*
	 * This method process the search type accordingly.
	 * 
	 * @param commandUtil {@code CommandUtils} and userTask {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */

	private CommandUtils analyseSearchType(CommandUtils commandUtil, String userTask) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_ANALYSING_SEARCH_TYPE);
		
		switch(searchType) {
			
		case BY_DATE:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_SEARCH_DATE);
			return processByDate(commandUtil, userTask);
		
		case BY_TASK:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_SEARCH_DATE);
			return processByTask(commandUtil, userTask);
		
		case BY_TAG:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_SEARCH_TAG);
			return processByTag(commandUtil, userTask);
		
		default:
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_INVALID);
			commandUtil.setSearchType(SEARCH_TYPE.INVALID);
			break;
		}
		
		return commandUtil;
	}

	/*
	 * This method processes the search task type.
	 * 
	 * @param commandUtil {@code CommandUtils} and userTask {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils processByTask(CommandUtils commandUtil, String userTask) {
		commandUtil.setSearchByTask(userTask.trim());
		return commandUtil;
	}

	/*
	 * This method processes the search tag type.
	 * 
	 * @param commandUtil {@code CommandUtils} and userTask {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils processByTag(CommandUtils commandUtil, String userTask) {
		
		tag = userTask.trim().substring(1, userTask.length());
		commandUtil.setSearchByTag(tag);
		
		return commandUtil;
	}

	/*
	 * This method processes the search date type.
	 * 
	 * @param commandUtil {@code CommandUtils} and userTask {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils processByDate(CommandUtils commandUtil, String userTask) {
		
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		commandUtil.setSearchByDate(dates.get(0));
		
		return commandUtil;
	}

	/*
	 * This method analyses the search type.
	 * 
	 * @param commandUtil {@code CommandUtils} and userTask {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils determineSearchType(CommandUtils commandUtil, String userTask) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_DETERMINE_SEARCH_TYPE);
		
		if (isSearchDate(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_IS_SEARCH_DATE);
			commandUtil.setSearchType(SEARCH_TYPE.BY_DATE);
		}
		else if (isSearchTaskName(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_IS_SEARCH_TASK);
			commandUtil.setSearchType(SEARCH_TYPE.BY_TASK);
		}
		else if (isSearchTag(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_IS_SEARCH_TAG);
			commandUtil.setSearchType(SEARCH_TYPE.BY_TAG);
		}
		else {
			logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_INVALID);
			commandUtil.setSearchType(SEARCH_TYPE.INVALID);
		}
		return commandUtil;
	}

	/*
	 * This method checks if search tag type.
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean isSearchTag(String userTask) {
		assert userTask.split(STRING_SPLITTER).length == 1;
		return (userTask.startsWith(STRING_HASH_TAG)) ? true : false;
	}

	/*
	 * This method checks if search date type.
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean isSearchDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		return (dates.size() != 0) ? true : false;
	}

	/*
	 * This method checks if search task name type.
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean isSearchTaskName(String userTask) {
		return (!userTask.startsWith(STRING_HASH_TAG)) ? true : false;
	}

	/*
	 * This method removes the "by" after search command
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code String} with the "by after search command removed.
	 * 			
	 * 
	 */
	private String removeBy(String userTask) {
		
		assert (userTask.length() > 0);
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_REMOVE_BY);
		
		String[] str = userTask.toLowerCase().split(STRING_SPLITTER);
		
		if (str[0].contains(PREPOSITION_BY)) {
			userTask.replace(PREPOSITION_BY_WITH_SPACE, STRING_EMPTY);
		}
		
		return userTask;
	}
		
}
