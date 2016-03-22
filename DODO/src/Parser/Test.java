package Parser;

public class Test {

		public static void main (String[] args) {
			
			Parser parser = new Parser("soc sports camp from tomorrow to saturday");
			System.out.println(parser.getType());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getStartTime());
			System.out.println(parser.getName());
		}
}
