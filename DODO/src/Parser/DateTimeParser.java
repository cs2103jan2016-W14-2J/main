//@@author: A0125552L
package Parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import Logger.LoggerFile;

public class DateTimeParser {
	
	public enum DATE_TYPES {
		TYPE_THIS_COMING_WEEKDAY, TYPE_NEXT_WEEK, TYPE_NEXT_WEEKDAY, TYPE_TOMORROW, TYPE_TODAY, TYPE_THE_DAY_AFTER_TOMORROW,
		TYPE_NEXT_FEW_DAYS, TYPE_DATE, TYPE_TIME, TYPE_NULL
	};
	
	private static Logger logger;
	
	private List<String> todayTypes;
	private List<String> tomorrowTypes;
	private ArrayList <String> taskItems;
	private ArrayList<String> dayTypes;
	private ArrayList<String> monthTypes;
	private ArrayList<String> preposition;
	
	private String confirmTaskName = "";
	private String possibleDate = "";
	private Date newDate;
	private SimpleDateFormat sf;
	private SimpleDateFormat sdf;
	private DateFormat df;
	
	// String constants.
	private final static String KEYWORD_AM = "am";
	private final static String KEYWORD_PM = "pm";
	private final static String KEYWORD_HRS = "hrs";
	private final static String KEYWORD_HR = "hr";
	private final static String KEYWORD_THE_DAY_AFTER_TOMORROW = " the day after tomorrow";
	private final static String KEYWORD_DAY_AFTER_TOMORROW = " day after tomorrow";
	private final static String KEYWORD_TOMORROW_1 = "tomorrow";
	private final static String KEYWORD_TOMORROW_2 = "tomorrow ";
	private final static String KEYWORD_YESTERDAY_SHORTFORM = "ytd";
	private final static String KEYWORD_YESTERDAY = "yesterday";
	private final static String KEYWORD_TODAY = "today";
	private final static String KEYWORD_COMING = "coming";
	private final static String KEYWORD_THIS_COMING = " this coming";
	private final static String KEYWORD_AT_1 = "at";
	private final static String KEYWORD_AT_2 = "@";
	private final static String KEYWORD_HASH_TAG = "#";
	private final static String KEYWORD_THE = " the ";
	private final static String KEYWORD_DAYS = "days";
	private final static String KEYWORD_NEXT_1 = "next";
	private final static String KEYWORD_NEXT_WEEK = " next week";
	private final static String KEYWORD_NEXT_WEEK_2 = "next week";
	
	private final static String STRING_EMPTY = "";
	private final static String STRING_SPACING = " ";
	private final static String STRING_SLASH = "/";
	private final static String STRING_HOLIDAY_VALENTINES_DAY = "valentine's day";
	private final static String STRING_HOLIDAY_CHRISTMAS = "christmas";
	private final static String STRING_HOLIDAY_NEW_YEAR = "new year's";
	private final static String STRING_HOLIDAY_NEW_YEAR_EVE = "new year's eve";
	private final static String STRING_HOLIDAY_HALLOWEEN = "halloween";
	private final static String STRING_HOLIDAY_MOTHERS_DAY = "mother's day";
	private final static String STRING_HOLIDAY_FATHERS_DAY = "father's day";
	private final static String STRING_SPLITTER = "\\s+";
	private final static String PUNCTUATION_REMOVER = "[.-/]";

	// Date constants
	private final static String DEFAULT_TIME_6PM = "18:00:00";
	private final static String DEFAULT_TIME_1159PM = "23:59:59";
	private final static String DEFAULT_TIME_2400HRS = "2400hrs";
	private final static String DEFAULT_TIME_0000HRS = "0000hrs";
	private final static String DEFAULT_TIME_000000 = "00:00:00";
	private final static String DATE_FORMAT_1 = "dd/MM/yyyy";
	private final static String DATE_FORMAT_2 = "yyyy/MM/dd";
	private final static String DATE_FORMAT_3 = "dd.MM.yyyy";
	private final static String DATE_FORMAT_4 ="dd-MM-yyyy";
	private final static String DATE_FORMAT_WITH_TIME = "dd/MM/yyyy HH:mm:ss";
	private final static String TIME_WITHOUT_DATE_FORMAT = "HH:mm";
	
