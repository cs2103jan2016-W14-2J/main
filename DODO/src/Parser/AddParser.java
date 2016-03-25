package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.PrettyTime;

import Command.*;
import Task.*;

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
	
	private int LAST_POSITION_OF_FROM = -1;
	private int LAST_POSITION_OF_TO = -1;
	private int LAST_POSITION_OF_ON = -1;
	private int LAST_POSITION_OF_AT = -1;
	private int LAST_POSITION_OF_BEFORE = -1;
	private int LAST_POSITION_OF_BY = -1;
	
	private String userTask = "";
	private String taskName = "";
	private String tempTaskName = "";
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
		executeAddParser(userTask);
	}
	
	private void executeAddParser(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		taskType = determineTaskType(taskItems);
		System.out.println("Debug AddParser: Test executeAddParser @line71: " + taskType);
		
		// initiate categorisation of a task.
		switch(taskType) {
			case DEADLINED:
				parseDEADLINED();
				break;
			case FLOATING:
				parseFloating(taskItems, userTask);
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
		
		LAST_POSITION_OF_FROM = taskItems.lastIndexOf(KEYWORD_FROM);
		LAST_POSITION_OF_TO = taskItems.lastIndexOf(KEYWORD_TO);
		
		// add study from <startDate> to <endDate>
		if (LAST_POSITION_OF_FROM < LAST_POSITION_OF_TO) {
			if (checkForDateAndTime(LAST_POSITION_OF_FROM, LAST_POSITION_OF_TO)) {
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

	private boolean checkForDateAndTime(int LAST_POSITION_OF_FROM, int LAST_POSITION_OF_TO) {	
		String str = userTask.substring(LAST_POSITION_OF_FROM, userTask.length());
		List<Date> dates = new PrettyTimeParser().parse(str);
		return (dates.size() == 0) ? false : true; 
	}

	private boolean checkIfDeadlinedTask(ArrayList<String> taskItems) {
		getKeywordPosition(taskItems);
		return (LAST_POSITION_OF_AT != -1 || LAST_POSITION_OF_ON != -1 || 
			LAST_POSITION_OF_BEFORE != -1 || LAST_POSITION_OF_BY != -1) ? true : false;
	}

	private void getKeywordPosition(ArrayList<String> taskItems) {
		LAST_POSITION_OF_AT = taskItems.lastIndexOf(KEYWORD_AT);
		LAST_POSITION_OF_ON = taskItems.lastIndexOf(KEYWORD_ON);
		LAST_POSITION_OF_BEFORE = taskItems.lastIndexOf(KEYWORD_BEFORE);
		LAST_POSITION_OF_BY = taskItems.lastIndexOf(KEYWORD_BY);
	}

	private boolean checkIfFloatingTask(ArrayList<String> taskItems) {
		return (!checkIfEventTask(taskItems) && !checkIfDeadlinedTask(taskItems)) ? true : false;
	}
	

	private void parseEVENT() {
		String str = "";
		
		// Parse string with "from" ... "to".
		if (userTask.lastIndexOf(KEYWORD_TO) > userTask.lastIndexOf(KEYWORD_FROM)) {
			str = userTask.substring(LAST_POSITION_OF_FROM, userTask.length());
			List<Date> dates = new PrettyTimeParser().parse(str);
			
			if (dates.size() == 2) {
				setStartTime(dates.get(0));
				setEndTime(dates.get(1));
			}
			
			else if (dates.size() == 1) {
				setStartTime(dates.get(0));
			}
			
		}
		else if (userTask.lastIndexOf(KEYWORD_TO) < userTask.lastIndexOf(KEYWORD_FROM)) {
			str = userTask.substring(LAST_POSITION_OF_FROM, userTask.length());
			List<Date> dates = new PrettyTimeParser().parse(str);
			
			if (dates.size() == 1) {
				setStartTime(dates.get(0));
			}
		}
	}

	
	private void parseDEADLINED() {

		if (LAST_POSITION_OF_BY != -1 && LAST_POSITION_OF_AT == -1) {
			taskName = userTask.substring(0, LAST_POSITION_OF_BY);
			setTaskName(taskName);
			contentToAnalyse = userTask.substring(LAST_POSITION_OF_BY , userTask.length() - 1);
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: submit assignment 1 by tomorrow
			if (dates.size() != 0) {
				setEndTime(dates.get(0));
			}
			// Example: take a walk by the beach
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
	
		}
		
		else if (LAST_POSITION_OF_ON != -1 && LAST_POSITION_OF_AT == -1) {
			taskName = userTask.substring(0, LAST_POSITION_OF_ON);
			setTaskName(taskName);
			contentToAnalyse = userTask.substring(LAST_POSITION_OF_ON , userTask.length() - 1);
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: revise on cs2130t chapter 1 on sunday
			if (dates.size() != 0) {
				setEndTime(dates.get(0));
			}
			// Example: take a walk by the beach
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		
		}
		else if (LAST_POSITION_OF_ON != -1 && LAST_POSITION_OF_AT != -1) {
			taskName = userTask.substring(0, LAST_POSITION_OF_ON);
			setTaskName(taskName);
			String str = userTask.substring(LAST_POSITION_OF_ON , LAST_POSITION_OF_AT);
		
			List<Date> date1 = new PrettyTimeParser().parse(str);
			contentToAnalyse = userTask.substring(LAST_POSITION_OF_AT , userTask.length() - 1);
			List<Date> date2 = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: revise on cs2130t at 2pm
			if (date1.size() == 0 && date2.size() != 0) {
				setTaskName(userTask.substring(0, LAST_POSITION_OF_AT));
				setEndTime(date2.get(0));
			}
			// Example: meet hannah on 4 april, 2016 at suntec city
			else if (date1.size() != 0 && date2.size() == 0){
				setTaskName(userTask + " " + userTask.substring(LAST_POSITION_OF_AT , userTask.length() - 1));
				setEndTime(date1.get(0));
			}
			// Example: meet hannah on 4th of April 2016 at 2pm
			else if (date1.size() != 0 && date2.size() != 0) {
				setTaskName(userTask.substring(0, LAST_POSITION_OF_ON));
				contentToAnalyse = userTask.substring(LAST_POSITION_OF_ON , userTask.length() - 1);
				List<Date> date3 = new PrettyTimeParser().parse(contentToAnalyse);
				setEndTime(date3.get(0));
			}
			// Example: sleep on the study bench at the void deck
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		
		}
		else if (LAST_POSITION_OF_AT != -1 && LAST_POSITION_OF_ON == -1) {
			taskName = userTask.substring(0, LAST_POSITION_OF_AT);
			setTaskName(taskName);
			contentToAnalyse = userTask.substring(LAST_POSITION_OF_AT , userTask.length() - 1);
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: submit assignment 1 at 2359hrs
			if (dates.size() != 0) {
				setEndTime(dates.get(0));
			}
			// Example: take a walk by the beach
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		}
			
		else if (LAST_POSITION_OF_BEFORE != -1) {
			taskName = userTask.substring(0, LAST_POSITION_OF_BEFORE);
			setTaskName(taskName);
			contentToAnalyse = userTask.substring(LAST_POSITION_OF_BEFORE , userTask.length() - 1);
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: submit assignment 1 before 2359hrs
			if (dates.size() != 0) {
				setEndTime(dates.get(0));
			}
			// Example: let the kids eat before us.
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		}
		
	//	verifyIfDeadLineTask(dateTimeParser.getTempTaskName());
			
	}

	private void verifyIfDeadLineTask(String tempTaskName) {
		String temp = taskName.trim() + " " + tempTaskName.trim();
		System.out.println("Debug: verifyIfDeadLineTask. " + temp.trim().length());
		System.out.println("Debug: verifyIfDeadLineTask. " + userTask.trim().length());
		
		if (userTask.trim().length() == temp.trim().length()) {
			System.out.println("Debug: verifyIfDeadLineTask. " + temp);
			setTaskName(temp);
			setTaskType(TASK_TYPE.FLOATING);
		}
		else if (temp.contains(KEYWORD_FROM) && !temp.contains(KEYWORD_TO)){
			setTaskName(userTask);
			setTaskType(TASK_TYPE.DEADLINED);
		}
/*		else {
			setTaskName(temp);
			setTaskType(TASK_TYPE.DEADLINED);
		}
*/	}

	private void parseFloating(ArrayList<String> taskItems, String userTask) {
	//	this.taskName = toStringTaskElements(taskItems).trim();
		DateAndTimeParser parser = new DateAndTimeParser();
		Date endTime = parser.analysePossibleDateElements(taskItems);
		setEndTime(endTime);
		verifyIfDeadLineTask(parser.getTempTaskName());
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