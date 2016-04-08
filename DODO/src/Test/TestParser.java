//@@author: Hao Jie

package Test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Date;

import Command.*;
import Task.*;
import Parser.*;

public class TestParser {

	@Test
	public void testTaskType() throws Exception {
		
		CommandUtils cu = new CommandUtils();
		Parser parser = new Parser();
		
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
		CommandUtils cu = new CommandUtils();
		Parser parser = new Parser();
		
		cu = parser.executeCommand(cu,"Drive by the beach");
		assertEquals("Drive by the beach", cu.getName());
		
		cu = parser.executeCommand(cu,"fetch my brother from school");
		assertEquals("fetch my brother from school", cu.getName());
		
		cu = parser.executeCommand(cu,"Pick Sheena from the school");
		assertEquals("Pick Sheena from the school", cu.getName());
		
		cu = parser.executeCommand(cu,"lie on the bed with Hannah");
		assertEquals("lie on the bed with Hannah", cu.getName());
		
		cu = parser.executeCommand(cu,"lie on the bed with Hannah at hotel 81");
		assertEquals("lie on the bed with Hannah at hotel 81", cu.getName());
		
		cu = parser.executeCommand(cu,"brush my teeth before bedtime");
		assertEquals("brush my teeth before bedtime", cu.getName());
		
		cu = parser.executeCommand(cu,"cycle with jun lem from sengkang to nus");
		assertEquals("cycle with jun lem from sengkang to nus", cu.getName());
	
		cu = parser.executeCommand(cu,"buy slurpee from |7/11|");
		assertEquals("buy slurpee from |7/11|", cu.getName());
		
		cu = parser.executeCommand(cu,"dance on the dancefloor at zouk");
		assertEquals("dance on the dancefloor at zouk", cu.getName());
		
		cu = parser.executeCommand(cu,"Meet Carousell buyer on the ground floor at block 2359");
		assertEquals("Meet Carousell buyer on the ground floor at block 2359", cu.getName());
		
		cu = parser.executeCommand(cu,"Watch |the day after tomorrow|");
		assertEquals("Watch |the day after tomorrow|", cu.getName());
		
	}

/*	
	@Test
	public void testDeadlined() throws Exception {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		Parser parser = new Parser("drive baby howard home by tomorrow");
		String endDate = "Thu Mar 31 23:59:59 SGT 2016";
		Date expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("watch movie at 2400hrs");
		endDate = "";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("watch movie on 0 feb 2017");
		endDate = "Tue Jan 31 23:59:00 SGT 2017";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("submit assignment 1 by thursday");
		endDate = "Thu Mar 24 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("submit assignment 1 by 27.5 feb 2017");
		endDate = "Fri May 27 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("meeting with boss on 27 march 2016 at 2pm");
		endDate = "Sun Mar 27 14:00:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("fetch mum from airport on this coming friday");
		endDate = "Fri Mar 25 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
	
		parser = new Parser("collect graduation certification on the day after tml");
		endDate = "Fri Mar 25 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("meet girlfriend on 29/03/16");
		endDate = "Tue Mar 29 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
		parser = new Parser("tuition at 2:35pm");
		endDate = "Wed Mar 23 14:35:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
		
	
		parser = new Parser("watch movie tomorrow at 5pm");
		endDate = "Thu Mar 24 17:00:00 SGT 2016";
		expectedStart = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getEndTime());
	
	
	}

	@Test
	public void testEvent() throws Exception {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		Parser parser = new Parser("study cs2103t from 01.04.2016 2pm to 05.04.2016");
		String endDate = "Tue Apr 05 14:00:00 SGT 2016";
		String startDate = "Fri Apr 01 14:00:00 SGT 2016";
		Date expectedStart = dateFormat.parse(startDate);
		Date expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getStartTime());
		assertEquals(expectedEnd, parser.getEndTime());
		
		parser = new Parser("serve the nation from 1 jan to 4 feb");
		startDate = "Sun Jan 01 23:59:00 SGT 2017";
		endDate = "Sat Feb 04 23:59:00 SGT 2017";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getStartTime());
		assertEquals(expectedEnd, parser.getEndTime());
		
		parser = new Parser("soc sports camp from 20 march 2016 to 22 march 2016");
		endDate = "Wed Mar 22 23:59:00 SGT 2017";
		startDate = "Mon Mar 20 23:59:00 SGT 2017";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getStartTime());
		assertEquals(expectedEnd, parser.getEndTime());
		
		parser = new Parser("attend photoshop refresher course from tomorrow to saturday");
		endDate = "Sat Mar 26 23:59:00 SGT 2016";
		startDate = "Thu Mar 24 23:59:00 SGT 2016";
		expectedStart = dateFormat.parse(startDate);
		expectedEnd = dateFormat.parse(endDate);
		assertEquals(expectedStart, parser.getStartTime());
		assertEquals(expectedEnd, parser.getEndTime());
	
	}
	
	@Test
	public void testComplete() {
		Parser parser = new Parser("complete 1");
		assertEquals(FLAGANDCOMPLETE_TYPE.SINGLE, parser.getFlagAndCompleteType());
		
		parser = new Parser("complete 1, 6, 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.MULTIPLE, parser.getFlagAndCompleteType());
		
		parser = new Parser("complete 1 to 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.RANGE, parser.getFlagAndCompleteType());
		
		parser = new Parser("complete all");
		assertEquals(FLAGANDCOMPLETE_TYPE.ALL, parser.getFlagAndCompleteType());

	}

	@Test
	public void testFlag() {
		Parser parser = new Parser("flag 1");
		assertEquals(FLAGANDCOMPLETE_TYPE.SINGLE, parser.getFlagAndCompleteType());
		
		parser = new Parser("flag 1, 6, 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.MULTIPLE, parser.getFlagAndCompleteType());
		
		parser = new Parser("flag 1 to 9");
		assertEquals(FLAGANDCOMPLETE_TYPE.RANGE, parser.getFlagAndCompleteType());
		
		parser = new Parser("flag all");
		assertEquals(FLAGANDCOMPLETE_TYPE.ALL, parser.getFlagAndCompleteType());
		

	}
	
	@Test
	public void testDelete() {
		Parser parser = new Parser("delete 1");
		Integer single_delete = 1;
		assertEquals(DELETE_TYPE.SINGLE_INDEX, parser.getDeleteType());
		assertEquals( single_delete, parser.getIndexToDelete().get(0));
		
		parser = new Parser("delete 1, 6, 9");
		Integer delete1 = 1;
		Integer delete2 = 6;
		Integer delete3 = 9;
		assertEquals(DELETE_TYPE.MULTIPLE_INDEXES, parser.getDeleteType());
		assertEquals(delete1, parser.getIndexToDelete().get(0));
		assertEquals(delete2, parser.getIndexToDelete().get(1));
		assertEquals(delete3, parser.getIndexToDelete().get(2));
		
		parser = new Parser("delete 1 to 4");
		delete1 = 1;
		delete2 = 2;
		delete3 = 3;
		Integer delete4 = 4;
		assertEquals(DELETE_TYPE.RANGE_INDEXES, parser.getDeleteType());
		assertEquals(delete1, parser.getIndexToDelete().get(0));
		assertEquals(delete2, parser.getIndexToDelete().get(1));
		assertEquals(delete3, parser.getIndexToDelete().get(2));
		assertEquals(delete4, parser.getIndexToDelete().get(3));
		
		parser = new Parser("delete all");
		assertEquals(DELETE_TYPE.ALL_INDEXES, parser.getDeleteType());
		
		parser = new Parser("delete all tags");
		assertEquals(DELETE_TYPE.ALL_TAGS, parser.getDeleteType());
		
		parser = new Parser("delete #nus");
		String single_tag = "nus";
		assertEquals(DELETE_TYPE.SINGLE_TAG, parser.getDeleteType());
		assertEquals( single_tag, parser.getTagToDelete().get(0));

		parser = new Parser("delete #nus #singapore #SoC");
		String tag1 = "nus";
		String tag2 = "singapore";
		String tag3 = "SoC";
		assertEquals(DELETE_TYPE.MULTIPLE_TAGS, parser.getDeleteType());
		assertEquals(tag1, parser.getTagToDelete().get(0));
		assertEquals(tag2, parser.getTagToDelete().get(1));
		assertEquals(tag3, parser.getTagToDelete().get(2));
		
	}
	
	@Test
	public void testEdit() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		
		Parser parser = new Parser("edit 2 meet Hannah at Chong Pang");
		int taskID = 2;
		assertEquals(EDIT_TYPE.TASK_NAME, parser.getEditType());
		assertEquals("meet Hannah at Chong Pang", parser.getName());
		assertEquals(taskID, parser.getTaskID());
		
		parser = new Parser("edit 4 from 5th of May 2016 2359hrs");
		taskID = 4;
		String str = "Thu May 05 23:59:00 SGT 2016";
		Date startTime = dateFormat.parse(str);
		assertEquals(EDIT_TYPE.START_TIME, parser.getEditType());
		assertEquals(taskID, parser.getTaskID());
		assertEquals(startTime, parser.getStartTime());
		
		parser = new Parser("edit 2 to 5th of Nov 2016 2359hrs");
		taskID = 2;
		str = "Sat Nov 05 23:59:00 SGT 2016";
		Date endTime = dateFormat.parse(str);
		assertEquals(EDIT_TYPE.END_TIME, parser.getEditType());
		assertEquals(taskID, parser.getTaskID());
		assertEquals(endTime, parser.getEndTime());
		
		parser = new Parser("edit 4 by 5th of May 2016 2359hrs");
		taskID = 4;
		str = "Thu May 05 23:59:00 SGT 2016";
		endTime = dateFormat.parse(str);
		assertEquals(EDIT_TYPE.DEADLINED, parser.getEditType());
		assertEquals(taskID, parser.getTaskID());
		assertEquals(startTime, parser.getEndTime());
		
		parser = new Parser("edit 10 from 5th of Nov 2016 2359hrs to 12th Dec 2016");
		taskID = 10;
		str = "Sat Nov 05 23:59:00 SGT 2016";
		startTime = dateFormat.parse(str);
		String str2 = "Mon Dec 12 23:59:00 SGT 2016";
		endTime = dateFormat.parse(str2);
		assertEquals(EDIT_TYPE.EVENT_TIME, parser.getEditType());
		assertEquals(taskID, parser.getTaskID());
		assertEquals(startTime, parser.getStartTime());
	//	assertEquals(endTime, parser.getEndTime());
		
		parser = new Parser("edit #Singapore to #Melbourne");
		String oldTag = "Singapore";
		String newTag = "Melbourne";
		assertEquals(EDIT_TYPE.TAG, parser.getEditType());
		assertEquals(oldTag, parser.getOldTag());
		assertEquals(newTag, parser.getTag().get(0));
	
	}*/
}
