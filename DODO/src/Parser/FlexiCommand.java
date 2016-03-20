package Parser;

import java.util.HashMap;
import Command.*;
import Task.*;
/*
 *@author Pay Hao Jie
 *@Description: This class consolidates the possible inputs for a specific command 
 *				if the user happens to key in a different variation. 
 */

public class FlexiCommand {
	
	private static HashMap<String, COMMAND_TYPE> possibleCommands;
	
	public FlexiCommand() {
		possibleCommands = new HashMap<String, COMMAND_TYPE>();
		possibleCommands = initiateFlexiAddCommand();
		possibleCommands = initiateFlexiDeleteCommand();
		possibleCommands = initiateFlexiEditCommand();
		possibleCommands = initiateFlexiCompleteCommand();
		possibleCommands = initiateFlexiUndoCommand();
		possibleCommands = initiateFlexiRedoCommand();
/*		possibleCommands = initiateFlexiHelpCommand();
		possibleCommands = initiateFlexiDisplayCommand();
		possibleCommands = initiateFlexiTagCommand();
		possibleCommands = initiateFlexiUntagCommand();
		possibleCommands = initiateFlexiFlagCommand();
		possibleCommands = initiateFlexiUnflagCommand();
		possibleCommands = initiateFlexiExitCommand();
		possibleCommands = initiateFlexiSearchCommand();
		possibleCommands = initiateFlexiSortCommand();
		possibleCommands = initiateFlexiClearCommand();*/
		
	}
	
