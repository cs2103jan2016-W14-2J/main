package Command;

/* @@author: Lu Yang */

import java.util.*;
import Task.*;

public class History {
	private static Stack<ArrayList<ArrayList<Task>>> undo_data;
	private static Stack<ArrayList<ArrayList<Task>>> redo_data;
	private static Stack<TreeMap<String, Category>> undo_categories;
	private static Stack<TreeMap<String, Category>> redo_categories;
	
	public History() {
		undo_data = new Stack<ArrayList<ArrayList<Task>>>();
		redo_data = new Stack<ArrayList<ArrayList<Task>>>();
		undo_categories = new Stack<TreeMap<String, Category>>();
		redo_categories = new Stack<TreeMap<String, Category>>();
	}

	public String save(ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		undo_data.push(data);
		undo_categories.push(categories);
		redo_data = new Stack<ArrayList<ArrayList<Task>>>();
		redo_categories = new Stack<TreeMap<String, Category>>();
		return "[INTERNAL MESSAGE/HISTORY]: data saved.";
	}
	
	public ArrayList<ArrayList<Task>> undoData(ArrayList<ArrayList<Task>> data) {
		redo_data.push(data);
		return undo_data.pop();
	}
	
	public TreeMap<String, Category> undoCategories(TreeMap<String, Category> categories) {
		redo_categories.push(categories);
		return redo_categories.pop();
	}
	
	public ArrayList<ArrayList<Task>> redoData() {
		return redo_data.pop();
	}
	
	public TreeMap<String, Category> redoCategories() {
		return redo_categories.pop();
	}
}
