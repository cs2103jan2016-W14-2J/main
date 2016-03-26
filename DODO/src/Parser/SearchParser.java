package Parser;

import java.util.Date;
import java.util.List;

import Command.*;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class SearchParser {
	private String userTask;
	private String tag;
	private SEARCH_TYPE searchType;
	private Date searchByDate;
	
	public SearchParser(String userTask) {
		this.userTask = userTask;
		executeSearchParser(userTask);
	}

	private void executeSearchParser(String userTask) {
		userTask = removeBy(userTask);
		
		switch(determineSearchType(userTask)) {
			
		case BY_DATE:
			processByDate(userTask);
			break;
		case BY_TASK:
			break;
		case BY_TAG:
			processByTag(userTask);
			break;
		case INVALID:
			setSearchType(SEARCH_TYPE.INVALID);
			break;
		}
			
	}

	private void processByTag(String userTask) {
		int firstIndex = userTask.trim().lastIndexOf(">");
		tag = userTask.trim().substring(1, firstIndex);
		setSearchByTag(tag);
	}

	private void setSearchByTag(String tag) {
		this.tag = tag;
	}
	
	protected String getSearchByTag() {
		return this.tag;
	}

	protected String getSearchByTask() {
		return this.userTask;
	}

	private void processByDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		setSearchByDate(dates.get(0));
	}

	private void setSearchByDate(Date searchByDate) {
		this.searchByDate = searchByDate;
		
	}
	
	protected Date getSearchByDate() {
		return this.searchByDate;
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
			setSearchType(SEARCH_TYPE.INVALID);
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
		String[] str = userTask.toLowerCase().split("\\s+");
		if (str[0].contains("by")) {
			userTask.replace(" by ", "");
		}
		return userTask;
	}
	
	private void setSearchType(SEARCH_TYPE searchType) {
		this.searchType = searchType;
	}
	
	protected SEARCH_TYPE getSearchType() {
		return this.searchType;
	}
	
}
