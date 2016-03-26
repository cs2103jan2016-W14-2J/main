package Parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Arrays;

import Command.*;
import Task.*;

/*
 * @author Pay Hao Jie
 */

public class Parser {

	private static String MESSAGE_ERROR_READING_COMMAND_TYPE = "You have entered the wrong command. Kindly enter a valid input.";
	private HashMap<String, COMMAND_TYPE> possibleCommandErrors = new HashMap<String, COMMAND_TYPE>();	
	
	private final String MESSAGE_INPUT_ERROR = "You have entered an invalid input.";
	
	private TASK_TYPE taskType;
	private COMMAND_TYPE command;
	private String taskName;
	private Date startTime;
	private Date endTime;
	private int taskID;
	private String tag;
	private boolean isImportant;
	
	private DELETE_TYPE deleteType;
	private ArrayList<String> tagToDelete;
	private ArrayList<Integer> indexToDelete;
	private ArrayList<String> taskItems;
	
	private ArrayList<Integer> indexOfFlagAndMark;
	private FLAGANDCOMPLETE_TYPE flagAndCompleteType;
	
	private String _commandAdd = "add";
	private EDIT_TYPE editType;
	private SEARCH_TYPE searchType;
	private String searchByTask;
	private Date searchByDate;
	private String searchByTag;
	
	public Parser() {
		
	}
	// update
	public Parser (String userInput) {
		assert userInput.length() > 0;
		executeCommand(userInput);
	}
	
	/*
	 *Returns the command type.
	 *@param userInput
	 *@return command type in enum format. 
	 */
	
	protected void executeCommand(String userInput) {
	
		userInput = userInput.trim();
		checkIfValidUserInput(userInput);
	
		String command = getUserCommand(userInput);
		COMMAND_TYPE commandType = determineCommandType(command);
		
		// concatenate add command in front for processing
		if (commandType == COMMAND_TYPE.ADD && !userInput.contains(_commandAdd)) {
			userInput = _commandAdd + " " + userInput;
		}
		
		System.out.println("DEBUG executeCommand @line97" + commandType);
		
		switch (commandType) {
			
			case ADD:
				userInput = processUserInput(userInput);
				AddParser addParser = new AddParser(userInput);
				setAddAttributes(addParser.getStartTime(), addParser.getEndTime(), addParser.getTaskName(), addParser.getTaskType());
				break;
			case DELETE:
				userInput = getUserInputContent(userInput);
				DeleteParser deleteParser = new DeleteParser(userInput);
				setDeleteAttributes(deleteParser.getDeleteType(), deleteParser.getTagToDelete(), deleteParser.getIndexToDelete());
				break;
			case EDIT:
				userInput = processUserInput(userInput);
				EditParser editParser = new EditParser(userInput);
				setEditAttributes(editParser.getTaskID(), editParser.getEndNewDate(), editParser.getStartNewDate(),
								  editParser.getNewTaskName(), editParser.getEditType());
				break;
			case COMPLETE:
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser completeParser = new FlagAndCompleteParser(userInput);
				setFlagAndCompleteAttributes(completeParser.getFlagCompleteType(), completeParser.getTaskIndex());
				break;
			case FLAG:
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser flagParser = new FlagAndCompleteParser(userInput);
				setFlagAndCompleteAttributes(flagParser.getFlagCompleteType(), flagParser.getTaskIndex());
				break;
			case UNFLAG:
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser unflagParser = new FlagAndCompleteParser(userInput);
				setFlagAndCompleteAttributes(unflagParser.getFlagCompleteType(), unflagParser.getTaskIndex());
				break;
			case TAG:
				userInput = getUserInputContent(userInput);
				setTaskIndex(userInput);
				break;
			case UNTAG:
				setTaskIndex(userInput);
				break;
			case UNDO:
				setCommandType(COMMAND_TYPE.UNDO);
				break;
			case REDO:
				setCommandType(COMMAND_TYPE.REDO);
				break;
			case SEARCH:
				userInput = getUserInputContent(userInput);
				SearchParser search = new SearchParser(userInput);
				setSearchAttributes(search.getSearchByDate(), search.getSearchByTask(), search.getSearchByTag(), search.getSearchType());
				break;
			case SORT:
				userInput = getUserInputContent(userInput);
				break;
			default:
				System.out.println(MESSAGE_ERROR_READING_COMMAND_TYPE);
		}
	}
	
	private String processUserInput(String userInput) {
		String userTask = getUserInputContent(userInput);
		taskItems = checkTaskImportance(userTask);
		userTask = extractTag(taskItems);	
		return userTask;
	}

	private void checkIfValidUserInput(String userInput) {
		if (userInput.equals("")) {
			System.out.println(MESSAGE_INPUT_ERROR);
		}
	}

