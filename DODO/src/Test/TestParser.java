package Test;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

	@Test
	public void testTaskType() {
		
		Parser parser = new Parser("drive by the beach");
		assertEquals(TASK_TYPE.FLOATING, parser.getType());
		
		parser = new Parser("fetch my brother at 2pm");
		assertEquals(TASK_TYPE.DEADLINED, parser.getType());
		
		
	}
	
	@Test
	public void testFloating() {
		
		Parser parser = new Parser("drive by the beach");
		assertEquals("drive by the beach", parser.getTaskName());
		
		parser = new Parser("fetch my brother from school");
		assertEquals("fetch my brother from school", parser.getTaskName());
		
		parser = new Parser("meet sheena at the school");
		assertEquals("meet sheena at the school", parser.getTaskName());
		
		parser = new Parser("lie on the bed with hannah");
		assertEquals("lie on the bed with hannah", parser.getTaskName());
		
		parser = new Parser("lie on the bed with hannah at hotel 81");
		assertEquals("lie on the bed with hannah at hotel 81", parser.getTaskName());
		
		parser = new Parser("brush my teeth before bedtime");
		assertEquals("brush my teeth before bedtime", parser.getTaskName());
		
		parser = new Parser("brush my teeth before bedtime");
		assertEquals("brush my teeth before bedtime", parser.getTaskName());
		
		parser = new Parser("cycle with jun lem from sengkang to nus");
		assertEquals("cycle with jun lem from sengkang to nus", parser.getTaskName());
		
		parser = new Parser("buy slurpee from 7/11");
		assertEquals("buy slurpee from 7/11", parser.getTaskName());
		
		
	}

}
