package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Parser.DateAndTimeParser.DATE_TYPES;

public class DateTimeParser {
	
	public enum DATE_TYPES {
		TYPE_THIS_COMING_WEEKDAY, TYPE_NEXT_WEEK, TYPE_NEXT_WEEKDAY, TYPE_TOMORROW, TYPE_TODAY, TYPE_THE_DAY_AFTER_TOMORROW,
		TYPE_NEXT_FEW_DAYS, TYPE_DATE, TYPE_TIME, TYPE_NULL
	};
	
	private List<String> todayTypes = new ArrayList<>(Arrays.asList("tdy"));
	private List<String> tomorrowTypes = new ArrayList<>(Arrays.asList("tmr", "tml", "tmrw", "2moro"));
	private List<String> yesterdayTypes = new ArrayList<>(Arrays.asList("ytd"));
	
	private final String KEYWORD_AM = "am";
	private final String KEYWORD_PM = "pm";
	private final String KEYWORD_HRS = "hrs";

	private String tempTaskName = "";
	
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
		if (userInput.contains(" tomorrow")) {
			userInput = userInput.replace(" tomorrow", "");
		}
		return userInput.trim();
	}
	
	protected String removeToday(String userInput) {
		if (userInput.contains(" today")) {
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
	
	protected String removeNextWeek(String userInput) {
	
		if (userInput.contains(" next week")) {
			userInput = userInput.replace(" next week", "");
		}
		return userInput.trim();
	}
	
	protected String removeTime (String userInput) {
		String[] str = userInput.toLowerCase().split("\\s+");
		ArrayList <String> taskItems = new ArrayList<String>(Arrays.asList(str));
		String temp = "";
				
		for (int i = 0; i < taskItems.size(); i++) {
			System.out.println("removeTime :" + i + " " + taskItems.get(i));
			temp = taskItems.get(i).substring(taskItems.get(i).length() - 2);
			if ((temp.equals(KEYWORD_AM) || temp.equals(KEYWORD_PM)) && taskItems.get(i).length() != 2) {
				taskItems.remove(i);
			}
			else if ((temp.equals(KEYWORD_AM) || temp.equals(KEYWORD_PM)) && taskItems.get(i).length() == 2) {
				taskItems.remove(i);
				taskItems.remove(i - 1);
			}
		}
		userInput = toStringTaskElements(taskItems);
		return userInput;
		
	}
	
	protected boolean containsYesterday(String userInput) {
		return (userInput.contains(" ytd ")) ? true : false;
	}
	
	protected String processYesterday (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if ((contentToAnalyse.get(i)).contains(" ytd ")) {
				contentToAnalyse.remove(i);
				contentToAnalyse.set(i, " yesterday ");
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	protected boolean containsToday(String userInput) {
		return (userInput.contains(" tdy ")) ? true : false;
	}
	
	protected boolean containsTheDayAfterTomorrow(String userInput) {
		return (userInput.contains(" the day after tomorrow") || 
				userInput.contains(" day after tomorrow")) ? true : false;
	}
	
	protected String processToday (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if (todayTypes.contains(contentToAnalyse.get(i))) {
				contentToAnalyse.add(i, "today ");
				contentToAnalyse.remove(i+1);
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	protected boolean containsTomorrow(String userInput) {
		return (userInput.contains(" tmr ") || userInput.contains(" tmrw ")
			|| userInput.contains(" tml ") || userInput.contains(" 2moro ")) ? true : false;
	}
	
	protected String processTomorrow (ArrayList<String> contentToAnalyse) {
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			System.out.println("checkForAbbrevation :" + i + " " + contentToAnalyse.get(i));
			if (tomorrowTypes.contains(contentToAnalyse.get(i))) {
				contentToAnalyse.add(i, "tomorrow ");
				contentToAnalyse.remove(i+1);
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
}
