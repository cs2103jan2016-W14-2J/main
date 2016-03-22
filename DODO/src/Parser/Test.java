package Parser;

public class Test {

		public static void main (String[] args) {
			
			Parser parser = new Parser("drive from sengkang to yishun");
			System.out.println(parser.getType());
			System.out.println(parser.getTaskName());
		}
}
