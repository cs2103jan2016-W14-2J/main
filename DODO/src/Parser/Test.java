package Parser;

public class Test {

		public static void main (String[] args) {
			
			Parser parser = new Parser("fetch my brother from 7/11");
			System.out.println(parser.getType());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getName());
		}
}
