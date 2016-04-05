//@@author: Hao Jie
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
	private String STRING_HASH_TAG = "#";
	private int INDEX_OF_LAST_TO = -1;
	private int INDEX_OF_LAST_AT = -1;
	private int INDEX_OF_LAST_BEFORE = -1;
	private int INDEX_OF_LAST_ON = -1;
	private int INDEX_OF_LAST_BY = -1;
	private int INDEX_OF_LAST_FROM = -1;
	
	private EDIT_TYPE editType;
	private ArrayList<String> editTaskElements;
	private ArrayList<String> newTag;
	
	private Date date;
	private DateTimeParser dt;
	
	public EditParser() {
		newTag = new ArrayList<String>();
		dt = new DateTimeParser();
		date = new Date();
	}

	
	protected CommandUtils executeEditParser(CommandUtils commandUtil, String userInput) {
		String[] editElements = userInput.replaceAll("[:-]", "").split("[\\s+]");
		editTaskElements = new ArrayList<String>(Arrays.asList(editElements));
		newTaskName = dt.checkForAbbreviation(editTaskElements);
		System.out.println("ExecuteEditParser : " + newTaskName);
		String[] updatedElements = newTaskName.replaceAll("[:-]", "").split("\\s+");
		editTaskElements = new ArrayList<String>(Arrays.asList(updatedElements));
		
		for (int i = 0; i < editTaskElements.size(); i++) {
			System.out.println(editTaskElements.get(i));
		}
		
		userInput = extractTaskID(commandUtil, editTaskElements);
		System.out.println("Debug at EditParser :" + userInput);
		commandUtil = determineEditType(commandUtil, userInput);
		editType = commandUtil.getEditType();
		
		switch(editType) {
		
		case TASK_NAME:
			System.out.println("Debug at TASK_NAME " + userInput);
			return parseEditTaskName(commandUtil, userInput);
		case EVENT_TIME:
			System.out.println("Debug at EVENT_TIME " + userInput);
			return parseEditEventTime(commandUtil, userInput);
		case START_TIME:
			System.out.println("Debug at START_TIME " + userInput);
			return parseEditStartTime(commandUtil, userInput);
		case DEADLINED:
			System.out.println("Debug at DEADLINED " + userInput);
			return parseEditDeadLined(commandUtil, userInput);
		case END_TIME:
			return parseEditEndTime(commandUtil, userInput);
		case TAG:
			System.out.println("Debug at TAG " + userInput);
			return parserEditTag(commandUtil, userInput);
		case INVALID:
			break;
		}
		return commandUtil;
	}

	private CommandUtils parserEditTag(CommandUtils commandUtil, String userInput) {
		String[] str = userInput.trim().split("\\s+");
		assert str.length <= 3 && str.length > 0;
		
		if (str[0].startsWith(STRING_HASH_TAG) && str[2].startsWith(STRING_HASH_TAG)) {
			commandUtil.setOldTag(str[0].substring(1, str[0].length()));
			newTag.add(str[2].substring(1, str[0].length()));
			commandUtil.setTaskTag(newTag);
		}
		else {
			commandUtil.setEditType(EDIT_TYPE.INVALID);
		}
		return commandUtil;
	}


	private CommandUtils parseEditEndTime(CommandUtils commandUtil, String userInput) {
		System.out.println("Debug at parseEditEndTime ");
		INDEX_OF_LAST_TO = userInput.lastIndexOf(KEYWORD_TO);
		String temp = userInput.substring(INDEX_OF_LAST_TO, userInput.length());
		commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_TO)));
		List<Date> dates = new PrettyTimeParser().parse(temp);
		commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
		return commandUtil;
	}


	private CommandUtils parseEditStartTime(CommandUtils commandUtil, String userInput) {
		System.out.println("Debug at parseEditStartTime ");
		INDEX_OF_LAST_FROM = userInput.lastIndexOf(KEYWORD_FROM);
		String temp = userInput.substring(INDEX_OF_LAST_FROM, userInput.length());
		commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_FROM)));
		List<Date> dates = new PrettyTimeParser().parse(temp);
		commandUtil.setStartTime(dates.get(0));
		return commandUtil;
	}


	private CommandUtils parseEditDeadLined(CommandUtils commandUtil, String userInput) {
		System.out.println("Debug at parseEditDeadlined ");
		
		String temp = "";
		INDEX_OF_LAST_ON = userInput.lastIndexOf(KEYWORD_ON);
		INDEX_OF_LAST_AT = userInput.lastIndexOf(KEYWORD_AT);
		
		if (userInput.lastIndexOf(KEYWORD_BY) != -1 && userInput.lastIndexOf(KEYWORD_AT) == -1) {
			INDEX_OF_LAST_BY = userInput.lastIndexOf(KEYWORD_BY);
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_BY)));
			temp = userInput.substring(INDEX_OF_LAST_BY, userInput.length());
		}
		
		else if (userInput.lastIndexOf(KEYWORD_ON) != -1 && userInput.lastIndexOf(KEYWORD_AT) == -1) {
			INDEX_OF_LAST_ON = userInput.lastIndexOf(KEYWORD_ON);
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_ON)));
			temp = userInput.substring(INDEX_OF_LAST_ON, userInput.length());
		
		}
	
		else if (userInput.lastIndexOf(KEYWORD_AT) != -1 && userInput.lastIndexOf(KEYWORD_ON) == -1) {
			INDEX_OF_LAST_AT = userInput.lastIndexOf(KEYWORD_AT);
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_AT)));
			temp = userInput.substring(INDEX_OF_LAST_AT, userInput.length());
		}
		
		else if (INDEX_OF_LAST_ON != -1 && INDEX_OF_LAST_ON < INDEX_OF_LAST_AT) {
			commandUtil.setTaskName(userInput.substring(0, INDEX_OF_LAST_ON));
			temp = userInput.substring(INDEX_OF_LAST_ON, INDEX_OF_LAST_AT);
			String temp2 = userInput.substring(INDEX_OF_LAST_AT, userInput.length());
			List<Date> dateTemp = new PrettyTimeParser().parse(temp);
			List<Date> dateTemp2 = new PrettyTimeParser().parse(temp2);
			if (dateTemp.size() == 0 && dateTemp2.size() != 0) {
				commandUtil.setTaskName(newTaskName + " " + temp.trim());
				temp = temp2;
			}
			else if (dateTemp.size() != 0 && dateTemp2.size() == 0) {
				commandUtil.setTaskName(newTaskName + " " + temp2.trim());
			}
			
		}
		else if (INDEX_OF_LAST_AT != -1 && INDEX_OF_LAST_ON > INDEX_OF_LAST_AT) {
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_AT)));
			temp = userInput.substring(INDEX_OF_LAST_AT, INDEX_OF_LAST_ON);
			String temp2 = userInput.substring(INDEX_OF_LAST_ON, userInput.length());
			List<Date> dateTemp = new PrettyTimeParser().parse(temp);
			List<Date> dateTemp2 = new PrettyTimeParser().parse(temp2);
			if (dateTemp.size() == 0 && dateTemp2.size() != 0) {
				commandUtil.setTaskName(newTaskName + " " + temp.trim());
				temp = temp2;
			}
			else if (dateTemp.size() != 0 && dateTemp2.size() == 0) {
				commandUtil.setTaskName(newTaskName + " " + temp2.trim());
			}
			
		}
			
		else if (userInput.lastIndexOf(KEYWORD_BEFORE) != -1) {
			INDEX_OF_LAST_BEFORE = userInput.lastIndexOf(KEYWORD_BEFORE);
			commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_BEFORE)));
			temp = userInput.substring(INDEX_OF_LAST_BEFORE, userInput.length());
		}
		
		List<Date> dates = new PrettyTimeParser().parse(temp);
		if (dates.size() != 0) {
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
		}
		return commandUtil;
	}


	private CommandUtils parseEditEventTime(CommandUtils commandUtil, String userInput) {
		System.out.println("Debug at parseEditEventTime ");
		INDEX_OF_LAST_FROM = userInput.lastIndexOf(KEYWORD_FROM);
		commandUtil.setTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_FROM)));
		String temp = userInput.substring(INDEX_OF_LAST_FROM, userInput.length());
		List<Date> dates = new PrettyTimeParser().parse(temp);
		System.out.println("Debug at parseEditEventTime :" + dates.get(0) + " " + dates.get(1));
		if (dates.size() == 2) {
			commandUtil.setStartTime(dates.get(0));
			commandUtil.setEndTime(dt.checkAndSetDefaultEndTime(dates.get(1), date));
		}
		return commandUtil;
	}

	private CommandUtils parseEditTaskName(CommandUtils commandUtil, String userTaskIndex) {
		commandUtil.setTaskName(userTaskIndex.trim());
		return commandUtil;
	}


	private CommandUtils determineEditType(CommandUtils commandUtil, String userInput) {
		
		if (hasTaskName(userInput) && !userInput.trim().startsWith(STRING_HASH_TAG)) {
			/*
			 * @Description: edits the content of the task name. 
			 * Example: edit 1 buy sweet
			 */
			if (!hasDeadLined(userInput) && !hasStartTime(userInput) && !hasEndTime(userInput)) {
				commandUtil.setEditType(EDIT_TYPE.TASK_NAME);
			}
			/*
			 * @ Description: edits the end time of an event
			 *  Example: edit 1 revise CS2103t from today to 24.07.2016
			 */
			else if (!hasDeadLined(userInput) && hasStartTime(userInput) && hasEndTime(userInput)) {
				commandUtil.setEditType(EDIT_TYPE.EVENT_TIME);
			}
			/*
			 * @Description: edits the starting time of an event
			 * Example: edit 1 buy sweet by tuesday
			 */
			else if (hasDeadLined(userInput) && !hasStartTime(userInput) && !hasEndTime(userInput)) {
				commandUtil.setEditType(EDIT_TYPE.DEADLINED);
			}
		}/*
		 * @Description: edits the deadline of task 
		 * Example: edit 1 by tomorrow
		 */
		else if (hasDeadLined(userInput) && !hasEndTime(userInput) && !hasStartTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.DEADLINED);
		}
		/*
		 * @ Description: edits the end time of an event
		 *  Example: edit 1 to 24.07.2016
		 */
		else if (!hasDeadLined(userInput) && hasEndTime(userInput) && !hasStartTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.END_TIME);
		}/*
		 * @ Description: edits the start time of an event
		 *  Example: edit 1 from 24.07.2016
		 */
		else if (!hasDeadLined(userInput) && !hasEndTime(userInput) && hasStartTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.START_TIME);
		}
		else if (!hasDeadLined(userInput) && hasStartTime(userInput) && hasEndTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			commandUtil.setEditType(EDIT_TYPE.EVENT_TIME);
		}
		else if (userInput.trim().startsWith(STRING_HASH_TAG)) {
			System.out.println("Debug @line253 = true");
			commandUtil.setEditType(EDIT_TYPE.TAG);
		}
		else {
			commandUtil.setEditType(EDIT_TYPE.INVALID);
		}
		return commandUtil;
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
		return (userInput.contains(KEYWORD_TO) && dates.size() != 0
				&& !userInput.contains(STRING_HASH_TAG)) ? true : false;
	}

	
	private boolean hasTaskName(String userInput) {
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		DateTimeParser dt = new DateTimeParser();
		boolean hasDateTime = dt.hasDateAndTimeElements(userInput);
		return (dates.size() == 0 || hasDateTime == false) ? true : false;
	}


	private String extractTaskID(CommandUtils commandUtil, ArrayList<String> editTaskElements) {
		String temp = "";
		if (editTaskElements.size() < 2) {
			return temp;
		}
		else if (!editTaskElements.get(0).startsWith(STRING_HASH_TAG)){
			commandUtil.setTaskID(editTaskElements.get(0));
			editTaskElements.remove(0);
			temp = toStringTaskElements(editTaskElements);
		}
		else if (editTaskElements.get(0).startsWith(STRING_HASH_TAG)) {
			temp = toStringTaskElements(editTaskElements);
		}
		return temp;
	}
	
	private String toStringTaskElements(ArrayList<String> taskNameArrayList) {
		String name = "";
		for (int i = 0; i < taskNameArrayList.size(); i++) {
			name += " " + taskNameArrayList.get(i); 
		}
		return name;
	}

}