	private final static int NUM_MAX_DAYS_PER_MONTH = 31;
	private final static int NUM_MAX_MONTH_PER_YEAR = 12;

	// Logging messages for processing DateAndTimeParser methods
	private static final String LOGGER_MESSAGE_REMOVE_THE_DAY_AFTER_TOMORROW = "DateAndTimeParser class : Removing the day after tomorrow.";
	private static final String LOGGER_MESSAGE_REMOVE_TOMORROW = "DateAndTimeParser class : Removing tomorrow.";
	private static final String LOGGER_MESSAGE_REMOVE_YESTERDAY = "DateAndTimeParser class : Removing yesterday.";
	private static final String LOGGER_MESSAGE_REMOVE_TODAY = "DateAndTimeParser class : Removing today.";
	private static final String LOGGER_MESSAGE_REMOVE_THIS_COMING_WEEKDAY = "DateAndTimeParser class : Removing this coming weekday.";
	private static final String LOGGER_MESSAGE_REMOVE_NEXT_FEW_DAYS = "DateAndTimeParser class : Removing next few days.";
	private static final String NULL_POINTER_EXCEPTION = "DateAndTimeParser class : May receive null pointer exception.";
	private static final String LOGGER_MESSAGE_REMOVE_NUMERICAL_DATE = "DateAndTimeParser class : Removing numerical date.";
	private static final String LOGGER_MESSAGE_REMOVE_NEXT_WEEK = "DateAndTimeParser class : Removing next week.";
	private static final String LOGGER_MESSAGE_REMOVE_WEEKDAY = "DateAndTimeParser class : Removing weekday.";
	private static final String LOGGER_MESSAGE_REMOVE_NEXT_WEEKDAY = "DateAndTimeParser class : Removing next weekday.";
	private static final String LOGGER_MESSAGE_REMOVE_TIME = "DateAndTimeParser class : Removing time.";
	private static final String LOGGER_MESSAGE_REMOVE_HOLIDAY = "DateAndTimeParser class : Removing holiday.";
	private static final String LOGGER_MESSAGE_SET_DEFAULT_END_TIME = "DateAndTimeParser class : Setting default end time.";
	private static final String LOGGER_MESSAGE_SET_DEFAULT_START_TIME = "DateAndTimeParser class : Setting default start time.";
	private static final String LOGGER_MESSAGE_PROCESS_EXTRACT_DATE = "DateAndTimeParser class : Extracting date.";
	private static final String LOGGER_MESSAGE_PROCESS_CONVERT_DATE_ELEMENT = "DateAndTimeParser class : Converting date.";
	private static final String LOGGER_MESSAGE_CHECK_ABBREVIATIONS = "DateAndTimeParser class : Checking for abbreviations.";


	
	public DateTimeParser () {
		
		logger = LoggerFile.getLogger();
		
		sf = new SimpleDateFormat(DATE_FORMAT_WITH_TIME);
		sdf = new SimpleDateFormat(TIME_WITHOUT_DATE_FORMAT);
		df = new SimpleDateFormat(DATE_FORMAT_1);
		
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
	
	/*
	 * This method extracts the word "the day after tomorrow" and "day after
	 * "tomorrow" from the user input. 
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with "day after tomorrow" removed.  
	 * 
	 */
	protected String removeTheDayAfterTomorrow(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_THE_DAY_AFTER_TOMORROW);
		
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		
		if (userInput.contains(KEYWORD_THE_DAY_AFTER_TOMORROW) && dates.size() != 0) {
			userInput = userInput.replace(KEYWORD_THE_DAY_AFTER_TOMORROW, STRING_EMPTY);
			possibleDate = KEYWORD_THE_DAY_AFTER_TOMORROW.trim();
		}
		else if (userInput.contains(KEYWORD_DAY_AFTER_TOMORROW) && dates.size() != 0) {
			userInput = userInput.replace(KEYWORD_DAY_AFTER_TOMORROW, STRING_EMPTY);
			possibleDate = KEYWORD_THE_DAY_AFTER_TOMORROW.trim();
		}
		
		return userInput.trim();
	}
	
