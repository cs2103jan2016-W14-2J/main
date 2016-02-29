package Parser;

import Command.*;
import Storage.*;
import Task.*;
import GUI.*;
import Logic.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;



/*
 * V 0.1: At this moment, this CommandParser function is only able to handle the following input format.
 * 
 * Timed Task:
 * 1. add revise on CS2103T at 1930hrs
 * 2. add finish CS2103T by 2359hrs
 * 3. add meeting with boss on 21.07.2016
 * 4. add cs2010 lab5 due on 01/03
 * 
 * Floating Task:
 * 1. add buy beer from 7-11
 * 2. add study CS2103T on the floor
 * 3. add revise CS2103T by the beach
 * 4. add visit ramen store @ nus
 * 5. add travel from 7/11 to yishun
 * 
 * Event Task:
 * 1. add study CS2103T from 1800hrs to 2000hrs
 * 2. add code from 1130pm to 1030am
 * 
 * 
 */

public class Parser {

	private static String MESSAGE_ERROR_READING_COMMAND_TYPE = "You have entered the wrong command. Kindly enter a valid input.";
	private HashMap<String, COMMAND_TYPE> possibleCommandErrors = new HashMap<String, COMMAND_TYPE>();	
	
	private int taskStartDate = 0;
	private int taskEndDate = 0;
	private int taskEndTime = 0;
	private int taskStartTime = 0;
//	private String taskTag;
	private String taskDescription = "";
	private boolean taskImportance;
//	private boolean isComplete;
	
	
	private final String KEYWORD_FROM = "from";
	private final String KEYWORD_TO = "to";
	private final String KEYWORD_DUE = "due";
	private final String KEYWORD_ON = "on";
	private final String KEYWORD_BY = "by";
	private final String KEYWORD_AT1 = "at";
	private final String KEYWORD_AT2 = "@";
	private final String KEYWORD_AM = "am";
	private final String KEYWORD_PM = "pm";
	private final String KEYWORD_HRS = "hrs";
	
	private final int MAX_DAY_PER_MONTH = 31;
	private final int MAX_MONTH_PER_YEAR = 12;
	private final int MAX_YEAR_PROPER = 2020;
	private final int MAX_YEAR_INPROPER = 20;
	private final int MAX_YEAR_LENGTH_PROPER = 4;
	private final int MAX_YEAR_LENGTH_INPROPER = 2;
	private final int NUM_CONTENT_OF_DATE_PROPER = 3;
	private final int NUM_CONTENT_OF_DATE_INPROPER = 2;
	private final int DEFAULT_DEADLINE = 2359;
	
	private final int MIN_TIME_LENGTH_STRING = 3;
	private final int MAX_TIME_LENGTH_STRING = 7;
	
	private final int LENGTH_OF_HRS_STRING = 3;
	private final int LENGTH_OF_AM_PM_STRING = 2;

	private TASK_TYPE taskType;
	private COMMAND_TYPE command;
	
	// update
	public Parser (String userInput) {
		executeCommand(userInput);
	}
	
	/*
	 *Returns the command type.
	 *@param userInput
	 *@return command type in enum format. 
	 */
	
