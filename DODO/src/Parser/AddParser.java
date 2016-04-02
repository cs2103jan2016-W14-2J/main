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
	private DateTimeParser dt = new DateTimeParser();
	
	private ArrayList<String> taskItems;

	public AddParser(String userTask) {
		this.userTask = userTask;
		executeAddParser(userTask);
	}
	
	private void executeAddParser(String userTask) {
		String[] str = userTask.split("\\s+");
		taskItems = new ArrayList<String>(Arrays.asList(str));
		taskName = checkForAbbreviation(taskItems);
		taskName = checkForDateElement(taskItems);

		taskType = determineTaskType(taskItems, taskName);
		System.out.println("At line 53 : " + taskType);
		switch(taskType) {
			case DEADLINED:
				taskName = parseDEADLINED();
				System.out.println("@line 59 :" + taskName);
				parseFloating(taskItems, taskName);
				break;
			case FLOATING:
				parseFloating(taskItems, taskName);
				break;
			case EVENT:
				taskName = parseEVENT();
				parseFloating(taskItems, taskName);
				break;
			default:
				System.exit(1);
		}
		
	}

	private String checkForDateElement(ArrayList<String> taskItems) {
		for (int i = 0; i < taskItems.size(); i++) {
			try {
				DateFormat srcDf = new SimpleDateFormat("dd/MM/yyyy");
				Date date = srcDf.parse(taskItems.get(i));
				DateFormat destDf = new SimpleDateFormat("yyyy/MM/dd");
				taskItems.set(i, destDf.format(date));
			}
			catch (ParseException e) {				
			}
			try {
				DateFormat srcDf = new SimpleDateFormat("dd.MM.yyyy");
				Date date = srcDf.parse(taskItems.get(i));
				DateFormat destDf = new SimpleDateFormat("yyyy/MM/dd");
				taskItems.set(i, destDf.format(date));
			}
			catch (ParseException e) {				
			}
			
			try {
				DateFormat srcDf = new SimpleDateFormat("dd-MM-yyyy");
				Date date = srcDf.parse(taskItems.get(i));
				DateFormat destDf = new SimpleDateFormat("yyyy/MM/dd");
				taskItems.set(i, destDf.format(date));
			}
			catch (ParseException e) {				
			}
			
		}
		return toStringTaskElements(taskItems);
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
		LAST_POSITION_OF_FROM = temp.lastIndexOf("from");
	}

	private ArrayList<String> convertArrayListToLowerCase(String taskName) {
		String[] str = taskName.toLowerCase().split("\\s+");
		ArrayList<String> temp = new ArrayList<String>(Arrays.asList(str));
		return temp;
	}

	private boolean checkIfFloatingTask(ArrayList<String> taskItems, String taskName) {
		return (!checkIfEventTask(taskName) && !checkIfDeadlinedTask(taskItems, taskName)) ? true : false;
	}
	

	private String parseEVENT() {
		String str = "";
		Date date = new Date();
		LAST_POSITION_OF_AT = taskName.lastIndexOf(KEYWORD_AT);
		System.out.println("DEBUG @line 201:" + LAST_POSITION_OF_AT);
		
		// Parse string with "from" ... "to".
		if (LAST_POSITION_OF_FROM < LAST_POSITION_OF_TO && LAST_POSITION_OF_AT == -1) {
			str = taskName.substring(LAST_POSITION_OF_FROM, taskName.length());
			List<Date> dates = new PrettyTimeParser().parse(str);
			if (dates.size() == 2) {
				System.out.println("Test parseEvent : " + dates.get(1));
				taskName = taskName.substring(0, LAST_POSITION_OF_FROM);
				setStartTime(dates.get(0));
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(1), date));
				setTaskName(taskName);
			}
			
			else if (dates.size() == 1) {
				taskName = taskName.substring(0, LAST_POSITION_OF_FROM);
				setStartTime(dates.get(0));
				setTaskName(taskName);
			}
			else if (dates.size() == 0) {
				setTaskName(taskName);
			}
			
		}
		else if (taskName.toLowerCase().lastIndexOf(KEYWORD_TO) < taskName.toLowerCase().lastIndexOf(KEYWORD_FROM)
				&& LAST_POSITION_OF_AT == -1) {
			str = taskName.substring(LAST_POSITION_OF_FROM, taskName.length());
			List<Date> dates = new PrettyTimeParser().parse(str);
			
			if (dates.size() == 1) {
				taskName = taskName.substring(0, taskName.toLowerCase().lastIndexOf(KEYWORD_FROM));
				setStartTime(dates.get(0));
				setTaskName(taskName);
			}
		}
		return taskName;
	}

	
	private String parseDEADLINED() {
		getKeywordPosition(taskName);
		Date date = new Date();
		boolean checkForDateTime = false;
		
		if (LAST_POSITION_OF_BY != -1 && LAST_POSITION_OF_AT == -1) {
			String tempTaskName = "";
			String confirmTaskName = "";
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_BY)));
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_BY, taskItems.size())));
			
			System.out.println("DEBUG @@250:" + taskName);
			// Example: meet Hannah by 2359hrs by the beach
			if (taskName.lastIndexOf(KEYWORD_BY) != -1) {
				tempTaskName = taskName.substring(taskName.lastIndexOf(KEYWORD_BY), taskName.length()).trim();
				confirmTaskName = taskName.substring(0, taskName.lastIndexOf(KEYWORD_BY)).trim();
			}
			else {
				confirmTaskName = taskName;
				setTaskName(confirmTaskName);
			}
			checkForDateTime = extractDate(tempTaskName);
			
			List<Date> dateOneBy = new PrettyTimeParser().parse(contentToAnalyse);
			List<Date> dateTwoBy = new PrettyTimeParser().parse(tempTaskName);
			
			// Example: finish jogging with baby by the beach by tomorrow
			if (dateOneBy.size() != 0 && dateTwoBy.size() == 0) {
				setEndTime(dt.checkAndSetDefaultEndTime(dateOneBy.get(0), date));
				taskName = confirmTaskName + " " + tempTaskName;
				setTaskName(taskName);
			}
			// Example: finish jogging with baby by tomorrow by the beach
			else if (dateOneBy.size() == 0 && dateTwoBy.size() != 0) {
				System.out.println("TEST test");
				setEndTime(dt.checkAndSetDefaultEndTime(dateTwoBy.get(0), date));
				taskName = confirmTaskName + " " + contentToAnalyse;
				setTaskName(taskName);
			}
			// Example: return hannah her stapler by tomorrow by 7.30pm
			else if (dateOneBy.size() != 0 && dateTwoBy.size() != 0 && checkForDateTime == true) {
				String temp = tempTaskName + " " + contentToAnalyse;
				List<Date> dateFinal = new PrettyTimeParser().parse(temp);
				setEndTime(dt.checkAndSetDefaultEndTime(dateFinal.get(0), date));
				setTaskName(confirmTaskName.trim());
			}
			// Example: take a walk by the beach
			else {
				taskName = userTask;
				setTaskName(taskName);
				setTaskType(TASK_TYPE.FLOATING);
			}
			return taskName;
		}
		
		else if (LAST_POSITION_OF_BY != -1 && LAST_POSITION_OF_AT != -1 && LAST_POSITION_OF_FROM == -1) {
			System.out.println("TEST @line 288");
			String tempTaskName = "";
			String confirmTaskName = "";
			
			if (LAST_POSITION_OF_BY < LAST_POSITION_OF_AT) {
				confirmTaskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_BY)));
				contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_BY, LAST_POSITION_OF_AT)));
				tempTaskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
				checkForDateTime = extractDate(tempTaskName);
				
				List<Date> dateByToAt = new PrettyTimeParser().parse(contentToAnalyse);
				List<Date> dateAtToEnd = new PrettyTimeParser().parse(tempTaskName);
				
				// Example: meet buyer by tonight at city hall
				if (dateByToAt.size()!= 0 && dateAtToEnd.size() == 0) {
					System.out.println("@line304 :");
					setEndTime(dt.checkAndSetDefaultEndTime(dateByToAt.get(0), date));
					taskName = confirmTaskName + " " + tempTaskName;
					setTaskName(taskName);
				}
				else if (dateByToAt.size() == 0 && dateAtToEnd.size() != 0) {
					setEndTime(dt.checkAndSetDefaultEndTime(dateAtToEnd.get(0), date));
					taskName = confirmTaskName + " " + contentToAnalyse;
					setTaskName(taskName);
				}
				else if (dateByToAt.size() != 0 && dateAtToEnd.size() != 0 && checkForDateTime == true) {
					String dateToExtract = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_BY, taskItems.size())));
					List<Date> dates = new PrettyTimeParser().parse(dateToExtract);
					setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
					taskName = confirmTaskName;
					setTaskName(taskName);
				}
				// Example: meet buyer by tonight at block 2359
				else if (checkForDateTime == false && dateAtToEnd.size() != 0) {
					System.out.println("@line322 :");
					taskName = confirmTaskName.trim() + " " + tempTaskName.trim();
					setEndTime(dt.checkAndSetDefaultEndTime(dateByToAt.get(0), date));
					setTaskName(taskName);
				}
				else {
					taskName = userTask;
					setTaskName(taskName);
					setTaskType(TASK_TYPE.FLOATING);
				}
			}
			else if (LAST_POSITION_OF_BY > LAST_POSITION_OF_AT) {
				confirmTaskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT)));
				contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, LAST_POSITION_OF_BY)));
				tempTaskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_BY, taskItems.size())));
				checkForDateTime = extractDate(contentToAnalyse);
				
				List<Date> dateAtToBy = new PrettyTimeParser().parse(contentToAnalyse);
				List<Date> dateByToEnd = new PrettyTimeParser().parse(tempTaskName);
				
				// Example: meet buyer by tonight at city hall
				if (dateAtToBy.size()!= 0 && dateByToEnd.size() == 0 && checkForDateTime == true) {
					setEndTime(dt.checkAndSetDefaultEndTime(dateAtToBy.get(0), date));
					taskName = confirmTaskName + " " + tempTaskName;
					setTaskName(taskName);
				}
				// Example: meet buyer at city hall by tonight
				else if (dateAtToBy.size() == 0 && dateByToEnd.size() != 0 && checkForDateTime == true) {
					setEndTime(dt.checkAndSetDefaultEndTime(dateByToEnd.get(0), date));
					taskName = confirmTaskName + " " + contentToAnalyse;
					setTaskName(taskName);
				}
				// Example: meeting with hannah at 7pm by monday
				else if (dateAtToBy.size() != 0 && dateByToEnd.size() != 0 && checkForDateTime == true) {
					String dateToExtract = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
					List<Date> dates = new PrettyTimeParser().parse(dateToExtract.replace(" by", ""));
					setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
					taskName = confirmTaskName;
					setTaskName(taskName);
				}
				// Example: meeting with hannah at block 2359 by thursday
				else if (checkForDateTime == false && dateAtToBy.size() != 0) {
					System.out.println("@line363 :");
					taskName = confirmTaskName.trim() + " " + contentToAnalyse.trim();
					setEndTime(dt.checkAndSetDefaultEndTime(dateByToEnd.get(0), date));
					setTaskName(taskName);
				}
				else {
					taskName = userTask;
					setTaskName(taskName);
					setTaskType(TASK_TYPE.FLOATING);
				}
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
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
			}
			// Example: sleep on the floor
			else {
				taskName = userTask;
				setTaskName(taskName);
				setTaskType(TASK_TYPE.FLOATING);
			}
		
		}
		else if (LAST_POSITION_OF_ON != -1 && LAST_POSITION_OF_AT != -1 
				&& LAST_POSITION_OF_ON < LAST_POSITION_OF_AT) {
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_ON)));
			System.out.println("DEBUG @250:" + taskName);
			setTaskName(taskName);
			
			String str = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, LAST_POSITION_OF_AT)));
			List<Date> date1 = new PrettyTimeParser().parse(str);
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
			checkForDateTime = extractDate(contentToAnalyse);
			List<Date> date2 = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: revise on cs2130t at 2pm
			if (date1.size() == 0 && date2.size() != 0 && checkForDateTime == true) {
				taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT)));
				setTaskName(taskName);
				setEndTime(dt.checkAndSetDefaultEndTime(date2.get(0), date));
			}
			// Example: meet hannah on 4 april, 2016 at suntec city
			else if (date1.size() != 0 && date2.size() == 0 && checkForDateTime == true){
				taskName = taskName + " " + toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
				setTaskName(taskName);
				setEndTime(dt.checkAndSetDefaultEndTime(date1.get(0), date));
			}
			// Example: meet hannah on 4th of April 2016 at 2pm
			else if (date1.size() != 0 && date2.size() != 0 && checkForDateTime == true) {
				taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_ON)));
				setTaskName(toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_ON))));
				contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, taskItems.size())));
				List<Date> date3 = new PrettyTimeParser().parse(contentToAnalyse);
				setEndTime(dt.checkAndSetDefaultEndTime(date3.get(0), date));
			}
			else if (checkForDateTime == false && date2.size() != 0 && date1.size() != 0) {
				System.out.println("@line322 :");
				taskName = taskName.trim() + " " + contentToAnalyse.trim();
				setEndTime(dt.checkAndSetDefaultEndTime(date1.get(0), date));
				setTaskName(taskName);
			}
			// Example: sleep on the study bench at the void deck
			else {
				taskName = userTask;
				setTaskName(taskName);
				setTaskType(TASK_TYPE.FLOATING);
			}
		
		}
		else if (LAST_POSITION_OF_ON != -1 && LAST_POSITION_OF_AT != -1 && 
				(LAST_POSITION_OF_ON > LAST_POSITION_OF_AT)) {
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT)));
			System.out.println("DEBUG @250:" + taskName);
			setTaskName(taskName);
			
			String str = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, LAST_POSITION_OF_ON)));
			List<Date> date1 = new PrettyTimeParser().parse(str);
			checkForDateTime = extractDate(str);
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, taskItems.size())));
			List<Date> date2 = new PrettyTimeParser().parse(contentToAnalyse);
			
			// Example: revise at chua chu kang on 24/7/2016
			if (date1.size() == 0 && date2.size() != 0 && checkForDateTime == true) {
				taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_ON)));
				setTaskName(taskName);
			//	setEndTime(date2.get(0));
				setEndTime(dt.checkAndSetDefaultEndTime(date2.get(0), date));
			}
			// Example: meet hannah at 7pm on top of the building
			else if (date1.size() != 0 && date2.size() == 0 && checkForDateTime == true){
				taskName = taskName + " " + toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_ON, taskItems.size())));
				setTaskName(taskName);
			//	setEndTime(date1.get(0));
				setEndTime(dt.checkAndSetDefaultEndTime(date1.get(0), date));
			}
			// Example: meet hannah at 2pm on 4th of April 2016 
			else if (date1.size() != 0 && date2.size() != 0 && checkForDateTime == true) {
				taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT)));
				setTaskName(taskName);
				contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
				List<Date> date3 = new PrettyTimeParser().parse(contentToAnalyse);
			//	setEndTime(date3.get(0));
				setEndTime(dt.checkAndSetDefaultEndTime(date3.get(0), date));
			}
			// Example: meet hannah at block 2359 on the ground floor.
			else if (checkForDateTime == false && date2.size() != 0 && date1.size() != 0) {
				taskName = taskName.trim() + " " + str.trim();
				setEndTime(dt.checkAndSetDefaultEndTime(date2.get(0), date));
				setTaskName(taskName);
			}
			// Example: sleep on the study bench at the void deck
			else {
				taskName = userTask;
				setTaskName(taskName);
				setTaskType(TASK_TYPE.FLOATING);
			}
		
		}
		else if (LAST_POSITION_OF_AT != -1 && LAST_POSITION_OF_ON == -1 && LAST_POSITION_OF_FROM == -1) {
			String tempTaskName = "";
			String confirmTaskName = "";
			
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT)));
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
			
			System.out.println("DEBUG @290:" + taskName);
			// Example: meet Hannah at 7pm at Jurong East
			if (taskName.lastIndexOf(KEYWORD_AT) != -1) {
				tempTaskName = taskName.substring(taskName.lastIndexOf(KEYWORD_AT), taskName.length()).trim();
				confirmTaskName = taskName.substring(0, taskName.lastIndexOf(KEYWORD_AT)).trim();
			}
			else {
				tempTaskName = taskName;
				setTaskName(tempTaskName);
			}
			
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			List<Date> date1 = new PrettyTimeParser().parse(tempTaskName);
			
			// Example: meet hannah at the beach at 2359hrs
			if (dates.size() != 0 && date1.size() == 0) {
			//	setEndTime(dates.get(0));
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
				taskName = confirmTaskName + " " + tempTaskName;
				setTaskName(taskName);
			}
			// Example: meet hannah at 2359hrs at the Padang
			else if (dates.size() == 0 && date1.size() != 0) {
				setEndTime(dt.checkAndSetDefaultEndTime(date1.get(0), date));
				taskName = confirmTaskName + " " + contentToAnalyse;
				setTaskName(taskName);
			}
			// Example: take a walk by the beach
			else {
				taskName= userTask;
				setTaskName(taskName);
				setTaskType(TASK_TYPE.FLOATING);
			}
		}
		else if (LAST_POSITION_OF_FROM != -1 && (LAST_POSITION_OF_AT > LAST_POSITION_OF_FROM)) {
			String tempTaskName = "";
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_FROM)));
			System.out.println("DEBUG @line415:" + taskName);
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_FROM, LAST_POSITION_OF_AT)));
			tempTaskName= toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, taskItems.size())));
			List<Date> dateFromToAt = new PrettyTimeParser().parse(contentToAnalyse);
			List<Date> dateAtToEnd = new PrettyTimeParser().parse(tempTaskName);
			
			// Example: jog from yishun to ang mo kio at 7pm
			if (dateFromToAt.size() == 0 && dateAtToEnd.size() != 0) {
				setEndTime(dt.checkAndSetDefaultEndTime(dateAtToEnd.get(0), date));
				setTaskName(taskName.trim() + " " + contentToAnalyse.trim());
			}
			// Example: jog from 2pm to 8pm at bishan park
			else if (dateFromToAt.size() != 0 && dateAtToEnd.size() == 0) {
				setEndTime(dt.checkAndSetDefaultEndTime(dateFromToAt.get(1), date));
				setStartTime(dateFromToAt.get(0));
				setTaskName(taskName.trim() + " " + tempTaskName.trim());
				setTaskType(TASK_TYPE.EVENT);
			}
			// Example: jog from nus to home at the park
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		}
		else if (LAST_POSITION_OF_AT != -1 && LAST_POSITION_OF_AT < LAST_POSITION_OF_FROM) {
			String tempTaskName = "";
			taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_AT)));
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_AT, LAST_POSITION_OF_FROM)));
			tempTaskName= toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_FROM, taskItems.size())));
			List<Date> dateAtToFrom = new PrettyTimeParser().parse(contentToAnalyse);
			List<Date> dateFromToEnd = new PrettyTimeParser().parse(tempTaskName);
			// Example: jog at 7pm from yishun to khatib
			if (dateAtToFrom.size() != 0 && dateFromToEnd.size() == 0) {
				setEndTime(dt.checkAndSetDefaultEndTime(dateAtToFrom.get(0), date));
				setTaskName(taskName.trim() + " " + tempTaskName.trim());
			}
			else if (dateAtToFrom.size() == 0 && dateFromToEnd.size() != 0) {
				setTaskName(taskName.trim() + " " + contentToAnalyse);
				setStartTime(dateFromToEnd.get(0));
				setEndTime(dt.checkAndSetDefaultEndTime(dateFromToEnd.get(1), date));
				setTaskType(TASK_TYPE.EVENT);
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
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
			}
			// Example: submit assignment by next 2 weeks
			else if (dates.size() != 0 && contentToAnalyse.contains(" next ")) {
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
			}
			// Example: let the kids eat before us.
			else {
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
		}
		return taskName;
	}


	private void parseFloating(ArrayList<String> taskItems, String userTask) {
		DateTimeParser dt = new DateTimeParser();
		String timeChecker = dt.removeTime(userTask);
		
		Date date = new Date();
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		System.out.println("TEST parseFloating :" + timeChecker);
		if (dates.size() == 0) {
			setTaskName(userTask);
		}
		else {
			if (dates.size() == 1 && (!userTask.equals(timeChecker))) {
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
				extractDateElement(userTask);
			}
			else if (dates.size() == 1 && (userTask.contains("tomorrow")
					|| userTask.contains("today") || userTask.contains("yesterday"))) {
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
				extractDateElement(userTask);
			}
			else if (dates.size() > 1){
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(1), date));
				setStartTime(dates.get(0));
				extractDateElement(userTask);
				setTaskType(TASK_TYPE.EVENT);
			}
		}
	
	}
	
	private void extractDateElement(String userTask) {
		String temp = "";
		DateTimeParser dt = new DateTimeParser();
		temp = dt.removeTheDayAfterTomorrow(userTask);
		temp = dt.removeYesterday(temp);
		temp = dt.removeTomorrow(temp);
		temp = dt.removeToday(temp);
		temp = dt.removeThisComingWeekday(temp);
		temp = dt.removeNextFewDays(temp);
		temp = dt.removeNextWeek(temp);
		temp = dt.removeTime(temp);
		temp = dt.removeDate(temp);
		setTaskName(extractLastPreposition(temp));
		if (temp.trim().equals(userTask.trim())) {
			setTaskType(TASK_TYPE.FLOATING);
		}
		else {
			setTaskType(TASK_TYPE.DEADLINED);
		}
	}
	
	private boolean extractDate(String userTask) {
		String temp = "";
		DateTimeParser dt = new DateTimeParser();
		temp = dt.removeTheDayAfterTomorrow(userTask);
		temp = dt.removeYesterday(temp);
		temp = dt.removeTomorrow(temp);
		temp = dt.removeToday(temp);
		temp = dt.removeThisComingWeekday(temp);
		temp = dt.removeNextFewDays(temp);
		temp = dt.removeNextWeek(temp);
		temp = dt.removeTime(temp);
		temp = dt.removeDate(temp);
	
		return (temp.trim().equals(userTask.trim())) ? false : true;
	}
	
	private String extractLastPreposition(String temp) {
		String[] str = temp.split("[\\s+]");
		String newStr = "";
		int lastWordPosition = str.length - 1;
		
		if (str[lastWordPosition].contains("at") || 
			str[lastWordPosition].contains("by") ||
			str[lastWordPosition].contains("on")) {
			str[lastWordPosition] = "";
		}
		for (int i = 0; i < str.length; i++) {
			newStr += str[i] + " ";
		}
		return newStr.trim();
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
		this.taskName = taskName.trim();
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