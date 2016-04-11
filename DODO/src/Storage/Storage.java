package Storage;

/* @@author: Lu Yang */

import Command.*;
import GUI.*;
import Logic.*;
import Parser.*;
import Task.*;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class Storage {
	private static final String MESSAGE_INVALID_TASK_STATUS = "Task status \"%1$s\" is invalid.";
	private static final String MESSAGE_SUCCESSFUL_FILE_SAVE = "%1$s is successfully saved.";
	private static final String FILENAME_ONGOING_TASKS = "OngoingTasks.txt";
	private static final String FILENAME_COMPLETED_TASKS = "CompletedTasks.txt";
	private static final String FILENAME_FLOATING_TASKS = "FloatingTasks.txt";
	private static final String FILENAME_OVERDUE_TASKS = "OverdueTasks.txt";
	private static final String FILENAME_CATEGORIES = "Categories.txt";
	private static final String FILENAME_CONFIG = "Config.txt";

	private static Storage theOne;
	private PrintWriter pw;
	private BufferedReader br;
	private File floating;
	private File ongoing;
	private File completed;
	private File overdue;
	private File categories;
	private String folderDirectory;
	
	/**
	 * This method constructs an instance of Storage.
	 * It implements Singleton Pattern so that only one instance of Storage is allowed to be constructed at all time.
	 * It initializes its memory, or its attributes. 
	 * @return {@code Storage theOne}
	 */
	public static Storage getInstance() {
		if (theOne==null) {
			theOne = new Storage();
		}
		return theOne;
	}
	
	/**
	 * This method reads ArrayList<Task> attributes from a specific text file on disk storage.
	 * It inputs a TASK_STATUS enum type and outputs the corresponding tasks list.
	 * @param {@code TASK_STATUS task_status}
	 * @return {@code ArrayList<Task> tasks}
	 */
	public ArrayList<Task> read(TASK_STATUS task_status) {
		switch (task_status) {
		case ONGOING:
			return readFromFile(this.ongoing);
		case COMPLETED:
			return readFromFile(this.completed);
		case FLOATING:
			return readFromFile(this.floating);
		case OVERDUE:
			return readFromFile(this.overdue);
		default:
			return null;
		}
	}
	
	/**
	 * This method saves ArrayList<Task> attributes into a text file residing on disk storage.
	 * 
	 * @param {@code TASK_STATUS task_status}
	 * @param {@code ArrayList<Task> tasks}
	 * @return {@code String feedback}
	 */
	public String save(TASK_STATUS task_status, ArrayList<Task> tasks) {
		switch (task_status) {
		case ONGOING:
			return printToFile(this.ongoing, tasks);
		case COMPLETED:
			return printToFile(this.completed, tasks);
		case FLOATING:
			return printToFile(this.floating, tasks);
		case OVERDUE:
			return printToFile(this.overdue, tasks);
		default:
			return MESSAGE_INVALID_TASK_STATUS;
		}
	}
	
	/**
	 * This method reads TreeMap<String, Category> categories from a specific text file on disk storage.
	 * 
	 * @return {@code TreeMap<String, Category> categories}
	 */

	public TreeMap<String, Category> readCategories() {
		TreeMap<String, Category> categories = new TreeMap<String, Category>();
		try {
			br = new BufferedReader(new FileReader(this.categories));
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss");
			Gson gson = gsonBuilder.create();
			categories = gson.fromJson(br, new TypeToken<TreeMap<String, Category>>() {}.getType());
			br.close();
			if (categories==null) {
				categories = new TreeMap<String, Category>();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return categories;
	}
	
	/**
	 * This method saves TreeMap<String, Cateogory> categories into a text file on disk storage.
	 * @param categories
	 * @return {@code String feedback}
	 */
	public String saveCategories(TreeMap<String, Category> categories)  {
		this.categories.delete();
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(this.categories), "UTF-8")) {
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").setPrettyPrinting();
			Gson gson = gsonBuilder.create();
			gson.toJson(categories, writer);
			writer.close();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return String.format(MESSAGE_SUCCESSFUL_FILE_SAVE, this.categories);
	}
	
	/**
	 * This method redirects storage files to a new directory on the disk.
	 */
	public void redirect() {
		this.floating.delete();
		this.ongoing.delete();
		this.completed.delete();
		this.overdue.delete();
		this.categories.delete();
		File oldDirectory = new File(this.folderDirectory);
		oldDirectory.delete();
		this.folderDirectory = saveConfig();
		
		intialise(this.folderDirectory);
	}
	/******************************INTERNAL***********************************************/
	private Storage() {
		this.folderDirectory = this.configDirectory();	
		this.intialise(this.folderDirectory);
	}
	
	private void intialise(String directory) {
		this.overdue = new File(directory + FILENAME_OVERDUE_TASKS);
		this.ongoing = new File(directory  + FILENAME_ONGOING_TASKS);
		this.floating = new File(directory + FILENAME_FLOATING_TASKS); 
		this.completed = new File(directory + FILENAME_COMPLETED_TASKS);
		this.categories = new File(directory + FILENAME_CATEGORIES);

		if (!this.categories.exists()) initialiseFile(this.categories);
		if (!this.ongoing.exists()) initialiseFile(this.ongoing);
		if (!this.completed.exists()) initialiseFile(this.completed);
		if (!this.floating.exists()) initialiseFile(this.floating);
		if (!this.overdue.exists()) initialiseFile(this.overdue);
	}
	
	private String configDirectory() {
		File config = new File(FILENAME_CONFIG);
		String directory = null;
		if (config.exists()) {
			directory = readConfig(config);
		} 
		else {
			directory = saveConfig();
		}
		return directory;
	}

	private String saveConfig() {
		String directory = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(FILENAME_CONFIG, false)));
			File folder = UIConfiguration.openDialogBox();
			directory = folder.getAbsolutePath() + "/";
			pw.println(directory);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return directory;
	}

	private String readConfig(File config) {
		String directory = null;
		try {
			br = new BufferedReader(new FileReader(config));
			directory = br.readLine();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return directory;

	}

	private String printToFile(File file, ArrayList<Task> tasks)  {
		file.delete();
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").setPrettyPrinting();
			Gson gson = gsonBuilder.create();
			gson.toJson(tasks, writer);
			writer.close();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return String.format(MESSAGE_SUCCESSFUL_FILE_SAVE, file);
	}

	private ArrayList<Task> readFromFile(File file) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		try {
			br = new BufferedReader(new FileReader(file));
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss");
			Gson gson = gsonBuilder.create();
			tasks = gson.fromJson(br, new TypeToken<ArrayList<Task>>() {}.getType());
			if (tasks==null) {
				tasks = new ArrayList<Task>();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tasks;
	}

	private void initialiseFile(File file) {
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			pw.print("");
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
