//@@author A0125552L
package Parser;

import java.util.ArrayList;
import java.util.Date;

import Command.COMMAND_TYPE;
import Command.DELETE_TYPE;
import Command.EDIT_TYPE;
import Command.FLAGANDCOMPLETE_TYPE;
import Command.SEARCH_TYPE;
import Command.SORT_TYPE;
import Task.TASK_TYPE;

public class CommandUtils {
	
	private ArrayList<Integer> indexOfFlagAndMark;
	private ArrayList<String> tags;
	private ArrayList<String> tagToDelete;
	private ArrayList<Integer> indexToDelete;
	
	private Date startTime;
	private Date endTime;
	private Date searchByDate;
	
	private String oldTag;
	private String taskName;
	private String searchByTask;
	private String searchByTag;
	private int taskID;
	private boolean isImportant;
	
	private COMMAND_TYPE command;
	private TASK_TYPE taskType;
	private FLAGANDCOMPLETE_TYPE flagAndCompleteType;
	private SEARCH_TYPE searchType;
	private SORT_TYPE sortType;
	private EDIT_TYPE editType;
	private DELETE_TYPE deleteType;
	
	public CommandUtils () {
		
		indexOfFlagAndMark = new ArrayList<Integer>();
		tags = new ArrayList<String>();
		tagToDelete = new ArrayList<String> ();
		indexToDelete = new ArrayList<Integer>();
		
		startTime = null;
		endTime = null;
		searchByDate = null;
		
		oldTag = "";
		taskName = "";
		searchByTask = "";
		searchByTag = "";
		taskID = -1;
		isImportant = false;
		
		command = COMMAND_TYPE.INVALID;
		taskType = TASK_TYPE.INVALID;
		flagAndCompleteType = FLAGANDCOMPLETE_TYPE.INVALID;
		searchType = SEARCH_TYPE.INVALID;
		sortType = SORT_TYPE.INVALID;
		editType = EDIT_TYPE.INVALID;
		deleteType = DELETE_TYPE.INVALID;
		
	}
	//****************************************Common Mutators *****************************************//
	/*
	 * 
	 * @param {COMMAND_TPE}			
	 * 
	 */
	protected void setCommandType(COMMAND_TYPE command) {
		this.command = command;
	}
	
	/*
	 * 
	 * @param {Date}			
	 * 
	 */
	protected void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	/*
	 * 
	 * @param {Date}			
	 * 
	 */
	protected void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/*
	 * 
	 * @param {String}			
	 * 
	 */
	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	/*
	 * 
	 * @param {TASK_TYPE}			
	 * 
	 */
	protected void setTaskType(TASK_TYPE taskType) {
		this.taskType = taskType;
	}
	
	/*
	 * 
	 * @param {ArrayList<String>}			
	 * 
	 */
	protected void setTaskTag(ArrayList<String> tags) {
		this.tags = tags;
	}
	
	/*
	 * 
	 * @param {String}			
	 * 
	 */
	protected void setTaskID(String index) {
		this.taskID = Integer.parseInt(index);
	}
	//***********************************Accessors for AddParser************************************//
	
	/*
	 * Returns the start time of a task.
	 * 
	 * @param {}
	 * 
	 * @return {@code Date} 
	 * 			
	 * 
	 */
	public Date getStartTime() {
		return this.startTime;
	}
	
	/*
	 * Returns the end time of a task.
	 * 
	 * @param {}
	 * 
	 * @return {@code Date} 
	 * 			
	 * 
	 */
	public Date getEndTime() {
		return this.endTime;
	}
	
	/*
	 * Returns the name without the start & end time of a task.
	 * 
	 * @param {}
	 * 
	 * @return {@code String} 
	 * 			
	 * 
	 */
	public String getName() {
		return this.taskName;
	}
	
	/*
	 * Returns the type of a task.
	 * 
	 * @param {}
	 * 
	 * @return {@code enum} 
	 * 			
	 * 
	 */
	
	public TASK_TYPE getType() {
		return this.taskType;
	}
	
	/*
	 * Returns the type of a command.
	 * 
	 * @param {}
	 * 
	 * @return {@code enum} 
	 * 			
	 * 
	 */
	
	public COMMAND_TYPE getCommandType() {
		return this.command;
	}
	
	/*
	 * 
	 * @param {boolean}			
	 * 
	 */
	protected void setTaskImportance(boolean isImportant) {
		this.isImportant = isImportant;
	}
	
	/*
	 * Returns the importance of a task.
	 * 
	 * @param {}
	 * 
	 * @return {@code boolean} 
	 * 			
	 * 
	 */
	
	public boolean getImportance() {
		return this.isImportant;
	}

	/*
	 * Returns the list of tags assigned to a task.
	 * 
	 * @param {}
	 * 
	 * @return {@code List<E>} 
	 * 			
	 * 
	 */
	
	public ArrayList<String> getTag() {
		return this.tags;
	}
	
		//********************************** Mutators and Accessors for DeleteParser ********************************//
	
	/*
	 * Returns the type of deletion.
	 * 
	 * @param {}
	 * 
	 * @return {@code enum} 
	 * 			
	 * 
	 */
	
	public DELETE_TYPE getDeleteType() {
		return this.deleteType;
	}
	/*
	 * 
	 * @param {DELETE_TYPE}			
	 * 
	 */
	
