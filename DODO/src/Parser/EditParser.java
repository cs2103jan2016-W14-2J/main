package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import Command.*;

public class EditParser {
	
	private String KEYWORD_TO = " to ";
	private String KEYWORD_FROM = " from ";
	private String KEYWORD_AT = " at ";
	private String KEYWORD_BEFORE = " before ";
	private String KEYWORD_ON = " on ";
	private String KEYWORD_BY = " by ";
	
	private String newTaskName = "";
	private int INDEX_OF_LAST_TO = -1;
	private int INDEX_OF_LAST_FROM = -1;
	
	private int taskID;
	private Date newDate;
	private EDIT_TYPE editType;
	private String MESSAGE_EDIT_INPUT_ERROR = "You have entered an invalid command.";
	private ArrayList<String> editTaskElements;
	private ArrayList<String> contentToAnalyse;
	
	public EditParser(String userInput) {
		System.out.println("Debug at EditParser");
		executeEditParser(userInput);
	}

	
	private void executeEditParser(String userInput) {
		String[] editElements = userInput.replaceAll("[:.-]", "").toLowerCase().split("\\s+");
		editTaskElements = new ArrayList<String>(Arrays.asList(editElements));
		
		for (int i = 0; i < editTaskElements.size(); i++) {
			System.out.println(editTaskElements.get(i));
		}
		
		taskID = checkIfValidEditCommandInput(editTaskElements);
		setTaskID(taskID);
	
		switch(determineEditType(userInput)) {
		
		case TASK_NAME:
			System.out.println("Debug at TASK_NAME " + userInput);
			setEditType(EDIT_TYPE.TASK_NAME);
			parseEditTaskName(editTaskElements);
			break;
		case EVENT_TIME:
			System.out.println("Debug at EVENT_TIME " + userInput);
			setEditType(EDIT_TYPE.EVENT_TIME);
			parseEditEventTime(userInput, editTaskElements);
			break;
		case START_TIME:
			System.out.println("Debug at START_TIME " + userInput);
			setEditType(EDIT_TYPE.START_TIME);
			parseEditStartTime(editTaskElements);
			break;
		case DEADLINED:
			parseEditDeadLined(editTaskElements);
			setEditType(EDIT_TYPE.DEADLINED);
			break;
		case END_TIME:
			parseEditEndTime(editTaskElements);
			setEditType(EDIT_TYPE.END_TIME);
		case INVALID:
			System.out.println(MESSAGE_EDIT_INPUT_ERROR);
			setEditType(EDIT_TYPE.INVALID);
		}
		
	}


	private void parseEditEndTime(ArrayList<String> editTaskElements) {
		DateAndTimeParser parser = new DateAndTimeParser();
		INDEX_OF_LAST_TO = editTaskElements.lastIndexOf(KEYWORD_TO);
	
		contentToAnalyse = new ArrayList<String>(editTaskElements.subList(INDEX_OF_LAST_TO, editTaskElements.size()));
		newDate = parser.analysePossibleDateElements(contentToAnalyse);	
		setNewEndDate(newDate);
	}


	private void parseEditStartTime(ArrayList<String> editTaskElements) {
		DateAndTimeParser parser = new DateAndTimeParser();
		
		System.out.println("Debug at parseEditStartTime ");
		
		INDEX_OF_LAST_FROM = editTaskElements.lastIndexOf(KEYWORD_FROM);
		
		System.out.println("Debug at parseEditStartTime2 " + INDEX_OF_LAST_FROM);
		contentToAnalyse = new ArrayList<String>(editTaskElements.subList(INDEX_OF_LAST_FROM, editTaskElements.size()));
		
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			System.out.println("Debug @line 98: " + contentToAnalyse.get(i));
		}
		
