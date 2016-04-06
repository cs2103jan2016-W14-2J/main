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
	
	protected String checkForAbbreviation(ArrayList<String> taskItems) {
		String name = "";
		name = processToday(taskItems);
		name = processTomorrow(taskItems);
		name = processYesterday(taskItems);
		name = processAt(taskItems);
		return name;
	}
	
}
