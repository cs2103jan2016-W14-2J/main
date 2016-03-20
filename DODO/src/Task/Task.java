package Task;

import java.util.*;

	/*
	 *@author Pay Hao Jie
	 *@Description: This class sets and returns the attributes of a task. 
	 */
public class Task {
	private TASK_TYPE type;
	private TASK_STATUS status;
	private String name;
	private String tag;
	private String description;
	private boolean flag;
	private boolean isComplete;
	
	public Task(TASK_TYPE type, String name, String tag) {
		this.type = type; // parser-determined
		this.name = name; // must have
		this.tag = tag; // may be null
		this.isComplete = false;
		this.flag = false;
	}
	
	/**************************************ACCESSORS**************************/

	// unusable
	public boolean getFlag() {
		return this.flag;
	}
	
	// usable
	public String getName() {
		return this.name;
	}
	
	// unusable
	public boolean getComplete() {
		return this.isComplete;
	}
	
	//unusable
	public String getDescription() {
		return this.description;
	}
	
	//unusable
	public String tag() {
		return this.tag;
	}
	
	/***********************************MUTATORS*****************************/
	
	public String getTag() {
		return this.tag;
	}
	
	public void setTag(String tag) {
		
		this.tag = tag;
	}
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void complete(boolean isComplete) {
		this.isComplete = isComplete;
	}
		
	@Override
	public String toString() {
		return this.name;
	}
}
