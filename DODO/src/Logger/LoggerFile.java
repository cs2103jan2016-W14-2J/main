// @@author A0125552L
package Logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFile {
	
	// Constants
	private static final String DODO_LOG_FILE = "DoDo.log";
	private static final String LOGGER_NAME = "logFile";
	private static final Level LVL_LOG_CONSOLE_LVL = Level.INFO;
	private static final Level LVL_LOG_LVL = Level.ALL;
	
	// Initialization
	private static FileHandler fileHandler;
	private static ConsoleHandler consoleHandler;
	private static LoggerFile instance = null;
	private static Logger logger;

	public static Logger getLogger() {
		
		if(instance == null) {
			instance = new LoggerFile();
		}
		
		return logger;
	}
	
	private LoggerFile() {
		
		try {	
			setUpLogger();
			setUpFileHandler();
			setUpConsoleHandler();
		}
		catch(SecurityException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setUpLogger() {
		logger = Logger.getLogger(LOGGER_NAME);
		logger.setLevel(LVL_LOG_LVL);
		logger.setUseParentHandlers(false);
	}
	
	private void setUpFileHandler() throws IOException {
		fileHandler = new FileHandler(DODO_LOG_FILE);
		logger.addHandler(fileHandler);
	}
	
	private void setUpConsoleHandler() {
		consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(LVL_LOG_CONSOLE_LVL);
		logger.addHandler(consoleHandler);
	}
}
