package Parser;

import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;

public class DateAndTimeParser {
	
	public enum DATE_TYPES {
		TYPE_THIS_COMING_WEEKDAY, TYPE_NEXT_WEEK, TYPE_NEXT_WEEKDAY, TYPE_TOMORROW, TYPE_TODAY, TYPE_THE_DAY_AFTER_TOMORROW,
		TYPE_NEXT_FEW_DAYS, TYPE_DATE, TYPE_TIME
	};
	
	private List<String> dayTypes = new ArrayList<>(Arrays.asList("null", "mon", "monday", "tue", "tuesday", "wed", "wednesday",
			"thurs", "thursday", "fri", "friday", "sat", "saturday", "sun", "sunday"));
	private List<String> tomorrowTypes = new ArrayList<>(Arrays.asList("tomorrow","tmr", "tml", "tmrw", "2moro"));
	private List<String> monthTypes = new ArrayList<>(Arrays.asList("jan", "january", "feb", "february", "mar",
			"march", "apr","april", "may","may", "june","june", "jul", "july", "aug", "august", "sept", "september", "oct",
			"october", "nov", "november", "dec", "december"));
	
	private final String KEYWORD_AM = "am";
	private final String KEYWORD_PM = "pm";
	private final String KEYWORD_HRS = "hrs";
	private final String KEYWORD_HR = "hr";
	
	private final int MAX_YEAR_PROPER = 2020;
	private final int MAX_YEAR_INPROPER = 20;
	private final int MAX_MONTH_PER_YEAR = 12;
	private final int NUM_CONTENT_OF_DATE_PROPER = 3;
	private final int NUM_CONTENT_OF_DATE_INPROPER = 2;
	private final int LENGTH_OF_HRS_STRING = 3;
	private final int LENGTH_OF_AM_PM_STRING = 2;
	private final int DUMMY_VALUE = -1;
	
	private String tempTaskName = "";
	
	private int[] daysPerMonth = new int[] {DUMMY_VALUE, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31,30, 31};
	
	public DateAndTimeParser() {
		
	}
	
	public Date analysePossibleDateElements(ArrayList<String> contentToAnalyse) {
		ArrayList<int[]> combined = new ArrayList<int[]>();
		System.out.println("Debug: Test DateandTimeParser @contentToAnalyse " + contentToAnalyse.get(0));
				
		// [taskName, currentDayOfTheWeek, Hour, minute, day, month, year]
		int[] dateTimeElements = new int[6];
		
		// set keyword into this method just in case user input the following example
		// from tampines to yishun.
		setTempTaskName(contentToAnalyse.get(0));
		contentToAnalyse.remove(0);
			
		System.out.println("Debug: Test currentWord @DateAndTimeParser line 56");
		
		for (int i = 0; i < contentToAnalyse.size(); i++) {
			String currentWord = contentToAnalyse.get(i);
			System.out.println("Debug currentWord @line 63: " + currentWord);
		
			switch (determineDateTypes(currentWord, contentToAnalyse)) {
		
		
			case TYPE_DATE:
				System.out.println("Debug: Test TYPE_DATE  @line 69.");
				dateTimeElements = getDate(currentWord, contentToAnalyse, dateTimeElements, i);
				combined.add(dateTimeElements);
				break;
				
			case TYPE_TIME:
				dateTimeElements = getTime(currentWord, dateTimeElements, i);
				combined.add(dateTimeElements);
				break;
					
			case TYPE_TODAY:
				dateTimeElements = getToday(currentWord, dateTimeElements, i);
				combined.add(dateTimeElements);			
				break;
				
			case TYPE_TOMORROW:
				dateTimeElements = getTomorrow(currentWord, dateTimeElements, i);
				combined.add(dateTimeElements);
				break;
					
			case TYPE_THIS_COMING_WEEKDAY:
				System.out.println("Debug currentWord @line 89: " + currentWord);
				dateTimeElements = getThisComingWeekday(currentWord, dateTimeElements, contentToAnalyse, i);
				combined.add(dateTimeElements);
				break;
				
			case TYPE_THE_DAY_AFTER_TOMORROW:
				dateTimeElements = getTheDayAfterTomorrow(currentWord, dateTimeElements, i);
				combined.add(dateTimeElements);
				break;
					
			case TYPE_NEXT_FEW_DAYS:
				dateTimeElements = getNextFewDays(currentWord, contentToAnalyse, dateTimeElements);
				combined.add(dateTimeElements);
				break;
					
	/*		case TYPE_NEXT_WEEK:
				dateTimeElements = getNextWeek(currentWord, dateElements, dateTimeElements, i);
				break;
	*/				
			case TYPE_NEXT_WEEKDAY:
				dateTimeElements = getNextWeekday(currentWord, contentToAnalyse, dateTimeElements, i);
				combined.add(dateTimeElements);
				break;
	
			default:
				continue;
				
			}
		}
		
		System.out.println("Debug: @DateAndTimeParser line 117.");
		return processDateAndTime(combined);
	}
	
