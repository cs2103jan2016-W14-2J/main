package Command;

import GUI.*;
import Logic.*;
import Parser.*;
import Storage.*;
import Task.*;

public abstract class Command {
	protected COMMAND_TYPE command_type;
	protected Parser parser;
	protected Logic logic;
	
	public Command(Parser parser, Logic logic, COMMAND_TYPE command_type) {
		this.parser = parser;
		this.logic = logic;
		this.command_type = command_type;
	}
	
	public COMMAND_TYPE getCommandType() {
		return this.command_type;
	}
	
	public abstract String execute();
	
	public abstract String undo();
}