	protected void setDeleteType(DELETE_TYPE deleteType) {
		this.deleteType = deleteType;
	}
	/*
	 * 
	 * @param {ArrayList<String>}			
	 * 
	 */
	protected void setTagToDelete(ArrayList<String> tagToDelete) {
		this.tagToDelete = tagToDelete;
	}
	
	/*
	 * Returns the list of tags to delete.
	 * 
	 * @param {}
	 * 
	 * @return {@code List<E>} 
	 * 			
	 * 
	 */
	public ArrayList<String> getTagToDelete() {
		return this.tagToDelete;
	}
	/*
	 * 
	 * @param {ArrayList<String>}			
	 * 
	 */
	
	protected void setIndexToDelete(ArrayList<Integer> indexToDelete) {
		this.indexToDelete = indexToDelete;
	}
	
	/*
	 * Returns the list of indexes to delete.
	 * 
	 * @param {}
	 * 
	 * @return {@code List<E>} 
	 * 			
	 * 
	 */
	
	public ArrayList<Integer> getIndexToDelete() {
		return this.indexToDelete;
	}
	
	//********************************** Mutators and Accessors for EditParser************************************//
	
	/*
	 * Returns the index of a task.
	 * 
	 * @param {}
	 * 
	 * @return {@code int} 
	 * 			
	 * 
	 */
	public int getTaskID() {
		return this.taskID;
	}
	
	/*
	 * 
	 * @param {EDIT_TYPE}			
	 * 
	 */
	protected void setEditType(EDIT_TYPE editType) {
		this.editType = editType;
	}
	
	/*
	 * Returns the type of edits.
	 * 
	 * @param {}
	 * 
	 * @return {@code enum} 
	 * 			
	 * 
	 */
	public EDIT_TYPE getEditType() {
		return this.editType;
	}
	/*
	 * 
	 * @param {String}			
	 * 
	 */
	protected void setOldTag(String oldTag) {
		this.oldTag = oldTag;
	}
	
	/*
	 * Returns the tag to be replaced.
	 * 
	 * @param {}
	 * 
	 * @return {@code String} 
	 * 			
	 * 
	 */
	public String getOldTag() {
		return this.oldTag;
	}
	
	//**************************** Mutators and Accessors for Flag/Unflag/CompleteParser ***********************//
	
	/*
	 * 
	 * @param {FLAGANDCOMPLETE_TYPE}			
	 * 
	 */
	protected void setFlagCompleteType(FLAGANDCOMPLETE_TYPE flagAndCompleteType) {
		this.flagAndCompleteType = flagAndCompleteType;
	}
	/*
	 * 
	 * @param {ArrayList<Integer>}			
	 * 
	 */
	protected void setTaskToFlagAndMark(ArrayList<Integer> indexOfFlagAndMark) {
		this.indexOfFlagAndMark = indexOfFlagAndMark;
	}
	
	/*
	 * Returns the type of flagging / completion.
	 * 
	 * @param {}
	 * 
	 * @return {@code enum} 
	 * 			
	 * 
	 */
	public FLAGANDCOMPLETE_TYPE getFlagAndCompleteType() {
		return this.flagAndCompleteType;
	}
		
	/*
	 * Returns the list of indexes to flag/ complete.
	 * 
	 * @param {}
	 * 
	 * @return {@code List<E>} 
	 * 			
	 * 
	 */
	public ArrayList<Integer> getTaskToFlagAndMark() {
		return this.indexOfFlagAndMark;
	}
	//***************************** Mutators and Accessors for SearchParser ************************************//
	/*
	 * 
	 * @param {SEARCH_TYPE}			
	 * 
	 */
	protected void setSearchType (SEARCH_TYPE searchType) {
		this.searchType = searchType;
	}
	/*
	 * 
	 * @param {Date}			
	 * 
	 */
	protected void setSearchByDate (Date searchByDate) {
		this.searchByDate = searchByDate;
	}
	
	/*
	 * 
	 * @param {String}			
	 * 
	 */
	protected void setSearchByTag(String searchByTag) {
		this.searchByTag = searchByTag;
	}
	
	/*
	 * Returns the type of search made.
	 * 
	 * @param {}
	 * 
	 * @return {@code enum} 
	 * 			
	 * 
	 */
	public SEARCH_TYPE getSearchType() {
		return this.searchType;
	}
	
	protected void setSearchByTask(String searchByTask) {
		this.searchByTask = searchByTask;
	}
	
	/*
	 * Returns the string to be searched.
	 * 
	 * @param {}
	 * 
	 * @return {@code String} 
	 * 			
	 * 
	 */
	
	public String getSearchByTask() {
		return this.searchByTask.trim();
	}
	
	/*
	 * Returns the tag to search.
	 * 
	 * @param {}
	 * 
	 * @return {@code String} 
	 * 			
	 * 
	 */
	
	public String getSearchByTag() {
		return this.searchByTag.trim();
	}
	
	/*
	 * Returns the date to search.
	 * 
	 * @param {}
	 * 
	 * @return {@code Date} 
	 * 			
	 * 
	 */
	public Date getSearchByDate() {
		return this.searchByDate;
	}
	
	//******************************** Mutators and Accessors for SortParser ************************************//
	

	/*
	 *  Returns the type of sort.
	 * 
	 * @param {}
	 * 
	 * @return sort Type
	 * 			
	 * 
	 */
	public SORT_TYPE getSortType() {
		return this.sortType;
	}
	/*
	 * 
	 * @param {SORT_TYPE}			
	 * 
	 */
	protected void setSortType(SORT_TYPE sortType) {
		this.sortType = sortType;
	}
	
}
