package Storage;

/* @@author: Lu Yang */

import Command.*;
import GUI.*;
import Logic.*;
import Parser.*;
import Task.*;

import java.io.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Storage {
	private static final String FILENAME_ONGOING_TASKS = "OngoingTasks.txt";
	private static final String FILENAME_COMPLETED_TASKS = "CompletedTasks.txt";
	private static final String FILENAME_FLOATING_TASKS = "FloatingTasks.txt";
	private static final String FILENAME_OVERDUE_TASKS = "OverdueTasks.txt";

	private static final String EMPTY_STRING = "";

	PrintWriter pw;
	BufferedReader br;
	String ongoingDirectory;
	String completedDirectory;
	String floatingDirectory;
	String overdueDirectory;

	public Storage(String directory) {
		// it cannot create a file in a specific directory for now
		// it only creates a file in the same directory as the programme
		ongoingDirectory = directory  + FILENAME_ONGOING_TASKS;
		completedDirectory = directory + FILENAME_COMPLETED_TASKS;
		floatingDirectory = directory + FILENAME_FLOATING_TASKS;
		overdueDirectory = directory + FILENAME_OVERDUE_TASKS;
		if (!fileExists(ongoingDirectory)) initialiseFile(ongoingDirectory);
		if (!fileExists(completedDirectory)) initialiseFile(completedDirectory);
		if (!fileExists(floatingDirectory)) initialiseFile(floatingDirectory);
		if (!fileExists(overdueDirectory)) initialiseFile(overdueDirectory);
	}
	
	private boolean fileExists(String directory) {
		File file = new File(directory);
		if (file.exists()) return true;
		else return false; 
	}

	public ArrayList<Task> read(TASK_STATUS task_status) {
		switch (task_status) {
		case ONGOING:
			return readFromFile(ongoingDirectory);
		case COMPLETED:
			return readFromFile(completedDirectory);
		case FLOATING:
			return readFromFile(floatingDirectory);
		case OVERDUE:
			return readFromFile(overdueDirectory);
		default:
			return null;
		}
	}

	public String save(TASK_STATUS task_status, ArrayList<Task> tasks) {
		switch (task_status) {
		case ONGOING:
			return printToFile(ongoingDirectory, tasks);
		case COMPLETED:
			return printToFile(completedDirectory, tasks);
		case FLOATING:
			return printToFile(floatingDirectory, tasks);
		case OVERDUE:
			return printToFile(overdueDirectory, tasks);
		default:
			return "Invalid type of tasks.";
		}
	}

	private String printToFile(String filename, ArrayList<Task> tasks)  {
		clearFile(filename);
		String output = "";
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(tasks, writer);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "Saving " + filename + " is successful. :)";
	}

	private ArrayList<Task> readFromFile(String filename) {
		System.out.println("[DEBUG] directory :" + filename);
		ArrayList<Task> tasks = new ArrayList<Task>();
		try {
			br = new BufferedReader(new FileReader(filename));
			Gson gson = new GsonBuilder().create();
			tasks = gson.fromJson(br, new TypeToken<ArrayList<Task>>() {
			}.getType());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[DEBUG/Storage.java] readFromFile: " + filename);
		if (tasks==null) {
			System.out.println("[DEBUG/Storage] null");
			return new ArrayList<Task>();
		}
		return tasks;
	}

	private void initialiseFile(String filename) {
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
			pw.print("");
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void clearFile(String filename) {
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, false)));
			pw.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
