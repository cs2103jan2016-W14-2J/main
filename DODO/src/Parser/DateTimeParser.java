package Parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

	public DateTimeParser () {
		
	}
	
	protected String removeTheDayAfterTomorrow(String userInput) {
		if (userInput.contains(" the day after tomorrow")) {
			userInput = userInput.replace(" the day after tomorrow", "");
		}
		else if (userInput.contains(" day after tomorrow")) {
			userInput = userInput.replace(" day after tomorrow", "");
		}
		return userInput.trim();
	}
	
	protected String removeTomorrow(String userInput) {
		if (userInput.contains("tomorrow") || userInput.contains("tomorrow ")) {
			userInput = userInput.replace("tomorrow", "");
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
		
		if (userInput.contains(" next") && !userInput.contains(" next week")) {
			int index = taskItems.lastIndexOf("next");
			taskItems.remove(index + 2);
			taskItems.remove(index + 1);
			taskItems.remove(index);
			if (taskItems.get(index - 1).contains("the")) {
				taskItems.remove(index - 1);
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
		return name.trim();
	}
	
	protected Date checkAndSetDefaultEndTime(Date date, Date currentTime) {
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
}
