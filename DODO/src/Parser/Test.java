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
			
	*/		String str = "Employees review at 7pm from 05/12/16 to 06.12.16";
			PrettyTimeParser pt = new PrettyTimeParser();
			List<Date> dates = pt.parse(str);
			System.out.println(dates.size());
			System.out.println(dates.get(0));
			CommandUtils cu = new CommandUtils();
			Parser parser = new Parser();
			cu = parser.executeCommand(cu, str);
		
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
			System.out.println("Command type :" + cu.getCommandType());
			System.out.println("Task Type :" + cu.getFlagAndCompleteType());
			System.out.println("Task Type :" + cu.getType());
			System.out.println("Task Name :" + cu.getName());
			System.out.println("Start Time : " + cu.getStartTime());
			System.out.println("End Time : " +cu.getEndTime());
			System.out.println("New Tag : " +cu.getTag());
			System.out.println("Edit type : " + cu.getEditType());
			System.out.println("Tag to delete : " +cu.getTagToDelete());
			System.out.println("Search type : " + cu.getSearchType());
			System.out.println("Oldtag : " + cu.getOldTag());
			System.out.println("TaskID : " + cu.getTaskID());
			System.out.println("New Directory : " + cu.getNewDirectory());
			System.out.println("Sort Type : " + cu.getSortType());
			System.out.println("Flag and Complete Type : " + cu.getFlagAndCompleteType());
			System.out.println("Flag and Complete Indexes : " + cu.getTaskToFlagAndMark());
			System.out.println("Delete Indexes : " + cu.getIndexToDelete());
			System.out.println("Delete Type : " + cu.getDeleteType());
			System.out.println("Search Task Result : " + cu.getSearchByTask());
			System.out.println("Importance Result : " + cu.getImportance());
		}
}
