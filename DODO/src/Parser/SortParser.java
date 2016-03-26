package Parser;

import Command.*;

public class SortParser {
	
	private String userTask;
	
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
	
	private String removeBy(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		if (str[0].contains("by")) {
			userTask.replace(" by ", "");
		}
		return userTask;
	}
}
