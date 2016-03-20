package Logic;

import java.util.*;

import Command.COMMAND_TYPE;
import Command.Command;
import Parser.Parser;
import Task.Category;
import Task.Task;

public class Tag extends Command {
	private TreeMap<String, Category> categories;
	
	public Tag(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type, TreeMap<String, Category> categories) {
		super(parser, data, command_type);
		this.categories = categories;
	}

	@Override
	public String execute() {
		int index = parser.getTaskID()-INDEX_ADJUSTMENT;
		String tag = parser.getName();
		switch (this.UIStatus)  {
		case FLOATING:
			return tag(tag, index, this.floatingTasks);
		case ONGOING:
			return tag(tag, index, this.ongoingTasks);
		case COMPLETED:
			return tag(tag, index, this.completedTasks);
		case OVERDUE:
			return tag(tag, index, this.overdueTasks);
		default:
			return "ERROR";
		}
	}

	private String tag(String tag, int index, ArrayList<Task> tasks) {
		Task task = tasks.get(index);
		task.setTag(tag);
		
		if (categories.containsKey(tag)) {
			Category category = categories.get(tag);
			category.addTask(task);
			return "Task " + (index+INDEX_ADJUSTMENT) + " is tagged under a new Tag "  + "\"" + tag + "\"";
		}
		else {
			TreeMap<String, Task> tasksInCategory = new TreeMap<String, Task>();
			Category category = new Category(tag, tasksInCategory);
			categories.put(tag, category);
			return "Task " + (index+INDEX_ADJUSTMENT) + " is tagged under the existing Tag "  + "\"" + tag + "\"";
		}
	}
	
	@Override
	public String undo() {
		// TODO Auto-generated method stub
		return null;
	}

	public TreeMap<String, Category> getCategories() {
		return this.categories;
	}
}
