package Command;

import java.text.SimpleDateFormat;
import java.util.*;
import org.joda.time.*;

import Parser.Parser;
import Task.*;

public class Search extends Command {
	private ArrayList<Task> results;
	private TreeMap<String, Category> categories;

	public Search(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type, TreeMap<String, Category> categories) {
		super(parser, data, command_type);
		this.results = new ArrayList<Task>();
		this.categories = categories;
	}

	@Override
	public String execute() {
		SEARCH_TYPE type = parser.getSearchType();
		System.out.println("============SEARCH=========== type: " + type);
		switch (type) {
		case BY_TASK:
			String searchStr = parser.getSearchByTask();
			System.out.println("============SEARCH=========== searchStr: " + searchStr);
			return searchByKeyword(searchStr);
		case BY_DATE:
			Date searchDate = parser.getSearchByDate();
			System.out.println("============SEARCH=========== dt: " + searchDate);
			return "[SEARCH] UNDER DEVELOPMENT";
		case BY_TAG:
			String searchTag = parser.getSearchByTag();
			return searchByTag(searchTag);
		case INVALID:
			return "Invalid search value.";
		default:
			return "[SEARCH] INTERNAL ERROR.";
		}
	}
	
	

	public ArrayList<Task> getSearchResults() {
		return this.results;
	}
	
	/**********************************INTERNAL METHODS***************************************/
	
	private String searchByDate(DateTime searchDate) {
		ArrayList<Task> floatingResults = searchTasksByDate(floatingTasks, searchDate);
		return null;
	}
	
	private ArrayList<Task> searchTasksByDate(ArrayList<Task> tasks, DateTime searchDate) {
		System.out.println(searchDate);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		ArrayList<Task> results = new ArrayList<Task>();
		for (int i=0; i<tasks.size(); i++) {
			Task task = tasks.get(i);
			if (task instanceof DeadlinedTask) {
				DateTime endDate = new DateTime(((DeadlinedTask) task).getEndDateTime());
			}
			else if (task instanceof Event) {
				
			}
		}
		return null;
	}
	
	private String searchByTag(String searchTag) {
		Category category = categories.get(searchTag);
		if (category==null) return "There is no tag called \"" + searchTag + "\"";
		TreeMap<String, Task> tasksTreeMap = category.getTasks();
		this.results = new ArrayList<Task>(tasksTreeMap.values());
		return "Search for tag \"" + searchTag + "\" completed.";
	}

	private String searchByKeyword(String searchStr) {
		ArrayList<Task> floatingResults = searchTasksByKeyword(floatingTasks, searchStr);
		ArrayList<Task> ongoingResults = searchTasksByKeyword(ongoingTasks, searchStr);
		ArrayList<Task> completedResults = searchTasksByKeyword(completedTasks, searchStr);
		ArrayList<Task> overdueResults = searchTasksByKeyword(overdueTasks, searchStr);
		this.results.addAll(floatingResults);
		this.results.addAll(ongoingResults);
		this.results.addAll(completedResults);
		this.results.addAll(overdueResults);
		System.out.println(results); // FOR DEBUG
		return "Search for tasks named \"" + searchStr + "\" completed.";
	}

	private ArrayList<Task> searchTasksByKeyword(ArrayList<Task> tasks, String searchStr) {
		ArrayList<Task> results = new ArrayList<Task>();
		for (int i=0; i<tasks.size(); i++) {
			Task task = tasks.get(i);
			String name = task.getName();
			if (name.contains(searchStr)){
				results.add(task);
			}
		}
		return results;
	}

	@Override
	public String undo() {
		// TODO Auto-generated method stub
		return null;
	}

}