	/*
	 * This method extracts the word "tomorrow" 
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with "tomorrow" removed.  
	 * 
	 */
	protected String removeTomorrow(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_TOMORROW);
		
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		
		if ((userInput.contains(KEYWORD_TOMORROW_1) || userInput.contains(KEYWORD_TOMORROW_2))
			&& dates.size() != 0) {
			
			userInput = userInput.replace(KEYWORD_TOMORROW_1, STRING_EMPTY);
			setDateElements(KEYWORD_TOMORROW_1);
		}
		
		return userInput.trim();
	}
	
	/*
	 * This method extracts the word "yesterday" 
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with "yesterday" removed.  
	 * 
	 */
	protected String removeYesterday(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_YESTERDAY);
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		
		if (userInput.contains(KEYWORD_YESTERDAY) && dates.size() != 0) {
		
			userInput = userInput.replace(KEYWORD_YESTERDAY, STRING_EMPTY);
			possibleDate = KEYWORD_YESTERDAY;
		}
		
		return userInput.trim();
	}
	
	/*
	 * This method extracts the word "today" 
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with "today" removed.  
	 * 
	 */
	protected String removeToday(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_TODAY);
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		
		if (userInput.contains(KEYWORD_TODAY) && dates.size() != 0) {
		
			userInput = userInput.replace(KEYWORD_TODAY, STRING_EMPTY);
			possibleDate = KEYWORD_TODAY;
		}
		
		return userInput.trim();
	}
	
	/*
	 * This method extracts the word "this coming weekday" 
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with "this coming weekday" removed.  
	 * 
	 */
	protected String removeThisComingWeekday(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_THIS_COMING_WEEKDAY);
		
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		if (userInput.toLowerCase().contains(KEYWORD_THIS_COMING)) {
			int index = taskItems.lastIndexOf(KEYWORD_COMING);
		
			taskItems.remove(index + 1);
			taskItems.remove(index);
			taskItems.remove(index - 1);
			
			userInput = toStringTaskElements(taskItems);
			possibleDate = taskItems.get(index - 1) + STRING_SPACING + taskItems.get(index) +
						   STRING_SPACING+ taskItems.get(index - 1);
		}
		
		return userInput.trim();
	}
	
	/*
	 * This method extracts the word "next few days" 
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with "next few days" removed.  
	 * 
	 */
	protected String removeNextFewDays(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_NEXT_FEW_DAYS);
		
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		int indexOfNext = taskItems.lastIndexOf(KEYWORD_NEXT_1);
		int indexOfDays = taskItems.lastIndexOf(KEYWORD_DAYS);

		if (indexOfNext != 0 && (indexOfDays - indexOfNext) == 2  
			&& !userInput.contains(KEYWORD_NEXT_WEEK_2)) {
			
			logger.log(Level.WARNING, NULL_POINTER_EXCEPTION);
			
			possibleDate = taskItems.get(indexOfNext) + STRING_SPACING + taskItems.get(indexOfNext + 1) + 
						   STRING_SPACING + taskItems.get(indexOfDays);
			
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
	
	/*
	 * This method extracts the numerical date (11/11, 11/11/16 etc)
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with numerical date removed.  
	 * 
	 */
	protected String removeNumericalDate(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_NUMERICAL_DATE);
		
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		for (int i = 0; i < taskItems.size(); i++) {
		
			List<Date> dates = new PrettyTimeParser().parse(taskItems.get(i));
			
			if (dates.size() != 0) {
			
				String[] temp = taskItems.get(i).split(PUNCTUATION_REMOVER);
				
				if (temp.length >= 2) {
				
					possibleDate = taskItems.get(i);
					taskItems.remove(i);
				}
			}
		}
		
		userInput = toStringTaskElements(taskItems);
		return userInput;
		
	}
	
	/*
	 * This method extracts the words "next week" 
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with "next week" removed.  
	 * 
	 */
	
	protected String removeNextWeek(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_NEXT_WEEK);
		
		if (userInput.contains(KEYWORD_NEXT_WEEK) || userInput.contains(KEYWORD_NEXT_WEEK_2) ) {
			userInput = userInput.replace(KEYWORD_NEXT_WEEK_2, STRING_EMPTY);
		}
		
		return userInput.trim();
	}
	
	/*
	 * This method extracts weekday from the user input.
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with weekday element removed.  
	 * 
	 */
	
	protected String removeWeekday(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_WEEKDAY);
		
		List<Date> dates = new PrettyTimeParser().parse(userInput);
		String[] temp = userInput.split(STRING_SPLITTER);
		String newInput = "";
		
		if (dates.size() != 0) {
			for (int i = 0; i < temp.length; i++) {
				if (!dayTypes.contains(temp[i].toLowerCase())) {
					newInput += temp[i] + STRING_SPACING;
				}
			}
			userInput = newInput.trim();
		}
		
		return userInput;
	}
	
	/*
	 * This method extracts the next weekday elements (next thursday etc..) 
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with the next weekday element removed.  
	 * 
	 */
	protected String removeNextWeekday(String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_NEXT_WEEKDAY);
		
		String[] temp = userInput.split(STRING_SPLITTER);
		String newTaskName = "";
		int positionOfWeekday = 0;

		for (int i = 0; i < temp.length; i++) {
			
			if (temp[i].contains(KEYWORD_NEXT_1)) {
			
				positionOfWeekday = i + 1;
				List<Date> dates = new PrettyTimeParser().parse(temp[positionOfWeekday]);
				
				if (dates.size() != 0) {
				
					possibleDate += temp[i] + STRING_SPACING;
					temp[i] = STRING_EMPTY;
					
					possibleDate += temp[positionOfWeekday] + STRING_SPACING;
					temp[positionOfWeekday] = STRING_EMPTY;
					i++;
				}
			}
			newTaskName += temp[i] + STRING_SPACING;
		}
		
		return newTaskName.trim();
	}
	
	/*
	 * This method extracts the time element
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with the time element removed.  
	 * 
	 */
	protected String removeTime (String userInput) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_TIME);
		
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		for (int i = 0; i < taskItems.size(); i++) {
		
			List<Date> dates = new PrettyTimeParser().parse(taskItems.get(i));
	
			if ((taskItems.get(i).toLowerCase().contains(KEYWORD_AM) || taskItems.get(i).toLowerCase().contains(KEYWORD_PM) 
				|| taskItems.get(i).toLowerCase().contains(KEYWORD_HR) || taskItems.get(i).toLowerCase().contains(KEYWORD_HRS) ) 
				&& dates.size() != 0) {
			
				possibleDate += taskItems.get(i) + STRING_SPACING;
				taskItems.remove(i);
			}
		}
		
		userInput = toStringTaskElements(taskItems);
		return userInput;
	}
	
	/*
	 * This method extracts the holiday element 
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with the holiday element removed.  
	 * 
	 */
	private String removeHoliday(String userTask) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_REMOVE_HOLIDAY);
		
		String[] temp = userTask.split(STRING_SPLITTER);
		boolean containsPreposition = false;
		containsPreposition = containsPreposition(temp);
		
		if (userTask.toLowerCase().contains(STRING_HOLIDAY_VALENTINES_DAY) && containsPreposition == true) {
			userTask = userTask.toLowerCase().replace(STRING_HOLIDAY_VALENTINES_DAY, STRING_EMPTY);
			possibleDate = STRING_HOLIDAY_VALENTINES_DAY + STRING_SPACING;
		}
		else if (userTask.toLowerCase().contains(STRING_HOLIDAY_CHRISTMAS) && containsPreposition == true) {
			userTask = userTask.toLowerCase().replace(STRING_HOLIDAY_CHRISTMAS, STRING_EMPTY);
			possibleDate = STRING_HOLIDAY_CHRISTMAS + STRING_SPACING;
		}
		else if (userTask.toLowerCase().contains(STRING_HOLIDAY_NEW_YEAR) && containsPreposition == true) {
			userTask = userTask.toLowerCase().replace(STRING_HOLIDAY_NEW_YEAR, STRING_EMPTY);
			possibleDate = STRING_HOLIDAY_NEW_YEAR + STRING_SPACING;
		}
		else if (userTask.toLowerCase().contains(STRING_HOLIDAY_NEW_YEAR_EVE) && containsPreposition == true) {
			userTask = userTask.toLowerCase().replace(STRING_HOLIDAY_NEW_YEAR_EVE, STRING_EMPTY);
			possibleDate = STRING_HOLIDAY_NEW_YEAR_EVE + STRING_SPACING;
		}
		else if (userTask.toLowerCase().contains(STRING_HOLIDAY_HALLOWEEN) && containsPreposition == true) {
			userTask = userTask.toLowerCase().replace(STRING_HOLIDAY_HALLOWEEN, STRING_EMPTY);
			possibleDate = STRING_HOLIDAY_HALLOWEEN + STRING_SPACING;
		}
		else if (userTask.toLowerCase().contains(STRING_HOLIDAY_MOTHERS_DAY) && containsPreposition == true) {
			userTask = userTask.replace(STRING_HOLIDAY_MOTHERS_DAY, STRING_EMPTY);
			possibleDate = STRING_HOLIDAY_MOTHERS_DAY + STRING_SPACING;
		}
		else if (userTask.toLowerCase().contains(STRING_HOLIDAY_FATHERS_DAY) && containsPreposition == true) {
			userTask = userTask.toLowerCase().replace(STRING_HOLIDAY_FATHERS_DAY, STRING_EMPTY);
			possibleDate = STRING_HOLIDAY_FATHERS_DAY + STRING_SPACING;
		}
		
		return userTask;
	}

	/*
	 * This method extracts the english date element (1st May 2016 etc)
	 * 
	 * @param userInput {@code String} 
	 * 
	 * @return {@code String} with the english date element removed.  
	 * 
	 */
	protected String removeEnglishDate(String userInput) {

		List<Date> dates = new PrettyTimeParser().parse(userInput);
		String[] str = userInput.split(STRING_SPLITTER);
		taskItems = new ArrayList<String>(Arrays.asList(str));
		
		for (int i = 0; i < taskItems.size(); i++) {
			if (monthTypes.contains(taskItems.get(i).toLowerCase()) && dates.size() != 0) {
				taskItems.remove(i);
			}
		}

		return toStringTaskElements(taskItems);
	}

	/*
	 * This method checks if a string contains preposition
	 * 
	 * @param userInput {@code String[]} 
	 * 
	 * @return {@code boolean} 
	 * 
	 */
	protected boolean containsPreposition(String[] userInput) {
		
		for (int i = 0; i < userInput.length; i++) {
			if (preposition.contains(userInput[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * This method converts short form of yesterday to proper word.
	 * 
	 * @param contentToAnalyse {@code ArrayList<String} 
	 * 
	 * @return {@code String} with short form of the word yesterday being
	 * replaced. 
	 * 
	 */
	protected String processYesterday (ArrayList<String> contentToAnalyse) {

		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if ((contentToAnalyse.get(i).toLowerCase()).contains(KEYWORD_YESTERDAY_SHORTFORM)) {
				contentToAnalyse.set(i, KEYWORD_YESTERDAY);
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	/*
	 * This method converts short form of today to proper word.
	 * 
	 * @param contentToAnalyse {@code ArrayList<String} 
	 * 
	 * @return {@code String} with short form of the word today being
	 * replaced. 
	 * 
	 */
	protected String processToday (ArrayList<String> contentToAnalyse) {

		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if (todayTypes.contains(contentToAnalyse.get(i).toLowerCase())) {
				contentToAnalyse.set(i, KEYWORD_TODAY);
			}
		}
		return toStringTaskElements(contentToAnalyse);
	}
	
	/*
	 * This method converts short form of tomorrow to proper word.
	 * 
	 * @param contentToAnalyse {@code ArrayList<String} 
	 * 
	 * @return {@code String} with short form of the word tomorrow being
	 * replaced. 
	 * 
	 */
	protected String processTomorrow (ArrayList<String> contentToAnalyse) {
		
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if (tomorrowTypes.contains(contentToAnalyse.get(i).toLowerCase())) {
				contentToAnalyse.set(i, KEYWORD_TOMORROW_1);
			}
		}
		
		return toStringTaskElements(contentToAnalyse);
	}
	
	/*
	 * This method converts short form of "At" to proper word.
	 * 
	 * @param contentToAnalyse {@code ArrayList<String} 
	 * 
	 * @return {@code String} with short form of the word "At" being
	 * replaced. 
	 * 
	 */
	protected String processAt (ArrayList<String> contentToAnalyse) {

		for (int i = 0; i < contentToAnalyse.size(); i++) {
			if (contentToAnalyse.get(i).toLowerCase().contains(KEYWORD_AT_2)) {
				contentToAnalyse.set(i, KEYWORD_AT_1);
			}
		}
		
		return toStringTaskElements(contentToAnalyse);
	}
	
	/*
	 * This method converts numerical date dd/MM to dd/MM/yy
	 * 
	 * @param taskItems {@code ArrayList<String} 
	 * 
	 * @return {@code String} with numercial date converted to dd/MM/yy
	 * 
	 */
	protected String processNumericalDate(ArrayList<String> taskItems) {

		String[] str;
		
		for (int i = 0; i < taskItems.size(); i++) {
			List<Date> dates = new PrettyTimeParser().parse(taskItems.get(i));
			
			if (dates.size() != 0) {
				str = taskItems.get(i).split(PUNCTUATION_REMOVER);
			
				if (!taskItems.get(i).contains(KEYWORD_HASH_TAG) && !taskItems.get(i).toLowerCase().contains(KEYWORD_AM) 
					&& !taskItems.get(i).toLowerCase().contains(KEYWORD_PM)) {
					
					if (str.length == 2 && Integer.parseInt(str[0]) <= NUM_MAX_DAYS_PER_MONTH 
						&& Integer.parseInt(str[1]) <= NUM_MAX_MONTH_PER_YEAR) {
					
						int year = Calendar.getInstance().get(Calendar.YEAR);
						String newDate = str[0] + STRING_SLASH + str[1] + STRING_SLASH + year;
						taskItems.set(i, newDate);
					}
				}
			}
		}
		
		return toStringTaskElements(taskItems);
	}
	
	/*
	 * This method converts 2400HRS to 0000hrs on the following day.
	 * 
	 * @param taskItems {@code ArrayList<String} 
	 * 
	 * @return {@code String} 
	 * 
	 */
	protected String process2400hrs(ArrayList<String> taskItems) {
		
		for (int i = 0; i < taskItems.size(); i++) {
			if (taskItems.get(i).contains(DEFAULT_TIME_2400HRS)) {
				taskItems.set(i, DEFAULT_TIME_0000HRS);
			}
		}
		
		return toStringTaskElements(taskItems);
	}
	
	/*
	 * This method concatenates ArrayList<String> to String
	 * 
	 * @param taskNameArrayList {@code ArrayList<String} 
	 * 
	 * @return {@code String}
	 * 
	 */
	protected String toStringTaskElements(ArrayList<String> taskNameArrayList) {
		
		String name = "";
		
		for (int i = 0; i < taskNameArrayList.size(); i++) {
			name += taskNameArrayList.get(i) + STRING_SPACING; 
		}
		
		return name.trim();
	}
	
	/*
	 * This method checks and sets default end time.
	 * 
	 * @param date {@code Date} and currentTime {@code Date} 
	 * 
	 * @return {@code Date}
	 * 
	 */
	protected Date checkAndSetDefaultEndTime(Date date, Date currentTime) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_SET_DEFAULT_END_TIME);
		
		newDate = date;		
		String dateWithoutTime = df.format(date);
		String newDateWithTime= "";
		String timeAt6pm = dateWithoutTime + STRING_SPACING + DEFAULT_TIME_6PM;
		Calendar cal = Calendar.getInstance();
		
		try {
			
			Date dateWithoutDate = sdf.parse(sdf.format(date));
			Date currentWithoutDate = sdf.parse(sdf.format(currentTime));
			Date defaultDeadLineForExecutive = sf.parse(timeAt6pm);

			if (dateWithoutDate.equals(currentWithoutDate) && currentTime.before(defaultDeadLineForExecutive)) {
				
				newDate = defaultDeadLineForExecutive;
			} 
			else if (dateWithoutDate.equals(currentWithoutDate) && currentTime.after(defaultDeadLineForExecutive)) {
				
				newDateWithTime = dateWithoutTime + STRING_SPACING + DEFAULT_TIME_1159PM;
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
	
	
	/*
	 * This method checks and sets default start time.
	 * 
	 * @param date {@code Date} and currentTime {@code Date} 
	 * 
	 * @return {@code Date}
	 * 
	 */
	
	protected Date checkAndSetDefaultStartTime(Date date, Date currentTime) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_SET_DEFAULT_START_TIME);
		
		newDate = date;		
		String dateWithoutTime = df.format(date);
		String currentDateWithoutTime= df.format(currentTime);
		String timeAt12am = dateWithoutTime + STRING_SPACING + DEFAULT_TIME_000000;
		
		try {
			Date dateWithoutDate = sdf.parse(sdf.format(date));
			Date currentWithoutDate = sdf.parse(sdf.format(currentTime));
			Date defaultStartTime = sf.parse(timeAt12am);
			
			if (dateWithoutDate.equals(currentWithoutDate) && dateWithoutTime.equals(currentDateWithoutTime)) {
				newDate = currentTime;
			} 
			else if (!dateWithoutTime.equals(currentDateWithoutTime) && dateWithoutDate.equals(currentWithoutDate)) {
				newDate = defaultStartTime;
			}
			else {
				newDate = date;
				
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return newDate;
	}
	
	/*
	 * This method checks if a string has date and time elements.
	 * 
	 * @param userTask {@code String} 
	 * 
	 * @return {@code boolean} true if consists date and time elements.
	 * 
	 */
	protected boolean hasDateAndTimeElements(String userTask) {
		
		String temp = "";
		temp = extractDate(userTask);
		
		return (temp.trim().equals(userTask.trim())) ? false : true;
	}
	
	/*
	 * This method extracts date and time element from a string
	 * 
	 * @param userTask {@code String}
	 * 
	 * @return {@code String} with date and time elements removed.
	 * 
	 */
	protected String extractDate(String userTask) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_EXTRACT_DATE);
		
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
	
	/*
	 * This method checks for date and time element of an event task.
	 * 
	 * @param taskName {@code String}, LAST_POSITION_OF_FROM {@code int} and
	 * LAST_POSITION_OF_TO {@code int} 
	 * 
	 * @return {@code boolean}
	 * 
	 */
	protected boolean checkForDateAndTime(String taskName, int LAST_POSITION_OF_FROM, int LAST_POSITION_OF_TO) {	
		
		String str = taskName.substring(LAST_POSITION_OF_FROM, taskName.length());
		List<Date> dates = new PrettyTimeParser().parse(str);
		
		return (dates.size() != 0) ? true : false; 
	}
	
	/*
	 * This method converts dd/MM/yyyy to MM/dd/yyyy format for PrettyTime parsing.
	 * 
	 * @param taskItems {@code ArrayList<String>}
	 * 
	 * @return {@code String}
	 * 
	 */
	protected String checkAndConvertDateElement(ArrayList<String> taskItems) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_PROCESS_CONVERT_DATE_ELEMENT);
		
		DateFormat destDf = new SimpleDateFormat(DATE_FORMAT_2);
	
		for (int i = 0; i < taskItems.size(); i++) {
			
			try {
				DateFormat srcDf = new SimpleDateFormat(DATE_FORMAT_1);
				Date date = srcDf.parse(taskItems.get(i));
				taskItems.set(i, destDf.format(date));
			}
			catch (ParseException e) {		
				e.printStackTrace();
			}
			
			try {
				DateFormat srcDf = new SimpleDateFormat(DATE_FORMAT_3);
				Date date = srcDf.parse(taskItems.get(i));
				taskItems.set(i, destDf.format(date));
			}
			catch (ParseException e) {		
				e.printStackTrace();
			}
			
			try {
				DateFormat srcDf = new SimpleDateFormat(DATE_FORMAT_4);
				Date date = srcDf.parse(taskItems.get(i));
				taskItems.set(i, destDf.format(date));
			}
			catch (ParseException e) {		
				e.printStackTrace();
			}
		}
		
		return toStringTaskElements(taskItems);
	}
	
	/*
	 * This method checks and processes abbreviations of
	 * 
	 * @param taskName {@code String}, LAST_POSITION_OF_FROM {@code int} and
	 * LAST_POSITION_OF_TO {@code int} 
	 * 
	 * @return {@code boolean}
	 * 
	 */
	protected String checkForAbbreviation(ArrayList<String> taskItems) {
		
		logger.log(Level.INFO, LOGGER_MESSAGE_CHECK_ABBREVIATIONS);
		
		String name = "";
		name = processToday(taskItems);
		name = processTomorrow(taskItems);
		name = processYesterday(taskItems);
		name = processAt(taskItems);
		name = process2400hrs(taskItems);
		name = processNumericalDate(taskItems);
		
		return name;
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
