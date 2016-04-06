//@@author: Hao Jie
package Parser;

import java.util.Date;
import java.util.List;

import Command.*;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class SearchParser {
	private String tag;
	private SEARCH_TYPE searchType;
	private String PREPOSITION_BY = "by";
	private String PREPOSITION_BY_WITH_SPACE = "by";
	private String STRING_HASH_TAG = "#";

	public SearchParser() {
	}

	protected CommandUtils executeSearchParser(CommandUtils commandUtil, String userTask) {
		userTask = removeBy(userTask);
		commandUtil = determineSearchType(commandUtil, userTask);
		searchType = commandUtil.getSearchType();
		
		switch(searchType) {
			
		case BY_DATE:
			return processByDate(commandUtil, userTask);
		case BY_TASK:
			return processByTask(commandUtil, userTask);
		case BY_TAG:
			return processByTag(commandUtil, userTask);
		default:
			commandUtil.setSearchType(SEARCH_TYPE.INVALID);
			break;
		}
		return commandUtil;
	}

	private CommandUtils processByTask(CommandUtils commandUtil, String userTask) {
		commandUtil.setSearchByTask(userTask.trim());
		return commandUtil;
	}

	private CommandUtils processByTag(CommandUtils commandUtil, String userTask) {
		tag = userTask.trim().substring(1, userTask.length());
		commandUtil.setSearchByTag(tag);
		return commandUtil;
	}

	private CommandUtils processByDate(CommandUtils commandUtil, String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		commandUtil.setSearchByDate(dates.get(0));
		return commandUtil;
	}

	private CommandUtils determineSearchType(CommandUtils commandUtil, String userTask) {
		
		if (isSearchDate(userTask)) {
			commandUtil.setSearchType(SEARCH_TYPE.BY_DATE);
		}
		else if (isSearchTaskName(userTask)) {
			commandUtil.setSearchType(SEARCH_TYPE.BY_TASK);
		}
		else if (isSearchTag(userTask)) {
			commandUtil.setSearchType(SEARCH_TYPE.BY_TAG);
		}
		else {
			commandUtil.setSearchType(SEARCH_TYPE.INVALID);
		}
		return commandUtil;
	}

	private boolean isSearchTag(String userTask) {
		assert userTask.split("[\\s+]").length == 1;
		return (userTask.startsWith(STRING_HASH_TAG)) ? true : false;
	}

	private boolean isSearchDate(String userTask) {
		List<Date> dates = new PrettyTimeParser().parse(userTask);
		return (dates.size() != 0) ? true : false;
	}

	private boolean isSearchTaskName(String userTask) {
		return (!userTask.startsWith(STRING_HASH_TAG)) ? true : false;
	}

	private String removeBy(String userTask) {
		String[] str = userTask.toLowerCase().split("\\s+");
		if (str[0].contains(PREPOSITION_BY)) {
			userTask.replace(PREPOSITION_BY_WITH_SPACE, "");
		}
		return userTask;
	}
	

	
}
