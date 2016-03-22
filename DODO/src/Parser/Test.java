package Parser;

public class Test {

		public static void main (String[] args) {
			
			Parser parser = new Parser("buy grocery before monday");
			System.out.println(parser.getType());
			System.out.println(parser.getTaskName());
		}
}
