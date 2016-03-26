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
			Parser parser = new Parser("complete 1-10");
			ArrayList<Integer> arr = new ArrayList<Integer>(parser.getTaskToFlagAndMark());
			for (int i = 0; i < arr.size(); i++) {
				System.out.println(arr.get(i));
			}
			System.out.println(parser.getFlagAndCompleteType());
	//		System.out.println(parser.getin);
			System.out.println(parser.getName());
		
		}
}
