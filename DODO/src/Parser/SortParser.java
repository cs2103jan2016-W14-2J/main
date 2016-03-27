package Parser;

import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import Command.*;

public class SortParser {
	
	private Date sortByDate;
	private String sortByAlpha;
	private SORT_TYPE sortType;
	private String ALPHABETICAL_ORDER = "abc";
	private String PREPOSITION_BY = "by";
	
	
	public SortParser(String userTask) {
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

	private void processByAlphabetical(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		setSortByAlphabetical(str[0].trim());
	}

	private void processByDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		setSortByDate(dates.get(0));
	}


	private boolean isSortByAlphabetical(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		return (str[0].trim().contains(ALPHABETICAL_ORDER)) ? true : false;
	}

	private boolean isSortByDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		return (dates.size() != 0) ? true : false;
	}

	private String removeBy(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		if (str[0].contains(PREPOSITION_BY)) {
			userTask = userTask.replace("by", "");
		}
		return userTask.trim();
	}
	
	//*********** Setter ************//
	private void setSortType(SORT_TYPE sortType) {
		this.sortType = sortType;
	}
	
	private void setSortByAlphabetical(String sortByAlpha) {
		this.sortByAlpha = sortByAlpha;
	}
	
	private void setSortByDate(Date sortByDate) {
		this.sortByDate = sortByDate;
	}
	
	//*********** Getter ************//
	protected String getSortByAlphabetical () {
		return sortByAlpha;
	}
	protected SORT_TYPE getSortType() {
		return this.sortType;
	}
	
	protected Date getSortByDate() {
		return this.sortByDate;
	}
}
