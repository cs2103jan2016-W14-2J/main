package Command;

/* @@author: Lu Yang */

import java.util.*;

import Parser.Parser;
import Task.*;

public class Sort extends Command {
	private static final String INVALID_SORT = "Please enter a valid sort command.";
	private static final DescendingAlphabeticalComparator decendingAlphabeticalComparator = new DescendingAlphabeticalComparator();
	private static final AscendingAlphaticalComparator ascendingAlphabeticalComparator = new AscendingAlphaticalComparator();
	private static final DateComparator dateComparator = new DateComparator();

	public Sort(Parser parser, ArrayList<ArrayList<Task>> data, TreeMap<String, Category> categories) {
		super(parser, data, categories);
	}

	@Override
	public String execute() {
		SORT_TYPE type = parser.getSortType();
		switch (type) {
		case BY_ASCENDING:
		case BY_DESCENDING:
			return sortByAlphabet(type);
		case BY_DATE:
			return sortByDate();
		default: 
			return INVALID_SORT;
		}
	}

	public String sortByAlphabet(SORT_TYPE type) {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		if (type==SORT_TYPE.BY_DESCENDING) {
			Collections.sort(tasks, decendingAlphabeticalComparator);
		}
		else {
			Collections.sort(tasks, ascendingAlphabeticalComparator);
		}
		return "Sorted by Alphabets.";
	}
	
	public String sortByDate() {
		ArrayList<Task> tasks = retrieve(this.UIStatus);
		Collections.sort(tasks, dateComparator);
		return "Sorted by Dates.";
	}


	private static class AscendingAlphaticalComparator implements Comparator<Task> {
		@Override
		public int compare(Task a, Task b) {
			String nameA = a.getName();
			String nameB = b.getName();
			int comp = nameA.compareToIgnoreCase(nameB);
			return comp;
		}
	}
	
	private static class DescendingAlphabeticalComparator implements Comparator<Task> {
		@Override
		public int compare(Task a, Task b) {
			String nameA = a.getName();
			String nameB = b.getName();
			int comp = nameB.compareToIgnoreCase(nameA);
			return comp;
		}
	}
	
	private static class DateComparator implements Comparator<Task> {
		@Override
		public int compare(Task a, Task b) {
			Date aDate = a.getEnd();
			Date bDate = a.getEnd();
			if (aDate==null) return -1;
			if (bDate==null) return 1;
			if (aDate.after(bDate)) {
				return -1;
			}
			else if (aDate.before(bDate)) {
				return 1;
			}
			else return 0;
		}
	}
}
