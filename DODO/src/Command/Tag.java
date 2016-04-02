package Command;

/* @@author: Lu Yang */

import java.util.*;

import Parser.Parser;
import Task.Category;
import Task.Task;

public class Tag extends Command {

	public Tag(Parser parser, ArrayList<ArrayList<Task>> data, ArrayList<String> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		int index = parser.getTaskID()-INDEX_ADJUSTMENT;
		ArrayList<String> tags = parser.getTag();
		ArrayList<Task> tasks = getTasks(this.UIStatus);
		Task task;
		try {
			task = tasks.get(index);
		} catch (IndexOutOfBoundsException e) {
			return "Your index is out of bound.";
		}

		return tag(tags, task);
	}

	private String tag(ArrayList<String> tags, Task task) {
		for (String tag: tags) {
			if (indexOf(tag)==-1) {
				this.categories.add(tag);
			}
			task.addTag(tag);
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
		return "Your task \"" + task + "\" is successfully tagged by " + tags + ".";
	}
}