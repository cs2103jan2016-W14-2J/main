package Parser;
import java.util.*;

public class Test {

		public static void main (String[] args) {
			
			Parser parser = new Parser("attend photoshop refresher course from tomorrow to saturday");
			System.out.println(parser.getName());
			System.out.println(parser.getStartTime());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getType());
		}
}