	protected void executeCommand(String userInput) {
		String[] arrTask = getUserCommand(userInput);
		String command = arrTask[0];
		COMMAND_TYPE commandType = determineCommandType(command);
//		String input = getUserInputContent(userInput);
//		System.out.println("[DEBUG input]" + input);
		
		switch (commandType) {
			
			case ADD:
				extractTaskDetailsForAdd(userInput);
				break;
/*			case "EDIT":
				break;
			case "DELETE":
				break;
			case "DISPLAY":
				break;
			case "EXIT":
				break;
*/			default:
				System.out.println(MESSAGE_ERROR_READING_COMMAND_TYPE);
		}
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
	protected String[] getUserCommand(String userInput) {
		userInput = userInput.trim();
		String[] temp = userInput.split(" ");
		
		return temp;
	}
	
	/*
	 * Returns the portion of user input without the command
	 * @param user input.
	 * @return portion without the command.
	 */
	protected String getUserInputContent(String userInput) {
		userInput = userInput.trim();
		String[] temp = userInput.split(" ");
		
		if (temp.length > 2) {
			return temp[1];
		}
		else {
			return null;
		}
	}
	
	public void extractTaskDetailsForAdd(String userInput) {
//		String[] str = userInput;
		String[] str = userInput.split(" ");
		String originalStr = "";
		
		ArrayList<String> taskItems = new ArrayList<String>(Arrays.asList(str));
		taskItems.remove(0);
		
		for (int i = 0; i < taskItems.size(); i++) {
			originalStr += taskItems.get(i) + " ";
		}
		System.out.println("[DEBUG extract]" + originalStr);
		
		checkTaskImportance(taskItems);
		int currentWordPosition = 0;
		
		while (currentWordPosition < taskItems.size()) {
			String currentWord = taskItems.get(currentWordPosition);
			System.out.println(currentWord);
			if (isKeyword(currentWord)) {
				currentWordPosition = parseParameter(currentWord, taskItems);
			}
			else {
				constructTaskContent(currentWord);
			}
			currentWordPosition++;
		}
		System.out.println("DEBUG SET AS TIMED TASK");
		// check if the date and time has been removed from the task description.
		// if no, the length of task description should be the same as userinput
		// and therefore is a floating task
		if (taskDescription.equals(originalStr)) {
			setAsFloatingTask();
		}
		System.out.println(taskDescription);
//		return taskDescription;
		
	}
	
	
	private void setAsFloatingTask() {
		System.out.println("DEBUG set as floating task");
		setTaskEndTime(0);
		setTaskStartTime(0);
		setTaskStartDate(0);
		setTaskEndDate(0);
		
		setTaskType(TASK_TYPE.FLOATING);
	}


	private int parseTime(String keyword, ArrayList<String> taskItems) {
		int finalTime = 0;
		
		// Retrieve the string after keyword.
		// Example: add upload assignment 1 by 2359hrs || retrieves 2359hrs
		int startParsingPoint = taskItems.indexOf(keyword) + 1;
		if (startParsingPoint < taskItems.size()) {
			finalTime = checkForTime(keyword, startParsingPoint, taskItems);
			return finalTime;
		}
		else {
			return finalTime;
		}
	}
		
	private int parseParameter(String keyword, ArrayList<String> taskItems) {
		int finalTime = 0;
		int finalDate = 0;
		int nextWordPosition = 0;
		boolean isParsable = true;
		
		System.out.println("DEBUG PARSEPARAMETER");
		
		// assume that the time is at the end of keyword "@", "at", "by".
		// Example: add finish revision of CS2103T by 8pm
		// Example: add call Beatrice at 1700hrs.
		
		if (keyword.equals(KEYWORD_AT1) || keyword.equals(KEYWORD_AT2) || keyword.equals(KEYWORD_BY)) {
			finalTime = parseTime(keyword, taskItems); 
			System.out.println("DEBUG @ COMMAND");
			
			//System.out.println(finalTime); // pass but wrong answer. 7pm but gave 1207
			
			if (finalTime != 0) {
				setAsTimedTask(finalTime);
				System.out.println("DEBUG SET AS TIMED TASK");
				
				nextWordPosition = taskItems.indexOf(keyword) + 1;
			}
			else {
				constructTaskContent(keyword);
				System.out.println("DEBUG CONSTRUCT TASK CONTENT");
				nextWordPosition = taskItems.indexOf(keyword);
			}
		}
		else if (keyword.equals(KEYWORD_ON)){
			finalDate = parseDate(keyword, taskItems);
			if (finalDate != 0) {
				setAsDateTask(finalDate);
				nextWordPosition = taskItems.indexOf(keyword) + 1;
			}
			else {
				constructTaskContent(keyword);
				nextWordPosition = taskItems.indexOf(keyword);
			}
		}
		else if (keyword.equals(KEYWORD_DUE)) {
			int isOnStringIndex = taskItems.indexOf(KEYWORD_DUE) + 1;
			if (taskItems.get(isOnStringIndex).equals(KEYWORD_ON)) {
				keyword = KEYWORD_ON;
				finalDate = parseDate(keyword, taskItems);
				if (finalDate != 0) {
					setAsDateTask(finalDate);
					nextWordPosition = taskItems.indexOf(keyword) + 1;
				}
				else {
					constructTaskContent(keyword);
					nextWordPosition = taskItems.indexOf(keyword);
				}
			}
			else{
				constructTaskContent(keyword);
				nextWordPosition = taskItems.indexOf(keyword);
			}
		}
		
		else if (keyword.equals(KEYWORD_FROM)) {
			System.out.println("Test From");
			if(checkIfEvent(keyword, taskItems)) {
				boolean parsable = canParseEvent(keyword, taskItems);
				if(parsable == isParsable) {
					nextWordPosition = taskItems.indexOf(KEYWORD_TO) + 1;
				}
				else {
					constructTaskContent(keyword);
					nextWordPosition = taskItems.indexOf(keyword);
				}
			}
			else {
				constructTaskContent(keyword);
				nextWordPosition = taskItems.indexOf(keyword);
			}
		}
		return nextWordPosition;
	}


	private boolean canParseEvent(String keyword, ArrayList<String> taskItems) {
		int finalTimeStart = 0;
		int finalTimeEnd = 0;
		int finalDateStart = 0;
		int finalDateEnd = 0;
		
		System.out.println("Test canParseEvent");
		
		finalTimeStart = parseTime(keyword, taskItems);
		System.out.println(finalTimeStart);
		finalDateStart = parseDate(keyword, taskItems);
		
		if (finalTimeStart != 0 && finalDateStart == 0) {
			System.out.println("Test canParseEvent2");
			finalTimeEnd = parseTime(KEYWORD_TO, taskItems);
			System.out.println(finalTimeEnd);
			setAsTimedEvent(finalTimeStart, finalTimeEnd);
			return true;
		}
		else if (finalTimeStart == 0 && finalDateStart != 0) {
			finalDateEnd = parseDate(KEYWORD_TO, taskItems);
			setAsDateEvent(finalDateStart, finalDateEnd);
			return true;
		}
		else {
			return false;
		}
		
	}
	
	private void setAsTimedEvent(int finalTimeStart, int finalTimeEnd) {
		setTaskEndTime(finalTimeEnd);
		setTaskStartTime(finalTimeStart);
		
		taskStartDate = Integer.parseInt(getCurrentDate());
		setTaskStartDate(taskStartDate);
		setTaskEndDate(taskStartDate);
		
		setTaskType(TASK_TYPE.EVENT);
	}


	private void setAsDateEvent(int finalDateStart, int finalDateEnd) {
		setTaskEndDate(finalDateEnd);
		setTaskStartDate(finalDateStart);
		
		setTaskEndTime(DEFAULT_DEADLINE);
		taskStartTime = Integer.parseInt(getCurrentTime());
		setTaskStartTime(taskStartTime);
		
		setTaskType(TASK_TYPE.EVENT);
	}


	// check if user input is from .. to .. format
	private boolean checkIfEvent(String keyword, ArrayList<String> taskItems) {
		if (taskItems.contains(KEYWORD_TO)) {
			if (taskItems.indexOf(KEYWORD_TO) == taskItems.indexOf(KEYWORD_FROM) + 2) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}


	private int parseDate(String keyword, ArrayList<String> taskItems) {
		int startParsingPoint = taskItems.indexOf(keyword) + 1;
		String parameter = taskItems.get(startParsingPoint);
		String[] dateElements = parameter.split("[/.-]");
		int finalDate = 0;
		
		// check if date input is 12.04.16 or 12/05
		if (dateElements.length < NUM_CONTENT_OF_DATE_INPROPER 
			&& dateElements.length > NUM_CONTENT_OF_DATE_PROPER) {
			return finalDate;
		}
		else {
			System.out.println("Test parseDate");
			finalDate = anaylsePossibleDateElements(dateElements, taskItems);
			return finalDate;
		}
	}


	private int anaylsePossibleDateElements(String[] dateElements, ArrayList<String> taskItems) {
		int finalDate = 0;
		
		if (dateElements.length == NUM_CONTENT_OF_DATE_INPROPER) {
			finalDate = checkInproperDate(dateElements, taskItems);
		//	setAsDateTask(finalDate);
		}
		else if (dateElements.length == NUM_CONTENT_OF_DATE_PROPER) {
			System.out.println("Test analysePossibleDateElements");
			finalDate = checkProperDate(dateElements, taskItems);
			//	setAsDateTask(finalDate);
		}
		System.out.println(finalDate);
		return finalDate;
	}


	private int checkProperDate(String[] dateElements, ArrayList<String> taskItems) {
		String properDateFormat = "";
		int finalDate = 0;
		String properYearFormat = "";
		
		//check if input is really a date format and not a string format like H.I
		if (Integer.parseInt(dateElements[0]) <= MAX_DAY_PER_MONTH) {
			if (Integer.parseInt(dateElements[1]) <= MAX_MONTH_PER_YEAR){
				// check if the year input is in the format of 2016
				if (Integer.parseInt(dateElements[2]) <= MAX_YEAR_PROPER 
					&& dateElements[2].length() == MAX_YEAR_LENGTH_PROPER) {
					System.out.println("Test checkProperDate");
					properDateFormat = dateElements[0] + dateElements[1] + dateElements[2];
					finalDate = Integer.parseInt(properDateFormat);
					return finalDate;
				}
				// check if the year input is in the format of '16.
				// if yes, convert to 2016
				else if (Integer.parseInt(dateElements[2]) <= MAX_YEAR_INPROPER 
					&& dateElements[2].length() == MAX_YEAR_LENGTH_INPROPER) {
					System.out.println("Test checkProperDate2");
					properYearFormat = "20" + dateElements[2];
					properDateFormat = dateElements[0] + dateElements[1] + properYearFormat;
					finalDate = Integer.parseInt(properDateFormat);
					return finalDate;
				}
			}
		}
		return finalDate;
		
	}


	private int checkInproperDate(String[] dateElements, ArrayList<String> taskItems) {
		String properDateFormat = "";
		int finalDate = 0;
		
		//check if input is really a date format and not a string format like H.I
		if (Integer.parseInt(dateElements[0]) <= MAX_DAY_PER_MONTH) {
			if (Integer.parseInt(dateElements[1]) <= MAX_MONTH_PER_YEAR){
				//default set the year as current if user didn't specify
				properDateFormat = dateElements[0] + dateElements[1] + "2016";
				finalDate = Integer.parseInt(properDateFormat);
				return finalDate;
			}
		}
		return finalDate;
		
	}


	private void setAsDateTask(int finalDate) {
		
		setTaskEndDate(finalDate);
		
		taskStartDate = Integer.parseInt(getCurrentDate());
		setTaskStartDate(taskStartDate);
		
		setTaskEndTime(DEFAULT_DEADLINE);
		taskStartTime = Integer.parseInt(getCurrentTime());
		setTaskStartTime(taskStartTime);
		
		setTaskType(TASK_TYPE.DEADLINED);
	}


	private int checkForTime(String keyword, int startParsingPoint, ArrayList<String> taskItems) {
		String parameter = taskItems.get(startParsingPoint);
		int finalTime = 0;
		System.out.println("[DEBUG/parser/checkForTime] keyword: " + keyword);
		System.out.println("[DEBUG/parser/checkForTime] parameter: " + parameter);
		
		// Min time length: 7am etc
		// Max time length: 12:00pm / 1200hrs
		if (parameter.length() < MIN_TIME_LENGTH_STRING && parameter.length() > MAX_TIME_LENGTH_STRING) {
			constructTaskContent(keyword);
			constructTaskContent(parameter);
		}

		
/*		// check if string contains AM , PM or HRS
		// what about 1200 / 12:00?
		if (!parameter.contains(KEYWORD_AM) || !parameter.contains(KEYWORD_PM) ||
			!parameter.contains(KEYWORD_HRS)) {
			constructTaskContent(keyword);
			constructTaskContent(parameter);
		}
*/		
		String lastTwoChars = parameter.substring(parameter.length() - LENGTH_OF_AM_PM_STRING);
		String lastThreeChars = parameter.substring(parameter.length() - LENGTH_OF_HRS_STRING);
		
		// check if string contains AM or PM and check if it is indeed a time.
		if (containsAMPM(lastTwoChars)) {
			String time = parameter.substring(0, parameter.length() - LENGTH_OF_AM_PM_STRING);
			
			System.out.println(time); // correct
			
			finalTime = processAMPM(time, lastTwoChars);
		}
		// check if string contains HRs and check if it is indeed a time.
		else if (containsHRS(lastThreeChars)) {
			String time = parameter.substring(0, parameter.length() - LENGTH_OF_HRS_STRING);
			finalTime = processHRS(time); // set start time.
		}
		
		return finalTime; 
		
	}

	private int processHRS(String time) {
		int finalTime = 0;
		String[] _24hours = time.split(":");
		// change 12:00 to 1200
		if (_24hours.length == 2) {
			String temp = _24hours[0] + _24hours[1];
			finalTime = Integer.parseInt(temp);
		//	setAsTimedTask(finalTime);
		}
		// remain unchange if time has no ":".
		else {
			finalTime = Integer.parseInt(time);
		//	setAsTimedTask(finalTime);
		}
		return finalTime;
	}



	private void setAsTimedTask(int finalTime) {
		setTaskEndTime(finalTime);
		
		// set start time as the time when user input the task
		taskStartTime = Integer.parseInt(getCurrentTime()); // issue
		setTaskStartTime(taskStartTime);
		
		taskStartDate = Integer.parseInt(getCurrentDate());
		setTaskStartDate(taskStartDate);
		
		taskEndDate = Integer.parseInt(getCurrentDate());
		setTaskEndDate(taskEndDate);
		
		setTaskType(TASK_TYPE.DEADLINED);
		System.out.println("setAsTimedTask checked!");
	}

	
	private void setTaskType(TASK_TYPE taskType) {
		this.taskType = taskType;
		
	}
	
	public TASK_TYPE getType() {
		return this.taskType;
	}


	private void setTaskEndDate(int taskEndDate) {
		this.taskEndDate = taskEndDate;	
	}

	public int getTaskEndDate() {
		return this.taskEndDate;
		
	}

	private void setTaskStartDate(int taskStartDate) {
		this.taskStartDate = taskStartDate;
	}
	
	public int getTaskStartDate() {
		return this.taskStartDate;
	}


	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		Date date = new Date();
		return dateFormat.format(date);
		
	}
	
	/*
	 * function is correct
	 */
	public String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("HHmm");
		Calendar cal = Calendar.getInstance();
		
		return dateFormat.format(cal.getTime());
	}

