//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Command.*;
import Logger.LoggerFile;

public class FlagAndCompleteParser {
	
	private static Logger logger;
	private FLAGANDCOMPLETE_TYPE _type;
	private ArrayList<Integer> taskIndex; 
	
	// Constants
	private static final String STRING_SPLITTER = "\\s+";
	private static final String KEYWORD_ALL = "all";
	private static final String KEYWORD_TO = " to ";
	private static final String KEYWORD_DASH = " - ";
	private static final String KEYWORD_SPACE = " "; 
	private static final String PUNCTUATION_REMOVER = "[:.,]";
	private static final int NUM_SINGLE_INDEX = 1;
	private static final int NUM_FIRST_ELEMENT = 0;
	private static final int NUM_TWO_ELEMENTS = 2;
	
	// Logging messages for proccesses.
	private static final String LOGGER_MESSAGE_PROCESS_FLAG_COMPLETE_COMMAND = "FlagAndComplete class: Entering FlagAndComplete class.";
	private static final String LOGGER_MESSAGE_EXIT_FLAG_COMPLETE_PARSER = "FlagAndComplete class: Exiting FlagAndComplete class.";
	private static final String LOGGER_MESSAGE_ANALYSE_FLAG_COMPLETE_TYPE = "FlagAndComplete class: Analysing FlagAndComplete type.";
	private static final String LOGGER_MESSAGE_PROCESS_SINGLE_FLAG_COMPLETE = "FlagAndComplete class: Process single input.";
	private static final String LOGGER_MESSAGE_PROCESS_MULTIPLE_FLAG_COMPLETE = "FlagAndComplete class: Process multiple inputs.";
	private static final String LOGGER_MESSAGE_PROCESS_RANGE_FLAG_COMPLETE = "FlagAndComplete class: Process range inputs.";
	private static final String LOGGER_MESSAGE_PROCESS_ALL_FLAG_COMPLETE = "FlagAndComplete class: Process all inputs.";
	private static final String LOGGER_MESSAGE_PROCESS_DETERMINE_FLAG_COMPLETE_METHOD = "FlagAndComplete class: Determine flag and complete method.";
	private static final String LOGGER_MESSAGE_CHECK_IF_SINGLE = "FlagAndComplete class: Check if single input.";
	private static final String LOGGER_MESSAGE_CHECK_IF_RANGE = "FlagAndComplete class: Check if range inputs.";
	private static final String LOGGER_MESSAGE_CHECK_IF_MULTIPLE = "FlagAndComplete class: Check if multiple inputs.";
	private static final String LOGGER_MESSAGE_CHECK_IF_ALL = "FlagAndComplete class: Check if all inputs.";
	private static final String LOGGER_MESSAGE_INVALID = "FlagAndComplete class: Invalid FlagAndComplete type.";
	
	public FlagAndCompleteParser() {
		
		this.taskIndex = new ArrayList<Integer>();
		logger = LoggerFile.getLogger();
	}

