package Command;

import java.util.*;

import Parser.Parser;
import Task.*;

public class Sort extends Command {
	private static final DescendingAlphabeticalComparator decendingAlphabeticalComparator = new DescendingAlphabeticalComparator();
	private static final AscendingAlphaticalComparator ascendingAlphabeticalComparator = new AscendingAlphaticalComparator();
	private static final DateComparator dateComparator = new DateComparator();

	public Sort(Parser parser, ArrayList<ArrayList<Task>> data, COMMAND_TYPE command_type) {
		super(parser, data, command_type);
	}

	@Override
	public String execute() {
		SORT_TYPE type = parser.getSortType();
		System.out.println("============SORT==============: " + type);
		switch (type) {
		case BY_ASCENDING:
		case BY_DESCENDING:
			return sortByAlphabet(type);
		case BY_DATE:
			return sortByDate();
		default: 
			return "[SORT] INTERNAL ERROR.";
		}
	}

	public String sortByAlphabet(SORT_TYPE type) {
		ArrayList<Task> tasks = getTasks(this.UIStatus);
		if (type==SORT_TYPE.BY_DESCENDING) {
			Collections.sort(tasks, decendingAlphabeticalComparator);
		}
		else {
			Collections.sort(tasks, ascendingAlphabeticalComparator);
		}
		return "Sorted by Alphabets.";
	}
	
	public String sortByDate() {
		ArrayList<Task> tasks = getTasks(this.UIStatus);
		Collections.sort(tasks, dateComparator);
		return "Sorted by Dates.";
	}

	@Override
	public String undo() {
		return null;
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
			Date aDate = null;
			Date bDate = null;
			if (a instanceof DeadlinedTask) {
				aDate = ((DeadlinedTask) a).getEndDateTime();
				bDate = getDate(b);
			}
			else if (a instanceof Event) {
				aDate = ((Event) a).getStartDateTime();
				bDate = getDate(b);
			}
			else {
				return -1;
			}
			
			if (bDate == null) {
				//Task B is floating task
				return 1;
			}
			
			if (bDate.after(aDate)) return -1;
			else if (bDate.before(aDate)) return 1;
			else return 0;
		}
		
		private Date getDate(Task b) {
			Date bDate = null;
			if (b instanceof DeadlinedTask) {
				b = (DeadlinedTask) b;
				bDate = ((DeadlinedTask) b).getEndDateTime();
			}
			else if (b instanceof Event) {
				b = (Event) b;
				bDate = ((Event) b).getStartDateTime();
			}
			return bDate;
		}
	}
}
