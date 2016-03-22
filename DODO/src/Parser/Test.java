package Parser;

public class Test {

		public static void main (String[] args) {
			
			Parser parser = new Parser("watch movie tomorrow");
			System.out.println(parser.getType());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getName());
		}
}
