package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

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
	private int INDEX_OF_LAST_AT = -1;
	private int INDEX_OF_LAST_BEFORE = -1;
	private int INDEX_OF_LAST_ON = -1;
	private int INDEX_OF_LAST_BY = -1;
	private int INDEX_OF_LAST_FROM = -1;
	
	private int taskID;
	private Date newStartDate;
	private Date newEndDate;
	private EDIT_TYPE editType;
	private String MESSAGE_EDIT_INPUT_ERROR = "You have entered an invalid command.";
	private ArrayList<String> editTaskElements;
	
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
			parseEditTaskName(userInput);
			break;
		case EVENT_TIME:
			System.out.println("Debug at EVENT_TIME " + userInput);
			setEditType(EDIT_TYPE.EVENT_TIME);
			parseEditEventTime(userInput);
			break;
		case START_TIME:
			System.out.println("Debug at START_TIME " + userInput);
			setEditType(EDIT_TYPE.START_TIME);
			parseEditStartTime(userInput);
			break;
		case DEADLINED:
			parseEditDeadLined(userInput);
			setEditType(EDIT_TYPE.DEADLINED);
			break;
		case END_TIME:
			parseEditEndTime(userInput);
			setEditType(EDIT_TYPE.END_TIME);
			break;
		case INVALID:
			System.out.println(MESSAGE_EDIT_INPUT_ERROR);
			setEditType(EDIT_TYPE.INVALID);
		}
		
	}

	private void parseEditEndTime(String userInput) {
		System.out.println("Debug at parseEditEndTime ");
		INDEX_OF_LAST_TO = userInput.lastIndexOf(KEYWORD_TO);
		String temp = userInput.substring(INDEX_OF_LAST_TO, userInput.length());
		setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_TO)));
		List<Date> dates = new PrettyTimeParser().parse(temp);
		setNewEndDate(dates.get(0));
	}


	private void parseEditStartTime(String userInput) {
		System.out.println("Debug at parseEditStartTime ");
		INDEX_OF_LAST_FROM = userInput.lastIndexOf(KEYWORD_FROM);
		String temp = userInput.substring(INDEX_OF_LAST_FROM, userInput.length());
		setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_FROM)));
		List<Date> dates = new PrettyTimeParser().parse(temp);
		setNewStartDate(dates.get(0));
	}


	private void parseEditDeadLined(String userInput) {
		System.out.println("Debug at parseEditDeadlined ");
		String temp = "";
		
		if (userInput.lastIndexOf(KEYWORD_BY) != -1 && userInput.lastIndexOf(KEYWORD_AT) == -1) {
			INDEX_OF_LAST_BY = userInput.lastIndexOf(KEYWORD_BY);
			setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_BY)));
			temp = userInput.substring(INDEX_OF_LAST_BY, userInput.length());
		}
		
		else if (userInput.lastIndexOf(KEYWORD_ON) != -1) {
			INDEX_OF_LAST_ON = userInput.lastIndexOf(KEYWORD_ON);
			setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_ON)));
			temp = userInput.substring(INDEX_OF_LAST_ON, userInput.length());
		
		}
	
		else if (userInput.lastIndexOf(KEYWORD_AT) != -1 && userInput.lastIndexOf(KEYWORD_ON) == -1) {
			INDEX_OF_LAST_AT = userInput.lastIndexOf(KEYWORD_AT);
			setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_AT)));
			temp = userInput.substring(INDEX_OF_LAST_AT, userInput.length());
		}
			
		else if (userInput.lastIndexOf(KEYWORD_BEFORE) != -1) {
			INDEX_OF_LAST_BEFORE = userInput.lastIndexOf(KEYWORD_BEFORE);
			setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_BEFORE)));
			temp = userInput.substring(INDEX_OF_LAST_BEFORE, userInput.length());
		}
		
		List<Date> dates = new PrettyTimeParser().parse(temp);
		if (dates.size() != 0) {
			setNewEndDate(dates.get(0));
		}
	}


	private void parseEditEventTime(String userInput) {
		System.out.println("Debug at parseEditEventTime ");
		INDEX_OF_LAST_FROM = userInput.lastIndexOf(KEYWORD_FROM);
		setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_FROM)));
		String temp = userInput.substring(INDEX_OF_LAST_FROM, userInput.length());
		List<Date> dates = new PrettyTimeParser().parse(temp);
		System.out.println("Debug at parseEditEventTime :" + dates.get(0) + " " + dates.get(1));
		if (dates.size() == 2) {
			setNewStartDate(dates.get(0));
			setNewEndDate(dates.get(1));
		}
	}

	private void parseEditTaskName(String userTaskIndex) {
		setNewTaskName(userTaskIndex.trim());
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
			System.out.println("Debug at hasDeadLined true");
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
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		if (userInput.contains(KEYWORD_BEFORE) || userInput.contains(KEYWORD_BY) 
			|| userInput.contains(KEYWORD_ON) || userInput.contains(KEYWORD_AT)) {
			return (dates.size() != 0) ? true : false;
		}
		else {
			return false;
		}
	}
	
	/*
	 * @Description: check if user has entered an input with event element.
	 * 
	 */
	private boolean hasStartTime(String userInput) {
		System.out.println("Debug at hasStartTime " + userInput);
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		return (userInput.contains(KEYWORD_FROM) && dates.size() != 0) ? true : false;
	}


	private boolean hasEndTime(String userInput) {
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		return (userInput.contains(KEYWORD_TO) && dates.size() != 0) ? true : false;
	}

	
	private boolean hasTaskName(String userInput) {
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		return (dates.size() == 0) ? true : false;
/*		if (userInput.contains(KEYWORD_AT) || userInput.contains(KEYWORD_BEFORE) ||
			userInput.contains(KEYWORD_BY) || userInput.contains(KEYWORD_FROM) ||
			userInput.contains(KEYWORD_ON) || userInput.contains(KEYWORD_TO)) {
			System.out.println("Debug at hasTaskName " + userInput);
			return false;
		}
		else {
			return true;
		}
*/	}


	private int checkIfValidEditCommandInput(ArrayList<String> editTaskElements) {
		if (editTaskElements.size() < 2) {
			setEditType(EDIT_TYPE.INVALID);
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
	
	private void setNewStartDate(Date newStartDate) {
		this.newStartDate = newStartDate;
	}
	
	private void setNewEndDate(Date newEndDate) {
		this.newEndDate = newEndDate;
	}
	
	public Date getStartNewDate() {
		return this.newStartDate;
	}
	
	public Date getEndNewDate() {
		return this.newEndDate;
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
