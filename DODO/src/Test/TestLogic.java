package Test;

import java.util.*;
import java.io.*;
import Parser.*;
import Command.*;
import Logic.Logic;
import Test.ParserStub;

public class TestLogic extends Logic {

	public TestLogic(String directory) {
		super(directory);
	}
	
	public static void main(String args[]) {
		String filename = args[0];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String str = reader.readLine();
				while (str!=null && !str.equals("")) {
					str = reader.readLine();
					String[] inputs = str.split(",");
					String typeStr = inputs[0];
					String name = inputs[1];
					String tag = inputs[2];
					ParserStub parser = new ParserStub(typeStr, name, tag);
					String directory = "Dummy";
					TestLogic test = new TestLogic(directory);
					System.out.println(test.processCommand(parser));
				}
			reader.close();
		}
		catch (IOException e) {
			//file has not been created
			System.out.println();
		}
	}
}