		newDate = parser.analysePossibleDateElements(contentToAnalyse);	
		
		
		setNewStartDate(newDate);
	}


	private void parseEditDeadLined(ArrayList<String> editTaskElements) {
		DateAndTimeParser parser = new DateAndTimeParser();
		newDate = parser.analysePossibleDateElements(editTaskElements);	
		setNewEndDate(newDate);
	}


	private void parseEditEventTime(String userInput, ArrayList<String> editTasksElements) {
		
		System.out.println("Debug at parseEditEventTime " + editTasksElements.get(0));
		INDEX_OF_LAST_FROM = editTasksElements.indexOf(KEYWORD_FROM);
		INDEX_OF_LAST_TO = editTasksElements.indexOf(KEYWORD_TO);
		
		System.out.println("Debug at INDEX_OF_LAST_FROM " + INDEX_OF_LAST_FROM);
		
		ArrayList<String> temp = new ArrayList<String>(editTaskElements.subList(INDEX_OF_LAST_FROM, INDEX_OF_LAST_TO-1)); 
		
		parseEditStartTime(temp);
		ArrayList<String> temp2 = new ArrayList<String>(editTaskElements.subList(INDEX_OF_LAST_TO, editTaskElements.size()));
		parseEditEndTime(temp2);
	}


	private void parseEditTaskName(ArrayList<String> editTaskElements) {
		
		for (int i = 0; i < editTaskElements.size(); i++) {
			newTaskName += editTaskElements.get(i) + " ";
		}
		
		this.newTaskName = newTaskName.trim();
		setNewTaskName(newTaskName);
	}


	private EDIT_TYPE determineEditType(String userInput) {
		
		if (hasTaskName(userInput)) {
			/*
			 * @Description: edits the content of the task name. 
			 * Example: edit 1 buy sweet
			 */
			if (!hasDeadLined(userInput) && !hasStartTime(userInput) && !hasEndTime(userInput)) {
				return EDIT_TYPE.TASK_NAME;
			}
			/*
			 * @ Description: edits the end time of an event
			 *  Example: edit 1 revise CS2103t from today to 24.07.2016
			 */
			else if (!hasDeadLined(userInput) && hasStartTime(userInput) && hasEndTime(userInput)) {
				return EDIT_TYPE.EVENT_TIME;
			}
			/*
			 * @Description: edits the starting time of an event
			 * Example: edit 1 buy sweet by tuesday
			 */
			else if (hasDeadLined(userInput) && !hasStartTime(userInput) && !hasEndTime(userInput)) {
				return EDIT_TYPE.DEADLINED;
			}
		}/*
		 * @Description: edits the deadline of task 
		 * Example: edit 1 by tomorrow
		 */
		else if (hasDeadLined(userInput) && !hasEndTime(userInput) && !hasStartTime(userInput)) {
			return EDIT_TYPE.DEADLINED;
		}
		/*
		 * @ Description: edits the end time of an event
		 *  Example: edit 1 to 24.07.2016
		 */
		else if (!hasDeadLined(userInput) && hasEndTime(userInput) && !hasStartTime(userInput)) {
			return EDIT_TYPE.END_TIME;
		}/*
		 * @ Description: edits the start time of an event
		 *  Example: edit 1 from 24.07.2016
		 */
		else if (!hasDeadLined(userInput) && !hasEndTime(userInput) && hasStartTime(userInput)) {
			return EDIT_TYPE.START_TIME;
		}
		else if (!hasDeadLined(userInput) && hasStartTime(userInput) && hasEndTime(userInput)) {
			System.out.println("Debug @line175 = true");
			return EDIT_TYPE.EVENT_TIME;
		}
		else {
		}
		return EDIT_TYPE.INVALID;
	}

	/*
	 * @Description: check if user has entered deadlined elements.
	 * 
	 */
	private boolean hasDeadLined(String userInput) {
		if (userInput.contains(KEYWORD_BEFORE) || userInput.contains(KEYWORD_BY) 
			|| userInput.contains(KEYWORD_ON) || userInput.contains(KEYWORD_AT)) {
			return true;
		}
		else {
			System.out.println("Debug at hasDeadLined " + userInput);
			return false;
		}
	}
	
	/*
	 * @Description: check if user has entered an input with event element.
	 * 
	 */
	private boolean hasStartTime(String userInput) {
		System.out.println("Debug at hasStartTime " + userInput);
		return (userInput.contains(KEYWORD_FROM));
	}


	private boolean hasEndTime(String userInput) {
		System.out.println("Debug at hasEndTime " + userInput);
		return (userInput.contains(KEYWORD_TO));
	}

	
	private boolean hasTaskName(String userInput) {
		
		if (userInput.contains(KEYWORD_AT) || userInput.contains(KEYWORD_BEFORE) ||
			userInput.contains(KEYWORD_BY) || userInput.contains(KEYWORD_FROM) ||
			userInput.contains(KEYWORD_ON) || userInput.contains(KEYWORD_TO)) {
			System.out.println("Debug at hasTaskName " + userInput);
			return false;
		}
		else {
			return true;
		}
	}


	private int checkIfValidEditCommandInput(ArrayList<String> editTaskElements) {
		if (editTaskElements.size() < 2) {
			System.out.println(MESSAGE_EDIT_INPUT_ERROR);
		}
		else {
			taskID = Integer.parseInt(editTaskElements.get(0));
			editTaskElements.remove(0);
		}
		return taskID;
	}
	
	private void setNewTaskName(String newTaskName) {
		this.newTaskName = newTaskName;
	}
	
	public String getNewTaskName() {
		return this.newTaskName;
	}
	
	private void setNewStartDate(Date newDate) {
		this.newDate = newDate;
	}
	
	private void setNewEndDate(Date newDate) {
		this.newDate = newDate;
	}
	
	public Date getStartNewDate() {
		return this.newDate;
	}
	
	public Date getEndNewDate() {
		return this.newDate;
	}
	
	private void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	
	public int getTaskID() {
		return this.taskID;
	}
	
	private void setEditType(EDIT_TYPE editType) {
		this.editType = editType;
	}
	
	public EDIT_TYPE getEditType() {
		return this.editType;
	}
}
