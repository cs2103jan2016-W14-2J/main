//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Command.*;
import Logger.*;

public class DeleteParser {
	
	private static Logger logger;
	private DELETE_TYPE deleteType;
	private ArrayList<String> tagToDelete; 
	private ArrayList<Integer> indexToDelete;
	
	// Constants
	private static final String STRING_CHECKER_ALL = "all";
	private static final String STRING_CHECKER_TO = "to";
	private static final String STRING_CHECKER_HYPHEN = "-";
	private static final String STRING_HASH_TAG = "#";
	private static final String STRING_CHECKER_TAG = "tag";
	private static final String STRING_CHECKER_CATEGORY = "category";
	private static final String STRING_START_DATE = "start date";
	private static final String STRING_END_DATE = "end date";
	private static final String STRING_START_INDICATOR = "start";
	private static final String STRING_END_INDICATOR = "end";
	private static final String STRING_SPLITTER = "\\s+";
	private static final String STRING_EMPTY = "";
	private static final String PUNCTUATION_REMOVER = "[:.,]";
	private static final int FIRST_ELEMENT = 0;
	private static final int SECOND_ELEMENT = 2;
	private static final int NUM_ELEMENTS_IN_RANGE = 3;
	
	// Logging messages for methods and processes.
	private static final String LOGGER_MESSAGE_EXECUTE_DELETE_PARSER = "Delete Parser: Processing executeDeleteParser method.";
	private static final String LOGGER_MESSAGE_EXIT_DELETE_PARSER = "Delete Parser: Exiting executeDeleteParser method.";
	private static final String LOGGER_MESSAGE_PROCESS_DELETE_TYPE = "Delete Parser: Analysing delete type.";
	private static final String LOGGER_MESSAGE_DETERMINE_DELETE_TYPE = "Delete Parser: Processing delete type.";
	private static final String LOGGER_MESSAGE_IS_TASK_INDEX = "Delete Parser: Analysing if deleting tags, date or indexes.";
	private static final String LOGGER_MESSAGE_DELETE_ALL_INDEXES = "Delete Parser: Delete type is all indexes .";
	private static final String LOGGER_MESSAGE_DELETE_ALL_TAGS = "Delete Parser: Delete type is all tags .";
	private static final String LOGGER_MESSAGE_DELETE_SINGLE_INDEX = "Delete Parser: Delete type is singe index.";
	private static final String LOGGER_MESSAGE_DELETE_SINGLE_TAG = "Delete Parser: Delete type is single tag.";
	private static final String LOGGER_MESSAGE_DELETE_RANGE_INDEXES = "Delete Parser: Delete type is range indexes .";
	private static final String LOGGER_MESSAGE_DELETE_MULTIPLE_INDEXES = "Delete Parser: Delete type is multiple indexes .";
	private static final String LOGGER_MESSAGE_DELETE_MULTIPLE_TAGS = "Delete Parser: Delete type is multiple tags .";
	private static final String LOGGER_MESSAGE_DELETE_START_DATE = "Delete Parser: Delete type is start date .";
	private static final String LOGGER_MESSAGE_DELETE_END_DATE = "Delete Parser: Delete type is end date .";
	private static final String LOGGER_MESSAGE_DELETE_IS_INVALID = "Delete Parser: Delete type is invalid .";
	
	public DeleteParser() {
		this.tagToDelete = new ArrayList<String>();
		this.indexToDelete = new ArrayList<Integer>();
		logger = LoggerFile.getLogger();
	}

