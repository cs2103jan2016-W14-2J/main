package Parser;

import java.util.Date;
import java.util.HashMap;

import Command.*;
import GUI.*;
import Logic.*;
import Task.*;
/*
 * V 0.1: At this moment, this CommandParser function is only able to handle the following input format.
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
	
	// update
	public Parser (String userInput) {
		System.out.println("Test Parser");
		executeCommand(userInput);
		
	}
	
	/*
	 *Returns the command type.
	 *@param userInput
	 *@return command type in enum format. 
	 */
	
	protected void executeCommand(String userInput) {
		System.out.println("Debug: Test execute command");
		userInput = userInput.trim();
		checkIfValidUserInput(userInput);
	
		String command = getUserCommand(userInput);
		System.out.println("Debug: Test command: " + command);
		
		COMMAND_TYPE commandType = determineCommandType(command);
		System.out.println("Debug: Test commandType: " + commandType);
		
		String userTask = getUserInputContent(userInput);
		System.out.println("Debug: Test useTask: " + userTask);
		
		switch (commandType) {
			
			case ADD:
				AddParser addParser = new AddParser(commandType, userTask);
				setTaskAttributes(addParser.getStartTime(), addParser.getEndTime(), addParser.getTaskName(), addParser.getTaskType());
				break;
			case DELETE:
				DeleteParser addParser = new DeleteParser(commandType, userTask);
				break;
/*			case EDIT:
				break;
			case "DISPLAY":
				break;
			case "EXIT":
				break;
*/			default:
				System.out.println(MESSAGE_ERROR_READING_COMMAND_TYPE);
		}
	}
	
	private void checkIfValidUserInput(String userInput) {
		if (userInput.equals("")) {
			System.out.println(MESSAGE_INPUT_ERROR);
		}
		System.out.println("Debug: Test checkIfValidUserInput");
	}

	public COMMAND_TYPE determineCommandType(String commandType) {
		FlexiCommand flexiCommand = new FlexiCommand();
		possibleCommandErrors = flexiCommand.getKeywordsDataBase();
		
		if(possibleCommandErrors.containsKey(commandType)) {
			this.command = possibleCommandErrors.get(commandType);
			return command;
		}
		else {
			this.command = COMMAND_TYPE.ERROR;
			return command;
		}
	}
	
	/*
	 * Returns the command type of the string
	 * @param userInput
	 * @return command type of the string
	 */
	protected String getUserCommand(String userInput) {
		String[] temp = userInput.split("\\s+", 2);
		return temp[0];
	}
	
	protected String getUserInputContent(String userInput) {
		String[] temp = userInput.split("\\s+", 2);
		return temp[1];
	}
	
	private void setTaskAttributes(Date startTime, Date endTime, String taskName, TASK_TYPE taskType)  {
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
	
	public String getTaskName() {
		return this.taskName;
	}
	
	public TASK_TYPE getTaskType() {
		return this.taskType;
	}

}

