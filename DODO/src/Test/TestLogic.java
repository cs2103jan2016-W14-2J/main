package Test;

import java.util.*;
import java.io.*;
import Parser.*;
import Command.*;
import Logic.Logic;
import Test.ParserStub;

public class TestLogic /*extends Logic*/ {
	public static void main(String args[]) {
		System.out.println("hello world");
		String filename = args[0];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String str = reader.readLine();
				while (str!=null && !str.equals("")) {
/*					String[] inputs = str.split(" ");
					String typeStr = inputs[0];
					String name = inputs[1];
					String tag = inputs[2];*/
					Parser parser = new Parser(str);
					TestLogic test = new TestLogic();
/*					System.out.println(test.processCommand(parser));
*/					str = reader.readLine();
				}
			reader.close();
		}
		catch (IOException e) {
			//file has not been created
			e.printStackTrace();
		}
	}
	
	public TestLogic() {
		super();
	}
	
	
}
