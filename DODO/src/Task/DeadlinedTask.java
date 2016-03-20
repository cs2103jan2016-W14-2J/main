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
		System.out.println("deadlinedTask/checkoverdue: checkpoint1");
		Date current = new Date();
		System.out.println("deadlinedTask/checkoverdue: checkpoint2");
		System.out.println("[DEBUG] current: " + current);
		if  (current.after(endDateTime)) {
			System.out.println("deadlinedTask/checkoverdue: checkpoint3");
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