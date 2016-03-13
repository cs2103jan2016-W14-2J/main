package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import dodo.COMMAND_TYPE;
import dodo.DateAndTimeParser;
import dodo.TASK_TYPE;


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

public class AddParser {
	private final String KEYWORD_FROM = "from";
	private final String KEYWORD_TO = "to";
	private final String KEYWORD_ON = "on";
	private final String KEYWORD_BY = "by";
	private final String KEYWORD_AT = "at";
	private final String KEYWORD_BEFORE = "before";
	
	private int LAST_POSITION_OF_FROM = 0;
	private int LAST_POSITION_OF_TO = 0;
	private int LAST_POSITION_OF_ON = 0;
	private int LAST_POSITION_OF_AT = 0;
	private int LAST_POSITION_OF_BEFORE = 0;
	private int LAST_POSITION_OF_BY = 0;
	
	private String userTask = "";
	private String taskName = "";
	private String contentToAnalyse;
	private COMMAND_TYPE commandType;
	private TASK_TYPE taskType;
	
	private Date startTime;
	private Date endTime;
	
	private ArrayList<String> taskItems;
	private ArrayList<String> stringToAnalyse;
	private ArrayList<String> taskNameArrayList;
	
	public AddParser(String userTask) {
		this.userTask = userTask;
		executeAddParser();
	}
	
	private void executeAddParser() {
		String[] str = userTask.toLowerCase().split("\\s+");
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		taskType = determineTaskType(taskItems);
		System.out.println("Debug AddParser: Test executeAddParser @line64: " + taskType);
		
		// initiate categorisation of a task.
		switch(taskType) {
			case DEADLINED:
				parseDEADLINED();
				break;
			case FLOATING:
				parseFloating();
				break;
			case EVENT:
				parseEVENT();
				break;
			default:
				System.exit(1);
		}
		
	}

	public TASK_TYPE determineTaskType(ArrayList<String> taskItems) {
		
		if (checkIfFloatingTask(taskItems)) {
			setTaskType(TASK_TYPE.FLOATING);
			return TASK_TYPE.FLOATING;
		}
		else if (checkIfDeadlinedTask(taskItems)) {
			setTaskType(TASK_TYPE.DEADLINED);
			return TASK_TYPE.DEADLINED;
		}
		else if (checkIfEventTask(taskItems)) {
			setTaskType(TASK_TYPE.EVENT);
			return TASK_TYPE.EVENT;
		}
		else {
			return null;
		}
	}

	private boolean checkIfEventTask(ArrayList<String> taskItems) {
		
		System.out.println("Debug AddParser: Test checkIfEventTask @line108");
		
		LAST_POSITION_OF_FROM = taskItems.lastIndexOf(KEYWORD_FROM);
		LAST_POSITION_OF_TO = taskItems.lastIndexOf(KEYWORD_TO);
		
		// add study from <startDate> to <endDate>
		if (LAST_POSITION_OF_FROM < LAST_POSITION_OF_TO) {
			if (checkForDateAndTime(taskItems, LAST_POSITION_OF_FROM, LAST_POSITION_OF_TO)) {
				return true;
			}
			else {
				setTaskType(TASK_TYPE.FLOATING);
				return false;
			}
		}
		else {
			return false;
		}
	}

