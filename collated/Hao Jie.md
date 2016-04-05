# Hao Jie
###### DODO\src\Command\DELETE_TYPE.java
``` java
package Command;

public enum DELETE_TYPE {
	SINGLE_INDEX, SINGLE_TAG, MULTIPLE_INDEXES, MULTIPLE_TAGS, RANGE_INDEXES, ALL_INDEXES, ALL_TAGS,
	START_DATE, END_DATE, INVALID;
}
```
###### DODO\src\Command\EDIT_TYPE.java
``` java
package Command;

public enum EDIT_TYPE {
	TASK_NAME, EVENT_TIME, END_TIME, START_TIME, DEADLINED, INVALID, TAG;
}
```
###### DODO\src\Command\FLAGANDCOMPLETE_TYPE.java
``` java
package Command;

public enum FLAGANDCOMPLETE_TYPE {
	SINGLE, MULTIPLE, RANGE, ALL, INVALID;
}
```
###### DODO\src\Command\SEARCH_TYPE.java
``` java
package Command;

public enum SEARCH_TYPE {
	BY_DATE, BY_TASK, BY_TAG, INVALID
}
```
###### DODO\src\Command\SORT_TYPE.java
``` java
package Command;

public enum SORT_TYPE {
	BY_DATE, BY_ASCENDING, BY_DESCENDING, INVALID;
}
```
###### DODO\src\Parser\AddParser.java
``` java
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
	private final String KEYWORD_IN = " in ";

	private int LAST_POSITION_OF_FROM = -1;
	private int LAST_POSITION_OF_TO = -1;
	private int LAST_POSITION_OF_ON = -1;
	private int LAST_POSITION_OF_AT = -1;
	private int LAST_POSITION_OF_BEFORE = -1;
	private int LAST_POSITION_OF_BY = -1;
	private int LAST_POSITION_OF_IN = -1;
	
	private String userTask = "";
	private String taskName = "";
	private String contentToAnalyse;
	private COMMAND_TYPE commandType;
	private TASK_TYPE taskType;
	
	private Date startTime;
	private Date endTime;
	private DateTimeParser dt;
	private PrettyTimeParser pt;
	private List<Date> dates;
	
	private ArrayList<String> taskItems;

	public AddParser() {
		pt = new PrettyTimeParser();
	}
	
	public AddParser(String userTask) {
		this.userTask = userTask;
		executeAddParser(userTask);
	}
	
	private void executeAddParser(String userTask) {
		dt = new DateTimeParser();
		String[] str = userTask.split("\\s+");
		taskItems = new ArrayList<String>(Arrays.asList(str));
		taskName = checkForAbbreviation(taskItems);
		taskName = dt.checkAndConvertDateElement(taskItems);
		taskType = determineTaskType(taskItems, taskName);
		
		switch(taskType) {
			case DEADLINED:
				taskName = parseDEADLINED();
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
				setTaskType(TASK_TYPE.INVALID);
		}
	}

	
	private String checkForAbbreviation(ArrayList<String> taskItems) {
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
			if (dt.checkForDateAndTime(taskName, LAST_POSITION_OF_FROM, LAST_POSITION_OF_TO)) {
				return true;
			}
			else {
				setTaskType(TASK_TYPE.FLOATING);
				return false;
			}
		}
		else if (LAST_POSITION_OF_FROM > LAST_POSITION_OF_TO) {
			if (dt.checkForDateAndTime(taskName, LAST_POSITION_OF_FROM, taskName.length())) {
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

	private boolean checkIfDeadlinedTask(ArrayList<String> taskItems, String taskName) {
		getKeywordPosition(taskName);
		return (LAST_POSITION_OF_AT != -1 || LAST_POSITION_OF_ON != -1 || 
			LAST_POSITION_OF_BEFORE != -1 || LAST_POSITION_OF_BY != -1
			|| LAST_POSITION_OF_IN != -1) ? true : false;
	}

	private void getKeywordPosition(String taskName) {
		ArrayList<String> temp = new ArrayList<String>(convertArrayListToLowerCase(taskName));
		LAST_POSITION_OF_AT = temp.lastIndexOf("at");
		LAST_POSITION_OF_ON = temp.lastIndexOf("on");
		LAST_POSITION_OF_BEFORE = temp.lastIndexOf("before");
		LAST_POSITION_OF_BY = temp.lastIndexOf("by");
		LAST_POSITION_OF_FROM = temp.lastIndexOf("from");
		LAST_POSITION_OF_IN = temp.lastIndexOf("in");
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
		System.out.println("parseDeadline : " + LAST_POSITION_OF_IN);
		
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
			checkForDateTime = dt.hasDateAndTimeElements(tempTaskName);
			
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
				checkForDateTime = dt.hasDateAndTimeElements(tempTaskName);
				
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
				checkForDateTime = dt.hasDateAndTimeElements(contentToAnalyse);
				
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
			checkForDateTime = dt.hasDateAndTimeElements(contentToAnalyse);
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
			checkForDateTime = dt.hasDateAndTimeElements(str);
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
			checkForDateTime = dt.hasDateAndTimeElements(contentToAnalyse);
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
			if (dates.size() != 0 && date1.size() == 0 && checkForDateTime == true) {
			//	setEndTime(dates.get(0));
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
				taskName = confirmTaskName + " " + tempTaskName;
				setTaskName(taskName);
			}
			// Example: meet hannah at 2359hrs at the Padang
			else if (dates.size() == 0 && date1.size() != 0 && checkForDateTime == true) {
				setEndTime(dt.checkAndSetDefaultEndTime(date1.get(0), date));
				taskName = confirmTaskName + " " + contentToAnalyse;
				setTaskName(taskName);
			}
			// Example: take a walk at block 2359
			else if (checkForDateTime == false && dates.size() != 0) {
				System.out.println("@line540 :");
				setTaskName(userTask);
				setTaskType(TASK_TYPE.FLOATING);
			}
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
		else if (LAST_POSITION_OF_IN != -1) {
			contentToAnalyse = toStringTaskElements(new ArrayList<String>(taskItems.subList(LAST_POSITION_OF_IN, taskItems.size())));
			List<Date> dates = new PrettyTimeParser().parse(contentToAnalyse);
			System.out.println("DEBUG IN:" + dates.size());
			if ((contentToAnalyse.contains(" days ") || contentToAnalyse.contains(" days' ") 
				|| contentToAnalyse.contains(" day ")) && (contentToAnalyse.contains(" time") 
				|| contentToAnalyse.contains(" times")) && dates.size() != 0) {
				taskName = toStringTaskElements(new ArrayList<String>(taskItems.subList(0, LAST_POSITION_OF_IN)));
				setTaskName(taskName);
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
			}
		}
		return taskName;
	}


	private void parseFloating(ArrayList<String> taskItems, String userTask) {
		DateTimeParser dt = new DateTimeParser();
	//	String timeChecker = dt.removeTime(userTask);
		boolean hasTimeDateElement = dt.hasDateAndTimeElements(userTask);
		Date date = new Date();
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		System.out.println("TEST parseFloating 123:" + userTask);
		if (dates.size() == 0) {
			setTaskName(userTask);
		}
		else {
			if (dates.size() == 1 && hasTimeDateElement == true) {
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(0), date));
				finalVerification(userTask);
			}
			else if (dates.size() > 1){
				setEndTime(dt.checkAndSetDefaultEndTime(dates.get(1), date));
				setStartTime(dates.get(0));
				finalVerification(userTask);
				setTaskType(TASK_TYPE.EVENT);
			}
		}
	
	}
	
	private void finalVerification(String userTask) {
		String temp = "";
		temp = dt.extractDate(userTask);
		setTaskName(extractLastPreposition(temp));
		if (temp.trim().equals(userTask.trim())) {
			setTaskType(TASK_TYPE.FLOATING);
		}
		else {
			setTaskType(TASK_TYPE.DEADLINED);
		}
	}
	
	private String extractLastPreposition(String temp) {
		String[] str = temp.split("[\\s+]");
		String newStr = "";
		int lastWordPosition = str.length - 1;
		
		if (str[lastWordPosition].toLowerCase().contains(KEYWORD_AT) || 
			str[lastWordPosition].toLowerCase().contains(KEYWORD_BY) ||
			str[lastWordPosition].toLowerCase().contains(KEYWORD_ON)) {
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
```
###### DODO\src\Parser\DateTimeParser.java
``` java
package Parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class DateTimeParser {
	
	public enum DATE_TYPES {
		TYPE_THIS_COMING_WEEKDAY, TYPE_NEXT_WEEK, TYPE_NEXT_WEEKDAY, TYPE_TOMORROW, TYPE_TODAY, TYPE_THE_DAY_AFTER_TOMORROW,
		TYPE_NEXT_FEW_DAYS, TYPE_DATE, TYPE_TIME, TYPE_NULL
	};
	
	private List<String> todayTypes;
	private List<String> tomorrowTypes;
	private ArrayList <String> taskItems;
	
	private final String KEYWORD_AM = "am";
	private final String KEYWORD_PM = "pm";
	private final String KEYWORD_HRS = "hrs";
	private final String KEYWORD_HR = "hr";
	private final String KEYWORD_THE_DAY_AFTER_TOMORROW = " the day after tomorrow";
	private final String KEYWORD_DAY_AFTER_TOMORROW = " day after tomorrow";
	private final String KEYWORD_TOMORROW_1 = "tomorrow";
	private final String KEYWORD_TOMORROW_2 = "tomorrow ";
	private final String KEYWORD_YESTERDAY_SHORTFORM = "ytd ";
	private final String KEYWORD_YESTERDAY = "yesterday";
	private final String KEYWORD_TODAY = "today";
	private final String KEYWORD_COMING = "coming";
	private final String KEYWORD_THIS_COMING = " this coming";
	private final String KEYWORD_AT_1 = "at";
	private final String KEYWORD_AT_2 = "@";
	private final String KEYWORD_THE = " the ";
	private final String KEYWORD_DAYS = " days";
	private final String KEYWORD_NEXT = " next ";
	private final String KEYWORD_NEXT_WEEK = " next week";
	
	private final String DEFAULT_TIME_6PM = "18:00:00";
	private final String DEFAULT_TIME_1159PM = "23:59:59";
	private final String DATE_FORMAT_1 = "dd/MM/yyyy";
	private final String DATE_FORMAT_2 = "yyyy/MM/dd";
	private final String DATE_FORMAT_3 = "dd.MM.yyyy";
	private final String DATE_FORMAT_4 ="dd-MM-yyyy";
	private final String DATE_FORMAT_WITH_TIME = "dd/MM/yyyy HH:mm:ss";
	private final String TIME_WITHOUT_DATE_FORMAT = "HH:mm";
	private final String STRING_SPLITTER = "\\s+";

	public DateTimeParser () {
		todayTypes = new ArrayList<>(Arrays.asList("tdy"));
		tomorrowTypes = new ArrayList<>(Arrays.asList("tmr", "tml", "tmrw", "2moro"));
	}
	
	protected String removeTheDayAfterTomorrow(String userInput) {
		if (userInput.contains(KEYWORD_THE_DAY_AFTER_TOMORROW)) {
			userInput = userInput.replace(KEYWORD_THE_DAY_AFTER_TOMORROW, "");
		}
		else if (userInput.contains(KEYWORD_DAY_AFTER_TOMORROW)) {
			userInput = userInput.replace(KEYWORD_DAY_AFTER_TOMORROW, "");
		}
		return userInput.trim();
	}
	
	protected String removeTomorrow(String userInput) {
		if (userInput.contains(KEYWORD_TOMORROW_1) || userInput.contains(KEYWORD_TOMORROW_2)) {
			userInput = userInput.replace(KEYWORD_TOMORROW_1, "");
		}
		return userInput.trim();
	}
	
	protected String removeYesterday(String userInput) {
		if (userInput.contains(KEYWORD_YESTERDAY)) {
			userInput = userInput.replace(KEYWORD_YESTERDAY, "");
		}
		else if (userInput.contains(KEYWORD_YESTERDAY_SHORTFORM)) {
			userInput = userInput.replace(KEYWORD_YESTERDAY_SHORTFORM, "");
		}
		return userInput.trim();
	}
	
	protected String removeToday(String userInput) {
		if (userInput.contains(KEYWORD_TODAY)) {
			userInput = userInput.replace(KEYWORD_TODAY, "");
		}
		return userInput.trim();
	}
	
	protected String removeThisComingWeekday(String userInput) {
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		if (userInput.toLowerCase().contains(KEYWORD_THIS_COMING)) {
			int index = taskItems.lastIndexOf(KEYWORD_COMING);
			taskItems.remove(index + 1);
			taskItems.remove(index);
			taskItems.remove(index - 1);
			userInput = toStringTaskElements(taskItems);
		}
		return userInput.trim();
	}
	
	protected String removeNextFewDays(String userInput) {
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		int indexOfNext = taskItems.lastIndexOf(KEYWORD_NEXT);
		int indexOfDays = taskItems.lastIndexOf(KEYWORD_DAYS);

		if (indexOfNext != 0 && (indexOfDays - indexOfNext) == 2  
			&& !userInput.contains(KEYWORD_NEXT_WEEK)) {
			
			taskItems.remove(indexOfDays);
			taskItems.remove(indexOfNext + 1);
			taskItems.remove(indexOfNext);
			if (taskItems.get(indexOfNext - 1).contains(KEYWORD_THE)) {
				taskItems.remove(indexOfNext - 1);
			}
			userInput = toStringTaskElements(taskItems);
		}
		return userInput.trim();
	}
	
	protected String removeDate(String userInput) {
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		for (int i = 0; i < taskItems.size(); i++) {
			List<Date> dates = new PrettyTimeParser().parse(taskItems.get(i));
			if (dates.size() != 0) {
				String[] temp = taskItems.get(i).toLowerCase().split(STRING_SPLITTER);
				// string maybe in 20/12 or 20/12/16 format
				if (temp.length > 2) {
					taskItems.remove(i);
				}
			}
		}
		userInput = toStringTaskElements(taskItems);
		return userInput;
		
	}
	protected String removeNextWeek(String userInput) {
	
		if (userInput.contains(KEYWORD_NEXT_WEEK)) {
			userInput = userInput.replace(KEYWORD_NEXT_WEEK, "");
		}
		return userInput.trim();
	}
	protected String removeNextWeekday(String userInput) {
		String[] temp = userInput.split(STRING_SPLITTER);
		String newTaskName = "";
		int positionOfWeekday = 0;

		for (int i = 0; i < temp.length; i++) {
			if (temp[i].contains(KEYWORD_NEXT)) {
				positionOfWeekday = i + 1;
				List<Date> dates = new PrettyTimeParser().parse(temp[positionOfWeekday]);
				if (dates.size() != 0) {
					temp[i] = "";
					temp[positionOfWeekday] = "";
					i++;
				}
			}
			newTaskName += temp[i] + " ";
		}
		return newTaskName.trim();
	}
	
	protected String removeTime (String userInput) {
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
	
		for (int i = 0; i < taskItems.size(); i++) {
			List<Date> dates = new PrettyTimeParser().parse(taskItems.get(i));
	
			if ((taskItems.get(i).toLowerCase().contains(KEYWORD_AM) || taskItems.get(i).toLowerCase().contains(KEYWORD_PM) 
				|| taskItems.get(i).toLowerCase().contains(KEYWORD_HR) || taskItems.get(i).toLowerCase().contains(KEYWORD_HRS) ) 
				&& dates.size() != 0) {
				taskItems.remove(i);
			}
		}
		userInput = toStringTaskElements(taskItems);
		return userInput;
	}
	
	protected String processYesterday (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if ((contentToAnalyse.get(i).toLowerCase()).contains(KEYWORD_YESTERDAY_SHORTFORM)) {
				contentToAnalyse.set(i, KEYWORD_YESTERDAY);
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	protected String processToday (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if (todayTypes.contains(contentToAnalyse.get(i).toLowerCase())) {
				contentToAnalyse.set(i, KEYWORD_TODAY);
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	protected String processTomorrow (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if (tomorrowTypes.contains(contentToAnalyse.get(i).toLowerCase())) {
				contentToAnalyse.set(i, KEYWORD_TOMORROW_1);
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	protected String processAt (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if (contentToAnalyse.get(i).contains(KEYWORD_AT_2)) {
				contentToAnalyse.set(i, KEYWORD_AT_1);
			}
		}
		return toStringTaskElements(contentToAnalyse);
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
	
	protected Date checkAndSetDefaultEndTime(Date date, Date currentTime) {
		Date newDate = date;		
		SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT_WITH_TIME);
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_WITHOUT_DATE_FORMAT);
		DateFormat df = new SimpleDateFormat(DATE_FORMAT_1);
		String dateWithoutTime = df.format(date);
		String newDateWithTime= "";
		String timeAt6pm = dateWithoutTime + " " + DEFAULT_TIME_6PM;
		Calendar cal = Calendar.getInstance();
		
		try {
			Date dateWithoutDate = sdf.parse(sdf.format(date));
			Date currentWithoutDate = sdf.parse(sdf.format(currentTime));
			Date defaultDeadLineForExecutive = sf.parse(timeAt6pm);

			if (dateWithoutDate.equals(currentWithoutDate) && currentTime.before(defaultDeadLineForExecutive)) {
				newDate = defaultDeadLineForExecutive;
			} 
			else if (dateWithoutDate.equals(currentWithoutDate) && currentTime.after(defaultDeadLineForExecutive)) {
				newDateWithTime = dateWithoutTime + " " + DEFAULT_TIME_1159PM;
				newDate = sf.parse(newDateWithTime);
			}
/*			else if (date.before(currentTime)) {
				cal.setTime(date);
				cal.add(Calendar.DATE, 1);
				newDateWithTime = sf.format(cal.getTime()); 
				newDate = sf.parse(newDateWithTime);
			}
*/		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate;
	}
	
	protected boolean hasDateAndTimeElements(String userTask) {
		String temp = "";
		temp = extractDate(userTask);
		return (temp.trim().equals(userTask.trim())) ? false : true;
	}
	
	protected String extractDate(String userTask) {
		String temp = "";
		temp = removeTheDayAfterTomorrow(userTask);
		temp = removeYesterday(temp);
		temp = removeTomorrow(temp);
		temp = removeToday(temp);
		temp = removeThisComingWeekday(temp);
		temp = removeNextFewDays(temp);
		temp = removeNextWeek(temp);
		temp = removeNextWeekday(temp);
		temp = removeTime(temp);
		temp = removeDate(temp);
		return temp;
	}
	

	protected boolean checkForDateAndTime(String taskName, int LAST_POSITION_OF_FROM, int LAST_POSITION_OF_TO) {	
		String str = taskName.substring(LAST_POSITION_OF_FROM, taskName.length());
		List<Date> dates = new PrettyTimeParser().parse(str);
		return (dates.size() != 0) ? true : false; 
	}
	
	protected String checkAndConvertDateElement(ArrayList<String> taskItems) {
		DateFormat destDf = new SimpleDateFormat(DATE_FORMAT_2);
		
		for (int i = 0; i < taskItems.size(); i++) {
			
			try {
				DateFormat srcDf = new SimpleDateFormat(DATE_FORMAT_1);
				Date date = srcDf.parse(taskItems.get(i));
				taskItems.set(i, destDf.format(date));
			}
			catch (ParseException e) {				
			}
			
			try {
				DateFormat srcDf = new SimpleDateFormat(DATE_FORMAT_3);
				Date date = srcDf.parse(taskItems.get(i));
				taskItems.set(i, destDf.format(date));
			}
			catch (ParseException e) {				
			}
			
			try {
				DateFormat srcDf = new SimpleDateFormat(DATE_FORMAT_4);
				Date date = srcDf.parse(taskItems.get(i));
				taskItems.set(i, destDf.format(date));
			}
			catch (ParseException e) {				
			}
			
		}
		return toStringTaskElements(taskItems);
	}
	
}
```
###### DODO\src\Parser\DeleteParser.java
``` java
package Parser;

import java.util.ArrayList;
import Command.*;

public class DeleteParser {
	private String userTask;
	private DELETE_TYPE deleteType;
	private ArrayList<String> tagToDelete; 
	private ArrayList<Integer> indexToDelete;
	
	private String STRING_CHECKER_ALL = "all";
	private String STRING_CHECKER_TO = "to";
	private String STRING_CHECKER_HYPHEN = "-";
	private String STRING_HASH_TAG = "#";
	private String STRING_CHECKER_TAG = "tag";
	private String STRING_CHECKER_CATEGORY = "category";
	private String STRING_START_DATE = "start date";
	private String STRING_END_DATE = "end date";
	private String STRING_START_INDICATOR = "start";
	private String STRING_END_INDICATOR = "end";
	
	public DeleteParser(String userTask) {
		this.userTask = userTask;
		this.tagToDelete = new ArrayList<String>();
		this.indexToDelete = new ArrayList<Integer>();
		executeDeleteParser();
	}

	private void executeDeleteParser() {
		boolean isInteger = false;
		String[] str = userTask.replaceAll("[:.,]", "").split("\\s+");
		isInteger = isTaskIndex(str);
		
		switch (detemineDeleteType(userTask.toLowerCase(), isInteger)) {
		
			case SINGLE_INDEX:
				parseSingleDeleteIndex(str);
				break;
			case SINGLE_TAG:
				parseSingleDeleteTag(str);
				break;
			case MULTIPLE_INDEXES:
				parseMultipleDeleteIndexes(str);
				break;
			case MULTIPLE_TAGS:
				parseMutipleDeleteTags(str);
				break;
			case RANGE_INDEXES:
				parseRangeDelete(str);
				break;
			case ALL_INDEXES:
				break;
			case ALL_TAGS:
				break;
			case START_DATE:
				parseDeleteStartDate(str);
				break;
			case END_DATE:
				parseDeleteEndDate(str);
				break;
			default:
				setDeleteType(DELETE_TYPE.INVALID);
				break;
		}
	}

	private boolean isTaskIndex(String[] str) {
		return (str[0].startsWith(STRING_HASH_TAG)) ? false : true;
	}

	private void parseDeleteEndDate(String[] str) {
		if (!str[0].contains(STRING_END_INDICATOR)) {
			indexToDelete.add(Integer.parseInt(str[0]));
		}
		else {
			setDeleteType(DELETE_TYPE.INVALID);
		}
	}

	private void parseDeleteStartDate(String[] str) {
		if (!str[0].contains(STRING_START_INDICATOR)) {
			indexToDelete.add(Integer.parseInt(str[0]));
		}
		else {
			setDeleteType(DELETE_TYPE.INVALID);
		}
	}

	private void parseRangeDelete(String[] str) {
		// Example: delete 1 to 5 / delete 1 - 5
		if (str.length == 3) {
			for (int i = Integer.parseInt(str[0]); i < Integer.parseInt(str[2]) + 1; i++) {
				indexToDelete.add(i);
			}
		}
	}

	private void parseMultipleDeleteIndexes(String[] str) {
		// Example: delete 1 4 9 14
		for (int i = 0; i < str.length; i++) {
			indexToDelete.add(Integer.parseInt(str[i]));
		}
	}
	
	private void parseMutipleDeleteTags(String[] str) {
		// Example: delete #nus #soc #singapore
		for (int i = 0; i < str.length; i++) {
			tagToDelete.add(str[i].substring(1, str[i].length()));
		}
	}

	
	private void parseSingleDeleteTag(String[] str) {
		// Example: delete #nus
		tagToDelete.add(str[0].substring(1, str[0].length()));
	}
	
	private void parseSingleDeleteIndex(String[] str) {
		indexToDelete.add(Integer.parseInt(str[0]));
	}

	private DELETE_TYPE detemineDeleteType(String userTask, boolean isInteger) {

		if (checkIfDeleteAll(userTask)) {
			setDeleteType(DELETE_TYPE.ALL_INDEXES);
			return DELETE_TYPE.ALL_INDEXES;
		}
		else if (checkIfDeleteAllTag(userTask)) {
			setDeleteType(DELETE_TYPE.ALL_TAGS);
			return DELETE_TYPE.ALL_TAGS;
		}
		else if (checkIfDeleteSingleIndex(userTask) && isInteger == true){
			setDeleteType(DELETE_TYPE.SINGLE_INDEX);
			return DELETE_TYPE.SINGLE_INDEX;
		}
		else if (checkIfDeleteSingleTag(userTask) && isInteger == false) {
			setDeleteType(DELETE_TYPE.SINGLE_TAG);
			return DELETE_TYPE.SINGLE_TAG;	
		}
		else if (checkIfDeleteRange(userTask) && isInteger == true) {
			setDeleteType(DELETE_TYPE.RANGE_INDEXES);
			return DELETE_TYPE.RANGE_INDEXES;
		}
		else if (checkIfDeleteMultiple(userTask) && isInteger == true) {
			setDeleteType(DELETE_TYPE.MULTIPLE_INDEXES);
			return DELETE_TYPE.MULTIPLE_INDEXES;
		}
		else if (checkIfDeleteMultiple(userTask) && isInteger == false) {
			setDeleteType(DELETE_TYPE.MULTIPLE_TAGS);
			return DELETE_TYPE.MULTIPLE_TAGS;
		}
		else if(checkIfDeleteStartDate(userTask)) {
			setDeleteType(DELETE_TYPE.START_DATE);
			return DELETE_TYPE.START_DATE;
		}
		else if(checkIfDeleteEndDate(userTask)) {
			setDeleteType(DELETE_TYPE.END_DATE);
			return DELETE_TYPE.END_DATE;
		}
		else {
			setDeleteType(DELETE_TYPE.INVALID);
			return DELETE_TYPE.INVALID;
		}
	}

	private boolean checkIfDeleteEndDate(String userTask) {
		return (userTask.contains(STRING_END_DATE)) ? true : false;
	}

	private boolean checkIfDeleteStartDate(String userTask) {
		return (userTask.contains(STRING_START_DATE)) ? true : false;
	}

	private boolean checkIfDeleteAllTag(String userTask) {
		return (userTask.contains(STRING_CHECKER_ALL) && (userTask.contains(STRING_CHECKER_TAG) 
				|| userTask.contains(STRING_CHECKER_CATEGORY))) ? true : false;
	}

	private boolean checkIfDeleteAll(String userTask) {
		return (userTask.contains(STRING_CHECKER_ALL) && !(userTask.contains(STRING_CHECKER_TAG) 
				|| userTask.contains(STRING_CHECKER_CATEGORY))) ? true : false;
	}

	private boolean checkIfDeleteRange(String userTask) {
		return (userTask.contains(STRING_CHECKER_HYPHEN) || (userTask.contains(STRING_CHECKER_TO))) 
				? true : false;
	}

	private boolean checkIfDeleteMultiple(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");	
		return (str.length > 1 && !checkIfDeleteRange(userTask) && 
				!checkIfDeleteEndDate(userTask) &&
				!checkIfDeleteStartDate(userTask)) ? true : false;
	}

	private boolean checkIfDeleteSingleIndex(String userTask) {
		return (userTask.length() == 1 && !userTask.contains(STRING_CHECKER_ALL)) ? true : false;
	}
	
	private boolean checkIfDeleteSingleTag(String userTask) {
		String[] temp = userTask.split(" ");
		return (temp.length == 1 && temp[0].startsWith(STRING_HASH_TAG)) ? true : false;
	}
	
	private void setDeleteType(DELETE_TYPE deleteType) {
		this.deleteType = deleteType;
	}
	
	public DELETE_TYPE getDeleteType() {
		return this.deleteType;
	}
	
	public ArrayList<String> getTagToDelete() {
		return this.tagToDelete;
	}
	
	public ArrayList<Integer> getIndexToDelete() {
		return this.indexToDelete;
	}
}
```
###### DODO\src\Parser\EditParser.java
``` java
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
	
	private int taskID;
	private Date newStartDate;
	private Date newEndDate;
	private EDIT_TYPE editType;
	private ArrayList<String> editTaskElements;
	private String oldTag = "";
	private ArrayList<String> newTag = new ArrayList<String>();
	
	private Date date = new Date();
	private DateTimeParser dt = new DateTimeParser();
	
	public EditParser(String userInput) {
		executeEditParser(userInput);
	}

	
	private void executeEditParser(String userInput) {
		String[] editElements = userInput.replaceAll("[:-]", "").split("\\s+]");
		editTaskElements = new ArrayList<String>(Arrays.asList(editElements));
		newTaskName = checkForAbbreviation(editTaskElements);
		String[] updatedElements = newTaskName.replaceAll("[:-]", "").split("\\s+");
		editTaskElements = new ArrayList<String>(Arrays.asList(updatedElements));
		
		for (int i = 0; i < editTaskElements.size(); i++) {
			System.out.println(editTaskElements.get(i));
		}
		
		userInput = checkIfValidEditCommandInput(editTaskElements);
		System.out.println("Debug at EditParser :" + userInput);
	
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
			System.out.println("Debug at DEADLINED " + userInput);
			parseEditDeadLined(userInput);
			setEditType(EDIT_TYPE.DEADLINED);
			break;
		case END_TIME:
			parseEditEndTime(userInput);
			setEditType(EDIT_TYPE.END_TIME);
			break;
		case TAG:
			System.out.println("Debug at TAG " + userInput);
			parserEditTag(userInput);
			setEditType(EDIT_TYPE.TAG);
			break;
		case INVALID:
			setEditType(EDIT_TYPE.INVALID);
			break;
		}	
	}

	private void parserEditTag(String userInput) {
		String[] str = userInput.trim().split("\\s+");
		assert str.length <= 3 && str.length > 0;
		
		if (str[0].startsWith(STRING_HASH_TAG) && str[2].startsWith(STRING_HASH_TAG)) {
			oldTag = str[0].substring(1, str[0].length());
			newTag.add(str[2].substring(1, str[0].length()));
		}
		else {
			setEditType(EDIT_TYPE.INVALID);
		}
	}


	private void parseEditEndTime(String userInput) {
		System.out.println("Debug at parseEditEndTime ");
		INDEX_OF_LAST_TO = userInput.lastIndexOf(KEYWORD_TO);
		String temp = userInput.substring(INDEX_OF_LAST_TO, userInput.length());
		setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_TO)));
		List<Date> dates = new PrettyTimeParser().parse(temp);
		setNewEndDate(dt.checkAndSetDefaultEndTime(dates.get(0), date));
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
		INDEX_OF_LAST_ON = userInput.lastIndexOf(KEYWORD_ON);
		INDEX_OF_LAST_AT = userInput.lastIndexOf(KEYWORD_AT);
		
		if (userInput.lastIndexOf(KEYWORD_BY) != -1 && userInput.lastIndexOf(KEYWORD_AT) == -1) {
			INDEX_OF_LAST_BY = userInput.lastIndexOf(KEYWORD_BY);
			setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_BY)));
			temp = userInput.substring(INDEX_OF_LAST_BY, userInput.length());
		}
		
		else if (userInput.lastIndexOf(KEYWORD_ON) != -1 && userInput.lastIndexOf(KEYWORD_AT) == -1) {
			INDEX_OF_LAST_ON = userInput.lastIndexOf(KEYWORD_ON);
			setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_ON)));
			temp = userInput.substring(INDEX_OF_LAST_ON, userInput.length());
		
		}
	
		else if (userInput.lastIndexOf(KEYWORD_AT) != -1 && userInput.lastIndexOf(KEYWORD_ON) == -1) {
			INDEX_OF_LAST_AT = userInput.lastIndexOf(KEYWORD_AT);
			setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_AT)));
			temp = userInput.substring(INDEX_OF_LAST_AT, userInput.length());
		}
		
		else if (INDEX_OF_LAST_ON != -1 && INDEX_OF_LAST_ON < INDEX_OF_LAST_AT) {
			setNewTaskName(userInput.substring(0, INDEX_OF_LAST_ON));
			temp = userInput.substring(INDEX_OF_LAST_ON, INDEX_OF_LAST_AT);
			String temp2 = userInput.substring(INDEX_OF_LAST_AT, userInput.length());
			List<Date> dateTemp = new PrettyTimeParser().parse(temp);
			List<Date> dateTemp2 = new PrettyTimeParser().parse(temp2);
			if (dateTemp.size() == 0 && dateTemp2.size() != 0) {
				setNewTaskName(newTaskName + " " + temp.trim());
				temp = temp2;
			}
			else if (dateTemp.size() != 0 && dateTemp2.size() == 0) {
				setNewTaskName(newTaskName + " " + temp2.trim());
			}
			
		}
		else if (INDEX_OF_LAST_AT != -1 && INDEX_OF_LAST_ON > INDEX_OF_LAST_AT) {
			setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_AT)));
			temp = userInput.substring(INDEX_OF_LAST_AT, INDEX_OF_LAST_ON);
			String temp2 = userInput.substring(INDEX_OF_LAST_ON, userInput.length());
			List<Date> dateTemp = new PrettyTimeParser().parse(temp);
			List<Date> dateTemp2 = new PrettyTimeParser().parse(temp2);
			if (dateTemp.size() == 0 && dateTemp2.size() != 0) {
				setNewTaskName(newTaskName + " " + temp.trim());
				temp = temp2;
			}
			else if (dateTemp.size() != 0 && dateTemp2.size() == 0) {
				setNewTaskName(newTaskName + " " + temp2.trim());
			}
			
		}
			
		else if (userInput.lastIndexOf(KEYWORD_BEFORE) != -1) {
			INDEX_OF_LAST_BEFORE = userInput.lastIndexOf(KEYWORD_BEFORE);
			setNewTaskName(userInput.substring(0, userInput.lastIndexOf(KEYWORD_BEFORE)));
			temp = userInput.substring(INDEX_OF_LAST_BEFORE, userInput.length());
		}
		
		List<Date> dates = new PrettyTimeParser().parse(temp);
		if (dates.size() != 0) {
			setNewEndDate(dt.checkAndSetDefaultEndTime(dates.get(0), date));
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
			setNewEndDate(dt.checkAndSetDefaultEndTime(dates.get(1), date));
		}
	}

	private void parseEditTaskName(String userTaskIndex) {
		setNewTaskName(userTaskIndex.trim());
	}


	private EDIT_TYPE determineEditType(String userInput) {
		
		if (hasTaskName(userInput) && !userInput.trim().startsWith(STRING_HASH_TAG)) {
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
		else if (hasDeadLined(userInput) && !hasEndTime(userInput) && !hasStartTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			System.out.println("Debug at hasDeadLined true");
			return EDIT_TYPE.DEADLINED;
		}
		/*
		 * @ Description: edits the end time of an event
		 *  Example: edit 1 to 24.07.2016
		 */
		else if (!hasDeadLined(userInput) && hasEndTime(userInput) && !hasStartTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			return EDIT_TYPE.END_TIME;
		}/*
		 * @ Description: edits the start time of an event
		 *  Example: edit 1 from 24.07.2016
		 */
		else if (!hasDeadLined(userInput) && !hasEndTime(userInput) && hasStartTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			return EDIT_TYPE.START_TIME;
		}
		else if (!hasDeadLined(userInput) && hasStartTime(userInput) && hasEndTime(userInput)
				&& !userInput.trim().startsWith(STRING_HASH_TAG)) {
			System.out.println("Debug @line175 = true");
			return EDIT_TYPE.EVENT_TIME;
		}
		else if (userInput.trim().startsWith(STRING_HASH_TAG)) {
			System.out.println("Debug @line253 = true");
			return EDIT_TYPE.TAG;
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
		return (userInput.contains(KEYWORD_TO) && dates.size() != 0
				&& !userInput.contains(STRING_HASH_TAG)) ? true : false;
	}

	
	private boolean hasTaskName(String userInput) {
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		DateTimeParser dt = new DateTimeParser();
		boolean hasDateTime = dt.hasDateAndTimeElements(userInput);
		return (dates.size() == 0 || hasDateTime == false) ? true : false;
	}


	private String checkIfValidEditCommandInput(ArrayList<String> editTaskElements) {
		String temp = "";
		if (editTaskElements.size() < 2) {
			setEditType(EDIT_TYPE.INVALID);
		}
		else if (!editTaskElements.get(0).startsWith(STRING_HASH_TAG)){
			taskID = Integer.parseInt(editTaskElements.get(0));
			setTaskID(taskID);
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
	
	private String checkForAbbreviation(ArrayList<String> editTaskElements) {
		String name = "";
		name = dt.processToday(editTaskElements);
		name = dt.processTomorrow(editTaskElements);
		name = dt.processYesterday(editTaskElements);
		name = dt.processAt(editTaskElements);
		System.out.println("checkForAbbrevation name :" + name);
		return name;
	}
	
	private void setNewTaskName(String newTaskName) {
		this.newTaskName = newTaskName;
	}
	
	public String getNewTaskName() {
		return this.newTaskName.trim();
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
	
	protected ArrayList<String> getNewTag() {
		return this.newTag;
	}
	
	protected String getOldTag() {
		return this.oldTag;
	}
}
```
###### DODO\src\Parser\FlagAndCompleteParser.java
``` java
package Parser;

import java.util.ArrayList;
import Command.*;

public class FlagAndCompleteParser {
	private String userTaskIndex;
	private FLAGANDCOMPLETE_TYPE _type;
	private ArrayList<Integer> taskIndex; 
	
	private static String MESSAGE_WRONG_DELETE_COMMAND = "Oops. Please enter a valid range.";
	
	
	public FlagAndCompleteParser(String userTaskIndex) {
		this.userTaskIndex = userTaskIndex;
		this.taskIndex = new ArrayList<Integer>();
		executeDeleteParser(userTaskIndex);
	}

	private void executeDeleteParser(String userTaskIndex) {
		
		String[] str = userTaskIndex.trim().replaceAll("[:.,]", " ").toLowerCase().split("\\s+");
		
		switch (detemineDeleteType(userTaskIndex.toLowerCase())) {
		
			case SINGLE:
				parseSingle(str);
				break;
			case MULTIPLE:
				System.out.print("DEBUG @line 33: MULTIPLE");
				parseMultiple(str);
				break;
			case RANGE:
				parseRange();
				break;
			case ALL:
				break;
			default:
				System.out.println(MESSAGE_WRONG_DELETE_COMMAND);
				break;
		}
	}

	private void parseRange() {
		
		if (userTaskIndex.contains("-")) {
			userTaskIndex = userTaskIndex.replace("-", " ");
		}
		else if (userTaskIndex.contains("to")) {
			userTaskIndex = userTaskIndex.replace("to", " ");
		}
		
		String[] temp = userTaskIndex.split("\\s+");
		
		if (temp.length == 2) {
			for (int i = Integer.parseInt(temp[0]); i < Integer.parseInt(temp[1]) + 1; i++) {
				taskIndex.add(i);
			}
			setTaskIndex(taskIndex);
		}
	}

	private void parseMultiple(String[] str) {
		for (int i = 0; i < str.length; i++) {
			taskIndex.add(Integer.parseInt(str[i]));
		}
		setTaskIndex(taskIndex);
	}

	private void parseSingle(String[] str) {
		taskIndex.add(Integer.parseInt(str[0]));
		setTaskIndex(taskIndex);
	}

	private FLAGANDCOMPLETE_TYPE detemineDeleteType(String usertaskIndex) {

		if (checkIfSingle(usertaskIndex)) {
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.SINGLE);
			return FLAGANDCOMPLETE_TYPE.SINGLE;
		}
		else if (checkIfRange(usertaskIndex)) {
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.RANGE);
			return FLAGANDCOMPLETE_TYPE.RANGE;
		}
		else if (checkIfMultiple(usertaskIndex)) {
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.MULTIPLE);
			return FLAGANDCOMPLETE_TYPE.MULTIPLE;
		}
		else if (checkIfAll(usertaskIndex)) {
			setFlagCompleteType(FLAGANDCOMPLETE_TYPE.ALL);
			return FLAGANDCOMPLETE_TYPE.ALL;
		}
		else {
			return null;
		}
	}

	private boolean checkIfAll(String usertaskIndex) {
		return (usertaskIndex.contains("all")) ? true: false;
	}

	private boolean checkIfRange(String usertaskIndex) {
		return (usertaskIndex.contains("-") || (usertaskIndex.contains("to"))) ? true : false;
	}

	private boolean checkIfMultiple(String usertaskIndex) {
		String[] str = usertaskIndex.replaceAll("[,]", " ").toLowerCase().split("\\s+");
		return (str.length > 1 && !checkIfRange(usertaskIndex)) ? true : false;
	}

	private boolean checkIfSingle(String usertaskIndex) {
		return (!checkIfRange(usertaskIndex) && !checkIfMultiple(usertaskIndex)
				&& !usertaskIndex.contains("all")) ? true : false;
	}
	
	private void setFlagCompleteType(FLAGANDCOMPLETE_TYPE _type) {
		this._type = _type;
	}
	
	public FLAGANDCOMPLETE_TYPE getFlagCompleteType() {
		return this._type;
	}
	
	private void setTaskIndex(ArrayList<Integer> taskIndex) {
		this.taskIndex = taskIndex;
	}
	
	public ArrayList<Integer> getTaskIndex() {
		return this.taskIndex;
	}
}
```
###### DODO\src\Parser\FlexiCommand.java
``` java
package Parser;

import java.util.HashMap;
import Command.*;

/*
 *@author Pay Hao Jie
 *@Description: This class consolidates the possible inputs for a specific command 
 *				if the user happens to key in a different variation. 
 */

public class FlexiCommand {
	
	private static HashMap<String, COMMAND_TYPE> possibleCommands;
	
	public FlexiCommand() {
		possibleCommands = new HashMap<String, COMMAND_TYPE>();
		possibleCommands = initiateFlexiDeleteCommand();
		possibleCommands = initiateFlexiEditCommand();
		possibleCommands = initiateFlexiCompleteCommand();
		possibleCommands = initiateFlexiUndoCommand();
		possibleCommands = initiateFlexiRedoCommand();
		possibleCommands = initiateFlexiFlagCommand();
		possibleCommands = initiateFlexiUnflagCommand();
		possibleCommands = initiateFlexiTagCommand();
		possibleCommands = initiateFlexiUntagCommand();
		possibleCommands = initiateFlexiSearchCommand();
		possibleCommands = initiateFlexiSortCommand();
		possibleCommands = initiateFlexiHelpCommand();
		possibleCommands = initiateFlexiChangeDirectoryCommand();
		possibleCommands = initiateFlexiExitCommand();

	}
	
	public HashMap<String, COMMAND_TYPE> getKeywordsDataBase() {
		return possibleCommands;
	}

	private HashMap<String, COMMAND_TYPE> initiateFlexiDeleteCommand() {
		possibleCommands.put("d", COMMAND_TYPE.DELETE);
		possibleCommands.put("delete", COMMAND_TYPE.DELETE);
		possibleCommands.put("dlt", COMMAND_TYPE.DELETE);
		possibleCommands.put("del", COMMAND_TYPE.DELETE);
		possibleCommands.put("deletes", COMMAND_TYPE.DELETE);
		possibleCommands.put("remove", COMMAND_TYPE.DELETE);
		possibleCommands.put("rm", COMMAND_TYPE.DELETE);
		possibleCommands.put("r", COMMAND_TYPE.DELETE);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiEditCommand() {
		possibleCommands.put("ed", COMMAND_TYPE.EDIT);
		possibleCommands.put("edit", COMMAND_TYPE.EDIT);
		possibleCommands.put("edits", COMMAND_TYPE.EDIT);
		possibleCommands.put("update", COMMAND_TYPE.EDIT);
		possibleCommands.put("updates", COMMAND_TYPE.EDIT);
		possibleCommands.put("change", COMMAND_TYPE.EDIT);
		possibleCommands.put("changes", COMMAND_TYPE.EDIT);
		possibleCommands.put("correct", COMMAND_TYPE.EDIT);
		return possibleCommands;
	}
	
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiUndoCommand() {
		possibleCommands.put("undo", COMMAND_TYPE.UNDO);
		possibleCommands.put("back", COMMAND_TYPE.UNDO);
		possibleCommands.put("previous", COMMAND_TYPE.UNDO);
		possibleCommands.put("revert", COMMAND_TYPE.UNDO);
		possibleCommands.put("b", COMMAND_TYPE.UNDO);
		possibleCommands.put("goback", COMMAND_TYPE.UNDO);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiCompleteCommand() {
		possibleCommands.put("complete", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("completes", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("done", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("fin", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("end", COMMAND_TYPE.COMPLETE);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiRedoCommand() {
		possibleCommands.put("redo", COMMAND_TYPE.REDO);
		possibleCommands.put("redos", COMMAND_TYPE.REDO);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiFlagCommand() {
		possibleCommands.put("flag", COMMAND_TYPE.FLAG);
		possibleCommands.put("flags", COMMAND_TYPE.FLAG);
		possibleCommands.put("important", COMMAND_TYPE.FLAG);
		possibleCommands.put("impt", COMMAND_TYPE.FLAG);
		possibleCommands.put("starred", COMMAND_TYPE.FLAG);
		possibleCommands.put("star", COMMAND_TYPE.FLAG);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiUnflagCommand() {
		possibleCommands.put("unflag", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unflags", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unimportant", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unimpt", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unstarred", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unstar", COMMAND_TYPE.UNFLAG);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiTagCommand() {
		possibleCommands.put("tag", COMMAND_TYPE.TAG);
		possibleCommands.put("cat", COMMAND_TYPE.TAG);
		possibleCommands.put("tags", COMMAND_TYPE.TAG);
		possibleCommands.put("category", COMMAND_TYPE.TAG);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiUntagCommand() {
		possibleCommands.put("untag", COMMAND_TYPE.UNTAG);
		possibleCommands.put("untags", COMMAND_TYPE.UNTAG);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiSortCommand() {
		possibleCommands.put("sort", COMMAND_TYPE.SORT);
		possibleCommands.put("arrange", COMMAND_TYPE.SORT);
		possibleCommands.put("sorts", COMMAND_TYPE.SORT);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiSearchCommand() {
		possibleCommands.put("search", COMMAND_TYPE.SEARCH);
		possibleCommands.put("find", COMMAND_TYPE.SEARCH);
		possibleCommands.put("f", COMMAND_TYPE.SEARCH);
		return possibleCommands;
	}
	
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiHelpCommand() {
		possibleCommands.put("help", COMMAND_TYPE.HELP);
		possibleCommands.put("helps", COMMAND_TYPE.HELP);
		possibleCommands.put("h", COMMAND_TYPE.HELP);
		possibleCommands.put("assist", COMMAND_TYPE.HELP);
		return possibleCommands;
	}
	private HashMap<String, COMMAND_TYPE> initiateFlexiChangeDirectoryCommand() {
		possibleCommands.put("cd", COMMAND_TYPE.CHANGE_DIRECTORY);
		possibleCommands.put("redirect", COMMAND_TYPE.CHANGE_DIRECTORY);
		return possibleCommands;
	}

	private HashMap<String, COMMAND_TYPE> initiateFlexiExitCommand() {
		possibleCommands.put("exit", COMMAND_TYPE.EXIT);
		possibleCommands.put("exits", COMMAND_TYPE.EXIT);
		possibleCommands.put("quit", COMMAND_TYPE.EXIT);
		possibleCommands.put("quits", COMMAND_TYPE.EXIT);
		possibleCommands.put("q", COMMAND_TYPE.EXIT);
		possibleCommands.put("q!", COMMAND_TYPE.EXIT);
		possibleCommands.put("terminate", COMMAND_TYPE.EXIT);
		return possibleCommands;
	}
	
}
```
###### DODO\src\Parser\Parser.java
``` java
package Parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Command.*;
import Task.*;

public class Parser {

	private HashMap<String, COMMAND_TYPE> possibleCommandErrors = new HashMap<String, COMMAND_TYPE>();		
	private final String MESSAGE_INPUT_ERROR = "You have entered an invalid input.";
	
	protected TASK_TYPE taskType;
	protected COMMAND_TYPE command;
	protected String taskName;
	protected Date startTime;
	protected Date endTime;
	protected int taskID;
	protected String tag;
	protected boolean isImportant;
	
	private DELETE_TYPE deleteType;
	private ArrayList<String> tagToDelete;
	private ArrayList<Integer> indexToDelete;
	private ArrayList<String> tags;
	private String oldTag;
	
	private ArrayList<Integer> indexOfFlagAndMark;
	private FLAGANDCOMPLETE_TYPE flagAndCompleteType;
	
	private String _commandAdd = "add ";
	private EDIT_TYPE editType;
	private SEARCH_TYPE searchType;
	private String searchByTask;
	private Date searchByDate;
	private String searchByTag;
	

	private SORT_TYPE sortType;
	private String newDirectory = "";
	
	public Parser (String userInput) {
		assert userInput.length() > 0;
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
		
		switch (commandType) {
			
			case ADD:
				userInput = processUserInput(_commandAdd + " " + userInput);
				AddParser addParser = new AddParser(userInput);
				setAddAttributes(addParser.getStartTime(), addParser.getEndTime(), addParser.getTaskName(), addParser.getTaskType());
				break;
			case DELETE:
				userInput = getUserInputContent(userInput);
				DeleteParser deleteParser = new DeleteParser(userInput);
				setDeleteAttributes(deleteParser.getDeleteType(), deleteParser.getTagToDelete(), deleteParser.getIndexToDelete());
				break;
			case EDIT:
				userInput = getUserInputContent(userInput);
				EditParser editParser = new EditParser(userInput);
				setEditAttributes(editParser.getTaskID(), editParser.getEndNewDate(), editParser.getStartNewDate(),
								  editParser.getNewTaskName(), editParser.getEditType(), editParser.getNewTag(),
								  editParser.getOldTag());
				break;
			case COMPLETE:
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser completeParser = new FlagAndCompleteParser(userInput);
				setFlagAndCompleteAttributes(completeParser.getFlagCompleteType(), completeParser.getTaskIndex());
				break;
			case FLAG:
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser flagParser = new FlagAndCompleteParser(userInput);
				setFlagAndCompleteAttributes(flagParser.getFlagCompleteType(), flagParser.getTaskIndex());
				break;
			case UNFLAG:
				userInput = getUserInputContent(userInput);
				FlagAndCompleteParser unflagParser = new FlagAndCompleteParser(userInput);
				setFlagAndCompleteAttributes(unflagParser.getFlagCompleteType(), unflagParser.getTaskIndex());
				break;
			case TAG:
				userInput = processUserInput(userInput);
				setTaskIndex(userInput.trim());
				break;
			case UNTAG:
				setTaskIndex(userInput);
				break;
			case SEARCH:
				userInput = getUserInputContent(userInput);
				SearchParser search = new SearchParser(userInput);
				setSearchAttributes(search.getSearchByDate(), search.getSearchByTask(), search.getSearchByTag(), search.getSearchType());
				break;
			case SORT:
				userInput = getUserInputContent(userInput);
				SortParser sort = new SortParser(userInput);
				setSortAttributes(sort.determineSortType(userInput));
				break;
			case CHANGE_DIRECTORY:
				newDirectory = getUserInputContent(userInput);
				break;
			case EXIT:
				break;
			case HELP:
				break;
			case UNDO:
				break;
			case REDO:
				break;
			case INVALID:
				break;
		}
	}
	
	private String processUserInput(String userInput) {
		String userTask = getUserInputContent(userInput);
		userTask = checkTaskImportance(userTask);
		return extractTag(userTask);	
	}

	private void checkIfValidUserInput(String userInput) {
		if (userInput.isEmpty()) {
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
		return temp[0].toLowerCase();
	}
	
	private String getUserInputContent(String userInput) {
		String[] temp = userInput.split("\\s+", 2);
		return temp[1];
	}
	
	private String checkTaskImportance(String userInput) {
		if (userInput.substring(userInput.length() - 1).equals("!")) {
			userInput = userInput.replace(userInput.substring(userInput.length() - 1), "");
			setTaskImportance(true);
		}
		else {
			setTaskImportance(false);
		}
		return userInput;
	}
	
	private String extractTag(String userTask) {
		tags = new ArrayList<String>();
		String[] str = userTask.split("[\\s+]");
		String temp = "";
		for (int i = 0; i < str.length; i++) {
			if (str[i].contains("#") && !str[i].contains("-")) {
				tags.add(str[i].replace("#", "").trim());
				str[i] = " ";
			}
			else {
				temp += str[i] + " ";
			}
		}
		return temp.trim();
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
	
	protected void setTaskTag(ArrayList<String> tags) {
		this.tags = tags;
	}
	
	protected void setTaskIndex(String index) {
		this.taskID = Integer.parseInt(index);
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

	public ArrayList<String> getTag() {
		return this.tags;
	}
	
	
/*	public TASK_TYPE getTaskType() {
		return this.taskType;
	}
*/	//*********************************** Accessors for DeleteParser ********************************//
	private void setDeleteAttributes(DELETE_TYPE deleteType, ArrayList<String> tagToDelete, ArrayList<Integer> indexToDelete) {
		this.tagToDelete = new ArrayList<String>();
		this.indexToDelete = new ArrayList<Integer>();
		
		this.indexToDelete = indexToDelete;
		this.tagToDelete = tagToDelete;
		this.deleteType = deleteType;
	}
	
	public DELETE_TYPE getDeleteType() {
		return this.deleteType;
	}
	
	public ArrayList<String> getTagToDelete() {
		return this.tagToDelete;
	}
	
	public ArrayList<Integer> getIndexToDelete() {
		return this.indexToDelete;
	}
	
	//***********************************Accessors for EditParser************************************//
	private void setEditAttributes(int taskID, Date endTime, Date startTime, String taskName, EDIT_TYPE editType,
									ArrayList<String> newTag, String oldTag) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.taskName = taskName;
		this.taskID = taskID;
		this.editType = editType;
		this.tags = newTag;
		this.oldTag = oldTag;
	}
	
	public int getTaskID() {
		return this.taskID;
	}
	
	public EDIT_TYPE getEditType() {
		return this.editType;
	}
	
	public String getOldTag() {
		return this.oldTag;
	}
	
	//**************************** Accessors for Flag/Unflag/CompleteParser ***********************//
	private void setFlagAndCompleteAttributes(FLAGANDCOMPLETE_TYPE flagAndCompleteType, ArrayList<Integer> indexOfFlagAndMark) {
		this.indexOfFlagAndMark = new ArrayList<Integer>();
		this.indexOfFlagAndMark = indexOfFlagAndMark;
		this.flagAndCompleteType = flagAndCompleteType;
	}
		
	public FLAGANDCOMPLETE_TYPE getFlagAndCompleteType() {
		return this.flagAndCompleteType;
	}
		
	public ArrayList<Integer> getTaskToFlagAndMark() {
		return this.indexOfFlagAndMark;
	}
	//********************************************* SearchParser ************************************//
	private void setSearchAttributes(Date searchByDate, String searchByTask, String searchByTag,
			SEARCH_TYPE searchType) {
		this.searchType = searchType;
		this.searchByTask = searchByTask;
		this.searchByDate = searchByDate;
		this.searchByTag = searchByTag;
	}
	
	public SEARCH_TYPE getSearchType() {
		return this.searchType;
	}
	
	public String getSearchByTask() {
		return this.searchByTask.trim();
	}
	
	public String getSearchByTag() {
		return this.searchByTag.trim();
	}
	
	public Date getSearchByDate() {
		return this.searchByDate;
	}
	//********************************************* SortParser ************************************//
	private void setSortAttributes(SORT_TYPE sortType) {
		this.sortType = sortType;	
	}

	public SORT_TYPE getSortType() {
		return this.sortType;
	}
	
	//************************************** ChangeDirectory Parser *********************************//
	public String getNewDirectory() {
		return this.newDirectory;
	}
}
```
###### DODO\src\Parser\SearchParser.java
``` java
package Parser;

import java.util.Date;
import java.util.List;

import Command.*;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class SearchParser {
	private String userTask;
	private String tag;
	private SEARCH_TYPE searchType;
	private Date searchByDate;
	private String PREPOSITION_BY = "by";
	private String PREPOSITION_BY_WITH_SPACE = "by";
	private String STRING_HASH_TAG = "#";
	
	public SearchParser(String userTask) {
		this.userTask = userTask;
		executeSearchParser(userTask);
	}

	private void executeSearchParser(String userTask) {
		userTask = removeBy(userTask);
		
		switch(determineSearchType(userTask)) {
			
		case BY_DATE:
			processByDate(userTask);
			break;
		case BY_TASK:
			break;
		case BY_TAG:
			processByTag(userTask);
			break;
		case INVALID:
			setSearchType(SEARCH_TYPE.INVALID);
			break;
		}
			
	}

	private void processByTag(String userTask) {
		tag = userTask.trim().substring(1, userTask.length());
		setSearchByTag(tag);
	}

	private void processByDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		setSearchByDate(dates.get(0));
	}

	private SEARCH_TYPE determineSearchType(String userTask) {
		
		if (isSearchDate(userTask)) {
			setSearchType(SEARCH_TYPE.BY_DATE);
			return SEARCH_TYPE.BY_DATE;
		}
		else if (isSearchTaskName(userTask)) {
			setSearchType(SEARCH_TYPE.BY_TASK);
			return SEARCH_TYPE.BY_TASK;
		}
		else if (isSearchTag(userTask)) {
			setSearchType(SEARCH_TYPE.BY_TAG);
			return SEARCH_TYPE.BY_TAG;
		}
		else {
			setSearchType(SEARCH_TYPE.INVALID);
			return SEARCH_TYPE.INVALID;
		}
		
	}

	private boolean isSearchTag(String userTask) {
		assert userTask.split("[\\s+]").length == 1;
		return (userTask.startsWith(STRING_HASH_TAG)) ? true : false;
	}

	private boolean isSearchDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		return (dates.size() != 0) ? true : false;
	}

	private boolean isSearchTaskName(String userTask) {
		return (!userTask.startsWith(STRING_HASH_TAG)) ? true : false;
	}

	private String removeBy(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		if (str[0].contains(PREPOSITION_BY)) {
			userTask.replace(PREPOSITION_BY_WITH_SPACE, "");
		}
		return userTask;
	}
	
	//*********** Setter ************//
	private void setSearchByDate(Date searchByDate) {
		this.searchByDate = searchByDate;
	}
	
	private void setSearchType(SEARCH_TYPE searchType) {
		this.searchType = searchType;
	}
	
	private void setSearchByTag(String tag) {
		this.tag = tag;
	}
	
	//*********** Getter ************//
	protected SEARCH_TYPE getSearchType() {
		return this.searchType;
	}
	
	protected Date getSearchByDate() {
		return this.searchByDate;
	}
	
	protected String getSearchByTag() {
		return this.tag;
	}

	protected String getSearchByTask() {
		return this.userTask;
	}

	
}
```
###### DODO\src\Parser\SortParser.java
``` java
package Parser;

import Command.*;

public class SortParser {

	private String ALPHABETICAL_ORDER = "abc";
	private String NUMERICAL_ORDER = "123";
	private String REVERSE_ALPHABETICAL_ORDER = "cba";
	private String REVERSE_NUMERICAL_ORDER = "321";
	private String DATE_ORDER = "date";
	private String PREPOSITION_BY = "by";
	
	
	public SortParser(String userTask) {
	
	}
	
	protected SORT_TYPE determineSortType(String userTask) {
		userTask = removeBy(userTask.toLowerCase());
		
		if (isSortByDate(userTask)) {
			return SORT_TYPE.BY_DATE;
		}
		else if (isSortByAlphabetical(userTask)) {
			return SORT_TYPE.BY_ASCENDING;
		}
		else if (isSortByReverseAlphabetical(userTask)) {
			return SORT_TYPE.BY_DESCENDING;
		}
		else {
			return SORT_TYPE.INVALID;
		}
	}

	private boolean isSortByReverseAlphabetical(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		return (str[0].trim().contains(REVERSE_ALPHABETICAL_ORDER) ||
				str[0].trim().contains(REVERSE_NUMERICAL_ORDER)) ? true : false;
	}

	private boolean isSortByAlphabetical(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		return (str[0].trim().contains(ALPHABETICAL_ORDER) ||
				str[0].trim().contains(NUMERICAL_ORDER)) ? true : false;
	}

	private boolean isSortByDate(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		return (str[0].trim().contains(DATE_ORDER)) ? true : false;
	}

	private String removeBy(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		if (str[0].contains(PREPOSITION_BY)) {
			userTask = userTask.replace("by", "");
		}
		return userTask.trim();
	}
}
```
###### DODO\src\Test\TestParser.java
``` java

package Test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Date;

import Command.*;
import Task.*;
import Parser.*;

public class TestParser {

	@Test
	public void testTaskType() throws Exception {
		
		
		Parser parser = new Parser("drive by the beach");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
		parser = new Parser("submit assignment 1 by tomorrow");
		assertEquals(TASK_TYPE.DEADLINED, parser.getType());
		
		parser = new Parser("take a walk by the beach by 2359hrs");
		assertEquals(TASK_TYPE.DEADLINED, parser.getType());
		
		parser = new Parser("submit assignment 1 by tomorrow by 2359hrs");
		assertEquals(TASK_TYPE.DEADLINED, parser.getType());
		
		parser = new Parser("fetch my brother at 2pm");
		assertEquals(TASK_TYPE.DEADLINED, parser.getType());
		
		parser = new Parser("attend soc camp from 27/6/2016 to 28/6/2016");
		assertEquals(TASK_TYPE.EVENT, parser.getType());
		
		parser = new Parser("jog from school to home");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
		parser = new Parser("bake matcha cheesecake at home");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
		parser = new Parser("piano tuning on sunday");
		assertEquals(TASK_TYPE.DEADLINED, parser.getType());
		
		parser = new Parser("build sandcastle on the road");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
		parser = new Parser("dancing on the dancefloor at zouk");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
	}
	
	@Test
	public void testFloating() throws Exception{
		
		Parser parser = new Parser("Drive by the beach");
		assertEquals("Drive by the beach", parser.getName());
		
		parser = new Parser("fetch my brother from school");
		assertEquals("fetch my brother from school", parser.getName());
		
		parser = new Parser("Pick Sheena from the school");
		assertEquals("Pick Sheena from the school", parser.getName());
		
		parser = new Parser("lie on the bed with Hannah");
		assertEquals("lie on the bed with Hannah", parser.getName());
		
		parser = new Parser("lie on the bed with Hannah at hotel 81");
		assertEquals("lie on the bed with Hannah at hotel 81", parser.getName());
		
		parser = new Parser("brush my teeth before bedtime");
		assertEquals("brush my teeth before bedtime", parser.getName());
		
		parser = new Parser("brush my teeth before bedtime");
		assertEquals("brush my teeth before bedtime", parser.getName());
		
		parser = new Parser("cycle with jun lem from sengkang to nus");
		assertEquals("cycle with jun lem from sengkang to nus", parser.getName());
	
		parser = new Parser("buy slurpee from |7/11|");
		assertEquals("buy slurpee from |7/11|", parser.getName());
		
		parser = new Parser("dance on the dancefloor at zouk");
		assertEquals("dance on the dancefloor at zouk", parser.getName());
		
		parser = new Parser("Meet Carousell buyer on the ground floor at block 2359");
		assertEquals("Meet Carousell buyer on the ground floor at block 2359", parser.getName());
		
	}
	/*
	@Test
	public void testDeadlined() throws Exception {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		Parser parser = new Parser("drive baby howard home by tomorrow");
		String endDate = "Thu Mar 31 23:59:59 SGT 2016";
		Date expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("watch movie at 2400hrs");
		endDate = "";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("watch movie on 0 feb 2017");
		endDate = "Tue Jan 31 23:59:00 SGT 2017";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("submit assignment 1 by thursday");
		endDate = "Thu Mar 24 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("submit assignment 1 by 27.5 feb 2017");
		endDate = "Fri May 27 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("meeting with boss on 27 march 2016 at 2pm");
		endDate = "Sun Mar 27 14:00:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("fetch mum from airport on this coming friday");
		endDate = "Fri Mar 25 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
	
		parser = new Parser("collect graduation certification on the day after tml");
		endDate = "Fri Mar 25 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("meet girlfriend on 29/03/16");
		endDate = "Tue Mar 29 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("tuition at 2:35pm");
		endDate = "Wed Mar 23 14:35:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
	
		parser = new Parser("watch movie tomorrow at 5pm");
		endDate = "Thu Mar 24 17:00:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
	
	
	}

	@Test
	public void testEvent() throws Exception {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		Parser parser = new Parser("study cs2103t from 01.04.2016 2pm to 05.04.2016");
		String endDate = "Tue Apr 05 14:00:00 SGT 2016";
		String startDate = "Fri Apr 01 14:00:00 SGT 2016";
		Date expectedStart = dateFormat.parse(startDate);
		Date expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getStartTime());
		assertEquals(expectedEnd, parser.getEndTime());
		
		parser = new Parser("serve the nation from 1 jan to 4 feb");
		startDate = "Sun Jan 01 23:59:00 SGT 2017";
		endDate = "Sat Feb 04 23:59:00 SGT 2017";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getStartTime());
		assertEquals(expectedEnd, parser.getEndTime());
		
		parser = new Parser("soc sports camp from 20 march 2016 to 22 march 2016");
		endDate = "Wed Mar 22 23:59:00 SGT 2017";
		startDate = "Mon Mar 20 23:59:00 SGT 2017";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getStartTime());
		assertEquals(expectedEnd, parser.getEndTime());
		
		parser = new Parser("attend photoshop refresher course from tomorrow to saturday");
		endDate = "Sat Mar 26 23:59:00 SGT 2016";
		startDate = "Thu Mar 24 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getStartTime());
		assertEquals(expectedEnd, parser.getEndTime());
	
	}
	*/
	@Test
	public void testComplete() {
		Parser parser = new Parser("complete 1");
		assertEquals(FLAGANDCOMPLETE_TYPE.SINGLE, parser.getFlagAndCompleteType());
		
		parser = new Parser("complete 1, 6, 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.MULTIPLE, parser.getFlagAndCompleteType());
		
		parser = new Parser("complete 1 to 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.RANGE, parser.getFlagAndCompleteType());
		
		parser = new Parser("complete all");
		assertEquals(FLAGANDCOMPLETE_TYPE.ALL, parser.getFlagAndCompleteType());

	}

	@Test
	public void testFlag() {
		Parser parser = new Parser("flag 1");
		assertEquals(FLAGANDCOMPLETE_TYPE.SINGLE, parser.getFlagAndCompleteType());
		
		parser = new Parser("flag 1, 6, 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.MULTIPLE, parser.getFlagAndCompleteType());
		
		parser = new Parser("flag 1 to 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.RANGE, parser.getFlagAndCompleteType());
		
		parser = new Parser("flag all");
		assertEquals(FLAGANDCOMPLETE_TYPE.ALL, parser.getFlagAndCompleteType());

	}
	
	@Test
	public void testDelete() {
		Parser parser = new Parser("delete 1");
		Integer single_delete = 1;
		assertEquals(DELETE_TYPE.SINGLE_INDEX, parser.getDeleteType());
		assertEquals( single_delete, parser.getIndexToDelete().get(0));
		
		parser = new Parser("delete 1, 6, 9");
		Integer delete1 = 1;
		Integer delete2 = 6;
		Integer delete3 = 9;
		assertEquals(DELETE_TYPE.MULTIPLE_INDEXES, parser.getDeleteType());
		assertEquals(delete1, parser.getIndexToDelete().get(0));
		assertEquals(delete2, parser.getIndexToDelete().get(1));
		assertEquals(delete3, parser.getIndexToDelete().get(2));
		
		parser = new Parser("delete 1 to 4");
		delete1 = 1;
		delete2 = 2;
		delete3 = 3;
		Integer delete4 = 4;
		assertEquals(DELETE_TYPE.RANGE_INDEXES, parser.getDeleteType());
		assertEquals(delete1, parser.getIndexToDelete().get(0));
		assertEquals(delete2, parser.getIndexToDelete().get(1));
		assertEquals(delete3, parser.getIndexToDelete().get(2));
		assertEquals(delete4, parser.getIndexToDelete().get(3));
		
		parser = new Parser("delete all");
		assertEquals(DELETE_TYPE.ALL_INDEXES, parser.getDeleteType());
		
		parser = new Parser("delete all tags");
		assertEquals(DELETE_TYPE.ALL_TAGS, parser.getDeleteType());
		
		parser = new Parser("delete #nus");
		String single_tag = "nus";
		assertEquals(DELETE_TYPE.SINGLE_TAG, parser.getDeleteType());
		assertEquals( single_tag, parser.getTagToDelete().get(0));

		parser = new Parser("delete #nus #singapore #SoC");
		String tag1 = "nus";
		String tag2 = "singapore";
		String tag3 = "SoC";
		assertEquals(DELETE_TYPE.MULTIPLE_TAGS, parser.getDeleteType());
		assertEquals(tag1, parser.getTagToDelete().get(0));
		assertEquals(tag2, parser.getTagToDelete().get(1));
		assertEquals(tag3, parser.getTagToDelete().get(2));
		
	}
	
	@Test
	public void testEdit() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		
		Parser parser = new Parser("edit 2 meet Hannah at Chong Pang");
		int taskID = 2;
		assertEquals(EDIT_TYPE.TASK_NAME, parser.getEditType());
		assertEquals("meet Hannah at Chong Pang", parser.getName());
		assertEquals(taskID, parser.getTaskID());
		
		parser = new Parser("edit 4 from 5th of May 2016 2359hrs");
		taskID = 4;
		String str = "Thu May 05 23:59:00 SGT 2016";
		Date startTime = dateFormat.parse(str);
		assertEquals(EDIT_TYPE.START_TIME, parser.getEditType());
		assertEquals(taskID, parser.getTaskID());
		assertEquals(startTime, parser.getStartTime());
		
		parser = new Parser("edit 2 to 5th of Nov 2016 2359hrs");
		taskID = 2;
		str = "Sat Nov 05 23:59:00 SGT 2016";
		Date endTime = dateFormat.parse(str);
		assertEquals(EDIT_TYPE.END_TIME, parser.getEditType());
		assertEquals(taskID, parser.getTaskID());
		assertEquals(endTime, parser.getEndTime());
		
		parser = new Parser("edit 4 by 5th of May 2016 2359hrs");
		taskID = 4;
		str = "Thu May 05 23:59:00 SGT 2016";
		endTime = dateFormat.parse(str);
		assertEquals(EDIT_TYPE.DEADLINED, parser.getEditType());
		assertEquals(taskID, parser.getTaskID());
		assertEquals(startTime, parser.getEndTime());
		
		parser = new Parser("edit 10 from 5th of Nov 2016 2359hrs to 12th Dec 2016");
		taskID = 10;
		str = "Sat Nov 05 23:59:00 SGT 2016";
		startTime = dateFormat.parse(str);
		String str2 = "Mon Dec 12 23:59:00 SGT 2016";
		endTime = dateFormat.parse(str2);
		assertEquals(EDIT_TYPE.EVENT_TIME, parser.getEditType());
		assertEquals(taskID, parser.getTaskID());
		assertEquals(startTime, parser.getStartTime());
	//	assertEquals(endTime, parser.getEndTime());
		
		parser = new Parser("edit #Singapore to #Melbourne");
		String oldTag = "Singapore";
		String newTag = "Melbourne";
		assertEquals(EDIT_TYPE.TAG, parser.getEditType());
		assertEquals(oldTag, parser.getOldTag());
		assertEquals(newTag, parser.getTag().get(0));
	
	}
}
```
