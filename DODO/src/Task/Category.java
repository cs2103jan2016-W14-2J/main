package Task;

/* @@author: A0130684H */

import java.util.*;

public class Category {
	private String name;
	private ArrayList<Task> tasks;
	
	public Category() {
		this("");
	}
	
	public Category(String name) {
		this.name = name;
		this.tasks = new ArrayList<Task>();
	}
	
	public Category(Category category) {
		this.name = category.getName();
		this.tasks = category.getTasks();
	}
	
	/****************************MUTATORS******************************/
	public boolean addTask(Task task) {
		if (!tasks.contains(task)) {
			tasks.add(task);
			boolean flag = task.addCategory(this.name);
			System.out.println("[DUBUG] task flag: " + flag);
			return flag;
		}
		return false;
	}
	
	public boolean deleteTask(Task task) {
		if (tasks.contains(task)) {
			tasks.remove(task);
			return true;
		}
		return false;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/****************************ACCESSORS*****************************/
	public String getName() {
		return this.name;
	}
	
	public ArrayList<Task> getTasks() {
		return this.tasks;
	}
	
	@Override
	public String toString() {
		return "#" + name;
	}
}
