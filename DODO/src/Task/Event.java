package Task;

import java.util.Date;

/*
 * @author: Lu Yang
 */

public class Event extends Task {
	private Date startDateTime;
	private Date endDateTime;
	boolean isOverdue;
	
	public Event(TASK_TYPE type, String name, String tag, Date startDateTime, Date endDateTime) {
		super(type, name, tag);
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		// TODO
	}
	
	private void checkIsOverdue() {
		// TODO
	}
	
	public boolean getIsOverdue() {
		return this.isOverdue;
	}
	
}
