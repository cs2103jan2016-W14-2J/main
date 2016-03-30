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
		this(TASK_TYPE.FLOATING, null, null);
	}
	
	// Constructor of Floating Tasks
	public Task(TASK_TYPE type, String name, String tag) {
		this.type = type; // parser-determined
		this.name = name; // must have
		this.tag = tag; // may be null
		this.flag = false;
	}
	
	// Constructor of Timed Tasks
	public Task(TASK_TYPE type, String name, String tag, Date end) {
		this.type = type;
		this.name = name;
		this.tag = tag;
		this.flag = false;;
	}
	
	// Constructor of Event
	public Task(TASK_TYPE type, String name, String tag, Date start, Date end) {
		this.type = type;
		this.name = name;
		this.tag = tag;
		
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
	
	public Date getStart() throws ParseException {
		Date date = formatter.parse(this.start);
		return date;
	}
	
	public Date getEnd() throws ParseException {
		Date date = formatter.parse(this.end);
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
		this.start = formatter.format(start);
	}
	
	public void setEnd(Date end) {
		this.end = formatter.format(end);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
