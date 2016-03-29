package Task;

import java.util.Date;

/* @@author: Lu Yang */

public class Event extends Task {
	private Date startDateTime;
	private Date endDateTime;
	private boolean isOverdue;
	
	public Event() {
		super();
		this.startDateTime = null;
		this.endDateTime = null;
		isOverdue = false;
	}
	
	public Event(TASK_TYPE type, String name, String tag, Date startDateTime, Date endDateTime) {
		super(type, name, tag);
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		isOverdue = checkOverdue();
	}
	
	private boolean checkOverdue() {
		Date current = new Date();
		System.out.println("[DEBUG] current: " + current);
		if  (current.after(endDateTime)) {
			return true;
		}
		else return false;
	}
	
	/*******************************ACCESSORS********************************/
	public boolean getIsOverdue() {
		return this.isOverdue;
	}
	
	public Date getStartDateTime() {
		return this.startDateTime;
	}
	
	public Date getEndDateTime() {
		return this.endDateTime;
	}
	
	/*******************************MUTATORS********************************/
	
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}
	
	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}
	
}
