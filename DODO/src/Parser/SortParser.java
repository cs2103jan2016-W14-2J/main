package Parser;

import Command.*;

public class SortParser {

	private SORT_TYPE sortType;
	private String ALPHABETICAL_ORDER = "abc";
	private String REVERSE_ALPHABETICAL_ORDER = "cba";
	private String DATE_ORDER = "date";
	private String PREPOSITION_BY = "by";
	
	
	public SortParser(String userTask) {
		userTask = removeBy(userTask);
		determineSortType(userTask);
	}
	
	private SORT_TYPE determineSortType(String userTask) {
		if (isSortByDate(userTask)) {
			setSortType(SORT_TYPE.BY_DATE);
			return SORT_TYPE.BY_DATE;
		}
		else if (isSortByAlphabetical(userTask)) {
			setSortType(SORT_TYPE.BY_ALPHABETICAL);
			return SORT_TYPE.BY_ALPHABETICAL;
		}
		else if (isSortByReverseAlphabetical(userTask)) {
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
		return (str[0].trim().contains(ALPHABETICAL_ORDER)) ? true : false;
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
	
	//*********** Setter ************//
	private void setSortType(SORT_TYPE sortType) {
		this.sortType = sortType;
	}
	
/*	private void setSortByAlphabetical(String sortByAlpha) {
		this.sortByAlpha = sortByAlpha;
	}
	
	private void setSortByDate(Date sortByDate) {
		this.sortByDate = sortByDate;
	}
	
*/	//*********** Getter ************//
	protected SORT_TYPE getSortType() {
		return this.sortType;
	}
/*	protected String getSortByAlphabetical () {
		return sortByAlpha;
	}
	protected SORT_TYPE getSortType() {
		return this.sortType;
	}
	
	protected Date getSortByDate() {
		return this.sortByDate;
	}*/ 
}
