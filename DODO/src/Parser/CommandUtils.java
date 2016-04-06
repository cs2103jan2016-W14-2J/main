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
	private String newDirectory;
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
		newDirectory = "";
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
	//******************************************* Mutators *****************************************//
	protected void setCommandType(COMMAND_TYPE command) {
		this.command = command;
	}
	protected void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	protected void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	protected void setTaskType(TASK_TYPE taskType) {
		this.taskType = taskType;
	}
	
	protected void setTaskTag(ArrayList<String> tags) {
		this.tags = tags;
	}
	
	protected void setTaskID(String index) {
		this.taskID = Integer.parseInt(index);
	}
	//***********************************Accessors for AddParser************************************//
	
	public Date getStartTime() {
		return this.startTime;
	}
	
	public Date getEndTime() {
		return this.endTime;
	}
	
	public String getName() {
		return this.taskName;
	}
	
	public TASK_TYPE getType() {
		return this.taskType;
	}
	
	public COMMAND_TYPE getCommandType() {
		return this.command;
	}
	
	protected void setTaskImportance(boolean isImportant) {
		this.isImportant = isImportant;
	}
	
	public boolean getImportance() {
		return this.isImportant;
	}

	public ArrayList<String> getTag() {
		return this.tags;
	}
	
	
/*	public TASK_TYPE getTaskType() {
		return this.taskType;
	}
*/	//*********************************** Accessors for DeleteParser ********************************//
	
	public DELETE_TYPE getDeleteType() {
		return this.deleteType;
	}
	
	protected void setDeleteType(DELETE_TYPE deleteType) {
		this.deleteType = deleteType;
	}
	
	protected void setTagToDelete(ArrayList<String> tagToDelete) {
		this.tagToDelete = tagToDelete;
	}
	
	public ArrayList<String> getTagToDelete() {
		return this.tagToDelete;
	}
	
	protected void setIndexToDelete(ArrayList<Integer> indexToDelete) {
		this.indexToDelete = indexToDelete;
	}
	
	public ArrayList<Integer> getIndexToDelete() {
		return this.indexToDelete;
	}
	
	//***********************************Accessors for EditParser************************************//
	
	public int getTaskID() {
		return this.taskID;
	}
	
	protected void setEditType(EDIT_TYPE editType) {
		this.editType = editType;
	}
	
	public EDIT_TYPE getEditType() {
		return this.editType;
	}
	protected void setOldTag(String oldTag) {
		this.oldTag = oldTag;
	}
	public String getOldTag() {
		return this.oldTag;
	}
	
	//**************************** Accessors for Flag/Unflag/CompleteParser ***********************//
	protected void setFlagCompleteType(FLAGANDCOMPLETE_TYPE flagAndCompleteType) {
		this.flagAndCompleteType = flagAndCompleteType;
	}
	protected void setTaskToFlagAndMark(ArrayList<Integer> indexOfFlagAndMark) {
		this.indexOfFlagAndMark = indexOfFlagAndMark;
	}
	public FLAGANDCOMPLETE_TYPE getFlagAndCompleteType() {
		return this.flagAndCompleteType;
	}
		
	public ArrayList<Integer> getTaskToFlagAndMark() {
		return this.indexOfFlagAndMark;
	}
	//********************************************* SearchParser ************************************//

	protected void setSearchType (SEARCH_TYPE searchType) {
		this.searchType = searchType;
	}
	
	protected void setSearchByDate (Date searchByDate) {
		this.searchByDate = searchByDate;
	}
	protected void setSearchByTag(String searchByTag) {
		this.searchByTag = searchByTag;
	}
	public SEARCH_TYPE getSearchType() {
		return this.searchType;
	}
	
	protected void setSearchByTask(String searchByTask) {
		this.searchByTask = searchByTask;
	}
	
	public String getSearchByTask() {
		return this.searchByTask.trim();
	}
	
	public String getSearchByTag() {
		return this.searchByTag.trim();
	}
	
	public Date getSearchByDate() {
		return this.searchByDate;
	}
	//********************************************* SortParser ************************************//
	protected void setSortAttributes(SORT_TYPE sortType) {
		this.sortType = sortType;	
	}

	public SORT_TYPE getSortType() {
		return this.sortType;
	}
	
	protected void setSortType(SORT_TYPE sortType) {
		this.sortType = sortType;
	}
	//************************************** ChangeDirectory Parser *********************************//
	public String getNewDirectory() {
		return this.newDirectory;
	}
	
}
