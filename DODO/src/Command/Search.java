package Command;

/* @@author: Lu Yang */

import java.util.*;

import GUI.UI_TAB;
import Parser.*;
import Task.*;

public class Search extends Command {
	private static final String MESSAGE_INVALID_SEARCH = "Please enter a valid search command.";
	private static final String MESSAGE_SUCCESSFUL_SEARCH_KEYWORD = "Search keyword \"%1$s\" is successful.";
	private static final String MESSAGE_SUCCESSFUL_SEARCH_TAG = "Search tag \"%1$s\" is successful.";
	private static final String MESSAGE_UNSUCCESSFUL_SEARCH_KEYWORD = "There is no task named \"%1$s\".";
	
	
	
	public Search(CommandUtils cu, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		super(cu, floatingTasks, ongoingTasks, completedTasks, overdueTasks, results, categories);
	}
	@Override
	public String execute() {
		SEARCH_TYPE type = cu.getSearchType();
		switch (type) {
		case BY_TASK:
			String keyword = cu.getSearchByTask();
			return searchByKeyword(keyword);
			/*Date searchDate = cu.getSearchByDate();
			System.out.println("============SEARCH=========== dt: " + searchDate);
			return "[SEARCH] UNDER DEVELOPMENT";*/
		case BY_TAG:
			String searchTag = cu.getSearchByTag();
			return searchByTag(searchTag);
		case BY_DATE:
			/*Date searchDate = cu.getSearchByDate();
			System.out.println("============SEARCH=========== dt: " + searchDate);
			return "[SEARCH] UNDER DEVELOPMENT";*/
		case INVALID:
		default:
			return MESSAGE_INVALID_SEARCH;
		}
	}
	
	/**********************************INTERNAL METHODS***************************************/
	
	/*private String searchByDate(DateTime searchDate) {
		ArrayList<Task> floatingResults = searchTasksByDate(floatingTasks, searchDate);
		return null;
	}*/
	
	/*private ArrayList<Task> searchTasksByDate(ArrayList<Task> tasks, DateTime searchDate) {
		System.out.println(searchDate);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		ArrayList<Task> results = new ArrayList<Task>();
		for (int i=0; i<tasks.size(); i++) {
			Task task = tasks.get(i);
			if (task.getStatus()==TASK_TYPE.DEADLINED) {
				DateTime endDate = new DateTime(((DeadlinedTask) task).getEndDateTime());
			}
			else if (task instanceof Event) {
				
			}
		}
		return null;
	}*/
	
	private String searchByTag(String searchTag) {
		Category category = this.findCategory(searchTag);
		if (category==null) {
			return String.format(MESSAGE_UNSUCCESSFUL_SEARCH_TAG, searchTag);
		}
		this.results.addAll(category.getTasks());
		this.UIStatus = UI_TAB.SEARCH;
		return String.format(MESSAGE_SUCCESSFUL_SEARCH_TAG, searchTag);
	}

	private String searchByKeyword(String keyword) {
		if (keyword.length()<2) {
			return MESSAGE_INVALID_SEARCH;
		}
		
		ArrayList<Task> floatingResults = searchTasksByKeyword(floatingTasks, keyword);
		ArrayList<Task> ongoingResults = searchTasksByKeyword(ongoingTasks, keyword);
		ArrayList<Task> completedResults = searchTasksByKeyword(completedTasks, keyword);
		ArrayList<Task> overdueResults = searchTasksByKeyword(overdueTasks, keyword);
		this.results.addAll(floatingResults);
		this.results.addAll(ongoingResults);
		this.results.addAll(completedResults);
		this.results.addAll(overdueResults);
		this.UIStatus = UI_TAB.SEARCH;
		
		if (this.results.size()==0) {
			return String.format(MESSAGE_UNSUCCESSFUL_SEARCH_KEYWORD, keyword);
		}
		return String.format(MESSAGE_SUCCESSFUL_SEARCH_KEYWORD, keyword);
	}

	private ArrayList<Task> searchTasksByKeyword(ArrayList<Task> tasks, String searchStr) {
		searchStr = searchStr.toLowerCase();
		String[] fragments = searchStr.split(" ");
		
		ArrayList<Task> results = new ArrayList<Task>();
		for (Task task: tasks) {
			String name = task.getName().toLowerCase();
			
			boolean search = true;
			for (String fragment: fragments) {
				if (!name.contains(fragment)) search = false;
			}
			if (search==true) results.add(task);
		}
		return results;
	}
}
