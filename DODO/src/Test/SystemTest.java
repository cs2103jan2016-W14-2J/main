package Test;
import static org.junit.Assert.*;

import org.junit.After;
/* @@author: A0130684H */
import org.junit.Before;
import org.junit.Test;
import Logic.*;
import Task.*;
import java.util.*;

public class SystemTest {
	private static final String MESSAGE_INVALID_START_TIME = "Please enter a valid start time of the event.";
	private static final String MESSAGE_INVALID_END_TIME = "Please enter a valid end time of the task.";
	private static final String MESSAGE_INVALID_EVENT_TIME = "Please enter a valid start and end time of the event.";
	private static final String MESSAGE_INDEXOUTOFBOUND = "Please enter a valid index.";
	private static final String MESSAGE_EMPTY_LIST = "There is no task in this tab. Please add more tasks.";
	private static final String MESSAGE_UNSUCCESSFUL_SEARCH_TAG = "There is no tag named \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_UNDO = "Undo successful.";
	private static final String MESSAGE_SUCCESSFUL_REDO = "Redo successful.";
	private static final String MESSAGE_SUCCESSFUL_SAVE = "Save successful.";
	private static final String MESSAGE_UNSUCCESSFUL_UNDO = "Undo not successful. There is nothing to undo";
	private static final String MESSAGE_UNSUCCESSFUL_REDO = "Redo not successful. There is nothing to redo";
	private static final String MESSAGE_SWITCH_VIEW = "Successfully switch to %1$s.";
	private static final String MESSAGE_INVALID_COMMAND = "Please enter a valid command.";
	private static final String MESSAGE_INVALID_COMPLETE = "Please enter a valid complete command.";
	private static final String MESSAGE_SUCCESSFUL_COMPLETE = 
			"Congratulation! Task(s) at \"%1$s\" is/are successfully completed. ";
	private static final String MESSAGE_UNSUCCESSFUL_COMPLETE = 
			"Task(s) at \"%1$s\" failed to complete due to its/their invalid index or already completed status. ";
	private static final String MESSAGE_INVALID_FLAG = "Please enter a valid flag command.";
	private static final String MESSAGE_SUCCESSFUL_FLAG = 
			"Task(s) at \"%1$s\" is/are successfully flagged. ";
	private static final String MESSAGE_UNSUCCESSFUL_FLAG = 
			"Task(s) at \"%1$s\" failed to flag due to its/their invalid index or already flagged/unflagged status. ";
	private static final String MESSAGE_SUCCESSFUL_UNFLAG =
			"Task(s) at \"%1$s\" is/are successfully unflagged. ";
	private static final String MESSAGE_UNSUCCESSFUL_UNFLAG = 
			"Task(s) at \"%1$s\" failed to unflag due to its/their invalid index or already flagged/unflagged status. ";
	private static final String MESSAGE_SUCCESSFUL_TAG = 
			"Task(s) \"%1$s\" is/are successfully tagged by \"%2$s\". ";
	private static final String MESSAGE_UNSUCCESSFUL_TAG = 
			"Task(s) \"%1$s\" failed to be tagged by \"%2$s\", because it has been already tagged before. ";
	private static final String MESSAGE_INVALID_EDIT = 
			"Please enter a valid edit.";
	private static final String MESSAGE_SUCCESSFUL_EDIT_NAME = 
			"Task name has been successfully edited to \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_START = 
			"Task start time has been successfully edited to \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_END = 
			"Task end time has been successfully edited to \"%1$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_EVENT = 
			"Task event time has been successfully edited to start from \"%1$s\" to \"%2$s\".";
	private static final String MESSAGE_SUCCESSFUL_EDIT_TAG = 
			"Tag name \"%1$s\" has been successfully edit to \"%2$s\".";
	private static final String MESSAGE_INVALID_DELETE = "Please enter a valid delete command.";
	private static final String MESSAGE_SUCCESSFUL_DELETE = "Task(s) at \"%1$s\" is/are successfully deleted. ";
	private static final String MESSAGE_SUCCESSFUL_DELETE_TAG = "Tag(s) \"%1$s\" are successfully deleted. ";
	private static final String MESSAGE_UNSUCCESSFUL_DELETE_TAG = "Tag(s) \"%1$s\" are not successfully deleted. ";
	private static final String MESSAGE_DELETE_ALL = "All tasks and tags are deleted.";
	private static final String MESSAGE_INVALID_SEARCH = "Please enter a valid search command.";
	private static final String MESSAGE_SUCCESSFUL_SEARCH_KEYWORD = "Search keyword \"%1$s\" is successful.";
	private static final String MESSAGE_SUCCESSFUL_SEARCH_TAG = "Search tag \"%1$s\" is successful.";
	private static final String MESSAGE_UNSUCCESSFUL_SEARCH_KEYWORD = "There is no task named \"%1$s\".";
	private Logic logic;
	private String actual;
	private String expected;

