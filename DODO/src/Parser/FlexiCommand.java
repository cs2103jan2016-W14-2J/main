package Parser;

import java.util.HashMap;
import Command.*;

/*
 *@author Pay Hao Jie
 *@Description: This class consolidates the possible inputs for a specific command 
 *				if the user happens to key in a different variation. 
 */

public class FlexiCommand {
	
	private static HashMap<String, COMMAND_TYPE> possibleCommands;
	
	public FlexiCommand() {
		possibleCommands = new HashMap<String, COMMAND_TYPE>();
//		possibleCommands = initiateFlexiAddCommand();
		possibleCommands = initiateFlexiDeleteCommand();
		possibleCommands = initiateFlexiEditCommand();
		possibleCommands = initiateFlexiCompleteCommand();
		possibleCommands = initiateFlexiUndoCommand();
		possibleCommands = initiateFlexiRedoCommand();
		possibleCommands = initiateFlexiFlagCommand();
		possibleCommands = initiateFlexiUnflagCommand();
		possibleCommands = initiateFlexiTagCommand();
		possibleCommands = initiateFlexiUntagCommand();
		possibleCommands = initiateFlexiSearchCommand();
		possibleCommands = initiateFlexiSortCommand();
		possibleCommands = initiateFlexiHelpCommand();
		possibleCommands = initiateFlexiChangeDirectoryCommand();
		possibleCommands = initiateFlexiExitCommand();
/*		possibleCommands = initiateFlexiDisplayCommand();
		possibleCommands = initiateFlexiClearCommand();*/
		
	}
	
	public HashMap<String, COMMAND_TYPE> getKeywordsDataBase() {
		return possibleCommands;
	}
	
/*	
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
*/	
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
		possibleCommands.put("fin", COMMAND_TYPE.COMPLETE);
		possibleCommands.put("end", COMMAND_TYPE.COMPLETE);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiRedoCommand() {
		possibleCommands.put("redo", COMMAND_TYPE.REDO);
		possibleCommands.put("redos", COMMAND_TYPE.REDO);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiFlagCommand() {
		possibleCommands.put("flag", COMMAND_TYPE.FLAG);
		possibleCommands.put("flags", COMMAND_TYPE.FLAG);
		possibleCommands.put("important", COMMAND_TYPE.FLAG);
		possibleCommands.put("impt", COMMAND_TYPE.FLAG);
		possibleCommands.put("starred", COMMAND_TYPE.FLAG);
		possibleCommands.put("star", COMMAND_TYPE.FLAG);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiUnflagCommand() {
		possibleCommands.put("unflag", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unflags", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unimportant", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unimpt", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unstarred", COMMAND_TYPE.UNFLAG);
		possibleCommands.put("unstar", COMMAND_TYPE.UNFLAG);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiTagCommand() {
		possibleCommands.put("tag", COMMAND_TYPE.TAG);
		possibleCommands.put("cat", COMMAND_TYPE.TAG);
		possibleCommands.put("tags", COMMAND_TYPE.TAG);
		possibleCommands.put("category", COMMAND_TYPE.TAG);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiUntagCommand() {
		possibleCommands.put("untag", COMMAND_TYPE.UNTAG);
		possibleCommands.put("untags", COMMAND_TYPE.UNTAG);
		return possibleCommands;
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiSortCommand() {
		possibleCommands.put("sort", COMMAND_TYPE.SORT);
		possibleCommands.put("arrange", COMMAND_TYPE.SORT);
		possibleCommands.put("sorts", COMMAND_TYPE.SORT);
		return possibleCommands;
		
	}
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiSearchCommand() {
		possibleCommands.put("search", COMMAND_TYPE.SEARCH);
		possibleCommands.put("find", COMMAND_TYPE.SEARCH);
		possibleCommands.put("f", COMMAND_TYPE.SEARCH);
		return possibleCommands;
	}
	
	
	private HashMap<String, COMMAND_TYPE> initiateFlexiHelpCommand() {
		possibleCommands.put("help", COMMAND_TYPE.HELP);
		possibleCommands.put("helps", COMMAND_TYPE.HELP);
		possibleCommands.put("h", COMMAND_TYPE.HELP);
		possibleCommands.put("assist", COMMAND_TYPE.HELP);
		return possibleCommands;
	}
	private HashMap<String, COMMAND_TYPE> initiateFlexiChangeDirectoryCommand() {
		possibleCommands.put("cd", COMMAND_TYPE.CHANGE_DIRECTORY);
		possibleCommands.put("redirect", COMMAND_TYPE.CHANGE_DIRECTORY);
		return possibleCommands;
	}
	/*
	private HashMap<String, CommandType> initiateFlexiDisplayCommand() {
		possibleCommands.put("display", DISPLAY);
		possibleCommands.put("displays", DISPLAY);
		possibleCommands.put("show", DISPLAY);
		possibleCommands.put("view", DISPLAY);
		possibleCommands.put("v", DISPLAY);
		possibleCommands.put("see", DISPLAY);
		return possibleCommands;
	}
	*/
	private HashMap<String, COMMAND_TYPE> initiateFlexiExitCommand() {
		possibleCommands.put("exit", COMMAND_TYPE.EXIT);
		possibleCommands.put("exits", COMMAND_TYPE.EXIT);
		possibleCommands.put("quit", COMMAND_TYPE.EXIT);
		possibleCommands.put("quits", COMMAND_TYPE.EXIT);
		possibleCommands.put("q", COMMAND_TYPE.EXIT);
		possibleCommands.put("q!", COMMAND_TYPE.EXIT);
		possibleCommands.put("terminate", COMMAND_TYPE.EXIT);
		return possibleCommands;
	}
	
	/*
	private HashMap<String, CommandType> initiateFlexiClearCommand() {
		possibleCommands.put("clear", CLEAR);
		possibleCommands.put("clears", CLEAR);
		possibleCommands.put("wipe", CLEAR);
		possibleCommands.put("wipeout", CLEAR);
		return possibleCommands;
	}*/
	
	
}
