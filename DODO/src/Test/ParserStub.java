package Test;

import Command.COMMAND_TYPE;
import Parser.Parser;
import Task.TASK_TYPE;

import java.util.*;

public class ParserStub extends Parser {

	public ParserStub() {
	}

	public ParserStub(String commandType, String taskName, String tag) {
		this.command = this.determineCommandType(commandType);
		this.taskName = taskName;
		this.tag = tag;
		this.taskType = TASK_TYPE.FLOATING;
	}
}
