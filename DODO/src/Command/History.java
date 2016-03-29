package Command;

/* @@author: Lu Yang */

import java.util.*;
import Task.*;

public class History {
	private static Stack<ArrayList<ArrayList<Task>>> undo_data;
	private static Stack<ArrayList<ArrayList<Task>>> redo_data;
	
	public History() {
		undo_data = new Stack<ArrayList<ArrayList<Task>>>();
		redo_data = new Stack<ArrayList<ArrayList<Task>>>();
	}

	public String save(ArrayList<ArrayList<Task>> data) {
		undo_data.push(data);
		redo_data = new Stack<ArrayList<ArrayList<Task>>>();
		return "[INTERNAL MESSAGE/HISTORY]: data saved.";
	}
	
	public ArrayList<ArrayList<Task>> undo(ArrayList<ArrayList<Task>> data) {
		redo_data.push(data);
		return undo_data.pop();
	}
	
	public ArrayList<ArrayList<Task>> redo() {
		return redo_data.pop();
	}
}
