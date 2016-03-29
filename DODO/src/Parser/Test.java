package Parser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class Test {

		private static ArrayList<SimpleDateFormat> dateFormats;

		public static void main (String[] args) {
	//		List<Date> dates = new PrettyTimeParser().parse("16/10/18");
	//		System.out.println(dates.get(0));
			Parser parser = new Parser("read book from 26/05/2016 to 26.05.17");
			System.out.println(parser.getName());
			System.out.println(parser.getStartTime());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getType());


/*
			Parser parser = new Parser("meeting with hannah from 2016/05/28 7pm to 2016/05/30 9am");
			
			System.out.println("command type :" + parser.getCommandType());
			System.out.println("Task Type :" + parser.getType());
			System.out.println(parser.getName());
			System.out.println(parser.getStartTime());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getTag());
			System.out.println(parser.getEditType());
*/
	
			
		}
}