	private boolean checkForDateAndTime(ArrayList<String> taskItems, int LAST_POSITION_OF_FROM, int LAST_POSITION_OF_TO) {
		System.out.println("Debug checkForDateAndTime @line132");
		
		DateAndTimeParser parser = new DateAndTimeParser();
		ArrayList<String> temp = new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_FROM + 1, LAST_POSITION_OF_TO));
		
		if (parser.isNumericalDateType(taskItems.get(LAST_POSITION_OF_FROM + 1)) || 
			parser.isTimeType(taskItems.get(LAST_POSITION_OF_FROM + 1)) ||
			parser.isTodayType(temp) || parser.isTomorrowType(temp)) {
			return true;
		}
		else if (parser.isEnglishDateType(temp)) {
			return true;
		}
		else {
			System.out.println("Debug checkForDateAndTime @line144");
			return false;
		}
		
	}

	private boolean checkIfDeadlinedTask(ArrayList<String> taskItems) {
		getKeywordPosition(taskItems);
	
		if (LAST_POSITION_OF_AT != -1) {
			return true;
		}
		else if (LAST_POSITION_OF_ON != -1) {
			return true;
		}
		else if (LAST_POSITION_OF_BEFORE != -1) {
			return true;
		}
		else if (LAST_POSITION_OF_BY != -1) {
			return true;
		}
		else {
			return false;
		}
	}

	private void getKeywordPosition(ArrayList<String> taskItems) {
		LAST_POSITION_OF_AT = taskItems.lastIndexOf(KEYWORD_AT);
		LAST_POSITION_OF_ON = taskItems.lastIndexOf(KEYWORD_ON);
		LAST_POSITION_OF_BEFORE = taskItems.lastIndexOf(KEYWORD_BEFORE);
		LAST_POSITION_OF_BY = taskItems.lastIndexOf(KEYWORD_BY);
		System.out.println("Debug AddParser: Test lastIndexOfBefore @line128: " + LAST_POSITION_OF_BEFORE);
		
	}

	private boolean checkIfFloatingTask(ArrayList<String> taskItems) {
		
		System.out.println("Debug AddParser: Test checkIfEventTask @line108");
		
		if (!checkIfEventTask(taskItems) && !checkIfDeadlinedTask(taskItems)) {
			return true;
		}
		else {
			return false;
		}
		
	}
	

	private void parseEVENT() {
		System.out.println("Debug: Test parseEvent");
		DateAndTimeParser dateTimeParser = new DateAndTimeParser();
		
		taskNameArrayList = new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_FROM));
		setTaskName(toStringTaskElements(taskNameArrayList));
		
		
		// Parse string with "from" ... "to".
		if (userTask.lastIndexOf(KEYWORD_TO) > userTask.lastIndexOf(KEYWORD_FROM)) {
			
			taskItems.add("-1");
			stringToAnalyse =  new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_FROM, LAST_POSITION_OF_TO));
			startTime = dateTimeParser.analysePossibleDateElements(stringToAnalyse);
			setStartTime(startTime);
			
			stringToAnalyse =  new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_TO, taskItems.size() - 1));
			endTime = dateTimeParser.analysePossibleDateElements(stringToAnalyse);
			setEndTime(endTime);
			
			//debug
			System.out.println("Debug: Test contentToAnalyse @parseEvent1: " + contentToAnalyse);
		}
		else if (userTask.lastIndexOf(KEYWORD_TO) < userTask.lastIndexOf(KEYWORD_FROM)) {
		
			//debug
			System.out.println("Debug: Test contentToAnalyse @parseEvent2: " + contentToAnalyse);
			taskItems.add("-1");
			
			stringToAnalyse =  new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_FROM, taskItems.size() - 1));
			startTime = dateTimeParser.analysePossibleDateElements(stringToAnalyse);
			setStartTime(startTime);
			
		}
	
	}

	
	private void parseDEADLINED() {
		
		System.out.println("Debug: Test parseDEADLINED @line 205");
		
		DateAndTimeParser dateTimeParser = new DateAndTimeParser();
		
		if (LAST_POSITION_OF_BY != -1) {
			// dummy
			
			taskNameArrayList = new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_BY));
			setTaskName(toStringTaskElements(taskNameArrayList));
			
			taskItems.add("-1");
			stringToAnalyse =  new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_BY, taskItems.size()-1));
			endTime = dateTimeParser.analysePossibleDateElements(stringToAnalyse);
			setEndTime(endTime);
		}
	
		else if (LAST_POSITION_OF_ON != -1) {
			
			System.out.println("Debug: Test line 223");
			
			taskNameArrayList = new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_ON));
			setTaskName(toStringTaskElements(taskNameArrayList));
			
			taskItems.add("-1");
			stringToAnalyse =  new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, taskItems.size()-1));
			endTime = dateTimeParser.analysePossibleDateElements(stringToAnalyse);
			setEndTime(endTime);
		
		}
		else if (LAST_POSITION_OF_AT != -1) {
			
			System.out.println("Debug: Test line 209");
			
			taskNameArrayList = new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT));
			setTaskName(toStringTaskElements(taskNameArrayList));
			
			taskItems.add("-1");
			stringToAnalyse =  new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size() - 1));
			endTime = dateTimeParser.analysePossibleDateElements(stringToAnalyse);
			setEndTime(endTime);
			
		}
/*		else if (LAST_POSITION_OF_AT != -1 && LAST_POSITION_OF_ON == -1) {
			taskName = userTask.substring(0, LAST_POSITION_OF_AT);
			
			System.out.println("Debug: Test line 217");
			
			setTaskName(taskName);
			contentToAnalyse = userTask.substring(LAST_POSITION_OF_AT).replace(KEYWORD_AT, "").trim();
			endTime = dateTimeParser.analysePossibleDateElements(contentToAnalyse);
			setEndTime(endTime);
		}
		else if ((LAST_POSITION_OF_ON != -1 && LAST_POSITION_OF_BEFORE != -1) && 
				  LAST_POSITION_OF_ON < LAST_POSITION_OF_BEFORE) {
			
			System.out.println("Debug: Test line 224");
			
			taskName = userTask.substring(0, LAST_POSITION_OF_ON);
			setTaskName(taskName);
			contentToAnalyse = userTask.substring(LAST_POSITION_OF_ON).replace(KEYWORD_ON, "").trim();
			endTime = dateTimeParser.analysePossibleDateElements(contentToAnalyse);
			setEndTime(endTime);
		}
*/		else if (LAST_POSITION_OF_BEFORE != -1) {
	
			System.out.println("Debug: Test line 270");
	
			taskNameArrayList = new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_BEFORE));
			setTaskName(toStringTaskElements(taskNameArrayList));
			
			taskItems.add("-1");
			stringToAnalyse =  new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_BEFORE, taskItems.size() - 1));
			endTime = dateTimeParser.analysePossibleDateElements(stringToAnalyse);
			setEndTime(endTime);
			
		}
		
		System.out.println("Debug: End of parseDeadLined.");
			
	}

	private void parseFloating() {
		this.taskName = toStringTaskElements(taskItems).trim();
	}
	
	/*
	 * @param: an arraylist of task elements.
	 * @description: concatenate the content of a task input together
	 * @return: a string of task name.
	 */
	private String toStringTaskElements(ArrayList<String> taskNameArrayList) {
		
		for (int i = 0; i < taskNameArrayList.size(); i++) {
			taskName += taskNameArrayList.get(i) + " "; 
		}
		return taskName;
	}

	private void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	private void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	private void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getTaskName() {
		return this.taskName;
	}
	
	public TASK_TYPE getTaskType() {
		return this.taskType;
	}
	
	private void setTaskType(TASK_TYPE taskType) {
		this.taskType = taskType;
	}
	
	public COMMAND_TYPE getCommandType() {
		return commandType;
	}
}