	private void setTempTaskName(String current) {
		this.tempTaskName += current + " ";
	}
	
	public String tempTaskName() {
		return this.tempTaskName;
	}

	private Date processDateAndTime(ArrayList<int[]> combined) {
		
		String timeString = "";
		Date newDate = new Date();
		
		int[] getDateTimeElements = new int[6];
		int[] temp = new int[6];		
		
		for (int i = 0; i < combined.size(); i++) {
			getDateTimeElements = combined.get(i);
			temp = compareAndExtractDetails(getDateTimeElements, temp);
		}
		
		temp = setDefaultDeadline(temp);
		
		timeString = convertToTimeString(temp);
		
		System.out.println("Debug: @DateAndTimeParser line 149. " + timeString);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		try {
			newDate = simpleDateFormat.parse(timeString);
			System.out.println(newDate);
			
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate;
	}

	
	/*
	 * @Description: set default deadline for tasks without a given time or date.
	 * 				 The default deadline for time is 2359hrs and for date is the current date.
	 * 
	 */
	private int[] setDefaultDeadline(int[] temp) {
		//[currentDayOfTheWeek, Hour, minute, day, month, year]
		if (temp[1] == 0 && temp[2] == 0) {
			temp[1] = 23;
			temp[2] = 59;
		}
		else if (temp[3] == 0 && temp[4] == 0 && temp[5] == 0) {
			Calendar cal = Calendar.getInstance();
			temp[3] = cal.get(Calendar.DAY_OF_MONTH);
			temp[4] = cal.get(Calendar.MONTH);
			temp[5] = cal.get(Calendar.YEAR);
		}
		return temp;
	}

	private String convertToTimeString(int[] temp) {
		String time = "";
		String date = "";
		
		time = temp[1] + ":" + temp[1];
		date = temp[3] + "/" + temp[4] + "/" + temp[5];
		
		String combined = date + " " + time;
		return combined;
	}


	private int[] compareAndExtractDetails(int[] getDateTimeElements, int[] temp) {
		
		for (int i = 0; i < getDateTimeElements.length; i++) {
			if (getDateTimeElements[i] >= temp[i]) {
				temp[i] = getDateTimeElements[i];
			}
		}
		return temp;
	}


	private DATE_TYPES determineDateTypes(String currentWord, ArrayList<String> contentToAnalyse) {
		
		System.out.println("Debug: Test currentWord @determineDateType line 184. " + contentToAnalyse.size());
		
		if (isNumericalDateType(currentWord)) {
			System.out.println("Debug: Test isnumericaldateType @line 187.");
			return DATE_TYPES.TYPE_DATE;
		}
		if (isEnglishDateType(contentToAnalyse)) {
			System.out.println("Debug: Test isEnglishDateType @line 191.");
			return DATE_TYPES.TYPE_DATE;
		}
		else if (isTimeType(currentWord)) {
			System.out.println("Debug: Test isTimeType @line 195.");
			return DATE_TYPES.TYPE_TIME;
		}
		else if (isTodayType(contentToAnalyse)) {
			System.out.println("Debug: Test isTodayType @line 199.");
			return DATE_TYPES.TYPE_TODAY;
		}
		else if (isTomorrowType(contentToAnalyse)) {
			System.out.println("Debug: Test isTomorrowType @line 203.");
			return DATE_TYPES.TYPE_TOMORROW;
		}
		else if (isThisComingWeekday(contentToAnalyse)) {
			System.out.println("Debug: Test isThisComingWeekday @line 207.");
			return DATE_TYPES.TYPE_THIS_COMING_WEEKDAY;
		}
		else if (isTheDayAfterTomorrow(contentToAnalyse)) {
			System.out.println("Debug: Test isTheDayAfterTomorrow @line 211.");
			return DATE_TYPES.TYPE_THE_DAY_AFTER_TOMORROW;
		}
		else if (isNextFewDays(contentToAnalyse)) {
			System.out.println("Debug: Test isNextFewDays @line 215.");
			return DATE_TYPES.TYPE_NEXT_FEW_DAYS;
		}
/*		else if (isNextWeek(currentWord, dateElements, i)) {
			return DATE_TYPES.TYPE_NEXT_WEEK;
		}
*/		else if (isNextWeekday(contentToAnalyse)) {
			System.out.println("Debug: Test isNextWeekDay @line 222.");
			return DATE_TYPES.TYPE_NEXT_WEEKDAY;
		}
		else {
			return null;
		}

	}
	
	//**********************************************    get date type    **********************************************//
	
	
	private int[] getNextWeekday(String currentWord, ArrayList<String> contentToAnalyse, int[] dateTimeElements, int i) {
		String dayOfWeek = "";
		
		if ( i + 1 <= contentToAnalyse.size()) {
			dayOfWeek = contentToAnalyse.get(i+1);
		}
		
		if (dayTypes.contains(dayOfWeek) && currentWord.equals("next")) {
			int daysToAdd = checkWeekday(dayOfWeek);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, daysToAdd);
			dateTimeElements = getCalendar(cal, dateTimeElements);
		}
		
		return dateTimeElements;
		
	}

