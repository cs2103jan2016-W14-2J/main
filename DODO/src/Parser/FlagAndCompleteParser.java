//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import Command.*;

public class FlagAndCompleteParser {
	
	private FLAGANDCOMPLETE_TYPE _type;
	private ArrayList<Integer> taskIndex; 
	
	// Constants
	private static final String STRING_SPLITTER = "\\s+";
	private static final String KEYWORD_ALL = "all";
	private static final String KEYWORD_TO = " to ";
	private static final String KEYWORD_DASH = " - ";
	private static final String KEYWORD_SPACE = " "; 
	private static final String PUNCTUATION_REMOVER = "[:.,]";
	private static final int SINGLE_INDEX = 1;
	
	public FlagAndCompleteParser() {
		this.taskIndex = new ArrayList<Integer>();
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
	
		String[] str = userInput.trim().replaceAll(PUNCTUATION_REMOVER, KEYWORD_SPACE).toLowerCase().split(STRING_SPLITTER);
		commandUtil = detemineFlagAndCompleteType(commandUtil, userInput.toLowerCase());
		_type = commandUtil.getFlagAndCompleteType();
		
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
		
		switch (_type) {
		
			case SINGLE:
				return parseSingle(commandUtil, str);
			case MULTIPLE:
				return parseMultiple(commandUtil, str);
			case RANGE:
				return parseRange(commandUtil, userInput);
			case ALL:
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
		
		if (temp.length == 2) {
			for (int i = Integer.parseInt(temp[0]); i < Integer.parseInt(temp[1]) + 1; i++) {
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
		
		taskIndex.add(Integer.parseInt(str[0]));
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
	private CommandUtils detemineFlagAndCompleteType(CommandUtils commandUtil, String userInput) {
		
		if (checkIfSingle(userInput)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.SINGLE);
		}
		else if (checkIfRange(userInput)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.RANGE);
		}
		else if (checkIfMultiple(userInput)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.MULTIPLE);
		}
		else if (checkIfAll(userInput)) {
			commandUtil.setFlagCompleteType(FLAGANDCOMPLETE_TYPE.ALL);
		}
		else {
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
		return (userInput.toLowerCase().contains(KEYWORD_ALL) && temp.length == 1) ? true: false;
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
		return (userInput.contains(KEYWORD_DASH) || (userInput.contains(KEYWORD_TO))) ? true : false;
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
		return (str.length > SINGLE_INDEX && !checkIfRange(userInput)) ? true : false;
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
		return (temp.length == 1 && !temp[0].toLowerCase().contains(KEYWORD_ALL)) ? true : false;
	}
}
