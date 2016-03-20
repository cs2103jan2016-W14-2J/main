package Parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Arrays;

import Command.*;
import Task.*;

/*
 * V 0.1: At this moment, this Parser function is only able to handle the following input format.
 * 
 * Timed Task:
 * 1. add revise on CS2103T at 1930hrs
 * 2. add finish CS2103T by this thursday
 * 3. add meeting with boss on 21.07.2016
 * 4. add cs2010 lab5 due on 01/03
 * 5. add submit assignment 1 before monday
 * 6. add start building sandcastle by the next 2 days.
 * 7. add paint my toe nails by friday 2359hrs
 * 
 * Floating Task:
 * 1. add buy beer from 7-11
 * 2. add study CS2103T on the floor
 * 3. add revise CS2103T by the beach
 * 4. add visit ramen store at nus
 * 5. add travel from yishun to nus
 * 
 * Event Task:
 * 1. add study cs2103t from 20/3/2016 to 23/04/2016.
 * 2. add nus soc sports camp from 20 feb 2017 to 20 feb 2018
 * 3. add babysit baby claudia from today to tomorrow
 * 
 * Delete:
 * Single delete: delete 1
 * Multiple delete: delete 1, 3, 5, 7
 * Range delete: delete 1 to 4 / delete 1-9
 * All delete: delete all
 * 
 * 
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
	private String userInput;
	private String tag;
	private boolean isImportant;
	
	private DELETE_TYPE deleteType;
	private ArrayList<Integer> taskToDelete;
	private ArrayList<String> taskItems;
	private String _commandAdd = "add";
	private EDIT_TYPE editType;
	
	public Parser() {
		
	}
	// update
	public Parser (String userInput) {
		assert userInput.length() > 0;
		this.userInput = userInput;
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
				DeleteFlagCompleteParser deleteParser = new DeleteFlagCompleteParser(userInput);
				setDeleteAttributes(deleteParser.getDeleteType(), deleteParser.getTaskToDelete());
				break;
			case EDIT:
				userInput = processUserInput(userInput);
				EditParser editParser = new EditParser(userInput);
				setEditAttributes(editParser.getTaskID(), editParser.getEndNewDate(), editParser.getStartNewDate(),
								  editParser.getNewTaskName(), editParser.getEditType());
				break;
			case COMPLETE:
				userInput = getUserInputContent(userInput);
				DeleteFlagCompleteParser completeParser = new DeleteFlagCompleteParser(userInput);
				setDeleteAttributes(completeParser.getDeleteType(), completeParser.getTaskToDelete());
				break;
			case FLAG:
				userInput = getUserInputContent(userInput);
				DeleteFlagCompleteParser flagParser = new DeleteFlagCompleteParser(userInput);
				setDeleteAttributes(flagParser.getDeleteType(), flagParser.getTaskToDelete());
				break;
			case UNFLAG:
				userInput = getUserInputContent(userInput);
				DeleteFlagCompleteParser unflagParser = new DeleteFlagCompleteParser(userInput);
				setDeleteAttributes(unflagParser.getDeleteType(), unflagParser.getTaskToDelete());
				break;
			case UNDO:
				setCommandType(COMMAND_TYPE.UNDO);
				break;
			case REDO:
				setCommandType(COMMAND_TYPE.REDO);
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
				setTaskTag(taskItems.get(i));
				taskItems.remove(i);
			}
		}
		
		for (String s: taskItems) {
			str.append(s);
			str.append(" ");
			System.out.println("Debug: Test checkIfValidUserInput" + str.toString());
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
	
	public String getTaskName() {
		return this.taskName;
	}
	
/*	public TASK_TYPE getTaskType() {
		return this.taskType;
	}
*/	//**************************Accessors for Flag/Unflag/Complete/DeleteParser*********************//
	private void setDeleteAttributes(DELETE_TYPE deleteType, ArrayList<Integer> taskToDelete) {
		this.taskToDelete = new ArrayList<Integer>();
		this.taskToDelete = taskToDelete;
		this.deleteType = deleteType;
	}
	
	public DELETE_TYPE getDeleteType() {
		return this.deleteType;
	}
	
	public ArrayList<Integer> getTaskToDelete() {
		return this.taskToDelete;
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

}