	private int[] getNextFewDays(String currentWord, ArrayList<String> contentToAnalyse, int[] dateTimeElements) {
		int numOfDays = 0;
		
		if (contentToAnalyse.size() > 3) {
			numOfDays = Integer.parseInt(contentToAnalyse.get(contentToAnalyse.indexOf("next") + 1));
			System.out.println("Debug: Test isTimeType @line 257." + numOfDays);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, numOfDays);
		dateTimeElements = getCalendar(cal, dateTimeElements);
		
		return dateTimeElements;
	}

	private int[] getTheDayAfterTomorrow(String currentWord, int[] dateTimeElements, int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 2);
		dateTimeElements = getCalendar(cal, dateTimeElements);
		return dateTimeElements;
	}

	private int[] getThisComingWeekday(String currentWord, int[] dateTimeElements, ArrayList<String> contentToAnalyse, int i) {
		
		// case for "this coming thursday"
		if (contentToAnalyse.size() == 2) {
			currentWord = contentToAnalyse.get(1);
		}
		// case for "this thursday"
		else if (contentToAnalyse.size() == 3) {
			currentWord = contentToAnalyse.get(2);
		}
		
		// get the number of days to add to current date.
		int daysToAdd = checkWeekday(currentWord);
		Calendar cal = Calendar.getInstance();
		
		System.out.println("Debug: Test checkWeekday @line 292. " + daysToAdd);
		
		cal.add(Calendar.DATE, daysToAdd);
		dateTimeElements = getCalendar(cal, dateTimeElements);
		
		return dateTimeElements;
	}


	/*
	 * @param
	 * @description: get the 
	 */
	private int checkWeekday(String currentWord) {
		int intendedWeekday = getNumDaysAhead(currentWord);
		System.out.println("Debug: Test intendedWeekday @line 307. " + intendedWeekday);
		int numDays = 0;
		
		System.out.println("Debug: Test checkWeekday @line 305. " + intendedWeekday);
		
		Calendar cal = Calendar.getInstance();
		System.out.println("Debug: Test checkWeekday @line 309. " + cal.get(Calendar.DAY_OF_WEEK));
		
		if (cal.get(Calendar.DAY_OF_WEEK) > intendedWeekday) {
			numDays = intendedWeekday - cal.get(Calendar.DAY_OF_WEEK) + 7;
			return numDays;
		}
		else if (cal.get(Calendar.DAY_OF_WEEK) == intendedWeekday) {
			numDays += 7;
			return numDays;
		}
		else {
			numDays = intendedWeekday - cal.get(Calendar.DAY_OF_WEEK);
			System.out.println("Debug: Test checkWeekday @line 321. " + numDays);
			return numDays;
		}
		
	}
	
	private int getNumDaysAhead(String currentWord) {
		double weekday = 0;
		
		for (int i = 0; i < dayTypes.size(); i++) {
			if (dayTypes.get(i).equals(currentWord)) {
				weekday = (double) i;
				break;
			}
		}
		
		weekday = Math.ceil(weekday / 2) + 1;
		
		return (int) weekday;
	}

	private int[] getTomorrow(String currentWord, int[] dateTimeElements, int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		dateTimeElements = getCalendar(cal, dateTimeElements);
		return dateTimeElements;
		
	}

	private int[] getToday(String currentWord, int[] dateTimeElements, int i) {
		
		Calendar cal = Calendar.getInstance();
		dateTimeElements = getCalendar(cal, dateTimeElements);
		return dateTimeElements;
	}
	

	private int[] getTime(String currentWord, int[] dateTimeElements, int i) {
		
		if (currentWord.contains("am") || currentWord.contains("pm")) {
			dateTimeElements = processAMPM(currentWord, dateTimeElements);
		}
		else if (currentWord.contains("hrs") || currentWord.contains("hr")) {
			currentWord = currentWord.replace("hrs", "");
			currentWord = currentWord.replace("hr", "");
			dateTimeElements = processHRS(currentWord, dateTimeElements);
		}
		return dateTimeElements;
		
	}
	
	/*
	 * convert 12hours into 24 hours time format
	 * Example: 2pm to 1400 or 2am to 0200
	 */
	private int[] processAMPM(String currentWord, int[] dateTimeElements) {
		boolean isAM = false;
		String time = "";
		
		if (currentWord.contains("am")) {
			currentWord = currentWord.replace("am", "");
			isAM = true;
		}
		else {
			currentWord = currentWord.replace("pm", "");
		}
		String[] _24hours = currentWord.split("[.:]");
		
		if (_24hours.length == 1) {
			time = currentWord;
		}
		else if (_24hours.length == 2) {
			time = _24hours[0] + _24hours[1];
		}
		
		dateTimeElements = convertToWorldTime(time, isAM, dateTimeElements);
			
		return dateTimeElements;
	}


	private int[] convertToWorldTime(String time, boolean isAM, int[] dateTimeElements) {
		int hours = Integer.parseInt(time) / 100;
		int minutes = Integer.parseInt(time) % 100;
		
		if (isAM == true && isMidnight(time) == true) {
			
			// if time = 12:35am / 12.35am
			if (checkValidTimeRange(hours, minutes) == true) {
				dateTimeElements[1] = hours;
				dateTimeElements[2] = minutes;
			}
			else {
				System.out.println("covertToWorldTime: invalid time input.");
			}
		}
			
		else if (isAM == true && isMidnight(time) == false) {
			if (checkValidTimeRange(hours, minutes) == true) {
				dateTimeElements[1] = hours;
				dateTimeElements[2] = minutes;
			}
		}
		// if user input time in PM
		else if (isAM == false && checkValidTimeRange(hours, minutes) == true) {
			// do not convert 1235pm to world time.
			// 1235pm = 1235hrs
			if (hours == 12) {
				dateTimeElements[1] = hours;
				dateTimeElements[2] = minutes;
			}
			//convert the range of 1pm to 1159pm to the range of 1300 to 2359
			else if (hours < 12 && hours > 0) {
				hours = hours + 12;
				dateTimeElements[1] = hours;
				dateTimeElements[2] = minutes;
			}
		}
		return dateTimeElements;
	}
	
	/*
	 * @description: check if the time given is between 12am to 1259am
	 * Example: 2pm to 1400 or 2am to 0200
	 */

	private boolean isMidnight(String time) {
		
		int hours = Integer.parseInt(time) / 100;
		
		if (hours == 12) {
			return true;
		}
		else {
			return false;
		}
		
	}


	private int[] processHRS(String currentWord, int[] dateTimeElements) {
		int minutes = 0;
		int hours = 0;
		
		String[] _24hours = currentWord.split(":");
		// change 12:00 to 1200
		if (_24hours.length == 2) {
			
			// check that user does not input a wrong time format. Example 2561hrs
			hours = Integer.parseInt(_24hours[0]);
			minutes = Integer.parseInt(_24hours[1]);
			
			if (checkValidTimeRange(hours, minutes) == true) {
				dateTimeElements[1] = hours;
				dateTimeElements[2] = minutes;
			}
		}
		// remain unchange if time has no ":".
		else {
			
			minutes = Integer.parseInt(_24hours[0]) % 100;
			hours = Integer.parseInt(_24hours[0]) / 100;
			
			if (checkValidTimeRange(hours, minutes) == true) {
				dateTimeElements[1] = hours;
				dateTimeElements[2] = minutes;
			}
			
		}
		return dateTimeElements;
	}
	
	private boolean checkValidTimeRange(int hours, int minutes) {
		if (hours < 25 && minutes < 60) {
			return true;
		}
		else {
			return false;
		}
	}

	private int[] getDate(String currentWord, ArrayList<String> contentToAnalyse, int[] dateTimeElements, int i) {
		
		// check if input is in 27/05/2015, 27-05-2015, 27.05.2015 format
		if (isNumericalDateType(currentWord) == true) {
			dateTimeElements = anaylsePossibleDateElements(currentWord, dateTimeElements);
		}
		// check if input is in 27 may 2015 format
		else if (checkIfStringDate(currentWord, contentToAnalyse, dateTimeElements, i) == true) {
			int monthInInteger = convertStringToInt(contentToAnalyse.get(i+1));
			
			if (i+2 <= contentToAnalyse.size() && (Integer.parseInt(contentToAnalyse.get(i+2))) <= MAX_YEAR_PROPER 
				|| Integer.parseInt(contentToAnalyse.get(i+2)) <= MAX_YEAR_INPROPER)  { 
				
				String numericalDate = currentWord + "/" + monthInInteger + contentToAnalyse.get(i+2);
				dateTimeElements = anaylsePossibleDateElements(numericalDate, dateTimeElements);
			}
			else {
				String numericalDate = currentWord + "/" + monthInInteger;
				dateTimeElements = anaylsePossibleDateElements(numericalDate, dateTimeElements);
			}
		}
		else if (dayTypes.contains(currentWord)) {
			int daysToAdd = checkWeekday(currentWord);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, daysToAdd);
			dateTimeElements = getCalendar(cal, dateTimeElements);
		}
		
		return dateTimeElements;
	}
	
	private boolean checkIfStringDate(String currentWord, ArrayList<String> contentToAnalyse, int[] dateTimeElements, int i) {
		
		if (i + 2 < contentToAnalyse.size()) {
			// check if string contains feb or february
			if (monthTypes.contains(contentToAnalyse.get(i+1))) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}


	private int convertStringToInt(String string) {
		double ans = 0;
		for (int i = 0; i < monthTypes.size(); i++) {
			if (monthTypes.equals(string)) {
				ans = (double) i;
				break;
			}
		}
		
		return (int) ans;
		
	}


	//
	private int[] anaylsePossibleDateElements(String currentWord, int[] dateTimeElements) {
		String[] dateComponents = currentWord.split("[/.-]");
		
		// Example: 27.05 (27th May) or 16/12 (16 Dec)
		if (dateComponents.length == NUM_CONTENT_OF_DATE_INPROPER) {
			dateTimeElements = checkInproperDate(dateComponents, dateTimeElements);
		}
		
		// Example: 27/05/16 (27 May 2016)
		else if (dateComponents.length == NUM_CONTENT_OF_DATE_PROPER) {
			
			dateTimeElements = checkProperDate(dateComponents, dateTimeElements);
		}
		return dateTimeElements;
	}
	
	private int[] checkProperDate(String[] dateComponents, int[] dateTimeElements) {
		//check if input is really a date format and not a string format like H.I
		if (Integer.parseInt(dateComponents[1]) <= MAX_MONTH_PER_YEAR) {
			
			dateComponents = verifyIfLeapYear(dateComponents);
			
			if (Integer.parseInt(dateComponents[0]) <= daysPerMonth[(Integer.parseInt(dateComponents[1]))]){
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				Date date = null;
				
				String _date = dateComponents[0] + "/" + dateComponents[1] + "/" + dateComponents[2];
				
				try {
					date = format.parse(_date);
					cal.setTime(date);
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
				
				dateTimeElements[0] = cal.get(Calendar.DAY_OF_WEEK);
				dateTimeElements[3] = cal.get(Calendar.DAY_OF_MONTH);
				dateTimeElements[4] = cal.get(Calendar.MONTH) + 1;
				dateTimeElements[5] = cal.get(Calendar.YEAR);
				
				// set default deadline for time at 2359hrs
/*				dateTimeElements[1] = 23;
				dateTimeElements[2] = 59;
*/			}
			else {
				System.out.println("Wrong Date Input");
			}
		}
		else {
			System.out.println("Wrong Date Input");
		}
		return dateTimeElements;
							
	}


	private int[] checkInproperDate(String[] dateComponents, int[] dateTimeElements) {
		
		//check if input is really a date format and not a string format like H.I
		if (Integer.parseInt(dateComponents[1]) <= MAX_MONTH_PER_YEAR) {
			
			dateComponents = verifyIfLeapYear(dateComponents);
			
			if (Integer.parseInt(dateComponents[0]) <= daysPerMonth[(Integer.parseInt(dateComponents[1]))] ){
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				Date date = null;
				
				String _date = dateComponents[0] + "/" + dateComponents[1] + "/" + cal.get(Calendar.YEAR);
				
				try {
					date = format.parse(_date);
					cal.setTime(date);
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
				
				dateTimeElements[0] = cal.get(Calendar.DAY_OF_WEEK);
				dateTimeElements[3] = Integer.parseInt(dateComponents[0]);
				dateTimeElements[4] = Integer.parseInt(dateComponents[1]);
				dateTimeElements[5] = cal.get(Calendar.YEAR);
				
				// set default deadline for time at 2359hrs
				dateTimeElements[1] = 23;
				dateTimeElements[2] = 59;
			}
		}
		return dateTimeElements;
	}


	private String[] verifyIfLeapYear(String[] dateComponents) {
		int currentYear = 0;
		Calendar cal = Calendar.getInstance();
		
		if (dateComponents.length == 2) {
			currentYear = cal.get(Calendar.YEAR);
		}
		else {
			currentYear = Integer.parseInt(dateComponents[2]);
		}
		
		// if month of february
		if (dateComponents[1].equals("2")) {
			// not leap year but user entered 29/02
			if (currentYear % 4 != 0 && dateComponents[0].equals("29")) {
				dateComponents[0] = "28";
			}
		}
		return dateComponents;
	}



	private int[] getCalendar(Calendar cal, int[] dateTimeElements) {
		dateTimeElements[0] = cal.get(Calendar.DAY_OF_WEEK);
		dateTimeElements[3] = cal.get(Calendar.DAY_OF_MONTH);
		dateTimeElements[4] = cal.get(Calendar.MONTH) + 1;
		dateTimeElements[5] = cal.get(Calendar.YEAR);
		
		dateTimeElements[1] = cal.get(Calendar.HOUR_OF_DAY);
		dateTimeElements[2] = cal.get(Calendar.MINUTE);
		
		return dateTimeElements;
	}
	
	//********************************************** determine date type **********************************************//
	private boolean isNextWeekday(ArrayList<String> contentToAnalyse) {
		
		System.out.println("Debug: Test isNextWeekday @line 686." + contentToAnalyse.size());
		
		if (contentToAnalyse.contains("next") && contentToAnalyse.indexOf("next") + 1 < contentToAnalyse.size()) {
			
			if (dayTypes.contains((contentToAnalyse.indexOf("next") + 1))) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			System.out.println("Debug: Test isNextWeekday @line 698." + contentToAnalyse.size());
			return false;
		}
	}

/*	private boolean isNextWeek(String currentWord, String[] dateElements, int i) {
		
		String keywordWeeks = dateElements[i+1];
		
		if (currentWord.equals("next") && (keywordWeeks.equals("week") || keywordWeeks.equals("wk"))) {
			return true;
		}
		else {
			return false;
		}
		
	}
*/
	private boolean isNextFewDays(ArrayList<String> contentToAnalyse) {
		
		System.out.println("Debug: Test isNextFewDays @line 714.");
		
		if (contentToAnalyse.contains("next") && (contentToAnalyse.contains("day") || contentToAnalyse.contains("days"))) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean isTheDayAfterTomorrow(ArrayList<String> contentToAnalyse) {
	
		System.out.println("Debug: Test isTheDayAfterTomorrow @line 724.");
		
		if (contentToAnalyse.contains("after") && (contentToAnalyse.contains("tomorrow") || 
			contentToAnalyse.contains("tml") || contentToAnalyse.contains("tmr"))) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean isThisComingWeekday(ArrayList<String> contentToAnalyse) {
		
		System.out.println("Debug: Test isThisComingWeekday @line 735.");
		
		// case for "this coming thursday"
		if (contentToAnalyse.contains("this") && contentToAnalyse.contains("coming") 
			&& (contentToAnalyse.indexOf("coming") + 1) < contentToAnalyse.size()) {
			System.out.println("Debug: Test isThisComingWeekday @line 748.");
			if (dayTypes.contains((contentToAnalyse.get(contentToAnalyse.indexOf("coming") + 1)))) {
				System.out.println("Debug: Test isThisComingWeekday @line 750.");
				return true;
			}
			else {
				return false;
			}
		}
		// case for "this thursday"
		else if (contentToAnalyse.contains("this") && (contentToAnalyse.indexOf("this") + 1) <
				contentToAnalyse.size()) {
			if (dayTypes.contains((contentToAnalyse.get(contentToAnalyse.indexOf("this") + 1)))) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
		
	}

	private boolean isTomorrowType(ArrayList<String> contentToAnalyse) {
		
		System.out.println("Debug: Test isTomorrowType @line 762.");
		
		if (contentToAnalyse.size() == 1) {
			if (tomorrowTypes.contains(contentToAnalyse.get(0))) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	private boolean isTodayType(ArrayList<String> contentToAnalyse) {
		System.out.println("Debug: Test isTodayType @line 777.");
		if (contentToAnalyse.size() == 1) {
			if (contentToAnalyse.get(0).contains("today") || contentToAnalyse.get(0).contains("tdy")) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	private boolean isEnglishDateType(ArrayList<String> contentToAnalyse) {
		System.out.println("Debug: Test isEnglishDateType  @line 792." + contentToAnalyse.size());
		String month = "";
		String day = "";
		
		
		// Example: add buy grocery by 20 feb 2016 / 20 feb
		if (contentToAnalyse.size() == 3 || contentToAnalyse.size() == 2) {
			if (monthTypes.contains(contentToAnalyse.get(1))) {
				month = contentToAnalyse.get(1);
			}
		}
		// Example: add buy grocery by monday.
		else if (contentToAnalyse.size() == 1 && dayTypes.contains(contentToAnalyse.get(0))) {
			day = contentToAnalyse.get(0);
			System.out.println("Debug: Test isEnglishDateType  @line 824." + day);
		}
		
		// if the word next to the current contains month type like feb, march
		// Example: 20 feb 2017
		
		if (monthTypes.contains(month) || dayTypes.contains(day)) {
//			System.out.println("Debug: Test currentWord @isDateType line 772: " + contentToAnalyse.get(0));
			return true;
		}
		else {
//			System.out.println("Debug: Test currentWord @isDateType line 777: " + contentToAnalyse.get(0));
//			setTempTaskName(contentToAnalyse.get(i));
			return false;
		}
	}
	
	// maybe redundant.
	private boolean isNumericalDateType(String currentWord) {
		
		System.out.println("Debug: Test isNumericalDateType @line 845.");
		
		String[] dateElements = currentWord.split("[/.-]");
		
		if (dateElements.length == NUM_CONTENT_OF_DATE_INPROPER 
				|| dateElements.length == NUM_CONTENT_OF_DATE_PROPER) {
				System.out.println("Debug: Test currentWord @isNumericalDateType line 822: true");
				return true;
			}
		else {
			return false;
		}
	}
	
	
	private boolean isTimeType(String currentWord) {
		
		System.out.println("Debug: Test isTimeType @line 845.");
		
		if (currentWord.length() < 3) {
			return false;
		}
		
		String lastTwoChars = currentWord.substring(currentWord.length() - LENGTH_OF_AM_PM_STRING);
		String lastThreeChars = currentWord.substring(currentWord.length() - LENGTH_OF_HRS_STRING);
		
		System.out.println("Debug: Test isTimeType @line 858.");
		
		// check if string contains AM or PM and check if it is indeed a time.
		if (containsAMPM(lastTwoChars)) {
			return true;
		}
		// check if string contains HRs and check if it is indeed a time.
		else if (containsHRS(lastThreeChars)) {
			return true;
		}
		// need to check if string contains 0700
		else {
			return false;
		}
	}

	// redundant
	private boolean containsHRS(String lastThreeChar) {
		lastThreeChar = lastThreeChar.toLowerCase();
		
		if (lastThreeChar.contains(KEYWORD_HRS)) {
			return true;
		}
		else {
			return false;
	
		}
	}

	// redundant
	private boolean containsAMPM(String lastTwoChar) {
		lastTwoChar = lastTwoChar.toLowerCase();
		if (lastTwoChar.contains(KEYWORD_AM)) {
			return true;
		}
		else if (lastTwoChar.contains(KEYWORD_PM)) {
			return true;
		}
		else {
			return false;
		}
	}
}
