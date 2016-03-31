package Task;

import java.text.*;
import java.util.*;

/* @@author: Lu Yang */

public class Task {
	private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss");

	private TASK_TYPE type;
	private TASK_STATUS status;
	private String name;
	private String tag;
	private String end;
	private String start;
	private boolean flag;
	private boolean isOverdue;

	// Default Constructor
	public Task() {
		this(null, null);
	}

	// Constructor of Floating Tasks
	public Task(String name, String tag) {
		this.type = TASK_TYPE.FLOATING;
		this.status = TASK_STATUS.FLOATING;
		this.name = name; // must have
		this.tag = tag; // may be null
		this.flag = false;
	}

	// Constructor of Deadlined Tasks
	public Task(String name, String tag, Date end) {
		this.type = TASK_TYPE.DEADLINED;
		this.status =TASK_STATUS.ONGOING;
		this.name = name;
		this.tag = tag;
		this.end = formatter.format(end);
		this.flag = false;;
		this.isOverdue = checkOverdue(end);
	}

	// Constructor of Event
	public Task(String name, String tag, Date start, Date end) {
		this.type = TASK_TYPE.EVENT;
		this.status =TASK_STATUS.ONGOING;
		this.name = name;
		this.tag = tag;
		this.start = formatter.format(start);
		this.end = formatter.format(end);
		this.flag = false;
		this.isOverdue = checkOverdue(end);
	}

	// copy constructor
	public Task(Task original) {
		this.type = original.type;
		this.status = original.status;
		this.name = original.name;
		this.tag = original.tag;
		this.end = original.end;
		this.start = original.start;
		this.flag = original.flag;
		this.isOverdue = original.isOverdue;
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

	public String getTag() {
		return this.tag;
	}

	public boolean getIsOverdue() {
		return this.isOverdue;
	}

	public Date getStart() {
		Date date = null;
		try {
			date = formatter.parse(this.start);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public Date getEnd() {
		Date date = null;
		try {
			date = formatter.parse(this.end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/***********************************MUTATORS*****************************/
	public void setTag(String tag) { 
		this.tag = tag; 
	}

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
				// convert to Event
				this.type = TASK_TYPE.EVENT;
			}
			this.start = formatter.format(start);
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
			this.isOverdue = checkOverdue(end);
			this.end = formatter.format(end);
		}
	}

	@Override
	public String toString() {
		return this.name;
	}

	private boolean checkOverdue(Date end) {
		Date current = new Date();
		if  (current.after(end)) {
			this.status = TASK_STATUS.OVERDUE;
			return true;
		}
		else {
			this.status = TASK_STATUS.ONGOING;
			return false;
		}
	}
}
