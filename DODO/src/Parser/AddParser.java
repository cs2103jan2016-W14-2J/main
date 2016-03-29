package Parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import Command.*;
import Task.*;

public class AddParser {
	private final String KEYWORD_FROM = "from ";
	private final String KEYWORD_TO = " to ";
	private final String KEYWORD_ON = " on ";
	private final String KEYWORD_BY = " by ";
	private final String KEYWORD_AT = " at ";
	private final String KEYWORD_BEFORE = " before ";

	private int LAST_POSITION_OF_FROM = -1;
	private int LAST_POSITION_OF_TO = -1;
	private int LAST_POSITION_OF_ON = -1;
	private int LAST_POSITION_OF_AT = -1;
	private int LAST_POSITION_OF_BEFORE = -1;
	private int LAST_POSITION_OF_BY = -1;
	
	private String userTask = "";
	private String taskName = "";
	private String contentToAnalyse;
	private COMMAND_TYPE commandType;
	private TASK_TYPE taskType;
	
	private Date startTime;
	private Date endTime;
	
	private ArrayList<String> taskItems;

	public AddParser(String userTask) {
		this.userTask = userTask;
		executeAddParser(userTask);
	}
	
	private void executeAddParser(String userTask) {
		String[] str = userTask.split("\\s+");
		taskItems = new ArrayList<String>(Arrays.asList(str));
		taskName = checkForAbbreviation(taskItems);

		taskType = determineTaskType(taskItems, taskName);
		System.out.println("At line 53 : " + taskType);
		switch(taskType) {
			case DEADLINED:
				parseDEADLINED();
				break;
			case FLOATING:
				parseFloating(taskItems, taskName);
				break;
			case EVENT:
				parseEVENT();
				break;
			default:
				System.exit(1);
		}
		
	}

	private String checkForAbbreviation(ArrayList<String> taskItems) {
		System.out.println("checkForAbbrevation");
		String name = "";
		DateTimeParser dt = new DateTimeParser();
		name = dt.processToday(taskItems);
		name = dt.processTomorrow(taskItems);
		name = dt.processYesterday(taskItems);
		name = dt.processAt(taskItems);
		return name;
	}

	public TASK_TYPE determineTaskType(ArrayList<String> taskItems, String taskName) {
		if (checkIfFloatingTask(taskItems, taskName)) {
			setTaskType(TASK_TYPE.FLOATING);
			return TASK_TYPE.FLOATING;
		}
		else if (checkIfDeadlinedTask(taskItems, taskName)) {
			setTaskType(TASK_TYPE.DEADLINED);
			return TASK_TYPE.DEADLINED;
		}
		else if (checkIfEventTask(taskName)) {
			setTaskType(TASK_TYPE.EVENT);
			return TASK_TYPE.EVENT;
		}
		else {
			return null;
		}
	}

