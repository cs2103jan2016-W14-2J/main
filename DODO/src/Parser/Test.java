package Parser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class Test {

		public static void main (String[] args) {
		//	List<Date> dates = new PrettyTimeParser().parse("2500hrs");
		//	System.out.println(dates.get(0));
/*			Parser parser = new Parser("delete haojie");
			System.out.println(parser.getName());
			System.out.println(parser.getDeleteType());
			System.out.println(parser.getStartTime());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getType());
			System.out.println(parser.getIndexToDelete());
			
*/	//		String str = "hello";
		//	PrettyTimeParser pt = new PrettyTimeParser();
		//	List<Date> dates = pt.parse(str);

			Parser parser = new Parser("add meet hannah");
		//	Parser parser = new Parser("jog with hannah at orchard from ntu to nus at home");
		//	Parser parser = new Parser("meet hannah at nus at 7pm");
		//	Parser parser = new Parser("meet hannah at 7pm at nus");
		//	Parser parser = new Parser("meet hannah tmr 7pm");
		//	Parser parser = new Parser("meet hannah on 24/07/2016 at 7pm");
		//	Parser parser = new Parser("meet hannah on THURSDAY at 7pm");
		//	Parser parser = new Parser("meet hannah on thursday 7pm");
		//	Parser parser = new Parser("meet hannah on the ground floor at 7pm");
		//	Parser parser = new Parser("meet hannah on thursday at #100am# mall");
		//	Parser parser = new Parser("meet hannah at block 2359 on the ground floor ");
		//	Parser parser = new Parser("meet hannah at block 2359 on 24th of march 2017 ");
		//	Parser parser = new Parser("meet hannah at block 2359 on 24th of march 2017 7pm");
		//	Parser parser = new Parser("meet hannah on thursday at block 2359");
			System.out.println("command type :" + parser.getCommandType());
			System.out.println("Task Type :" + parser.getType());
			System.out.println("Task Name :" + parser.getName());
			System.out.println(parser.getStartTime());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getTag());
			System.out.println("Edit type : " + parser.getEditType());
			System.out.println(parser.getTagToDelete());
			System.out.println("Search type : " + parser.getSearchType());
			System.out.println("Oldtag : " + parser.getTag());
			System.out.println("TaskID : " + parser.getTaskID());
			System.out.println("New Directory : " + parser.getNewDirectory());
	
			
		}
}
