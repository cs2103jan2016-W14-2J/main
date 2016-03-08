package Task;

import java.util.*;

/*
 * @author: Lu Yang
 */

public class DeadlinedTask extends Task {
	private Date endDateTime;
	private boolean isOverdue;
	
	public DeadlinedTask(TASK_TYPE type, String name, String tag, Date endDateTime) {
		super(type, name, tag);
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
	
	/*************************************ACCESSORS*********************************/
	
	public boolean getIsOverdue() {
		return this.isOverdue;
	}
	
	public Date getEndDateTime() {
		return this.endDateTime;
	}
}