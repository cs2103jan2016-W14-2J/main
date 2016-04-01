package Command;

/* @@author: Lu Yang */

import java.util.*;

import GUI.UI_TAB;
import Parser.*;
import Task.*;

public class Delete extends Command{
	private TreeMap<String, Category> categories;

	public Delete(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type, TreeMap<String, Category> categories) {
		super(parser, data, command_type);
		this.categories = categories;
	}

	public String execute() {
		DELETE_TYPE type = parser.getDeleteType();
		ArrayList<String> tags = parser.getTagToDelete();
		ArrayList<Integer> indexes = parser.getIndexToDelete();

		// SINGLE_INDEX, SINGLE_TAG, MULTIPLE_INDEXES, MULTIPLE_TAGS, RANGE_INDEXES, ALL_INDEXES, ALL_TAGS;
		switch (type) {
		case SINGLE_INDEX:
		case RANGE_INDEXES:
		case MULTIPLE_INDEXES:
			return deleteTask(indexes);
		case ALL_INDEXES:
			return deleteAllTasks();
		case SINGLE_TAG:
		case MULTIPLE_TAGS:
			return deleteTags(tags);
		case ALL_TAGS:
			return deleteAllTags();
		case START_DATE:
			return convertToDeadlined(indexes);
		case END_DATE:
			return convertToFloating(indexes);
		default:
			return "INVALID";
		}
	}

	private String deleteAllTags() {
		this.categories = new TreeMap<String, Category>();
		return "All tags deleted";
	}
	
	private String convertToDeadlined(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = getTasks(this.UIStatus); 
		for (Integer index: indexes) {
			Task task = tasks.get(index- INDEX_ADJUSTMENT);
			task.setStart(null);
		}
		return "Start Time of tasks at " + indexes + " has been removed.";
	}
	
	private String convertToFloating(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = getTasks(this.UIStatus); 
		for (Integer index: indexes) {
			Task task = tasks.get(index - INDEX_ADJUSTMENT);
			task.setEnd(null);
			this.floatingTasks.add(task);
			tasks.remove(index- INDEX_ADJUSTMENT);
		}
		this.setTasks(this.UIStatus, tasks);
		this.UIStatus = UI_TAB.FLOATING;
		return "Deadline of tasks at " + indexes + " has been removed.";
	}

	private String deleteTags(ArrayList<String> tags) {
		String status = "";
		for (int i=0; i<tags.size(); i++) {
			String element = tags.get(0);
			if (!categories.containsKey(element)) {
				status += "Tag " + element + " is not found.\n";
			}
			categories.remove(element);
			status += "Tag " + element + " is deleted.\n";
		}
		return status;
	}

	public String undo() {
		return "Undone";
	}

	private String deleteAllTasks() {
		setTasks(this.UIStatus, new ArrayList<Task>());
		return "All tasks in " + this.UIStatus + " are deleted.";
	}

	private String deleteTask(ArrayList<Integer> indexes) {
		ArrayList<Task> tasks = getTasks(this.UIStatus);
		if (tasks.size()==0) {
			return this.UIStatus + " is empty. There is nothing to delete.";
		}
		String status = "Task ";
		try {
			for (int i=0; i<indexes.size(); i++) {
				tasks.remove(indexes.get(i)-1-i);
				status += indexes.get(i) + ", ";
			}
			status = status.substring(0, status.length()-2);
			status += " deleted.";
			setTasks(this.UIStatus, tasks);
		} catch (IndexOutOfBoundsException e) {
			status = "Your index is out of bound.";
		}
		return status;
	}

	public TreeMap<String, Category> getCategories() {
		return this.categories;
	}

}
