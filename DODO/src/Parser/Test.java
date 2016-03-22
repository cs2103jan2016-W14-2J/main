package Parser;

public class Test {

		public static void main (String[] args) {
			
			Parser parser = new Parser("collect graduation certification on the day after tml");
			System.out.println(parser.getType());
			System.out.println(parser.getEndTime());
			System.out.println(parser.getName());
		}
}