	public COMMAND_TYPE determineCommandType(String commandType) {
		FlexiCommand flexiCommand = new FlexiCommand();
		possibleCommandErrors = flexiCommand.getKeywordsDataBase();
		
		if(possibleCommandErrors.containsKey(commandType)) {
			this.command = possibleCommandErrors.get(commandType);
			setCommandType(this.command);
			return command;
		}
		else {
			this.command = COMMAND_TYPE.ADD;
			setCommandType(this.command);
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
		return temp[0];
	}
	
	private String getUserInputContent(String userInput) {
		String[] temp = userInput.split("\\s+", 2);
		return temp[1];
	}
	
	private ArrayList<String> checkTaskImportance(String userInput) {
		String[] str = userInput.split("\\s+");
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		int importanceType = taskItems.size() - 1;
		String importance = taskItems.get(importanceType);
		
		if (importance.equals("!")) {
			taskItems.remove(importanceType);
			setTaskImportance(true);
		}
		else {
			setTaskImportance(false);
		}
		return taskItems;
	}
	
	private String extractTag(ArrayList<String> taskItems) {
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < taskItems.size(); i++) {
			if (taskItems.get(i).endsWith(">") && taskItems.get(i).startsWith("<")) {
				setTaskTag(taskItems.get(i).substring(1, taskItems.get(i).length() - 1));
				taskItems.remove(i);
			}
		}
		
		for (String s: taskItems) {
			str.append(s);
			str.append(" ");
		}
		return str.toString();
	}
	//******************************************* Mutators *****************************************//
	protected void setCommandType(COMMAND_TYPE command) {
		this.command = command;
	}
	protected void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	protected void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	protected void setTaskType(TASK_TYPE taskType) {
		this.taskType = taskType;
	}
	
	protected void setTaskTag(String tag) {
		this.tag = tag.substring(1, tag.length());
	}
	
	protected void setTaskIndex(String index) {
		this.taskID = Integer.parseInt(index);
	}
	//***********************************Accessors for AddParser************************************//
	private void setAddAttributes(Date startTime, Date endTime, String taskName, TASK_TYPE taskType)  {
		this.startTime = startTime;
		this.endTime = endTime;
		this.taskName = taskName;
		this.taskType = taskType;
	}
	
	public Date getStartTime() {
		return this.startTime;
	}
	
	public Date getEndTime() {
		return this.endTime;
	}
	
	public String getName() {
		return this.taskName;
	}
	
	public TASK_TYPE getType() {
		return this.taskType;
	}
	
	public COMMAND_TYPE getCommandType() {
		return this.command;
	}
	private void setTaskImportance(boolean isImportant) {
		this.isImportant = isImportant;
	}
	
	public boolean getImportance() {
		return this.isImportant;
	}

	public String getTag() {
		return this.tag;
	}
	
	
/*	public TASK_TYPE getTaskType() {
		return this.taskType;
	}
*/	//*********************************** Accessors for DeleteParser ********************************//
	private void setDeleteAttributes(DELETE_TYPE deleteType, ArrayList<String> tagToDelete, ArrayList<Integer> indexToDelete) {
		this.tagToDelete = new ArrayList<String>();
		this.indexToDelete = new ArrayList<Integer>();
		
		this.indexToDelete = indexToDelete;
		this.tagToDelete = tagToDelete;
		this.deleteType = deleteType;
	}
	
	public DELETE_TYPE getDeleteType() {
		return this.deleteType;
	}
	
	public ArrayList<String> getTagToDelete() {
		return this.tagToDelete;
	}
	
	public ArrayList<Integer> getIndexToDelete() {
		return this.indexToDelete;
	}
	
	//***********************************Accessors for EditParser************************************//
	private void setEditAttributes(int taskID, Date endTime, Date startTime, String taskName, EDIT_TYPE editType) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.taskName = taskName;
		this.taskID = taskID;
		this.editType = editType;
	}
	
	public int getTaskID() {
		return this.taskID;
	}
	
	public EDIT_TYPE getEditType() {
		return this.editType;
	}
	
	//**************************** Accessors for Flag/Unflag/CompleteParser ***********************//
	private void setFlagAndCompleteAttributes(FLAGANDCOMPLETE_TYPE flagAndCompleteType, ArrayList<Integer> indexOfFlagAndMark) {
		this.indexOfFlagAndMark = new ArrayList<Integer>();
		this.indexOfFlagAndMark = indexOfFlagAndMark;
		this.flagAndCompleteType = flagAndCompleteType;
	}
		
	public FLAGANDCOMPLETE_TYPE getFlagAndCompleteType() {
		return this.flagAndCompleteType;
	}
		
	public ArrayList<Integer> getTaskToFlagAndMark() {
		return this.indexOfFlagAndMark;
	}
	//********************************************* SearchParser ************************************//
	private void setSearchAttributes(Date searchByDate, String searchByTask, String searchByTag,
			SEARCH_TYPE searchType) {
		this.searchType = searchType;
		this.searchByTask = searchByTask;
		this.searchByDate = searchByDate;
		this.searchByTag = searchByTag;
	}
	
	public SEARCH_TYPE getSearchType() {
		return this.searchType;
	}
	
	public String getSearchByTask() {
		return this.searchByTask.trim();
	}
	
	public String getSearchByTag() {
		return this.searchByTag.trim();
	}
	
	public Date getSearchByDate() {
		return this.searchByDate;
	}
	
	
}