	private void setTaskEndTime(int finalTime) {
		this.taskEndTime = finalTime;
		
	}
	
	public int getTaskEndTime() {
		return this.taskEndTime;
	}
	
	private void setTaskStartTime(int taskStartTime) {
		this.taskStartTime = taskStartTime;
	}
	
	public int getTaskStartTime() {
		return this.taskStartTime;
	}

	/*
	 * convert 12hours into 24 hours time format
	 * Example: 2pm to 1400 or 2am to 0200
	 */
	private int processAMPM(String time, String lastTwoChars) {
		int finalTime = 0;
		String[] _24hours = time.split("[.:]");
		finalTime = convertToWorldTime(_24hours, lastTwoChars);
		
		return finalTime;
	}

	/*
	 * Check if it's AM or PM and do necessary conversion
	 */
	private int convertToWorldTime(String[] _24hours, String lastTwoChars) {
		int finalTime = 0;
		String hourHand = "";
		String worldTime = "";
		int hourHandWorldTime;
		
		System.out.println("convertToWorldTime Test"); // pass
		System.out.println(_24hours[0]);
		
		// Example: convert 2:15am to 0215		
		if (_24hours.length == 2 && lastTwoChars.equals(KEYWORD_AM)) {
			// add a zero in front 
			// 215am to 0215
			if (Integer.parseInt(_24hours[0]) > 10 && Integer.parseInt(_24hours[0]) < 12) {
				hourHand = "0" + _24hours[0];
				worldTime = hourHand + _24hours[1];
				finalTime = Integer.parseInt(worldTime);
			//	setAsTimedTask(finalTime);
			}
			// convert 12am to 0000
			else if (Integer.parseInt(_24hours[0]) == 12) {
				worldTime = "00" + _24hours[1];
				finalTime = Integer.parseInt(worldTime);
			}
			else {
				worldTime = _24hours[0] + _24hours[1];
				finalTime = Integer.parseInt(worldTime);
			//	setAsTimedTask(finalTime);
			}
		}
		// Add a zero in front of 959am
		// Example: 959am to 0959
		else if (_24hours.length == 1 && lastTwoChars.equals(KEYWORD_AM)) {
		
			if (Integer.parseInt(_24hours[0]) > 959 && Integer.parseInt(_24hours[0]) < 1200) {
				hourHand = "0" + _24hours[0];
				finalTime = Integer.parseInt(hourHand);
			//	setAsTimedTask(finalTime);
			}
			else if (Integer.parseInt(_24hours[0]) > 1201 && Integer.parseInt(_24hours[0]) < 1259) {
				finalTime = Integer.parseInt(_24hours[0]) - 1200;
			}
			else {
				finalTime = Integer.parseInt(_24hours[0]);
			//	setAsTimedTask(finalTime);
			}
		}
		else if (_24hours.length == 2 && lastTwoChars.equals(KEYWORD_PM)) {
			// not 12pm. only convert 1pm till 1159pm
			if (Integer.parseInt(_24hours[0]) < 12) {
				hourHandWorldTime = 12 + Integer.parseInt(_24hours[0]); 
				finalTime = (hourHandWorldTime * 100) + Integer.parseInt(_24hours[1]);
			//	setAsTimedTask(finalTime);
			}
			else {
				hourHand = _24hours[0] + _24hours[1];
				finalTime = Integer.parseInt(hourHand);
			//	setAsTimedTask(finalTime);
			}
		}
		// ERROR ///
		else if (_24hours.length == 1 && lastTwoChars.equals(KEYWORD_PM)) {
			// not 12pm. only convert 1pm till 1159pm
			if (Integer.parseInt(_24hours[0]) < 1259) {
				// convert 1pm, 2pm, 3pm ... 11pm to world time.
				if (Integer.parseInt(_24hours[0]) < 12) {
					finalTime = Integer.parseInt(_24hours[0]) * 100 + 1200; 
				}
				else {
				finalTime = 1200 + Integer.parseInt(_24hours[0]); 
				}
			//	setAsTimedTask(hourHandWorldTime);
			}
		}
		return finalTime;
	}


