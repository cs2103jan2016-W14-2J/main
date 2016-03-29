package Task;

/* @@author: Lu Yang */

public class Task extends Object {
	private TASK_TYPE type;
	private TASK_STATUS status;
	private String name;
	private String tag;
	private boolean flag;
	private boolean isComplete;

	public Task(TASK_TYPE type, String name, String tag) {
		this.type = type; // parser-determined
		this.name = name; // must have
		this.tag = tag; // may be null
		this.isComplete = false;
		this.flag = false;
	}
	
	// copy constructor
	public Task(Task original) {
		this.type = original.type;
		this.status = original.status;
		this.name = original.name;
		this.tag = original.tag;
		this.flag = original.flag;
		this.isComplete = original.isComplete;
	}

	/**************************************ACCESSORS**************************/
	public boolean getFlag() {
		return this.flag;
	}

	public String getName() {
		return this.name;
	}

	public boolean getComplete() {
		return this.isComplete;
	}
	
	public String getTag() {
		return this.tag;
	}

	/***********************************MUTATORS*****************************/
	public void setTag(String tag) { this.tag = tag; }
	public void setFlag(boolean flag) {	this.flag = flag; }
	public void setName(String name) { this.name = name; }
	public void complete(boolean isComplete) { this.isComplete = isComplete; }
	
	@Override
	public String toString() {
		return this.name;
	}
}
