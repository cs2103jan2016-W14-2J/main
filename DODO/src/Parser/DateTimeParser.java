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
	
	private List<String> todayTypes = new ArrayList<>(Arrays.asList("tdy"));
	private List<String> tomorrowTypes = new ArrayList<>(Arrays.asList("tmr", "tml", "tmrw", "2moro"));
	
	private final String KEYWORD_AM = "am";
	private final String KEYWORD_PM = "pm";
	private final String KEYWORD_HRS = "hrs";
	private final String KEYWORD_HR = "hr";
	private final String KEYWORD_THE_DAY_AFTER_TOMORROW = " the day after tomorrow";
	private final String KEYWORD_DAY_AFTER_TOMORROW = " day after tomorrow";
	private final String KEYWORD_TOMORROW_1 = "tomorrow";
	private final String KEYWORD_TOMORROW_2 = "tomorrow ";
	
	public DateTimeParser () {
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
		if (userInput.contains("yesterday")) {
			userInput = userInput.replace("yesterday", "");
		}
		else if (userInput.contains("ytd")) {
			userInput = userInput.replace("ytd", "");
		}
		return userInput.trim();
	}
	
	protected String removeToday(String userInput) {
		if (userInput.contains("today")) {
			userInput = userInput.replace(" today", "");
		}
		return userInput.trim();
	}
	
	protected String removeThisComingWeekday(String userInput) {
		String[] str = userInput.toLowerCase().split("\\s+");
		ArrayList <String> taskItems = new ArrayList<String>(Arrays.asList(str));
		
		if (userInput.contains(" this coming")) {
			int index = taskItems.lastIndexOf("coming");
			taskItems.remove(index + 1);
			taskItems.remove(index);
			taskItems.remove(index - 1);
			userInput = toStringTaskElements(taskItems);
		}
		return userInput.trim();
	}
	
	protected String removeNextFewDays(String userInput) {
		String[] str = userInput.toLowerCase().split("\\s+");
		ArrayList <String> taskItems = new ArrayList<String>(Arrays.asList(str));
		int indexOfNext = taskItems.lastIndexOf("next");
		int indexOfDays = taskItems.lastIndexOf("days");
		
		if (indexOfNext != 0 && (indexOfDays - indexOfNext) == 1  
			&& !userInput.contains(" next week")) {
			
			taskItems.remove(indexOfDays);
			taskItems.remove(indexOfNext + 1);
			taskItems.remove(indexOfNext);
			if (taskItems.get(indexOfNext - 1).contains("the")) {
				taskItems.remove(indexOfNext - 1);
			}
			userInput = toStringTaskElements(taskItems);
		}
		return userInput.trim();
	}
	
	protected String removeDate(String userInput) {
		String[] str = userInput.toLowerCase().split("\\s+");
		ArrayList <String> taskItems = new ArrayList<String>(Arrays.asList(str));
		
		for (int i = 0; i < taskItems.size(); i++) {
			List<Date> dates = new PrettyTimeParser().parse(taskItems.get(i));
			if (dates.size() != 0) {
				String[] temp = taskItems.get(i).split("\\s+");
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
	
		if (userInput.contains(" next week")) {
			userInput = userInput.replace(" next week", "");
		}
		return userInput.trim();
	}
	protected String removeNextWeekday(String userInput) {
		String[] temp = userInput.split("[\\s+]");
		String newTaskName = "";
		int positionOfWeekday = 0;
		
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].contains("next")) {
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
		String[] str = userInput.toLowerCase().split("\\s+");
		ArrayList <String> taskItems = new ArrayList<String>(Arrays.asList(str));
	
		for (int i = 0; i < taskItems.size(); i++) {
			System.out.println("removeTime :" + i + " " + taskItems.get(i));
			
			List<Date> dates = new PrettyTimeParser().parse(taskItems.get(i));
	
			if ((taskItems.get(i).contains(KEYWORD_AM) || taskItems.get(i).contains(KEYWORD_PM) 
				|| taskItems.get(i).contains(KEYWORD_HR) || taskItems.get(i).contains(KEYWORD_HRS) ) 
				&& dates.size() != 0) {
				taskItems.remove(i);
				System.out.println("removeTime :" + userInput);
			}
		}
		userInput = toStringTaskElements(taskItems);
		System.out.println("removeTime :" + userInput);
		return userInput;
	}
	
	protected String processYesterday (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if ((contentToAnalyse.get(i).toLowerCase()).contains("ytd")) {
				contentToAnalyse.set(i, "yesterday");
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	protected String processToday (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if (todayTypes.contains(contentToAnalyse.get(i).toLowerCase())) {
				contentToAnalyse.set(i, "today");
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	protected String processTomorrow (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			System.out.println("checkForAbbrevation :" + i + " " + contentToAnalyse.get(i));
			if (tomorrowTypes.contains(contentToAnalyse.get(i).toLowerCase())) {
				contentToAnalyse.set(i, "tomorrow");
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	protected String processAt (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			System.out.println("checkForAbbrevation :" + i + " " + contentToAnalyse.get(i));
			if (contentToAnalyse.get(i).contains("@")) {
				contentToAnalyse.set(i, "at");
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
		System.out.println("toStringTaskElements : " + name);
		return name.trim();
	}
	
	protected Date checkAndSetDefaultEndTime(Date date, Date currentTime) {
		Date newDate = date;		
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String dateWithoutTime = df.format(date);
		String newDateWithTime= "";
		String timeAt6pm = dateWithoutTime + " " + "18:00:00";
		Calendar cal = Calendar.getInstance();
		
		try {
			Date dateWithoutDate = sdf.parse(sdf.format(date));
			Date currentWithoutDate = sdf.parse(sdf.format(currentTime));
			Date defaultDeadLineForExecutive = sf.parse(timeAt6pm);

			if (dateWithoutDate.equals(currentWithoutDate) && currentTime.before(defaultDeadLineForExecutive)) {
				newDate = defaultDeadLineForExecutive;
			} 
			else if (dateWithoutDate.equals(currentWithoutDate) && currentTime.after(defaultDeadLineForExecutive)) {
				newDateWithTime = dateWithoutTime + " " + "23:59:59";
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
}
