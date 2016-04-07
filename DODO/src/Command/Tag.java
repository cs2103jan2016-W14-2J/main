package Command;

/* @@author: Lu Yang */

import java.util.*;

import Parser.*;
import Task.Category;
import Task.Task;

public class Tag extends Command {
	private static final String MESSAGE_SUCCESSFUL_TAG = 
			"Task(s) \"%1$s\" is/are successfully tagged by \"%2$s\". ";
	private static final String MESSAGE_UNSUCCESSFUL_TAG = 
			"Task(s) \"%1$s\" is/are not successfully tagged by \"%2$s\". ";
	
	public Tag(CommandUtils cu, ArrayList<Task> floatingTasks, ArrayList<Task> ongoingTasks,
			ArrayList<Task> completedTasks, ArrayList<Task> overdueTasks, ArrayList<Task> results, TreeMap<String, Category> categories) {
		super(cu, floatingTasks, ongoingTasks, completedTasks, overdueTasks, results, categories);
	}

	@Override
	public String execute() {
		int index = cu.getTaskID();
		ArrayList<String> tags = cu.getTag();
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		
		Task task;
		try {
			task = tasks.get(index-INDEX_ADJUSTMENT);
		} catch (IndexOutOfBoundsException e) {
			return MESSAGE_INDEXOUTOFBOUND;
		} catch (NullPointerException e) {
			return MESSAGE_EMPTY_LIST;
		}

		return tag(tags, task);
	}

	private String tag(ArrayList<String> tags, Task task) {
		ArrayList<String> unsuccessfulTags = new ArrayList<String>();
		ArrayList<String> successfulTags = new ArrayList<String>();
		
		for (String tag: tags) {
			if (this.tagTask(tag, task)) {
				successfulTags.add(tag);
			}
			else {
				// already tagged
				unsuccessfulTags.add(tag);
			}
		}
		/*if (categories.containsKey(tag)) {
				Category category = categories.get(tag);
				category.addTask(task);
				return "Task " + (index+INDEX_ADJUSTMENT) + " is tagged under a new Tag "  + "\"" + tag + "\"";
			}
			else {
				TreeMap<String, Task> tasksInCategory = new TreeMap<String, Task>();
				Category category = new Category(tag, tasksInCategory);
				categories.put(tag, category);
				return "Task " + (index+INDEX_ADJUSTMENT) + " is tagged under the existing Tag "  + "\"" + tag + "\"";
			}*/
		if (unsuccessfulTags.size()==0) {
			return String.format(MESSAGE_SUCCESSFUL_TAG, task, successfulTags);
		}
		if (successfulTags.size()==0) {
			return String.format(MESSAGE_UNSUCCESSFUL_TAG, task, unsuccessfulTags);
		}
		return String.format(MESSAGE_SUCCESSFUL_TAG, task, successfulTags) + 
				String.format(MESSAGE_UNSUCCESSFUL_TAG, task, unsuccessfulTags);
		
	}
}