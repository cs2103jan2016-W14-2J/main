//@@author: Hao Jie
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
	private ArrayList<String> dayTypes;
	private ArrayList<String> monthTypes;
	private ArrayList<String> preposition;
	private String confirmTaskName = "";
	private String possibleDate = "";
	
	private final String KEYWORD_AM = "am";
	private final String KEYWORD_PM = "pm";
	private final String KEYWORD_HRS = "hrs";
	private final String KEYWORD_HR = "hr";
	private final String KEYWORD_THE_DAY_AFTER_TOMORROW = " the day after tomorrow";
	private final String KEYWORD_DAY_AFTER_TOMORROW = " day after tomorrow";
	private final String KEYWORD_TOMORROW_1 = "tomorrow";
	private final String KEYWORD_TOMORROW_2 = "tomorrow ";
	private final String KEYWORD_YESTERDAY_SHORTFORM = "ytd";
	private final String KEYWORD_YESTERDAY = "yesterday";
	private final String KEYWORD_TODAY = "today";
	private final String KEYWORD_COMING = "coming";
	private final String KEYWORD_THIS_COMING = " this coming";
	private final String KEYWORD_AT_1 = "at";
	private final String KEYWORD_AT_2 = "@";
	private final String KEYWORD_THE = " the ";
	private final String KEYWORD_DAYS = "days";
	private final String KEYWORD_NEXT = " next ";
	private final String KEYWORD_NEXT_1 = "next";
	private final String KEYWORD_NEXT_WEEK = " next week";
	private final String KEYWORD_ST = "st";
	private final String KEYWORD_ND = "nd";
	private final String KEYWORD_RD = "rd";
	private final String KEYWORD_TH = "th";
	
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
		dayTypes = new ArrayList<>(Arrays.asList("mon", "monday", "tue", "tues", "tuesday", 
												 "wed", "wednesday", "thur", "thurs", "thursday", 
												 "fri", "friday", "sat", "saturday", "sun", "sunday"));
		monthTypes = new ArrayList<>(Arrays.asList("jan", "january", "feb", "february", "mar",
												   "march", "apr","april", "may","may", "june","june", 
												   "jul", "july", "aug", "august", "sept", "september", 
												   "oct", "october", "nov", "november", "dec", "december"));
		preposition = new ArrayList<>(Arrays.asList("on", "at", "by", "before", "in", "from" , "to"));
	}
	
	protected String removeTheDayAfterTomorrow(String userInput) {
		System.out.println("removeTheDayAfterTomorrow : " + userInput);
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		if (userInput.contains(KEYWORD_THE_DAY_AFTER_TOMORROW) && dates.size() != 0) {
			userInput = userInput.replace(KEYWORD_THE_DAY_AFTER_TOMORROW, "");
			possibleDate = KEYWORD_THE_DAY_AFTER_TOMORROW.trim();
		}
		else if (userInput.contains(KEYWORD_DAY_AFTER_TOMORROW) && dates.size() != 0) {
			userInput = userInput.replace(KEYWORD_DAY_AFTER_TOMORROW, "");
			possibleDate = KEYWORD_THE_DAY_AFTER_TOMORROW.trim();
		}
		return userInput.trim();
	}
	
	protected String removeTomorrow(String userInput) {
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		if ((userInput.contains(KEYWORD_TOMORROW_1) || userInput.contains(KEYWORD_TOMORROW_2))
			&& dates.size() != 0) {
			userInput = userInput.replace(KEYWORD_TOMORROW_1, "");
			setDateElements(KEYWORD_TOMORROW_1);
		}
		return userInput.trim();
	}
	
	protected String removeYesterday(String userInput) {
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		if (userInput.contains(KEYWORD_YESTERDAY) && dates.size() != 0) {
			userInput = userInput.replace(KEYWORD_YESTERDAY, "");
			possibleDate = KEYWORD_YESTERDAY;
		}
		return userInput.trim();
	}
	
	protected String removeToday(String userInput) {
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		if (userInput.contains(KEYWORD_TODAY) && dates.size() != 0) {
			userInput = userInput.replace(KEYWORD_TODAY, "");
			possibleDate = KEYWORD_TODAY;
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
			possibleDate = taskItems.get(index - 1) + " " + taskItems.get(index) + " " + taskItems.get(index - 1);
		}
		return userInput.trim();
	}
	
	protected String removeNextFewDays(String userInput) {

		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		int indexOfNext = taskItems.lastIndexOf(KEYWORD_NEXT_1);
		int indexOfDays = taskItems.lastIndexOf(KEYWORD_DAYS);

		if (indexOfNext != 0 && (indexOfDays - indexOfNext) == 2  
			&& !userInput.contains(KEYWORD_NEXT_WEEK)) {
			possibleDate = taskItems.get(indexOfNext) + " " + taskItems.get(indexOfNext + 1) 
							+ " " + taskItems.get(indexOfDays);
			taskItems.remove(indexOfDays);
			taskItems.remove(indexOfNext + 1);
			taskItems.remove(indexOfNext);
			if (taskItems.get(indexOfNext - 1).contains(KEYWORD_THE)) {
				possibleDate = "the " + possibleDate;
				taskItems.remove(indexOfNext - 1);
			}
			userInput = toStringTaskElements(taskItems);
		}
		return userInput.trim();
	}
	
	protected String removeNumericalDate(String userInput) {
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		for (int i = 0; i < taskItems.size(); i++) {
			List<Date> dates = new PrettyTimeParser().parse(taskItems.get(i));
			if (dates.size() != 0) {
				String[] temp = taskItems.get(i).split("[.-/]");
				System.out.println("removeDate : " + taskItems.get(i));
				if (temp.length >= 2) {
					possibleDate = taskItems.get(i);
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
	
	protected String removeWeekday(String userInput) {
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		String[] temp = userInput.split(STRING_SPLITTER);
		String newInput = "";
		if (dates.size() != 0) {
			for (int i = 0; i < temp.length; i++) {
				if (dayTypes.contains(temp[i].toLowerCase())) {
				}
				else {
					newInput += temp[i] + " ";
				}
			}
			userInput = newInput.trim();
		}
		return userInput;
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
					possibleDate += temp[i] + " ";
					temp[i] = "";
					possibleDate += temp[positionOfWeekday] + " ";
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
				possibleDate += taskItems.get(i) + " ";
				taskItems.remove(i);
			}
		}
		userInput = toStringTaskElements(taskItems);
		return userInput;
	}
	
	private String removeHoliday(String userTask) {
		boolean containsPreposition = false;
		String[] temp = userTask.split(STRING_SPLITTER);	
		for (int i = 0; i < temp.length; i++) {
			if (preposition.contains(temp[i])) {
				containsPreposition = true;
			}
		}
		
		if (userTask.contains("valentine's day") && containsPreposition == true) {
			userTask = userTask.replace("valentine's day", "");
			possibleDate = "valetine's day ";
		}
		else if (userTask.contains("christmas") && containsPreposition == true) {
			userTask = userTask.replace("christmas", "");
			possibleDate = "christmas ";
		}
		else if (userTask.contains("new year's") && containsPreposition == true) {
			userTask = userTask.replace("new year's", "");
			possibleDate = "new year's ";
		}
		else if (userTask.contains("new year's eve") && containsPreposition == true) {
			userTask = userTask.replace("new year's eve", "");
			possibleDate = "new year's eve ";
		}
		else if (userTask.contains("halloween") && containsPreposition == true) {
			userTask = userTask.replace("halloween", "");
			possibleDate = "halloween ";
		}
		else if (userTask.contains("mother's day") && containsPreposition == true) {
			userTask = userTask.replace("mother's day", "");
			possibleDate = "mother's day ";
		}
		else if (userTask.contains("father's day") && containsPreposition == true) {
			userTask = userTask.replace("father's day", "");
			possibleDate = "father's day ";
		}
		return userTask;
	}
	
	protected String removeEnglishDate(String userInput) {
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		System.out.println("CONFIRMTASKNAME #32 : " + dates.size());
		String[] temp = userInput.split(STRING_SPLITTER);
		int positionOfMonth = 0;
		int positionOfDay = 0;
		int positionOfYear = 0;
		String day = "";
		String month = "";
		String year = "";
		boolean containsPreposition = false;
		boolean containsMonth = false;
		
		for (int i = 0; i < temp.length; i++) {
			if (monthTypes.contains(temp[i].toLowerCase())) {
				positionOfMonth = i;
				month = temp[positionOfMonth];
				containsMonth = true;
			}
			else if (preposition.contains(temp[i])) {
				containsPreposition = true;
			}
		}
	
		if (dates.size() == 0 || (dates.size() != 0 && containsPreposition == false || containsMonth == false)) {
			System.out.println("CONFIRMTASKNAME : " + userInput);
			//		confirmTaskName = userInput;
			return userInput;
		}
		
		if (positionOfMonth + 1 < temp.length) {
			if (temp[positionOfMonth + 1].length() == 2 && Integer.parseInt(temp[positionOfMonth + 1]) < 20) {
				positionOfYear = positionOfMonth + 1;
				year = temp[positionOfYear];
			}
			else if (temp[positionOfMonth + 1].length() == 4 && Integer.parseInt(temp[positionOfMonth + 1]) < 2020) {
				positionOfYear = positionOfMonth + 1;
				year = temp[positionOfYear];
			}
		}
		
		if (positionOfMonth - 1 > 0) {
			if (temp[positionOfMonth - 1].endsWith(KEYWORD_ST) && (temp[positionOfMonth - 1].startsWith("1")
				|| temp[positionOfMonth - 1].startsWith("31"))) {
				positionOfDay = positionOfMonth - 1;
				day = temp[positionOfDay];
			}
			else if (temp[positionOfMonth - 1].endsWith(KEYWORD_ND) && temp[positionOfMonth - 1].startsWith("2")) {
				positionOfDay = positionOfMonth + 1;
				day = temp[positionOfDay];
			}
			else if (temp[positionOfMonth - 1].endsWith(KEYWORD_RD) && temp[positionOfMonth - 1].startsWith("3")) {
				positionOfDay = positionOfMonth + 1;
				day = temp[positionOfDay];
			}
			else if (temp[positionOfMonth - 1].endsWith(KEYWORD_TH)) {
				possibleDate = temp[positionOfMonth - 1].replace(KEYWORD_TH, "");
				if (Integer.parseInt(possibleDate) > 3 && Integer.parseInt(possibleDate) < 32) {
					positionOfDay = positionOfMonth - 1;
					day = temp[positionOfDay];
				}
			}
			else if (Integer.parseInt(temp[positionOfMonth - 1]) > 0 && Integer.parseInt(temp[positionOfMonth - 1]) < 32) {
				positionOfDay = positionOfMonth - 1;
				day = temp[positionOfDay];
			}
		}
		
		possibleDate = day + " " + month + " " + year;
		confirmTaskName = userInput.replace(possibleDate.trim(), "");
		return confirmTaskName;
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
			System.out.println("Process Tomorrow : " + contentToAnalyse.get(i));
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
			else if (date.before(currentTime)) {
				cal.setTime(date);
				cal.add(Calendar.DATE, 1);
				newDateWithTime = sf.format(cal.getTime()); 
				newDate = sf.parse(newDateWithTime);
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate;
	}
	protected Date checkAndSetDefaultStartTime(Date date, Date currentTime) {
		Date newDate = date;		
		SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT_WITH_TIME);
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_WITHOUT_DATE_FORMAT);
		DateFormat df = new SimpleDateFormat(DATE_FORMAT_1);
		String dateWithoutTime = df.format(date);
		System.out.println("HELLO : " + dateWithoutTime);
		String currentDateWithoutTime= df.format(currentTime);
		String timeAt12am = dateWithoutTime + " " + "00:00:00";
		Calendar cal = Calendar.getInstance();
		
		try {
			Date dateWithoutDate = sdf.parse(sdf.format(date));
			Date currentWithoutDate = sdf.parse(sdf.format(currentTime));
			Date defaultStartTime = sf.parse(timeAt12am);
			System.out.println("DefaultstartTime : " + dateWithoutTime);
			if (dateWithoutDate.equals(currentWithoutDate) && dateWithoutTime.equals(currentDateWithoutTime)) {
				System.out.println("DefaultstartTime : " + defaultStartTime);
				newDate = currentTime;
			} 
			else if (!dateWithoutTime.equals(currentDateWithoutTime) && dateWithoutDate.equals(currentWithoutDate)) {
				newDate = defaultStartTime;
			}
			else {
				newDate = date;
				
			}
	/*		else if (date.before(currentTime)) {
				cal.setTime(date);
				cal.add(Calendar.DATE, 1);
				newDateWithTime = sf.format(cal.getTime()); 
				newDate = sf.parse(newDateWithTime);
			}
	*/	}
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
		temp = removeHoliday(userTask);
		temp = removeTheDayAfterTomorrow(temp);
		temp = removeYesterday(temp);
		temp = removeTomorrow(temp);
		temp = removeToday(temp);
		temp = removeThisComingWeekday(temp);
		temp = removeNextFewDays(temp);
		temp = removeNextWeek(temp);
		temp = removeWeekday(temp);
		temp = removeNextWeekday(temp);
		temp = removeTime(temp);
		temp = removeNumericalDate(temp);
		temp = removeEnglishDate(temp);
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
	
	protected String checkForAbbreviation(ArrayList<String> taskItems) {
		String name = "";
		name = processToday(taskItems);
		name = processTomorrow(taskItems);
		name = processYesterday(taskItems);
		name = processAt(taskItems);
		name = process2400hrs(taskItems);
		name = processNumericalDate(taskItems);
		return name;
	}
	
	private String processNumericalDate(ArrayList<String> taskItems) {
		String temp = toStringTaskElements(taskItems);
		String[] str;
		List<Date> dates = new PrettyTimeParser().parse(temp);
		if (dates.size() != 0) {
			for (int i = 0; i < taskItems.size(); i++) {
				str = taskItems.get(i).split("[./-]");
				
				if (!taskItems.get(i).contains("#") && !taskItems.get(i).toLowerCase().contains("am") 
					&& !taskItems.get(i).toLowerCase().contains("pm")) {
					if (str.length == 2 && Integer.parseInt(str[0]) < 32 && Integer.parseInt(str[1]) < 13) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						String newDate = str[0] + "/" + str[1] + "/" + year;
						taskItems.set(i, newDate);
					}
				}
			}
		}
		return toStringTaskElements(taskItems);
	}

	private String process2400hrs(ArrayList<String> taskItems) {
		for (int i = 0; i < taskItems.size(); i++) {
			if (taskItems.get(i).contains("2400hrs")) {
				taskItems.set(i, "0000hrs");
			}
		}
		return toStringTaskElements(taskItems);
	}
	

	protected String getConfirmTaskName() {
		return this.confirmTaskName;
	}
	private void setDateElements(String possibleDate) {
		this.possibleDate = possibleDate;
	}
	protected String getDateElements() {
		return this.possibleDate;
	}
}
