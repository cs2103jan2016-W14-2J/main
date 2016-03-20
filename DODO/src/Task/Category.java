package Task;

import java.util.*;

public class Category {
	String name;
	TreeMap<String, Task> tasks;
	
	public Category(String name, TreeMap<String, Task> tasks) {
		this.name = name;
		this.tasks = tasks;
	}
	
	/****************************MUTATORS******************************/
	public void addTask(Task task) {
		tasks.put(task.getName(), task);
	}
	
	/****************************ACCESSORS*****************************/
	public String getName() {
		return this.name;
	}
	
	public TreeMap<String, Task> getTasks() {
		return this.tasks;
	}
}
