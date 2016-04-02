package Test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Command.*;
import Task.*;
import Parser.*;

/*
 * 
 * 
 * @author: Hao Jie
 */

public class TestParser {
/*
	@Test
	public void testTaskType() throws Exception {
		
		
		Parser parser = new Parser("drive by the beach");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
		parser = new Parser("submit assignment 1 by tomorrow");
		assertEquals(TASK_TYPE.DEADLINED, parser.getType());
		
		parser = new Parser("fetch my brother at 2pm");
		assertEquals(TASK_TYPE.DEADLINED, parser.getType());
		
		parser = new Parser("attend soc camp from 27/6/2016 to 28/6/2016");
		assertEquals(TASK_TYPE.EVENT, parser.getType());
		
		parser = new Parser("jog from school to home");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
		parser = new Parser("bake matcha cheesecake at home");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
		parser = new Parser("piano tuning on sunday");
		assertEquals(TASK_TYPE.DEADLINED, parser.getType());
		
		parser = new Parser("build sandcastle on the road");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
		parser = new Parser("dancing on the dancefloor at zouk");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
	}
	
	@Test
	public void testFloating() throws Exception{
		
		Parser parser = new Parser("drive by the beach");
		assertEquals("drive by the beach", parser.getName());
		
		parser = new Parser("fetch my brother from school");
		assertEquals("fetch my brother from school", parser.getName());
		
		parser = new Parser("meet sheena at the school");
		assertEquals("meet sheena at the school", parser.getName());
		
		parser = new Parser("lie on the bed with hannah");
		assertEquals("lie on the bed with hannah", parser.getName());
		
		parser = new Parser("lie on the bed with hannah at hotel 81");
		assertEquals("lie on the bed with hannah at hotel 81", parser.getName());
		
		parser = new Parser("brush my teeth before bedtime");
		assertEquals("brush my teeth before bedtime", parser.getName());
		
		parser = new Parser("brush my teeth before bedtime");
		assertEquals("brush my teeth before bedtime", parser.getName());
		
		parser = new Parser("cycle with jun lem from sengkang to nus");
		assertEquals("cycle with jun lem from sengkang to nus", parser.getName());
	
		parser = new Parser("buy slurpee from #7/11#");
		assertEquals("buy slurpee from #7/11#", parser.getName());
		
		parser = new Parser("dance on the dancefloor at zouk");
		assertEquals("dance on the dancefloor at zouk", parser.getName());
		
	}
	
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
	*/
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
}
