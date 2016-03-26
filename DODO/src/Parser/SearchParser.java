package Parser;

import java.util.Date;
import java.util.List;

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

	private SE determineSearchType(String userTask) {
		
		if (isSearchTaskName(userTask)) {
			
		}
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
