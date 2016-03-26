package Parser;

import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import Command.*;

public class SortParser {
	
	private String userTask;
	private Date sortByDate;
	private String sortByAlpha;
	private SORT_TYPE sortType;
	
	public SortParser(String userTask) {
		this.userTask = userTask;
		executeSortParser(userTask);
	}

	private void executeSortParser(String userTask) {
		userTask = removeBy(userTask);
		
		switch(determineSearchType(userTask)) {
			
		case BY_DATE:
			processByDate(userTask);
			break;
		case BY_ALPHABETICAL:
			processByAlphabetical(userTask);
			break;
		case INVALID:
			break;
		}
	}
	
	private void setSortType(SORT_TYPE sortType) {
		this.sortType = sortType;
	}
	
	protected SORT_TYPE getSortType() {
		return this.sortType;
	}

	private void processByAlphabetical(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		setSortByAlphabetical(str[0].trim());
	}

	private void setSortByAlphabetical(String sortByAlpha) {
		this.sortByAlpha = sortByAlpha;
	}
	
	protected String getSortByAlphabetical () {
		return sortByAlpha;
	}

	private void processByDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		setSortByDate(dates.get(0));
	}

	private void setSortByDate(Date sortByDate) {
		this.sortByDate = sortByDate;
	}
	
	protected Date getSortByDate() {
		return this.sortByDate;
	}

	private SORT_TYPE determineSearchType(String userTask) {
		if (isSortByDate(userTask)) {
			setSortType(SORT_TYPE.BY_DATE);
			return SORT_TYPE.BY_DATE;
		}
		else if (isSortByAlphabetical(userTask)) {
			setSortType(SORT_TYPE.BY_ALPHABETICAL);
			return SORT_TYPE.BY_ALPHABETICAL;
		}
		else {
			setSortType(SORT_TYPE.INVALID);
			return SORT_TYPE.INVALID;
		}
	}

	private boolean isSortByAlphabetical(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		return (str[0].trim().contains("abc")) ? true : false;
	}

	private boolean isSortByDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		return (dates.size() != 0) ? true : false;
	}

	private String removeBy(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		if (str[0].contains("by")) {
			userTask.replace(" by ", "");
		}
		return userTask;
	}
}
