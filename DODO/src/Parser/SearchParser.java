package Parser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Command.*;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class SearchParser {
	private String userTask;
	private SEARCH_TYPE searchType;
	
	public SearchParser(String userTask) {
		this.userTask = userTask;
		executeSearchParser(userTask);
	}

	private void executeSearchParser(String userTask) {
		userTask = removeBy(userTask);
		
		switch(determineSearchType(userTask)) {
		
		}
			
	}

	private SEARCH_TYPE determineSearchType(String userTask) {
		
		if (isSearchDate(userTask)) {
			setSearchType(SEARCH_TYPE.BY_DATE);
			return SEARCH_TYPE.BY_DATE;
		}
		else if (isSearchTaskName(userTask)) {
			setSearchType(SEARCH_TYPE.BY_TASK);
			return SEARCH_TYPE.BY_TASK;
		}
		else if (isSearchTag(userTask)) {
			setSearchType(SEARCH_TYPE.BY_TAG);
			return SEARCH_TYPE.BY_TAG;
		}
		else {
			return SEARCH_TYPE.INVALID;
		}
		
	}

	private boolean isSearchTag(String userTask) {
		return (userTask.startsWith("<") && userTask.endsWith(">")) ? true : false;
	}

	private boolean isSearchDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		return (dates.size() != 0) ? true : false;
	}

	private boolean isSearchTaskName(String userTask) {
		return (!userTask.startsWith("<") && !userTask.endsWith(">")) ? true : false;
	}

	private String removeBy(String userTask) {
		if (userTask.contains(" by ")) {
			userTask.replace(" by ", "");
		}
		return userTask;
	}
	
	private void setSearchType(SEARCH_TYPE searchType) {
		this.searchType = searchType;
	}
	
}
