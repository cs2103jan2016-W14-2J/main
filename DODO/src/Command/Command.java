package Command;

/* @@author: Lu Yang */

import java.util.ArrayList;

import GUI.*;
import Parser.*;
import Task.*;


public abstract class Command {
	protected static final int INDEX_ADJUSTMENT = 1;

	protected Parser parser;
	protected ArrayList<Task> ongoingTasks;
	protected ArrayList<Task> completedTasks;
	protected ArrayList<Task> overdueTasks;
	protected ArrayList<Task> floatingTasks;
	protected ArrayList<Task> results;
	protected ArrayList<String> categories;
	protected UI_TAB UIStatus;

	public Command(Parser parser, ArrayList<ArrayList<Task>> data, ArrayList<String> categories) {
		this.UIStatus = UIRightBox.getCurrentTab();
		this.parser = parser;
		this.floatingTasks = data.get(0);
		this.ongoingTasks = data.get(1);
		this.completedTasks = data.get(2);
		this.overdueTasks = data.get(3);
		this.results = data.get(4);
		this.categories = categories;
	}

	public abstract String execute();

	public ArrayList<ArrayList<Task>> getData() {
		return this.compress();
	}

	public UI_TAB getStatus() {
		return this.UIStatus;
	}

	public ArrayList<String> getCategories() {
		return this.categories;
	}

	/*********************************INTERNAL METHODS***************************************/
	protected ArrayList<ArrayList<Task>> compress() {
		ArrayList<ArrayList<Task>> tasks = new ArrayList<ArrayList<Task>>();
		tasks.add(floatingTasks);
		tasks.add(ongoingTasks);
		tasks.add(completedTasks);
		tasks.add(overdueTasks);
		tasks.add(results);
		return tasks;
	}

	protected ArrayList<Task> combine(ArrayList<ArrayList<Task>> all) {
		ArrayList<Task> temp = new ArrayList<Task>();
		for (ArrayList<Task> element: all) {
			temp.addAll(element);
		}
		return temp;
	}

	protected ArrayList<Task> getTasks(UI_TAB status) {
		switch (status) {
		case ONGOING:
			return this.ongoingTasks;
		case FLOATING:
			return this.floatingTasks;
		case COMPLETED:
			return this.completedTasks;
		case OVERDUE:
			return this.overdueTasks;
		case SEARCH:
			return (ArrayList<Task>) this.results.clone();
		case ALL:
			return combine(compress());
		default:
			return null;
		}
	}

	protected ArrayList<Task> getTasks(TASK_STATUS status) {
		switch (status) {
		case ONGOING:
			return this.ongoingTasks;
		case FLOATING:
			return this.floatingTasks;
		case COMPLETED:
			return this.completedTasks;
		case OVERDUE:
			return this.overdueTasks;
		default:
			return null;
		}
	}


	protected void setTasks(UI_TAB status, ArrayList<Task> list) {
		System.out.println("======COMMAND===== setTask/status: " + status);
		switch(status) {
		case ONGOING:
			this.ongoingTasks = list;
			break;
		case FLOATING:
			this.floatingTasks = list;
			break;
		case COMPLETED:
			this.completedTasks = list;
			break;
		case OVERDUE:
			this.overdueTasks = list;
			break;
		case SEARCH:
			update(list);
			this.results = list;
			break;
		case ALL:
			split(list);
		}
	}

	private void update(ArrayList<Task> list) {
		for (Task task: this.results) {
			if (!list.contains(task)) {
				// task has been deleted
				TASK_STATUS status = task.getStatus();
				switch (status) {
				case ONGOING:
					this.ongoingTasks.remove(task);
					break;
				case FLOATING:
					this.floatingTasks.remove(task);
					break;
				case COMPLETED:
					this.completedTasks.remove(task);
					break;
				case OVERDUE:
					this.overdueTasks.remove(task);
					break;
				}
			}
		}
	}

	private void split(ArrayList<Task> all) {
		this.floatingTasks.clear();
		this.ongoingTasks.clear();
		this.overdueTasks.clear();
		this.completedTasks.clear();
		for (Task task: all) {
			if (task.getStatus()==TASK_STATUS.FLOATING) {
				this.floatingTasks.add(task);
			}
			else if (task.getStatus()==TASK_STATUS.ONGOING) {
				this.ongoingTasks.add(task);
			}
			else if (task.getStatus()==TASK_STATUS.COMPLETED) {
				this.completedTasks.add(task);
			}
			else if (task.getStatus()==TASK_STATUS.OVERDUE) {
				this.overdueTasks.add(task);
			}
		}
	}

	protected void updateCategories(ArrayList<String> tags) {
		if (tags==null) return;
		for (String tag: tags) {
			if (indexOf(tag)==-1) {
				this.categories.add(tag);
			}
		}
	}

	protected int indexOf(String key) {
		int i=0;
		for (String tag: categories) {
			if (key.equalsIgnoreCase(tag)) {
				return i;
			}
			i++;
		}
		return -1;
	}

}