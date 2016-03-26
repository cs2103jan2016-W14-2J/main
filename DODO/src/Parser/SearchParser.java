package Parser;

import java.util.Date;
import java.util.List;

import Command.*;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class SearchParser {
	private String userTask;
	
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
		
		if (isSearchTaskName(userTask)) {
			return SEARCH_TYPE.BY_TASK;
		}
		ele if (isSearchDate())
	}

	private boolean isSearchTaskName(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		return (dates.size() == 0) ? true : false;
	}

	private String removeBy(String userTask) {
		if (userTask.contains(" by ")) {
			userTask.replace(" by ", "");
		}
		return userTask;
	}
	
}