	private boolean containsHRS(String lastThreeChar) {
		lastThreeChar = lastThreeChar.toLowerCase();
		
		if (lastThreeChar.contains(KEYWORD_HRS)) {
			return true;
		}
		else {
			return false;
	
		}
	}


	private boolean containsAMPM(String lastTwoChar) {
		lastTwoChar = lastTwoChar.toLowerCase();
		if (lastTwoChar.contains(KEYWORD_AM)) {
			return true;
		}
		else if (lastTwoChar.contains(KEYWORD_PM)) {
			return true;
		}
		else {
			return false;
		}
	}


	private boolean isKeyword(String token) {
		// TODO Auto-generated method stub
		token = token.toLowerCase();
		if (token.equals(KEYWORD_AT1) ||
			token.equals(KEYWORD_AT2) ||
			token.equals(KEYWORD_BY) ||
			token.equals(KEYWORD_FROM) ||
			token.equals(KEYWORD_ON) ||
			token.equals(KEYWORD_DUE)) {
			return true;
		}
		else {
			return false;
		}
	}

	private void constructTaskContent(String token) {
		// TODO Auto-generated method stub
		taskDescription += (token + " ");
		System.out.println("[DEBUG/ADD] taskDescription: " + taskDescription + " , token: " + token);
		
	}
	 
	private void checkTaskImportance(ArrayList<String> taskItems) {
		int importanceType = taskItems.size() - 1;
		String importance = taskItems.get(importanceType);
		
		if (importance.equals("1")) {
			taskItems.remove(importanceType);
			setTaskImportance(true);
		}
		else {
			setTaskImportance(false);
		}
	}
	
	private void setTaskImportance(boolean importance) {
		if (importance == true) {
			this.taskImportance = true;
		}
		else {
			this.taskImportance = false;
		}
	}
	
	public boolean getTaskImportance(boolean importance) {
		return this.taskImportance;
	}
	
	/******************************************ACCESSORS*******************************************/
	public String getName() {
		return this.taskDescription;
	}
	
	public String getTag() {
		return null;
	}
	
	public COMMAND_TYPE getCommandType() {
		return this.command;
	}
}

