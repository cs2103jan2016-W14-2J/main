package Parser;
import java.util.*;

public class Test {

		public static void main (String[] args) {
			
/*			Parser parser = new Parser("read book on 27th of may 2016");
			System.out.println(parser.getName());
			System.out.println(parser.getStartTime());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getType());
*/
			Parser parser = new Parser("add <add  adasda >dinner !");
			System.out.println(parser.getImportance());
			System.out.println(parser.getTag());
			System.out.println(parser.getName());
		
		}
}
