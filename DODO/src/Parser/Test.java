package Parser;

import Command.*;
import java.util.Date;
import Parser.*;
import Task.*;
import dodo.Parser;

public class Test {
	
	private static Date newDate;
	
	public static void main (String[] args) {
/*		String input = "add babysit baby claudia from today to tomorrow";
	
		Parser parser = new Parser(input);
		String taskName = parser.getTaskName();
		System.out.println("final result " + taskName);
*/
		
		String input = "edit 1 from 25/07/2016";
		
		Parser parser = new Parser(input);
		newDate = parser.getStartTime();
		System.out.println("final result " + newDate);
	}
}
