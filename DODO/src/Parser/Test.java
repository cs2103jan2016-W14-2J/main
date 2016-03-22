package Parser;

public class Test {

		public static void main (String[] args) {
			
			Parser parser = new Parser("dance on the dancefloor at zouk");
			System.out.println(parser.getType());
			System.out.println(parser.getTaskName());
		}
}
