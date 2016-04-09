//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.*;

import Command.*;
import Task.*;

public class Parser {

	private HashMap<String, COMMAND_TYPE> possibleCommandErrors;	
	private ArrayList<String> tags;
	private static Logger logger;
	
	protected TASK_TYPE taskType;
	protected COMMAND_TYPE command;
	protected String taskName;
	protected Date startTime;
	protected Date endTime;
	protected int taskID;
	protected String tag;
	private String _addCommand = "add ";
	protected boolean isImportant;
	private CommandUtils commandUtil;
	
	// Constants
	private final String SYMBOL_HASH_TAG = "#";
	private final String SYMBOL_DASH = "-";
	private final String SYMBOL_EXCLAMATION_MARK = "!";
	private final String SYMBOL_SPACING = " ";
	private final String SYMBOL_EMPTY = "";
	private final String STRING_SPLITTER = "\\s+";
	
	private final int POSITION_OF_TASK_CONTENT = 1;
	private final int POSITION_OF_COMMAND = 0;
	
	// Logging messages for processing commands.
	private final String LOGGER_MESSAGE_ADD_COMMAND = "Processing Add Command.";
	private final String LOGGER_MESSAGE_DELETE_COMMAND = "Processing Delete Command.";
	private final String LOGGER_MESSAGE_EDIT_COMMAND = "Processing Edit Command.";
	private final String LOGGER_MESSAGE_COMPLETE_COMMAND = "Processing Complete Command.";
	private final String LOGGER_MESSAGE_FLAG_COMMAND = "Processing Flag Command.";
	private final String LOGGER_MESSAGE_UNFLAG_COMMAND = "Processing Unflag Command.";
	private final String LOGGER_MESSAGE_TAG_COMMAND = "Processing Tag Command.";
	private final String LOGGER_MESSAGE_UNTAG_COMMAND = "Processing Untag Command.";
	private final String LOGGER_MESSAGE_SEARCH_COMMAND = "Processing Search Command.";
	private final String LOGGER_MESSAGE_SORT_COMMAND = "Processing Sort Command.";
	
	// Logging messages for processing methods.
	private final String LOGGER_MESSAGE_EXECUTE_COMMAND = "Parser Class: Processing user input in executeCommand method.";
	private final String LOGGER_MESSAGE_VERIFY_ADD_COMMAND = "Parser Class: Processing user input in verifyIfAddCommand method.";
	private final String LOGGER_MESSAGE_PROCESS_USER_INPUT = "Parser Class: Processing user input in processUserInput method.";
	private final String LOGGER_MESSAGE_CHECK_IF_VALID_INPUT = "Parser Class: Processing user input in checkIfValidUserInput method.";
	private final String LOGGER_MESSAGE_DETERMINE_COMMAND_TYPE = "Parser Class: Processing user input in determineCommandType method.";
	private final String LOGGER_MESSAGE_GET_USER_COMMAND = "Parser Class: Processing user input in getUserCommand method.";
	private final String LOGGER_MESSAGE_GET_USER_INPUT = "Parser Class: Processing user input in getUserInput method.";
	private final String LOGGER_MESSAGE_CHECK_IMPORTANCE = "Parser Class: Processing user input in checkTaskImportance method.";
	private final String LOGGER_MESSAGE_EXTRACT_TAG = "Parser Class: Processing user input in extractTag method.";
	
	public Parser() {
		possibleCommandErrors = new HashMap<String, COMMAND_TYPE>();
		logger = Logger.getLogger("Parser");
	}
	
	/*
	 *Returns the command type.
	 *@param userInput
	 *@return command type in enum format. 
	 */
	
