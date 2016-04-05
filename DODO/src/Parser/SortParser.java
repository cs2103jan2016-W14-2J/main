//@@author: Hao Jie
package Parser;

import Command.*;

public class SortParser {

	private String ALPHABETICAL_ORDER = "abc";
	private String NUMERICAL_ORDER = "123";
	private String REVERSE_ALPHABETICAL_ORDER = "cba";
	private String REVERSE_NUMERICAL_ORDER = "321";
	private String DATE_ORDER = "date";
	private String PREPOSITION_BY = "by";
	
	
	public SortParser() {
	
	}
	
	protected CommandUtils determineSortType(CommandUtils commandUtil, String userTask) {
		userTask = removeBy(userTask.toLowerCase());
		
		if (isSortByDate(userTask)) {
			commandUtil.setSortType(SORT_TYPE.BY_DATE);
		}
		else if (isSortByAlphabetical(userTask)) {
			commandUtil.setSortType(SORT_TYPE.BY_ASCENDING);
		}
		else if (isSortByReverseAlphabetical(userTask)) {
			commandUtil.setSortType(SORT_TYPE.BY_DESCENDING);
		}
		else {
			commandUtil.setSortType(SORT_TYPE.INVALID);
		}
		return commandUtil;
	}

	private boolean isSortByReverseAlphabetical(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		return (str[0].trim().contains(REVERSE_ALPHABETICAL_ORDER) ||
				str[0].trim().contains(REVERSE_NUMERICAL_ORDER)) ? true : false;
	}

	private boolean isSortByAlphabetical(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		return (str[0].trim().contains(ALPHABETICAL_ORDER) ||
				str[0].trim().contains(NUMERICAL_ORDER)) ? true : false;
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
}