	@Before 
	public void setUp() { 
		logic = Logic.getInstance();
		logic.run("V0.5 Project Manual");
		logic.run("V0.5 Project Manual today");
		logic.run("V0.5 Project Manual by yesterday");
	}
	
	@After
	public void cleanUp() {
		logic.run("Delete all");
	}

	@Test
	public void testAdd() {
		// add a floating task
		this.actual = logic.run("V0.5 Project Manual");
		this.expected = "Task \"V0.5 Project Manual\" is added to FLOATING.";
		assertEquals(expected, actual);
		
		// add a deadlined task
		this.actual = logic.run("V0.5 Project Manual today");
		this.expected = "Task \"V0.5 Project Manual\" is added to ONGOING.";
		assertEquals(expected, actual);
		
		// add an overdue task
		this.actual = logic.run("V0.5 Project Manual by yesterday");
		this.expected = "Task \"V0.5 Project Manual\" is added to OVERDUE.";
		assertEquals(expected, actual);

		this.actual = logic.run("");
		this.expected = MESSAGE_INVALID_COMMAND;
		assertEquals(expected, actual);

		this.actual = logic.run("");
		this.expected = MESSAGE_INVALID_COMMAND;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEdit() {
		// edit task name
		this.actual = logic.run("Edit 3 V0.4 Project Manual");
		this.expected = "Task name has been successfully edited to \"V0.4 Project Manual\".";
		assertEquals(expected, actual);
		
		ArrayList<Task> floating = logic.getFloatingTasks();
		System.out.println(floating.size());
		Task task  = logic.getFloatingTasks().get(0);
		this.actual = task.getName();
		this.expected = "V0.4 Project Manual";
		assertEquals(expected, actual);
		
		// edit task deadlined
		this.actual = logic.run("Edit 1 by next Monday.");
		this.expected = "Task end time has been successfully edited to \"18/04/2016 18:00:00\".";
		assertEquals(expected, actual);
		
		// edit task event
		this.actual = logic.run("Edit 1 from today 2pm to 3pm.");
		this.expected = "Task event time has been successfully edited to start from \"11/04/2016 14:00:00\" to \"12/04/2016 15:00:00\".";
		assertEquals(expected, actual);
		
		this.actual = logic.run("Edit -1");
		this.expected = String.format(MESSAGE_INDEXOUTOFBOUND);
		assertEquals(expected, actual);
		
		this.actual = logic.run("edit");
		this.expected = String.format(MESSAGE_INVALID_COMMAND);
		assertEquals(expected, actual);
		
		this.actual = logic.run("edit edit");
		this.expected = String.format(MESSAGE_INDEXOUTOFBOUND);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDelete() {
		// delete task at 1 (V0.5 Project Manual) in All Tab
		this.actual = logic.run("Delete 1");
		this.expected = String.format("Task(s) at \"%1$s\" is/are successfully deleted. ", "[1]");
		assertEquals(expected, actual);
		
		// ERROR HANDLING
		this.actual = logic.run("Delete -1");
		this.expected = MESSAGE_INDEXOUTOFBOUND;
		assertEquals(expected, actual);
		
		// ERROR HANDLING
		this.actual = logic.run("Delete");
		this.expected = MESSAGE_INVALID_COMMAND;
		assertEquals(expected, actual);
		
		this.actual = logic.run("Delete all");
		this.expected = String.format("All tasks and tags are deleted.");
		assertEquals(expected, actual);
		
		// delete a task in empty All Tab
		this.actual = logic.run("Delete 1");
		this.expected = MESSAGE_EMPTY_LIST;
		assertEquals(expected, actual);		
	}
	
	@Test
	public void testComplete() {	
		// complete the task
		this.actual = logic.run("Complete 1");
		this.expected = String.format("Congratulation! Task(s) at \"%1$s\" is/are successfully completed. ", "[1]");
		assertEquals(expected, actual);
		
		Task task = this.logic.getCompletedTasks().get(0);
		this.actual = task.getStatus().toString();
		this.expected = "COMPLETED";
		assertEquals(expected, actual);
		
		// complete all tasks
		this.actual = logic.run("Complete all");
		this.expected = String.format(MESSAGE_SUCCESSFUL_COMPLETE, "[1, 2]") + "\n " + 
				String.format(MESSAGE_UNSUCCESSFUL_COMPLETE, "[0, 3]");
		assertEquals(expected, actual);
		
		// ERROR HANDLING
		this.actual = logic.run("Complete");
		this.expected = MESSAGE_INVALID_COMMAND;
		assertEquals(expected, actual);
		
		// ERROR HANDLING
		this.actual = logic.run("Complete complete");
		this.expected = MESSAGE_INVALID_COMMAND;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testFlag() {
		this.actual = logic.run("Flag 1");
		this.expected = String.format(MESSAGE_SUCCESSFUL_FLAG, "[1]");
		assertEquals(expected, actual);
		
		this.actual = logic.run("Flag 1");
		this.expected = String.format(MESSAGE_UNSUCCESSFUL_FLAG, "[1]");
		assertEquals(expected, actual);
		
		this.actual = logic.run("Unflag 1");
		this.expected = String.format(MESSAGE_SUCCESSFUL_UNFLAG, "[1]");
		assertEquals(expected, actual);
		
		this.actual = logic.run("Unflag 1");
		this.expected = String.format(MESSAGE_UNSUCCESSFUL_UNFLAG, "[1]");
		assertEquals(expected, actual);
		
		this.actual = logic.run("flag");
		this.expected = String.format(MESSAGE_INVALID_COMMAND);
		assertEquals(expected, actual);
		
		this.actual = logic.run("Unflag");
		this.expected = String.format(MESSAGE_INVALID_COMMAND);
		assertEquals(expected, actual);
		
		this.actual = logic.run("Flag flag");
		this.expected = String.format(MESSAGE_INVALID_COMMAND);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testTag() {
		this.actual = logic.run("tag 1 #important");
		this.expected = String.format(MESSAGE_SUCCESSFUL_TAG, "V0.5 Project Manual", "[important]");
		assertEquals(expected, actual);
		
		this.actual = logic.run("tag 1 #IMPORTant");
		this.expected = String.format(MESSAGE_UNSUCCESSFUL_TAG, "V0.5 Project Manual", "[IMPORTant]");
		assertEquals(expected, actual);
		
		this.actual = logic.run("tag");
		this.expected = String.format(MESSAGE_INVALID_COMMAND);
		assertEquals(expected, actual);
		
		this.actual = logic.run("tag -1");
		this.expected = String.format(MESSAGE_INDEXOUTOFBOUND);
		
		this.actual = logic.run("tag tag");
		this.expected = String.format(MESSAGE_INVALID_COMMAND);
		assertEquals(expected, actual);
		
		this.actual = logic.run("edit #important to #superImpt");
		this.expected = String.format(MESSAGE_SUCCESSFUL_EDIT_TAG, "important", "superImpt");
		assertEquals(expected, actual);
		
		this.actual = logic.run("delete #superImpt");
		this.expected = String.format(MESSAGE_SUCCESSFUL_DELETE_TAG, "[superImpt]");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSearch() {
		this.actual = logic.run("search manual");
		this.expected = String.format(MESSAGE_SUCCESSFUL_SEARCH_KEYWORD, "manual");
		assertEquals(expected, actual);
		
		this.actual = logic.getSearchResults().get(0).getName();
		this.expected = "V0.5 Project Manual";
		assertEquals(expected, actual);
		
		logic.run("tag 1 #important");
		this.actual = logic.run("search #important");
		this.expected = String.format(MESSAGE_SUCCESSFUL_SEARCH_TAG, "important");
		assertEquals(expected, actual);
		
		this.actual = logic.getSearchResults().get(0).getName();
		this.expected = "V0.5 Project Manual";
		assertEquals(expected, actual);
		
		this.actual = logic.run("search");
		this.expected = String.format(MESSAGE_INVALID_COMMAND);
		assertEquals(expected, actual);
		
		this.actual = logic.run("search -1");
		this.expected = String.format(MESSAGE_INVALID_SEARCH);
		assertEquals(expected, actual);
	}
}