	private boolean checkIfEventTask(String taskName) {
	
		LAST_POSITION_OF_FROM = taskName.toLowerCase().lastIndexOf(KEYWORD_FROM);
		LAST_POSITION_OF_TO = taskName.toLowerCase().lastIndexOf(KEYWORD_TO);

		// add study from <startDate> to <endDate>
		if (LAST_POSITION_OF_FROM < LAST_POSITION_OF_TO && LAST_POSITION_OF_FROM != -1) {
			if (checkForDateAndTime(taskName, LAST_POSITION_OF_FROM, LAST_POSITION_OF_TO)) {
				return true;
			}
			else {
				setTaskType(TASK_TYPE.FLOATING);
				return false;
			}
		}
		else if (LAST_POSITION_OF_FROM > LAST_POSITION_OF_TO) {
			if (checkForDateAndTime(taskName, LAST_POSITION_OF_FROM, taskName.length())) {
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

	private boolean checkForDateAndTime(String taskName, int LAST_POSITION_OF_FROM, int LAST_POSITION_OF_TO) {	
		String str = taskName.substring(LAST_POSITION_OF_FROM, taskName.length());
		System.out.println("Test checkForDateAndTime : " + str);
		List<Date> dates = new PrettyTimeParser().parse(str);
		return (dates.size() != 0) ? true : false; 
	}

	private boolean checkIfDeadlinedTask(ArrayList<String> taskItems, String taskName) {
		getKeywordPosition(taskName);
		return (LAST_POSITION_OF_AT != -1 || LAST_POSITION_OF_ON != -1 || 
			LAST_POSITION_OF_BEFORE != -1 || LAST_POSITION_OF_BY != -1) ? true : false;
	}

	private void getKeywordPosition(String taskName) {
		ArrayList<String> temp = new ArrayList<String>(convertArrayListToLowerCase(taskName));
		LAST_POSITION_OF_AT = temp.lastIndexOf("at");
		LAST_POSITION_OF_ON = temp.lastIndexOf("on");
		LAST_POSITION_OF_BEFORE = temp.lastIndexOf("before");
		LAST_POSITION_OF_BY = temp.lastIndexOf("by");
	}

	private ArrayList<String> convertArrayListToLowerCase(String taskName) {
		String[] str = taskName.toLowerCase().split("\\s+");
		ArrayList<String> temp = new ArrayList<String>(Arrays.asList(str));
		return temp;
	}

	private boolean checkIfFloatingTask(ArrayList<String> taskItems, String taskName) {
		return (!checkIfEventTask(taskName) && !checkIfDeadlinedTask(taskItems, taskName)) ? true : false;
	}
	

	private void parseEVENT() {
		String str = "";
		Date date = new Date();
		
		// Parse string with "from" ... "to".
		if (LAST_POSITION_OF_FROM < LAST_POSITION_OF_TO) {
			str = taskName.substring(LAST_POSITION_OF_FROM, taskName.length());
			List<Date> dates = new PrettyTimeParser().parse(str);
			if (dates.size() == 2) {
				System.out.println("Test parseEvent : " + taskName);
				
				setStartTime(dates.get(0));
				setEndTime(checkAndSetDefaultEndTime(dates.get(1), date));
				setTaskName(taskName.substring(0, LAST_POSITION_OF_FROM));
			}
			
			else if (dates.size() == 1) {
				setStartTime(dates.get(0));
				setTaskName(taskName.substring(0, LAST_POSITION_OF_FROM));
			}
			else if (dates.size() == 0) {
				setTaskName(taskName);
			}
			
		}
		else if (taskName.toLowerCase().lastIndexOf(KEYWORD_TO) < taskName.toLowerCase().lastIndexOf(KEYWORD_FROM)) {
			str = taskName.substring(LAST_POSITION_OF_FROM, taskName.length());
			List<Date> dates = new PrettyTimeParser().parse(str);
			
			if (dates.size() == 1) {
				setStartTime(dates.get(0));
				setTaskName(taskName.substring(0, taskName.toLowerCase().lastIndexOf(KEYWORD_FROM)));
			}
		}
	}

	
	private void parseDEADLINED() {
		getKeywordPosition(taskName);
		Date date = new Date();
		
		if (LAST_POSITION_OF_BY != -1 && LAST_POSITION_OF_AT == -1) {
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_BY)));
			System.out.println("DEBUG @line 193:" + taskName);
			setTaskName(taskName);
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_BY, taskItems.size())));
			System.out.println("DEBUG2 :" + contentToAnalyse);
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: submit assignment 1 by tomorrow
			if (dates.size() != 0 && !contentToAnalyse.contains(" next ")) {
				System.out.println("TEST test");
			//	setEndTime(dates.get(0));
				setEndTime(checkAndSetDefaultEndTime(dates.get(0), date));
			}
			// Example: submit assignment by next 2 weeks
			else if (dates.size() != 0 && contentToAnalyse.contains(" next ")) {
			//	setEndTime(dates.get(1));
				setEndTime(checkAndSetDefaultEndTime(dates.get(1), date));
			}
			// Example: take a walk by the beach
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
	
		}
		
		else if (LAST_POSITION_OF_ON != -1 && LAST_POSITION_OF_AT == -1) {
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_ON)));
			System.out.println("DEBUG @line213:" + taskName);
			setTaskName(taskName);
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, taskItems.size())));
			System.out.println("DEBUG :" + contentToAnalyse);
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: revise on cs2130t chapter 1 on sunday
			if (dates.size() != 0) {
			//	setEndTime(dates.get(0));
				setEndTime(checkAndSetDefaultEndTime(dates.get(0), date));
			}
			// Example: take a walk by the beach
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		
		}
		else if (LAST_POSITION_OF_ON != -1 && LAST_POSITION_OF_AT != -1 && LAST_POSITION_OF_ON < LAST_POSITION_OF_AT) {
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_ON)));
			System.out.println("DEBUG @250:" + taskName);
			setTaskName(taskName);
			
			String str = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, LAST_POSITION_OF_AT)));
			List<Date> date1 = new PrettyTimeParser().parse(str);
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
			List<Date> date2 = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: revise on cs2130t at 2pm
			if (date1.size() == 0 && date2.size() != 0) {
				setTaskName(toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT))));
			//	setEndTime(date2.get(0));
				setEndTime(checkAndSetDefaultEndTime(date2.get(0), date));
			}
			// Example: meet hannah on 4 april, 2016 at suntec city
			else if (date1.size() != 0 && date2.size() == 0){
				setTaskName(taskName + " " + toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size()))));
			//	setEndTime(date1.get(0));
				setEndTime(checkAndSetDefaultEndTime(date1.get(0), date));
			}
			// Example: meet hannah on 4th of April 2016 at 2pm
			else if (date1.size() != 0 && date2.size() != 0) {
				setTaskName(toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_ON))));
				contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, taskItems.size())));
				List<Date> date3 = new PrettyTimeParser().parse(contentToAnalyse);
			//	setEndTime(date3.get(0));
				setEndTime(checkAndSetDefaultEndTime(date3.get(0), date));
			}
			// Example: sleep on the study bench at the void deck
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		
		}
		else if (LAST_POSITION_OF_ON != -1 && LAST_POSITION_OF_AT != -1 && LAST_POSITION_OF_ON > LAST_POSITION_OF_AT) {
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT)));
			System.out.println("DEBUG @250:" + taskName);
			setTaskName(taskName);
			
			String str = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, LAST_POSITION_OF_ON)));
			List<Date> date1 = new PrettyTimeParser().parse(str);
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, taskItems.size())));
			List<Date> date2 = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: revise at chua chu kang on 24/7/2016
			if (date1.size() == 0 && date2.size() != 0) {
				setTaskName(toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_ON))));
			//	setEndTime(date2.get(0));
				setEndTime(checkAndSetDefaultEndTime(date2.get(0), date));
			}
			// Example: meet hannah at 7pm on top of the building
			else if (date1.size() != 0 && date2.size() == 0){
				setTaskName(taskName + " " + toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, taskItems.size()))));
			//	setEndTime(date1.get(0));
				setEndTime(checkAndSetDefaultEndTime(date1.get(0), date));
			}
			// Example: meet hannah at 2pm on 4th of April 2016 
			else if (date1.size() != 0 && date2.size() != 0) {
				setTaskName(toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT))));
				contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
				List<Date> date3 = new PrettyTimeParser().parse(contentToAnalyse);
			//	setEndTime(date3.get(0));
				setEndTime(checkAndSetDefaultEndTime(date3.get(0), date));
			}
			// Example: sleep on the study bench at the void deck
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		
		}
		else if (LAST_POSITION_OF_AT != -1 && LAST_POSITION_OF_ON == -1) {
			String tempTaskName = "";
			String confirmTaskName = "";
			
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT)));
			System.out.println("DEBUG @290:" + taskName);
			// Example: meet Hannah at 7pm at Jurong East
			if (taskName.lastIndexOf(KEYWORD_AT) != -1) {
				tempTaskName = taskName.substring(taskName.lastIndexOf(KEYWORD_AT), taskName.length()).trim();
				confirmTaskName = taskName.substring(0, taskName.lastIndexOf(KEYWORD_AT)).trim();
			}
			else {
				setTaskName(taskName);
			}
			
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			List<Date> date1 = new PrettyTimeParser().parse(tempTaskName);
			
			// Example: meet hannah at the beach at 2359hrs
			if (dates.size() != 0 && date1.size() == 0) {
			//	setEndTime(dates.get(0));
				setEndTime(checkAndSetDefaultEndTime(dates.get(0), date));
				setTaskName(confirmTaskName + " " + tempTaskName);
			}
			// Example: meet hannah at 2359hrs at the Padang
			else if (dates.size() == 0 && date1.size() != 0) {
				setEndTime(checkAndSetDefaultEndTime(date1.get(0), date));
				setTaskName(confirmTaskName + " " + contentToAnalyse);
			}
			// Example: take a walk by the beach
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		}
			
		else if (LAST_POSITION_OF_BEFORE != -1) {
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_BEFORE)));
			setTaskName(taskName);
			System.out.println("DEBUG BEFORE:" + taskName);
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_BEFORE, taskItems.size())));
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: submit assignment 1 before monday 2359hrs
			if (dates.size() != 0 && !contentToAnalyse.contains(" next ")) {
			//	setEndTime(dates.get(0));
				setEndTime(checkAndSetDefaultEndTime(dates.get(0), date));
			}
			// Example: submit assignment by next 2 weeks
			else if (dates.size() != 0 && contentToAnalyse.contains(" next ")) {
			//	setEndTime(dates.get(1));
				setEndTime(checkAndSetDefaultEndTime(dates.get(0), date));
			}
			// Example: let the kids eat before us.
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		}
			
	}

	private Date checkAndSetDefaultEndTime(Date date, Date currentTime) {
		Date newDate = date;
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String dateWithoutTime = df.format(date);
		
		try {
			Date dateWithoutDate = sdf.parse(sdf.format(date));
			Date currentWithoutDate = sdf.parse(sdf.format(currentTime));

			if (dateWithoutDate.equals(currentWithoutDate)) {
				String newDateWithTime = dateWithoutTime + " " + "23:59:59";
				newDate = sf.parse(newDateWithTime);
			} 
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate;
		
	}

	private void parseFloating(ArrayList<String> taskItems, String userTask) {
		
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		System.out.println("TEST parseFloating :" + dates.size());
		if (dates.size() == 0) {
			setTaskName(userTask);
		}
		else {
			if (dates.size() == 1) {
				setEndTime(dates.get(0));
				extractDateElement(taskItems, userTask);
			}
			else if (dates.size() > 1){
				setEndTime(dates.get(1));
				setStartTime(dates.get(0));
				extractDateElement(taskItems, userTask);
				setTaskType(TASK_TYPE.EVENT);
			}
		}
	
	}
	
	private void extractDateElement(ArrayList<String> taskItems, String userTask) {
		String temp = "";
		DateTimeParser dt = new DateTimeParser();
		temp = dt.removeTheDayAfterTomorrow(userTask);
		temp = dt.removeTomorrow(temp);
		temp = dt.removeToday(temp);
		temp = dt.removeThisComingWeekday(temp);
		temp = dt.removeNextFewDays(temp);
		temp = dt.removeNextWeek(temp);
		temp = dt.removeTime(temp);
		temp = dt.removeDate(temp);
		setTaskName(userTask);
		if (temp.trim().equals(userTask.trim())) {
			setTaskType(TASK_TYPE.FLOATING);
		}
		else {
			setTaskType(TASK_TYPE.DEADLINED);
		}
	}

	/*
	 * @param: an arraylist of task elements.
	 * @description: concatenate the content of a task input together
	 * @return: a string of task name.
	 */
	private String toStringTaskElements(ArrayList<String> taskNameArrayList) {
		String name = "";
		for (int i = 0; i < taskNameArrayList.size(); i++) {
			name += taskNameArrayList.get(i) + " "; 
		}
		return name.trim();
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