package Task;

import java.text.*;
import java.util.*;

/* @@author: A0130684H */

public class Task {
	private static final SimpleDateFormat datetimeFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
	private TASK_TYPE type;
	private TASK_STATUS status;
	private String name;
	private Date start;
	private Date end;
	private boolean flag;
	private ArrayList<String> categories;

	// Default Constructor
	public Task() {
		this("");
	}

	// Constructor of Floating Tasks
	public Task(String name) {
		this.type = TASK_TYPE.FLOATING;
		this.status = TASK_STATUS.FLOATING;
		this.name = name; // must have

		this.categories = new ArrayList<String>();
		this.flag = false;
	}

	// Constructor of Deadlined Tasks
	public Task(String name, Date end) {
		this.type = TASK_TYPE.DEADLINED;
		this.name = name;
		/*this.end = formatter.format(end);*/
		this.end = end;
		this.status = checkOverdue(end);

		this.categories = new ArrayList<String>();
		this.flag = false;;
	}

	// Constructor of Event
	public Task(String name, Date start, Date end) {
		this.type = TASK_TYPE.EVENT;
		this.name = name;
		this.end = end;
		this.start = start;
		this.status = checkOverdue(end);

		this.categories = new ArrayList<String>();
		this.flag = false;
	}

	// copy constructor
	public Task(Task original) {
		this.type = original.type;
		this.status = original.status;
		this.name = original.name;
		this.end = original.end;
		this.start = original.start;
		this.flag = original.flag;
		this.categories = original.categories;
	}

	/**************************************ACCESSORS**************************/
	public TASK_TYPE getType() {
		return this.type;
	}

	public TASK_STATUS getStatus() {
		return this.status;
	}

	public boolean getFlag() {
		return this.flag;
	}

	public String getName() {
		return this.name;
	}

	public ArrayList<String> getCategories() {
		return this.categories;
	}

	public Date getStart() {
		return this.start;
	}

	public String getStartString() {
		return convertToString(this.start);
	}
	
	public String getEndString() {
		return convertToString(this.end);
	}

	public Date getEnd() {
		return this.end;
	}

	/***********************************MUTATORS*****************************/
	public void setFlag(boolean flag) {	
		this.flag = flag; 
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setComplete() {
		this.status = TASK_STATUS.COMPLETED;
	}

	public void setStart(Date start) {
		if (start==null) {
			// convert to deadlined
			this.type = TASK_TYPE.DEADLINED;
			this.start = null;
		}
		else {
			if (this.start==null) {
				// convert to Event from Deadlined/Floating
				this.type = TASK_TYPE.EVENT;
			}
			this.start = start;
		}
	}

	public void setEnd(Date end) {
		if (end==null) {
			// convert to floating
			this.type = TASK_TYPE.FLOATING;
			this.end = null;
		}
		else {
			if (this.type==TASK_TYPE.FLOATING) {
				// if the task is a floating task
				this.type = TASK_TYPE.DEADLINED;
			}
			this.end = end;
			this.status = checkOverdue(end);
		}
	}

	public void setCategory(ArrayList<String> categories) {
		this.categories = categories;
	}
	
	public boolean deleteCategory(String category) {
		for (String string: this.categories) {
			if (string.equalsIgnoreCase(category)) {
				this.categories.remove(string);
				return true;
			}
		}
		return false;
	}
	
	public boolean addCategory(String category) {
		for (String string: this.categories) {
			if (string.equalsIgnoreCase(category)) {
				return false;
			}
		}
		this.categories.add(category);
		return true;
	}

	/*@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task task = (Task) obj;
			return task==this;
		}
		else return false;
	}*/

	@Override
	public String toString() {
		return this.name;
	}
	
	public boolean update() {
		switch (this.status) {
		case ONGOING:
			TASK_STATUS newStatus = this.checkOverdue(this.end);
			if (newStatus==TASK_STATUS.OVERDUE) {
				this.status = newStatus;
				return true;
			}
			break;
		case OVERDUE:
			newStatus = this.checkOverdue(this.end);
			if (newStatus==TASK_STATUS.ONGOING) {
				this.status = newStatus;
				return true;
			}
			break;
		}
		return false;
	}
	
	
	/********************************************INTERNAL*************************************/
	private TASK_STATUS checkOverdue(Date end) {
		Date current = new Date();
		if  (current.after(end)) {
			return TASK_STATUS.OVERDUE;
		}
		else {
			return TASK_STATUS.ONGOING;
		}
	}

	private String convertToString(Date date) {
		if (date==null) {
			return null;
		}
		
		String timeStr = timeFormatter.format(date);
		if (timeStr.equals("23:59:59")) {
			return dateFormater.format(date);
		}
		else {
			return datetimeFormater.format(date);
		}
	}
}
