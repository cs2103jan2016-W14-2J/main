package Test;
import static org.junit.Assert.*;

// @@author: Lu Yang
import org.junit.Before;
import org.junit.Test;
import Logic.*;
import Storage.*;
import Parser.*;
import Command.*;
import GUI.UI_TAB;

public class SystemTest {
	Logic logic;
	String actual;
	String expected;

	@Before 
	public void setUp() { 
		this.logic = Logic.getInstance();
	}

	@Test
	public void testAdd() {
		// add a floating task
		this.actual = logic.run("V0.5 Project Manual");
		this.expected = "Task \"V0.5 Project Manual\" is added to FLOATING.";
		assertEquals(expected, actual);
		
		// add a deadlined task
		this.actual = logic.run("V0.5 Project Manual by tomorrow");
		this.expected = "Task \"V0.5 Project Manual\" is added to ONGOING.";
		assertEquals(expected, actual);
		
		// add an overdue task
		this.actual = logic.run("V0.5 Project Manual by yesterday");
		this.expected = "Task \"V0.5 Project Manual\" is added to OVERDUE.";
		assertEquals(expected, actual);

		this.actual = logic.run("");
		this.expected = "Please enter a valid command.";
		assertEquals(expected, actual);

		this.actual = logic.run("");
		this.expected = "Please enter a valid command.";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEdit() {
		// edit task name
		this.actual = logic.run("Edit 2 V0.4 Project Manaul");
		this.expected = "Task name has been successfully edited to \"V0.4 Project Manaul\".";
		assertEquals(expected, actual);
		
		// edit task deadlined
		this.actual = logic.run("Edit 1 by next Monday.");
		assertEquals(expected, actual);
	}

}
