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
		// TODO
	}
	
	private void checkOverdue() {
		// TODO
	}
	
	public boolean getIsOverdue() {
		return this.isOverdue;
	}
}