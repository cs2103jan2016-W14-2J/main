//@@author: A0125552L
package Parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Command.*;
import Task.*;

public class Parser {

	private HashMap<String, COMMAND_TYPE> possibleCommandErrors;	
	
	protected TASK_TYPE taskType;
	protected COMMAND_TYPE command;
	protected String taskName;
	protected Date startTime;
	protected Date endTime;
	protected int taskID;
	protected String tag;
	private String _addCommand = "add ";
	protected boolean isImportant;
	
	private ArrayList<String> tags;
	private String SYMBOL_HASH_TAG = "#";
	private String SYMBOL_DASH = "-";
	private String SYMBOL_EXCLAMATION_MARK = "!";
	private CommandUtils commandUtil;
	
	public Parser() {
		possibleCommandErrors = new HashMap<String, COMMAND_TYPE>();		
	}
	
	/*
	 *Returns the command type.
	 *@param userInput
	 *@return command type in enum format. 
	 */
	
	public CommandUtils executeCommand(CommandUtils commandUtil, String userInput) {
		assert userInput.length() > 0;
		this.commandUtil = commandUtil;
		userInput = userInput.trim();
		checkIfValidUserInput(userInput);
	
		String command = getUserCommand(userInput);
		COMMAND_TYPE commandType = determineCommandType(command);
		if (commandType == COMMAND_TYPE.ADD && !userInput.toLowerCase().contains(_addCommand)) {
			userInput = _addCommand + " " + userInput;
		}
		
		switch (commandType) {
			
			case ADD:
				userInput = processUserInput(commandUtil, userInput);
				AddParser addParser = new AddParser();
				return addParser.executeAddParser(commandUtil, userInput);
			case DELETE:
				userInput = getUserInputContent(userInput);
				DeleteParser deleteParser = new DeleteParser();
				return deleteParser.executeDeleteParser(commandUtil, userInput);
			case EDIT:
				userInput = getUserInputContent(userInput);
				EditParser editParser = new EditParser();
				return editParser.executeEditParser(commandUtil, userInput);
				
			case COMPLETE:
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser completeParser = new FlagAndCompleteParser();
				return completeParser.executeFlagCompleteParser(commandUtil, userInput);
			case FLAG:
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser flagParser = new FlagAndCompleteParser();
				return flagParser.executeFlagCompleteParser(commandUtil, userInput);
			case UNFLAG:
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser unflagParser = new FlagAndCompleteParser();
				return unflagParser.executeFlagCompleteParser(commandUtil, userInput);
			case TAG:
				userInput = processUserInput(commandUtil, userInput);
				commandUtil.setTaskID(userInput.trim());
				break;
			case UNTAG:
				userInput = processUserInput(commandUtil, userInput);
				commandUtil.setTaskID(userInput.trim());
				break;
			case SEARCH:
				userInput = getUserInputContent(userInput);
				SearchParser search = new SearchParser();
				return search.executeSearchParser(commandUtil, userInput);
			case SORT:
				userInput = getUserInputContent(userInput);
				SortParser sort = new SortParser();
				return sort.determineSortType(commandUtil, userInput);
			default:
				break;
		}
		return commandUtil;
	}
	
	private String processUserInput(CommandUtils commandUtil, String userInput) {
		String userTask = getUserInputContent(userInput);
		userTask = checkTaskImportance(commandUtil, userTask);
		return extractTag(commandUtil, userTask);	
	}

	private void checkIfValidUserInput(String userInput) {
		if (userInput.isEmpty()) {
			commandUtil.setCommandType(COMMAND_TYPE.INVALID);
		}
	}

	public COMMAND_TYPE determineCommandType(String commandType) {
		FlexiCommand flexiCommand = new FlexiCommand();
		possibleCommandErrors = flexiCommand.getKeywordsDataBase();

		if(possibleCommandErrors.containsKey(commandType)) {
			this.command = possibleCommandErrors.get(commandType);
			commandUtil.setCommandType(this.command);
			return command;
		}
		else {
			this.command = COMMAND_TYPE.ADD;
			commandUtil.setCommandType(this.command);
			return command;
		}
	}
	
	/*
	 * Returns the command type of the string
	 * @param userInput
	 * @return command type of the string
	 */
	private String getUserCommand(String userInput) {
		String[] temp = userInput.split("\\s+", 2);
		return temp[0].toLowerCase();
	}
	
	private String getUserInputContent(String userInput) {
		String[] temp = userInput.split("\\s+", 2);
		return temp[1];
	}
	
	private String checkTaskImportance(CommandUtils commandUtil, String userInput) {
		if (userInput.trim().endsWith(SYMBOL_EXCLAMATION_MARK)) {
			userInput = userInput.replace(SYMBOL_EXCLAMATION_MARK, "");
			commandUtil.setTaskImportance(true);
		}
		else {
			commandUtil.setTaskImportance(false);
		}
		return userInput;
	}
	
	private String extractTag(CommandUtils commandUtil, String userTask) {
		tags = new ArrayList<String>();
		String[] str = userTask.split("[\\s+]");
		String temp = "";
		for (int i = 0; i < str.length; i++) {
			if (str[i].contains(SYMBOL_HASH_TAG) && !str[i].contains(SYMBOL_DASH)) {
				tags.add(str[i].replace(SYMBOL_HASH_TAG, "").trim());
				str[i] = " ";
			}
			else {
				temp += str[i] + " ";
			}
		}
		commandUtil.setTaskTag(tags);
		return temp.trim();
	}

}
