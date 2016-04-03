package Command;

/* @@author: Lu Yang */

import java.util.*;

import Parser.Parser;
import Task.Category;
import Task.Task;

public class Tag extends Command {
	private static final String MESSAGE_SUCCESSFUL_TAG = 
			"Task(s) \"%1$s\" is/are successfully tagged by \"%2$s\". ";
	private static final String MESSAGE_UNSUCCESSFUL_TAG = 
			"Task(s) \"%1$s\" is/are not successfully tagged by \"%2$s\". ";
	
	public Tag(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		int index = parser.getTaskID();
		ArrayList<String> tags = parser.getTag();
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		
		Task task;
		try {
			task = tasks.get(index-INDEX_ADJUSTMENT);
		} catch (IndexOutOfBoundsException e) {
			return MESSAGE_INDEXOUTOFBOUND;
		}

		return tag(tags, task);
	}

	private String tag(ArrayList<String> tags, Task task) {
		ArrayList<String> unsuccessfulTags = new ArrayList<String>();
		ArrayList<String> successfulTags = new ArrayList<String>();
		
		for (String tag: tags) {
			Category category = this.findCategory(tag);
			if (category==null) {
				this.addCategory(tag, task);
			}
			// task interchangably add category in class Category
			if (category.addTask(task)) {
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