	public CommandUtils executeCommand(CommandUtils commandUtil, String userInput) {
		
		assert userInput.length() > 0;
		logger.log(Level.INFO, LOGGER_MESSAGE_EXECUTE_COMMAND);
		
		this.commandUtil = commandUtil;
		checkIfValidUserInput(userInput.trim());
		String command = getUserCommand(userInput);
		COMMAND_TYPE commandType = determineCommandType(command);
		userInput = verifyIfAddCommand(userInput, commandType);
		
		switch (commandType) {
			
			case ADD:
				logger.log(Level.INFO, LOGGER_MESSAGE_ADD_COMMAND);
				userInput = processUserInput(commandUtil, userInput);
				AddParser addParser = new AddParser();
				return addParser.executeAddParser(commandUtil, userInput);
				
			case DELETE:
				logger.log(Level.INFO, LOGGER_MESSAGE_DELETE_COMMAND);
				userInput = getUserInputContent(userInput);
				DeleteParser deleteParser = new DeleteParser();
				return deleteParser.executeDeleteParser(commandUtil, userInput);
				
			case EDIT:
				logger.log(Level.INFO, LOGGER_MESSAGE_EDIT_COMMAND);
				userInput = getUserInputContent(userInput);
				EditParser editParser = new EditParser();
				return editParser.executeEditParser(commandUtil, userInput);
				
			case COMPLETE:
				logger.log(Level.INFO, LOGGER_MESSAGE_COMPLETE_COMMAND);
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser completeParser = new FlagAndCompleteParser();
				return completeParser.executeFlagCompleteParser(commandUtil, userInput);
				
			case FLAG:
				logger.log(Level.INFO, LOGGER_MESSAGE_FLAG_COMMAND);
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser flagParser = new FlagAndCompleteParser();
				return flagParser.executeFlagCompleteParser(commandUtil, userInput);
				
			case UNFLAG:
				logger.log(Level.INFO, LOGGER_MESSAGE_UNFLAG_COMMAND);
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser unflagParser = new FlagAndCompleteParser();
				return unflagParser.executeFlagCompleteParser(commandUtil, userInput);
				
			case TAG:
				logger.log(Level.INFO, LOGGER_MESSAGE_TAG_COMMAND);
				userInput = processUserInput(commandUtil, userInput);
				commandUtil.setTaskID(userInput.trim());
				break;
				
			case UNTAG:
				logger.log(Level.INFO, LOGGER_MESSAGE_UNTAG_COMMAND);
				userInput = processUserInput(commandUtil, userInput);
				commandUtil.setTaskID(userInput.trim());
				break;
				
			case SEARCH:
				logger.log(Level.INFO, LOGGER_MESSAGE_SEARCH_COMMAND);
				userInput = getUserInputContent(userInput);
				SearchParser search = new SearchParser();
				return search.executeSearchParser(commandUtil, userInput);
				
			case SORT:
				logger.log(Level.INFO, LOGGER_MESSAGE_SORT_COMMAND);
				userInput = getUserInputContent(userInput);
				SortParser sort = new SortParser();
				return sort.determineSortType(commandUtil, userInput);
				
			default:
				break;
		}
		
		return commandUtil;
	}

	private String verifyIfAddCommand(String userInput, COMMAND_TYPE commandType) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_VERIFY_ADD_COMMAND);	
		
		if (commandType == COMMAND_TYPE.ADD && !userInput.toLowerCase().contains(_addCommand)) {
			userInput = _addCommand + SYMBOL_SPACING + userInput;
		}
		
		return userInput;
	}
	
	private String processUserInput(CommandUtils commandUtil, String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_USER_INPUT);
		String userTask = getUserInputContent(userInput);
		userTask = checkTaskImportance(commandUtil, userTask);
	
		return extractTag(commandUtil, userTask);	
	}

	private void checkIfValidUserInput(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_IF_VALID_INPUT);
		
		if (userInput.isEmpty()) {
			commandUtil.setCommandType(COMMAND_TYPE.INVALID);
		}
		
	}

	public COMMAND_TYPE determineCommandType(String commandType) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_DETERMINE_COMMAND_TYPE);
		
		FlexiCommand flexiCommand = new FlexiCommand();
		possibleCommandErrors = flexiCommand.getKeywordsDataBase();

		if(possibleCommandErrors.containsKey(commandType)) {
			this.command = possibleCommandErrors.get(commandType);
		}
		else {
			this.command = COMMAND_TYPE.ADD;
		}
		
		commandUtil.setCommandType(this.command);
		return command;
	}
	
	/*
	 * Returns the command type of the string
	 * @param userInput
	 * @return command type of the string
	 */
	private String getUserCommand(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_GET_USER_COMMAND);
		String[] temp = userInput.split(STRING_SPLITTER, 2);
		
		return temp[POSITION_OF_COMMAND].toLowerCase();
	}
	
	private String getUserInputContent(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_GET_USER_INPUT);
		String[] temp = userInput.split(STRING_SPLITTER, 2);
		
		return temp[POSITION_OF_TASK_CONTENT];
	}
	
	private String checkTaskImportance(CommandUtils commandUtil, String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_IMPORTANCE);
		
		String[] temp = userInput.split(STRING_SPLITTER);
		int positionOfImportance = temp.length;
		
		if (temp[positionOfImportance - 1].contains(SYMBOL_EXCLAMATION_MARK) 
			&& temp[positionOfImportance - 1].length() == 1) {
			
			userInput = userInput.replace(SYMBOL_EXCLAMATION_MARK, SYMBOL_EMPTY);
			commandUtil.setTaskImportance(true);
		
		}
		else {
			commandUtil.setTaskImportance(false);
		}
		
		return userInput;
	}
	
	private String extractTag(CommandUtils commandUtil, String userTask) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_EXTRACT_TAG);
		
		tags = new ArrayList<String>();
		String[] str = userTask.split(STRING_SPLITTER);
		String temp = "";
		
		for (int i = 0; i < str.length; i++) {
			
			if (str[i].contains(SYMBOL_HASH_TAG) && !str[i].contains(SYMBOL_DASH)) {
				tags.add(str[i].replace(SYMBOL_HASH_TAG, SYMBOL_EMPTY).trim());
				str[i] = SYMBOL_EMPTY;
			}
			else {
				temp += str[i] + SYMBOL_SPACING;
			}
		}
		
		commandUtil.setTaskTag(tags);
		return temp.trim();
	}

}
