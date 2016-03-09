package Task;

import java.util.Date;

/*
 * @author: Lu Yang
 */

public class Event extends Task {
	private Date startDateTime;
	private Date endDateTime;
	private boolean isOverdue;
	
	public Event(TASK_TYPE type, String name, String tag, Date startDateTime, Date endDateTime) {
		super(type, name, tag);
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		isOverdue = checkOverdue();
	}
	
	private boolean checkOverdue() {
		Date current = new Date();
		if  (endDateTime.after(current)) {
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
	
	
}