	public HashMap<String, COMMAND_TYPE> getKeywordsDataBase() {
		return possibleCommands;
	}
	
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiAddCommand() {
		possibleCommands.put("add", COMMAND_TYPE.ADD);
		possibleCommands.put("+", COMMAND_TYPE.ADD);
		possibleCommands.put("ad", COMMAND_TYPE.ADD);
		possibleCommands.put("adds", COMMAND_TYPE.ADD);
		possibleCommands.put("a", COMMAND_TYPE.ADD);
		possibleCommands.put("create", COMMAND_TYPE.ADD);
		possibleCommands.put("creates", COMMAND_TYPE.ADD);
		possibleCommands.put("creat", COMMAND_TYPE.ADD);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiDeleteCommand() {
		possibleCommands.put("d", COMMAND_TYPE.DELETE);
		possibleCommands.put("delete", COMMAND_TYPE.DELETE);
		possibleCommands.put("dlt", COMMAND_TYPE.DELETE);
		possibleCommands.put("del", COMMAND_TYPE.DELETE);
		possibleCommands.put("deletes", COMMAND_TYPE.DELETE);
		possibleCommands.put("remove", COMMAND_TYPE.DELETE);
		possibleCommands.put("rm", COMMAND_TYPE.DELETE);
		possibleCommands.put("r", COMMAND_TYPE.DELETE);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiEditCommand() {
		possibleCommands.put("ed", COMMAND_TYPE.EDIT);
		possibleCommands.put("edit", COMMAND_TYPE.EDIT);
		possibleCommands.put("edits", COMMAND_TYPE.EDIT);
		possibleCommands.put("update", COMMAND_TYPE.EDIT);
		possibleCommands.put("updates", COMMAND_TYPE.EDIT);
		possibleCommands.put("change", COMMAND_TYPE.EDIT);
		possibleCommands.put("changes", COMMAND_TYPE.EDIT);
		possibleCommands.put("correct", COMMAND_TYPE.EDIT);
		return possibleCommands;
	}
	
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiUndoCommand() {
		possibleCommands.put("undo", COMMAND_TYPE.UNDO);
		possibleCommands.put("back", COMMAND_TYPE.UNDO);
		possibleCommands.put("previous", COMMAND_TYPE.UNDO);
		possibleCommands.put("revert", COMMAND_TYPE.UNDO);
		possibleCommands.put("b", COMMAND_TYPE.UNDO);
		possibleCommands.put("goback", COMMAND_TYPE.UNDO);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiCompleteCommand() {
		possibleCommands.put("complete", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("completes", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("done", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("finish", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("fin", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("end", COMMAND_TYPE.COMPLETE);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiRedoCommand() {
		possibleCommands.put("redo", COMMAND_TYPE.REDO);
		possibleCommands.put("redos", COMMAND_TYPE.REDO);
		return possibleCommands;
		
	}
	
	/*
	private HashMap<String, COMMAND_TYPE> initiateFlexiHelpCommand() {
		possibleCommands.put("help", HELP);
		possibleCommands.put("helps", HELP);
		possibleCommands.put("h", HELP);
		possibleCommands.put("assist", HELP);
		return possibleCommands;
	}
	
	private HashMap<String, CommandType> initiateFlexiDisplayCommand() {
		possibleCommands.put("display", DISPLAY);
		possibleCommands.put("displays", DISPLAY);
		possibleCommands.put("show", DISPLAY);
		possibleCommands.put("view", DISPLAY);
		possibleCommands.put("v", DISPLAY);
		possibleCommands.put("see", DISPLAY);
		return possibleCommands;
	}
	
	private HashMap<String, CommandType> initiateFlexiTagCommand() {
		possibleCommands.put("tag", TAG);
		possibleCommands.put("cat", TAG);
		possibleCommands.put("tags", TAG);
		possibleCommands.put("category", TAG);
		return possibleCommands;
		
	}
	
	private HashMap<String, CommandType> initiateFlexiUntagCommand() {
		possibleCommands.put("untag", UNTAG);
		possibleCommands.put("untags", UNTAG);
		return possibleCommands;
	}


	
	private HashMap<String, CommandType> initiateFlexiFlagCommand() {
		possibleCommands.put("flag", FLAG);
		possibleCommands.put("flags", FLAG);
		possibleCommands.put("important", FLAG);
		possibleCommands.put("impt", FLAG);
		possibleCommands.put("starred", FLAG);
		possibleCommands.put("star", FLAG);
		return possibleCommands;
		
	}
	
	private HashMap<String, CommandType> initiateFlexiUnflagCommand() {
		possibleCommands.put("unflag", UNFLAG);
		possibleCommands.put("unflags", UNFLAG);
		possibleCommands.put("unimportant", UNFLAG);
		possibleCommands.put("unimpt", UNFLAG);
		possibleCommands.put("unstarred", UNFLAG);
		possibleCommands.put("unstar", UNFLAG);
		return possibleCommands;
	}
	
	private HashMap<String, CommandType> initiateFlexiExitCommand() {
		possibleCommands.put("exit", EXIT);
		possibleCommands.put("exits", EXIT);
		possibleCommands.put("quit", EXIT);
		possibleCommands.put("quits", EXIT);
		possibleCommands.put("q", EXIT);
		possibleCommands.put("q!", EXIT);
		possibleCommands.put("terminate", EXIT);
		return possibleCommands;
	}
	
	private HashMap<String, CommandType> initiateFlexiSortCommand() {
		possibleCommands.put("sort", SORT);
		possibleCommands.put("arrange", SORT);
		possibleCommands.put("sorts", SORT);
		return possibleCommands;
		
	}
	
	private HashMap<String, CommandType> initiateFlexiSearchCommand() {
		possibleCommands.put("search", SEARCH);
		possibleCommands.put("find", SEARCH);
		possibleCommands.put("f", SEARCH);
		return possibleCommands;
	}
	
	
	private HashMap<String, CommandType> initiateFlexiClearCommand() {
		possibleCommands.put("clear", CLEAR);
		possibleCommands.put("clears", CLEAR);
		possibleCommands.put("wipe", CLEAR);
		possibleCommands.put("wipeout", CLEAR);
		return possibleCommands;
	}*/
	
	
}
