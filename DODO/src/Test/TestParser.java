//@@author: Hao Jie

package Test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Command.*;
import Task.*;
import Parser.*;

public class TestParser {
	CommandUtils cu = new CommandUtils();
	Parser parser = new Parser();
	
	@Test
	/**
	 * Test to ensure that the user inputs has been correctly assigned 
	 * the task type.
	 * The title should be the whole user input.
	 * 
	 */
	public void testTaskType() throws Exception {
		
		cu = parser.executeCommand(cu, "drive by the beach");
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu, "submit assignment 1 by tomorrow");
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu, "take a walk by the beach by 2359hrs");
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu, "submit assignment 1 by tomorrow by 2359hrs");
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu, "fetch my brother at 2pm");
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu, "attend soc camp from 27/6/2016 to 28/6/2016");
		assertEquals(TASK_TYPE.EVENT, cu.getType());
		
		cu = parser.executeCommand(cu, "jog from school to home");
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"bake matcha cheesecake at home");
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu, "piano tuning on sunday");
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu,"build sandcastle on the road");
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu, "dancing on the dancefloor at zouk");
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
	}
	
	@Test
	public void testFloating() throws Exception{
		cu = parser.executeCommand(cu,"Drive by the beach");
		assertEquals(COMMAND_TYPE.ADD, cu.getCommandType());
		assertEquals("Drive by the beach", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"fetch my brother from school");
		assertEquals("fetch my brother from school", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"Pick Sheena from the school");
		assertEquals("Pick Sheena from the school", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"Set up PA system on the stage with Hannah");
		assertEquals("Set up PA system on the stage with Hannah", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"lie on the bed with Hannah at hotel 81");
		assertEquals("lie on the bed with Hannah at hotel 81", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"brush my teeth before bedtime");
		assertEquals("brush my teeth before bedtime", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"cycle with jun lem from sengkang to nus");
		assertEquals("cycle with jun lem from sengkang to nus", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
	
		cu = parser.executeCommand(cu,"buy slurpee from \"7/11\"");
		assertEquals("buy slurpee from \"7/11\"", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"dance on the dancefloor at zouk");
		assertEquals("dance on the dancefloor at zouk", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"Meet Carousell buyer on the ground floor at block 2359");
		assertEquals("Meet Carousell buyer on the ground floor at block 2359", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"Watch \"the day after tomorrow\"");
		assertEquals("Watch \"the day after tomorrow\"", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"Submit CS2103T Assignment 1 to Prof Henry Chia at his office");
		assertEquals("Submit CS2103T Assignment 1 to Prof Henry Chia at his office", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"Keep the dough in the dark before it gets mouldy");
		assertEquals("Keep the dough in the dark before it gets mouldy", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
		cu = parser.executeCommand(cu,"read my favourite storybook by \"June\" Tan");
		assertEquals("read my favourite storybook by \"June\" Tan", cu.getName());
		assertEquals(TASK_TYPE.FLOATING, cu.getType());
		
	}

	
	@Test
	
	public void testDeadlined() throws Exception {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		String endDate = "";
		
		/*
		 * When user enters an invalid date in the month of Feb, the program will
		 * return the difference of input date with the last day of Feb and add it
		 * to the last day of Feb.
		 * 
		 * Example: Last day of Feb 2017 is 28th Feb 2017.
		 * 			31st Feb - 28th Feb = 3 days.
		 * 			28th Feb + 3 days = 3rd March 2017.
		 */
		cu = parser.executeCommand(cu, "drive baby howard home by 31st Feb 2017");
		assertEquals(COMMAND_TYPE.ADD, cu.getCommandType());
		endDate = "Fri Mar 03 18:00:00 SGT 2017";
		Date expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getEndTime());
		assertEquals("drive baby howard home", cu.getName());
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu, "Deadline for COE bidding at 2400hrs on 27/06/2016");
		endDate = "Tue Jun 28 00:00:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getEndTime());
		assertEquals("Deadline for COE bidding", cu.getName());
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu, "watch movie on 0 feb 2017");
		endDate = "Wed Feb 01 00:00:00 SGT 2017";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getEndTime());
		assertEquals("watch movie", cu.getName());
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu, "submit assignment 1 before 1st June at 2359hrs");
		endDate = "Wed Jun 01 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getEndTime());
		assertEquals("submit assignment 1", cu.getName());
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu,"Take out my meringue cake from the oven at 13.45pm");
		endDate = "Sat Apr 09 13:45:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getEndTime());
		assertEquals("Take out my meringue cake from the oven", cu.getName());
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu,"Check in luggage at counter 7 at 23.45am");
		endDate = "Sat Apr 09 23:45:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getEndTime());
		assertEquals("Check in luggage at counter 7", cu.getName());
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
		
		cu = parser.executeCommand(cu, "meeting with boss on 27 MAY 2016 at 2pm");
		endDate = "Fri May 27 14:00:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getEndTime());
		assertEquals("meeting with boss", cu.getName());
		assertEquals(TASK_TYPE.DEADLINED, cu.getType());
	
	}

	@Test
	public void testEvent() throws Exception {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		String startDate = "";
		String endDate = "";
		Date expectedStart;
		Date expectedEnd; 
		
		cu = parser.executeCommand(cu, "Hackathon from 01.05.2016 2pm to 05.05.2016 7pm");
		assertEquals(COMMAND_TYPE.ADD, cu.getCommandType());
		startDate = "Sun May 01 14:00:00 SGT 2016";
		endDate = "Thu May 05 19:00:00 SGT 2016";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getStartTime());
		assertEquals(expectedEnd, cu.getEndTime());
		assertEquals("Hackathon", cu.getName());
		assertEquals(TASK_TYPE.EVENT, cu.getType());
		
		cu = parser.executeCommand(cu, "serve the nation from 24th Aug 2017 to 29th Oct 2017");
		startDate = "Thu Aug 24 00:00:00 SGT 2017";
		endDate = "Sun Oct 29 18:00:00 SGT 2017";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getStartTime());
		assertEquals(expectedEnd, cu.getEndTime());
		assertEquals("serve the nation", cu.getName());
		assertEquals(TASK_TYPE.EVENT, cu.getType());
		
		cu = parser.executeCommand(cu, "Dinner by the beach before sunset at Jumbo from 09/09/16 to 09.09.16");
		startDate = "Fri Sep 09 00:00:00 SGT 2016";
		endDate = "Fri Sep 09 18:00:00 SGT 2016";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getStartTime());
		assertEquals(expectedEnd, cu.getEndTime());
		assertEquals("Dinner by the beach before sunset at Jumbo", cu.getName());
		assertEquals(TASK_TYPE.EVENT, cu.getType());
		
		cu = parser.executeCommand(cu, "Employees review at 7pm from 05/12/16 to 06.12.16");
		startDate = "Mon Dec 05 19:00:00 SGT 2016";
		endDate = "Tue Dec 06 19:00:00 SGT 2016";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, cu.getStartTime());
		assertEquals(expectedEnd, cu.getEndTime());
		assertEquals("Employees review", cu.getName());
		assertEquals(TASK_TYPE.EVENT, cu.getType());

	}
	
	@Test
	public void testComplete() {
		cu = parser.executeCommand(cu, "complete 1");
		assertEquals(COMMAND_TYPE.COMPLETE, cu.getCommandType());
		assertEquals(FLAGANDCOMPLETE_TYPE.SINGLE, cu.getFlagAndCompleteType());
		
		cu = parser.executeCommand(cu,"complete 1, 6, 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.MULTIPLE, cu.getFlagAndCompleteType());
		
		cu = parser.executeCommand(cu,"complete 1 to 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.RANGE, cu.getFlagAndCompleteType());
		
		cu = parser.executeCommand(cu,"complete 1 - 100");
		assertEquals(FLAGANDCOMPLETE_TYPE.RANGE, cu.getFlagAndCompleteType());
		
		cu = parser.executeCommand(cu,"complete all");
		assertEquals(FLAGANDCOMPLETE_TYPE.ALL, cu.getFlagAndCompleteType());

	}

	@Test
	public void testFlag() {

		cu = parser.executeCommand(cu,"flag 1");
		assertEquals(COMMAND_TYPE.FLAG, cu.getCommandType());
		assertEquals(FLAGANDCOMPLETE_TYPE.SINGLE, cu.getFlagAndCompleteType());
		
		cu = parser.executeCommand(cu, "flag 1, 6, 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.MULTIPLE, cu.getFlagAndCompleteType());
		
		cu = parser.executeCommand(cu, "flag 1 to 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.RANGE, cu.getFlagAndCompleteType());
		
		cu = parser.executeCommand(cu, "flag all");
		assertEquals(FLAGANDCOMPLETE_TYPE.ALL, cu.getFlagAndCompleteType());
		
		cu = parser.executeCommand(cu,"Meet Mr Norman for Advertising Pitch !");
		assertEquals(true, cu.getImportance());
		
		cu = parser.executeCommand(cu,"I love you!!!!");
		assertEquals(false, cu.getImportance());

	}
	
	@Test
	public void testDelete() {

		cu = parser.executeCommand(cu,"delete 1");
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		Integer single_delete = 1;
		assertEquals(DELETE_TYPE.SINGLE_INDEX, cu.getDeleteType());
		assertEquals( single_delete, cu.getIndexToDelete().get(0));
		
		cu = parser.executeCommand(cu,"delete 1, 6, 9");
		Integer delete1 = 1;
		Integer delete2 = 6;
		Integer delete3 = 9;
		assertEquals(DELETE_TYPE.MULTIPLE_INDEXES, cu.getDeleteType());
		assertEquals(delete1, cu.getIndexToDelete().get(0));
		assertEquals(delete2, cu.getIndexToDelete().get(1));
		assertEquals(delete3, cu.getIndexToDelete().get(2));
		
		cu = parser.executeCommand(cu,"delete 1 to 4");
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		delete1 = 1;
		delete2 = 2;
		delete3 = 3;
		Integer delete4 = 4;
		assertEquals(DELETE_TYPE.RANGE_INDEXES, cu.getDeleteType());
		assertEquals(delete1, cu.getIndexToDelete().get(0));
		assertEquals(delete2, cu.getIndexToDelete().get(1));
		assertEquals(delete3, cu.getIndexToDelete().get(2));
		assertEquals(delete4, cu.getIndexToDelete().get(3));
		
		cu = parser.executeCommand(cu,"delete all");
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		assertEquals(DELETE_TYPE.ALL_INDEXES, cu.getDeleteType());
		
		cu = parser.executeCommand(cu,"delete all tags");
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		assertEquals(DELETE_TYPE.ALL_TAGS, cu.getDeleteType());
		
		cu = parser.executeCommand(cu,"delete #nus");
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		String single_tag = "nus";
		assertEquals(DELETE_TYPE.SINGLE_TAG, cu.getDeleteType());
		assertEquals( single_tag, cu.getTagToDelete().get(0));

		cu = parser.executeCommand(cu,"delete #nus #singapore #SoC");
		String tag1 = "nus";
		String tag2 = "singapore";
		String tag3 = "SoC";
		assertEquals(DELETE_TYPE.MULTIPLE_TAGS, cu.getDeleteType());
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		assertEquals(tag1, cu.getTagToDelete().get(0));
		assertEquals(tag2, cu.getTagToDelete().get(1));
		assertEquals(tag3, cu.getTagToDelete().get(2));
		
		cu = parser.executeCommand(cu,"delete #11-11");
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		assertEquals(DELETE_TYPE.INVALID, cu.getDeleteType());
		
		cu = parser.executeCommand(cu,"delete #hello #11-11 #adele ");
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		assertEquals(DELETE_TYPE.MULTIPLE_TAGS, cu.getDeleteType());
		assertEquals("hello", cu.getTagToDelete().get(0));
		assertEquals("adele", cu.getTagToDelete().get(1));
		
		cu = parser.executeCommand(cu,"delete 1 start date");
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		assertEquals(DELETE_TYPE.START_DATE, cu.getDeleteType());
		
		cu = parser.executeCommand(cu,"delete 10 end date");
		assertEquals(COMMAND_TYPE.DELETE, cu.getCommandType());
		assertEquals(DELETE_TYPE.END_DATE, cu.getDeleteType());
		
		
	}
	
	@Test
	public void testEdit() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		
		cu = parser.executeCommand(cu, "edit 2 meet Hannah at Chong Pang");
		assertEquals(COMMAND_TYPE.EDIT, cu.getCommandType());
		int taskID = 2;
		assertEquals(EDIT_TYPE.TASK_NAME, cu.getEditType());
		assertEquals("meet Hannah at Chong Pang", cu.getName());
		assertEquals(taskID, cu.getTaskID());
		
		cu = parser.executeCommand(cu,"edit 4 from 5th May 2016 2359hrs");
		assertEquals(COMMAND_TYPE.EDIT, cu.getCommandType());
		taskID = 4;
		String str = "Thu May 05 23:59:00 SGT 2016";
		Date startTime = dateFormat.parse(str);
		assertEquals(EDIT_TYPE.START_TIME, cu.getEditType());
		assertEquals(taskID, cu.getTaskID());
		assertEquals(startTime, cu.getStartTime());
		
		cu = parser.executeCommand(cu, "edit 2 to 5th Nov 2016 2359hrs");
		assertEquals(COMMAND_TYPE.EDIT, cu.getCommandType());
		taskID = 2;
		str = "Sat Nov 05 23:59:00 SGT 2016";
		Date endTime = dateFormat.parse(str);
		assertEquals(EDIT_TYPE.END_TIME, cu.getEditType());
		assertEquals(taskID, cu.getTaskID());
		assertEquals(endTime, cu.getEndTime());
		
		cu = parser.executeCommand(cu, "edit 4 by 5th May 2016 2359hrs");
		assertEquals(COMMAND_TYPE.EDIT, cu.getCommandType());
		taskID = 4;
		str = "Thu May 05 23:59:00 SGT 2016";
		endTime = dateFormat.parse(str);
		assertEquals(EDIT_TYPE.DEADLINED, cu.getEditType());
		assertEquals(taskID, cu.getTaskID());
		assertEquals(startTime, cu.getEndTime());
		
		cu = parser.executeCommand(cu, "edit 10 from 5th Nov 2016 2359hrs to 12th Dec 2016");
		assertEquals(COMMAND_TYPE.EDIT, cu.getCommandType());
		taskID = 10;
		str = "Sat Nov 05 23:59:00 SGT 2016";
		startTime = dateFormat.parse(str);
		String str2 = "Mon Dec 12 23:59:00 SGT 2016";
		endTime = dateFormat.parse(str2);
		assertEquals(EDIT_TYPE.EVENT_TIME, cu.getEditType());
		assertEquals(taskID, cu.getTaskID());
		assertEquals(startTime, cu.getStartTime());
		assertEquals(endTime, cu.getEndTime());
		
		cu = parser.executeCommand(cu, "edit #Singapore to #Melbourne");
		assertEquals(COMMAND_TYPE.EDIT, cu.getCommandType());
		String oldTag = "Singapore";
		String newTag = "Melbourne";
		assertEquals(EDIT_TYPE.TAG, cu.getEditType());
		assertEquals(oldTag, cu.getOldTag());
		assertEquals(newTag, cu.getTag().get(0));
	
	}
	
	@Test
	public void testSearch() throws Exception {

		cu = parser.executeCommand(cu, "search #Assignment");
		assertEquals(COMMAND_TYPE.SEARCH, cu.getCommandType());
		assertEquals(SEARCH_TYPE.BY_TAG, cu.getSearchType());
		assertEquals("Assignment", cu.getSearchByTag());
		
		cu = parser.executeCommand(cu, "search CS2103T");
		assertEquals(COMMAND_TYPE.SEARCH, cu.getCommandType());
		assertEquals(SEARCH_TYPE.BY_TASK, cu.getSearchType());
		assertEquals("CS2103T", cu.getSearchByTask());
		
		cu = parser.executeCommand(cu, "search 17/07/2016");
		assertEquals(COMMAND_TYPE.SEARCH, cu.getCommandType());
		assertEquals(SEARCH_TYPE.BY_DATE, cu.getSearchType());
	}
	
	@Test
	public void testSort() throws Exception {

		cu = parser.executeCommand(cu, "sort by abc");
		assertEquals(COMMAND_TYPE.SORT, cu.getCommandType());
		assertEquals(SORT_TYPE.BY_ASCENDING, cu.getSortType());
		
		cu = parser.executeCommand(cu, "sort by ABC");
		assertEquals(COMMAND_TYPE.SORT, cu.getCommandType());
		assertEquals(SORT_TYPE.BY_ASCENDING, cu.getSortType());
		
		cu = parser.executeCommand(cu, "sort by cba");
		assertEquals(COMMAND_TYPE.SORT, cu.getCommandType());
		assertEquals(SORT_TYPE.BY_DESCENDING, cu.getSortType());
		
		cu = parser.executeCommand(cu, "sort by CBA");
		assertEquals(COMMAND_TYPE.SORT, cu.getCommandType());
		assertEquals(SORT_TYPE.BY_DESCENDING, cu.getSortType());
		
		cu = parser.executeCommand(cu, "sort by date");
		assertEquals(COMMAND_TYPE.SORT, cu.getCommandType());
		assertEquals(SORT_TYPE.BY_DATE, cu.getSortType());
		
		cu = parser.executeCommand(cu, "sort by 123");
		assertEquals(COMMAND_TYPE.SORT, cu.getCommandType());
		assertEquals(SORT_TYPE.BY_ASCENDING, cu.getSortType());
		
		cu = parser.executeCommand(cu, "sort by 321");
		assertEquals(COMMAND_TYPE.SORT, cu.getCommandType());
		assertEquals(SORT_TYPE.BY_DESCENDING, cu.getSortType());
	}
	
	@Test
	public void testTag() throws Exception {
		int index = 1;
		cu = parser.executeCommand(cu, "tag 1 #HomeAlone");
		assertEquals(COMMAND_TYPE.TAG, cu.getCommandType());
		assertEquals(index, cu.getTaskID());
		assertEquals("HomeAlone", cu.getTag().get(0));
		
		cu = parser.executeCommand(cu, "tag 123 #This #is #HOME #truLy #SG50");
		index = 123;
		assertEquals(COMMAND_TYPE.TAG, cu.getCommandType());
		assertEquals(index, cu.getTaskID());
		assertEquals("This", cu.getTag().get(0));
		assertEquals("is", cu.getTag().get(1));
		assertEquals("HOME", cu.getTag().get(2));
		assertEquals("truLy", cu.getTag().get(3));
		assertEquals("SG50", cu.getTag().get(4));
		
		cu = parser.executeCommand(cu, "Procurement meeting at seminar room #Logistic #Planning");
		assertEquals(COMMAND_TYPE.ADD, cu.getCommandType());
		assertEquals("Procurement meeting at seminar room", cu.getName());
		assertEquals("Logistic", cu.getTag().get(0));
		assertEquals("Planning", cu.getTag().get(1));
		
		cu = parser.executeCommand(cu, "#dance #crewz Final rehearsal");
		assertEquals(COMMAND_TYPE.ADD, cu.getCommandType());
		assertEquals("Final rehearsal", cu.getName());
		assertEquals("dance", cu.getTag().get(0));
		assertEquals("crewz", cu.getTag().get(1));
		
		cu = parser.executeCommand(cu, "distribute 1000 flyers #Work to block 2359 #10-11");
		assertEquals(COMMAND_TYPE.ADD, cu.getCommandType());
		assertEquals("distribute 1000 flyers to block 2359 #10-11", cu.getName());
		assertEquals("Work", cu.getTag().get(0));
		
	}
	
	@Test
	public void testCommands() throws Exception {
		cu = parser.executeCommand(cu, "undo");
		assertEquals(COMMAND_TYPE.UNDO, cu.getCommandType());
		
		cu = parser.executeCommand(cu, "redo");
		assertEquals(COMMAND_TYPE.REDO, cu.getCommandType());
		
		cu = parser.executeCommand(cu, "RedIrEct");
		assertEquals(COMMAND_TYPE.CHANGE_DIRECTORY, cu.getCommandType());
		
		cu = parser.executeCommand(cu, "EXIT");
		assertEquals(COMMAND_TYPE.EXIT, cu.getCommandType());
		
		cu = parser.executeCommand(cu, "helP");
		assertEquals(COMMAND_TYPE.HELP, cu.getCommandType());
	}
}