	/*
	 * This method process the flag and complete command.
	 * 
	 * @param commandUtil {@code CommandUtils} and ususerTaskIndexerInput {@code String}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	protected CommandUtils executeFlagCompleteParser(CommandUtils commandUtil, String userInput) {
		
		assert (userInput != null);
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_FLAG_COMPLETE_COMMAND);
		
		String[] str = userInput.trim().replaceAll(PUNCTUATION_REMOVER, KEYWORD_SPACE).toLowerCase().split(STRING_SPLITTER);
		commandUtil = determineFlagAndCompleteType(commandUtil, userInput.toLowerCase());
		_type = commandUtil.getFlagAndCompleteType();
		
		logger.log(Level.INFO, LOGGER_MESSAGE_EXIT_FLAG_COMPLETE_PARSER);
		return processFlagAndCompleteType(commandUtil, userInput, str);
	}
	
	/*
	 * This method process the flag and complete type".
	 * 
	 * @param commandUtil {@code CommandUtils}, userInput {@code String}
	 * 		  str {@code String[]}
	 * 
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils processFlagAndCompleteType(CommandUtils commandUtil, String userInput, String[] str) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_ANALYSE_FLAG_COMPLETE_TYPE);
		
		switch (_type) {
		
			case SINGLE:
				logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_SINGLE_FLAG_COMPLETE);
				return parseSingle(commandUtil, str);
			
			case MULTIPLE:
				logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_MULTIPLE_FLAG_COMPLETE);
				return parseMultiple(commandUtil, str);
			
			case RANGE:
				logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_RANGE_FLAG_COMPLETE);
				return parseRange(commandUtil, userInput);
			
			case ALL:
				logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_ALL_FLAG_COMPLETE);
				break;
			
			default:
				break;
		}
		
		return commandUtil;
	}
	
	/*
	 * This method process a range of flag/complete inputs.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 *
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parseRange(CommandUtils commandUtil, String userInput) {
		
		if (userInput.contains(KEYWORD_DASH)) {
			userInput = userInput.replace(KEYWORD_DASH, KEYWORD_SPACE);
		}
		else if (userInput.contains(KEYWORD_TO)) {
			userInput = userInput.replace(KEYWORD_TO, KEYWORD_SPACE);
		}
		
		String[] temp = userInput.split(STRING_SPLITTER);
		
		if (temp.length == NUM_TWO_ELEMENTS) {
			for (int i = Integer.parseInt(temp[NUM_FIRST_ELEMENT]); i < Integer.parseInt(temp[1]) + 1; i++) {
				taskIndex.add(i);
			}
		}
		
		commandUtil.setTaskToFlagAndMark(taskIndex);
		return commandUtil;
	}

	/*
	 * This method process multiple flag/complete inputs.
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 *
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parseMultiple(CommandUtils commandUtil, String[] str) {
		
		for (int i = 0; i < str.length; i++) {
			taskIndex.add(Integer.parseInt(str[i]));
		}
		
		commandUtil.setTaskToFlagAndMark(taskIndex);
		return commandUtil;
	}
	
	/*
	 * This method process single flag/complete input.
	 * 
	 * @param commandUtil {@code CommandUtils} and str {@code String[]}
	 *
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils parseSingle(CommandUtils commandUtil, String[] str) {
		
		taskIndex.add(Integer.parseInt(str[NUM_FIRST_ELEMENT]));
		commandUtil.setTaskToFlagAndMark(taskIndex);
		
		return commandUtil;
	}

	/*
	 * This method determines the type of FlagAndComplete type. 
	 * 
	 * @param commandUtil {@code CommandUtils} and userInput {@code String}
	 *
	 * @return {@code CommandUtils}
	 * 			
	 * 
	 */
	private CommandUtils determineFlagAndCompleteType(CommandUtils commandUtil, String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_DETERMINE_FLAG_COMPLETE_METHOD);
		
		if (checkIfSingle(userInput)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_IF_SINGLE);
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.SINGLE);
		}
		else if (checkIfRange(userInput)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_IF_RANGE);
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.RANGE);
		}
		else if (checkIfMultiple(userInput)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_IF_MULTIPLE);
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.MULTIPLE);
		}
		else if (checkIfAll(userInput)) {
			logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_IF_ALL);
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.ALL);
		}
		else {
			logger.log(Level.INFO, LOGGER_MESSAGE_INVALID);
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.INVALID);
		}
		
		return commandUtil;
	}

	/*
	 * This method checks if it is a flag/complete all type. 
	 * 
	 * @param userInput {@code String}
	 *
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	private boolean checkIfAll(String userInput) {
		
		String[] temp = userInput.split(STRING_SPLITTER);
		
		return (userInput.toLowerCase().contains(KEYWORD_ALL) && 
				temp.length == NUM_SINGLE_INDEX) ? true: false;
	}

	/*
	 * This method checks if it is a flag/complete range type. 
	 * 
	 * @param userInput {@code String}
	 *
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	
	private boolean checkIfRange(String userInput) {
		
		return (userInput.contains(KEYWORD_DASH) || (userInput.contains(KEYWORD_TO))) 
				? true : false;
	}

	/*
	 * This method checks if it is a flag/complete multiple type. 
	 * 
	 * @param userInput {@code String}
	 *
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	
	private boolean checkIfMultiple(String userInput) {
		String[] str = userInput.replaceAll(PUNCTUATION_REMOVER, KEYWORD_SPACE).toLowerCase().split(STRING_SPLITTER);
		return (str.length > NUM_SINGLE_INDEX && !checkIfRange(userInput)) ? true : false;
	}
	
	/*
	 * This method checks if it is a flag/complete single type. 
	 * 
	 * @param userInput {@code String}
	 *
	 * @return {@code boolean}
	 * 			
	 * 
	 */
	
	private boolean checkIfSingle(String String) {
		String[] temp = String.split(STRING_SPLITTER);
		return (temp.length == NUM_SINGLE_INDEX && !temp[NUM_FIRST_ELEMENT].toLowerCase().contains(KEYWORD_ALL)) 
				? true : false;
	}
}