	/*
	 * This method executes the parsing of deletion.
	 * 
	 * @param commandUtil {@code CommandUtils} and userTask {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	
	protected CommandUtils executeDeleteParser(CommandUtils commandUtil, String userTask) {
		
		assert (userTask != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_EXECUTE_DELETE_PARSER);
		
		boolean isInteger = false;
		String[] str = userTask.replaceAll(PUNCTUATION_REMOVER, STRING_EMPTY).split(STRING_SPLITTER);
		isInteger = isTaskIndex(str);
		commandUtil = detemineDeleteType(commandUtil, userTask.toLowerCase(), isInteger);
		deleteType = commandUtil.getDeleteType();
		
		logger.log(Level.INFO, LOGGER_MESSAGE_EXIT_DELETE_PARSER);
		return processDeleteType(commandUtil, str);
	}

	/*
	 * This method process the input based on deletion type.
	 * 
	 * @param commandUtil {@code CommandUtils} and userTask {@code String[]}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	
	private CommandUtils processDeleteType(CommandUtils commandUtil, String[] str) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_DELETE_TYPE);
		
		switch (deleteType) {
		
			case SINGLE_INDEX:
				return parseSingleDeleteIndex(commandUtil,str);
			case SINGLE_TAG:
				return parseSingleDeleteTag(commandUtil,str);
			case MULTIPLE_INDEXES:
				return parseMultipleDeleteIndexes(commandUtil,str);
			case MULTIPLE_TAGS:
				return parseMutipleDeleteTags(commandUtil,str);
			case RANGE_INDEXES:
				return parseRangeDelete(commandUtil, str);
			case ALL_INDEXES:
				break;
			case ALL_TAGS:
				break;
			case START_DATE:
				return parseDeleteStartDate(commandUtil, str);
			case END_DATE:
				return parseDeleteEndDate(commandUtil, str);
			default:
				break;
		}
		return commandUtil;
	}
	

	/*
	 * This method determines the type of deletion.
	 * 
	 * @param commandUtil {@code CommandUtils}, userTask {@code String}
	 * 		  and isInteger {@code boolean}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils detemineDeleteType(CommandUtils commandUtil, String userTask, boolean isInteger) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_DETERMINE_DELETE_TYPE);
		
		if (checkIfDeleteAll(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_ALL_INDEXES);
			commandUtil.setDeleteType(DELETE_TYPE.ALL_INDEXES);
		}
		else if (checkIfDeleteAllTag(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_ALL_TAGS);
			commandUtil.setDeleteType(DELETE_TYPE.ALL_TAGS);
		}
		else if (checkIfDeleteSingleIndex(userTask) && isInteger == true){
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_SINGLE_INDEX);
			commandUtil.setDeleteType(DELETE_TYPE.SINGLE_INDEX);
		}
		else if (checkIfDeleteSingleTag(userTask) && isInteger == false) {
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_SINGLE_TAG);
			commandUtil.setDeleteType(DELETE_TYPE.SINGLE_TAG);	
		}
		else if (checkIfDeleteRange(userTask) && isInteger == true) {
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_RANGE_INDEXES);
			commandUtil.setDeleteType(DELETE_TYPE.RANGE_INDEXES);
		}
		else if (checkIfDeleteMultiple(userTask) && isInteger == true) {
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_MULTIPLE_INDEXES);
			commandUtil.setDeleteType(DELETE_TYPE.MULTIPLE_INDEXES);
		}
		else if (checkIfDeleteMultiple(userTask) && isInteger == false) {
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_MULTIPLE_TAGS);
			commandUtil.setDeleteType(DELETE_TYPE.MULTIPLE_TAGS);
		}
		else if(checkIfDeleteStartDate(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_START_DATE);
			commandUtil.setDeleteType(DELETE_TYPE.START_DATE);
		}
		else if(checkIfDeleteEndDate(userTask)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_END_DATE);
			commandUtil.setDeleteType(DELETE_TYPE.END_DATE);
		}
		else {
			logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_IS_INVALID);
			commandUtil.setDeleteType(DELETE_TYPE.INVALID);
		}
		return commandUtil;
	}

	/*
	 * This method checks if it is deleting tag(s) or index(es)
	 * 
	 * @param str {@code String[]}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean isTaskIndex(String[] str) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_IS_TASK_INDEX);
		return (str[FIRST_ELEMENT].startsWith(STRING_HASH_TAG)) ? false : true;
	}

	/*
	 * This method checks if it is deleting tag(s) or index(es)
	 * 
	 * @param commandUtil {@code CommandUtils} and str {@code String[]}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parseDeleteEndDate(CommandUtils commandUtil, String[] str) {
		
		if (!str[FIRST_ELEMENT].contains(STRING_END_INDICATOR)) {
			indexToDelete.add(Integer.parseInt(str[0]));
			commandUtil.setIndexToDelete(indexToDelete);
		}
		else {
			commandUtil.setDeleteType(DELETE_TYPE.INVALID);
		}
		return commandUtil;
	}

	/*
	 * This method processes the deletion of start date
	 * 
	 * @param commandUtil {@code CommandUtils} and str {@code String[]}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	
	private CommandUtils parseDeleteStartDate(CommandUtils commandUtil, String[] str) {
		
		if (!str[FIRST_ELEMENT].contains(STRING_START_INDICATOR)) {
			indexToDelete.add(Integer.parseInt(str[FIRST_ELEMENT]));
			commandUtil.setIndexToDelete(indexToDelete);
		}
		else {
			commandUtil.setDeleteType(DELETE_TYPE.INVALID);
		}
		return commandUtil;
	}

	/*
	 * This method processes the deletion of a range of indexes
	 * 
	 * @param commandUtil {@code CommandUtils} and str {@code String[]}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	
	private CommandUtils parseRangeDelete(CommandUtils commandUtil, String[] str) {
		// Example: delete 1 to 5 / delete 1 - 5
		if (str.length == NUM_ELEMENTS_IN_RANGE) {
			for (int i = Integer.parseInt(str[FIRST_ELEMENT]); i < Integer.parseInt(str[SECOND_ELEMENT]) + 1; i++) {
				indexToDelete.add(i);
			}
		}
		commandUtil.setIndexToDelete(indexToDelete);
		return commandUtil;
	}

	/*
	 * This method processes the deletion of multiple indexes
	 * 
	 * @param commandUtil {@code CommandUtils} and str {@code String[]}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	
	private CommandUtils parseMultipleDeleteIndexes(CommandUtils commandUtil, String[] str) {
		// Example: delete 1 4 9 14
		for (int i = 0; i < str.length; i++) {
			indexToDelete.add(Integer.parseInt(str[i]));
		}
		commandUtil.setIndexToDelete(indexToDelete);
		return commandUtil;
	}
	
	/*
	 * This method processes the deletion of multiple tags.
	 * 
	 * @param commandUtil {@code CommandUtils} and str {@code String[]}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	
	private CommandUtils parseMutipleDeleteTags(CommandUtils commandUtil, String[] str) {
		// Example: delete #nus #soc #singapore
		for (int i = 0; i < str.length; i++) {
			if (!str[i].contains(STRING_CHECKER_HYPHEN)) {
				tagToDelete.add(str[i].substring(1, str[i].length()));
			}
		}
		commandUtil.setTagToDelete(tagToDelete);
		return commandUtil;
	}

	/*
	 * This method processes deletion of a single tag
	 * 
	 * @param commandUtil {@code CommandUtils} and str {@code String[]}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	
	private CommandUtils parseSingleDeleteTag(CommandUtils commandUtil, String[] str) {
		// Example: delete #nus
		if (!str[FIRST_ELEMENT].contains(STRING_CHECKER_HYPHEN)) {
			tagToDelete.add(str[FIRST_ELEMENT].substring(1, str[FIRST_ELEMENT].length()));
			commandUtil.setTagToDelete(tagToDelete);
		}
		return commandUtil;
	}
	
	/*
	 * This method processes deletion of a single index
	 * 
	 * @param commandUtil {@code CommandUtils} and str {@code String[]}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parseSingleDeleteIndex(CommandUtils commandUtil, String[] str) {
		indexToDelete.add(Integer.parseInt(str[FIRST_ELEMENT]));
		commandUtil.setIndexToDelete(indexToDelete);
		return commandUtil;
	}

	/*
	 * This method checks if it is deleting an end date of a task.
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	
	private boolean checkIfDeleteEndDate(String userTask) {
		return (userTask.contains(STRING_END_DATE)) ? true : false;
	}
	
	/*
	 * This method checks if it is deleting a start date of a task.
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	
	private boolean checkIfDeleteStartDate(String userTask) {
		return (userTask.contains(STRING_START_DATE)) ? true : false;
	}

	/*
	 * This method checks if it is deleting all tags
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	
	private boolean checkIfDeleteAllTag(String userTask) {
		return (userTask.contains(STRING_CHECKER_ALL) && (userTask.contains(STRING_CHECKER_TAG) 
				|| userTask.contains(STRING_CHECKER_CATEGORY))) ? true : false;
	}


	/*
	 * This method checks if it is deleting all indexes
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean checkIfDeleteAll(String userTask) {
		return (userTask.contains(STRING_CHECKER_ALL) && !(userTask.contains(STRING_CHECKER_TAG) 
				|| userTask.contains(STRING_CHECKER_CATEGORY))) ? true : false;
	}


	/*
	 * This method checks if it is deleting a range of indexes or tags
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean checkIfDeleteRange(String userTask) {
		return ((userTask.contains(STRING_CHECKER_HYPHEN) && !userTask.contains(STRING_HASH_TAG))
				|| (userTask.contains(STRING_CHECKER_TO))) 
				? true : false;
	}


	/*
	 * This method checks if it is deleting multiple tags or indexes
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	
	private boolean checkIfDeleteMultiple(String userTask) {
		
		String[] str = userTask.toLowerCase().split(STRING_SPLITTER);	
		
		return (str.length > 1 && !checkIfDeleteRange(userTask) && 
				!checkIfDeleteEndDate(userTask) &&
				!checkIfDeleteStartDate(userTask)) ? true : false;
	
	}
	

	/*
	 * This method checks if it is deleting single indexes
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */

	private boolean checkIfDeleteSingleIndex(String userTask) {
		
		String[] str = userTask.toLowerCase().split(STRING_SPLITTER);	
		
		return (str.length == 1 && !userTask.contains(STRING_CHECKER_ALL)) ? true : false;
	
	}
	

	/*
	 * This method checks if it is deleting single tag.
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean checkIfDeleteSingleTag(String userTask) {
		
		String[] temp = userTask.split(STRING_SPLITTER);
		
		return (temp.length == 1 && temp[FIRST_ELEMENT].startsWith(STRING_HASH_TAG) 
				&& !temp[FIRST_ELEMENT].contains(STRING_CHECKER_HYPHEN)) ? true : false;
	
	}

}

