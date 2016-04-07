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
	private static Storage theOne;
	private static final String FILENAME_ONGOING_TASKS = "OngoingTasks.txt";
	private static final String FILENAME_COMPLETED_TASKS = "CompletedTasks.txt";
	private static final String FILENAME_FLOATING_TASKS = "FloatingTasks.txt";
	private static final String FILENAME_OVERDUE_TASKS = "OverdueTasks.txt";
	private static final String FILENAME_CATEGORIES = "Categories.txt";
	private static final String FILENAME_CONFIG = "Config.txt";

	private PrintWriter pw;
	private BufferedReader br;
	private String ongoingDirectory;
	private String completedDirectory;
	private String floatingDirectory;
	private String overdueDirectory;
	private String categoriesDirectory;
	private File config;
	
	public static Storage getInstance() {
		if (theOne==null) {
			theOne = new Storage();
		}
		return theOne;
	}
	
	private Storage() {
		String directory;
		File config = new File(FILENAME_CONFIG);
		if (config.exists()) {
			try {
				br = new BufferedReader(new FileReader(config));
				directory = br.readLine();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			config = UIConfiguration.openDialogBox();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(config, true)));
			pw.println(config.getAbsolutePath());
			directory = config.getAbsolutePath();
		}
		// it cannot create a file in a specific directory for now
		// it only creates a file in the same directory as the programme
		ongoingDirectory = directory  + FILENAME_ONGOING_TASKS;
		completedDirectory = directory + FILENAME_COMPLETED_TASKS;
		floatingDirectory = directory + FILENAME_FLOATING_TASKS;
		overdueDirectory = directory + FILENAME_OVERDUE_TASKS;
		categoriesDirectory = directory + FILENAME_CATEGORIES;
		if (!fileExists(categoriesDirectory)) initialiseFile(categoriesDirectory);
		if (!fileExists(ongoingDirectory)) initialiseFile(ongoingDirectory);
		if (!fileExists(completedDirectory)) initialiseFile(completedDirectory);
		if (!fileExists(floatingDirectory)) initialiseFile(floatingDirectory);
		if (!fileExists(overdueDirectory)) initialiseFile(overdueDirectory);
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
	
	public TreeMap<String, Category> readCategories() {
		 TreeMap<String, Category> categories = new TreeMap<String, Category>();
		try {
			br = new BufferedReader(new FileReader(this.categoriesDirectory));
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss");
			Gson gson = gsonBuilder.create();
			categories = gson.fromJson(br, new TypeToken<TreeMap<String, Category>>() {}.getType());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (categories==null) {
			System.out.println("[DEBUG/Storage] null");
			return new TreeMap<String, Category>();
		}
		return categories;
	}
	
	public String saveCategories(TreeMap<String, Category> categories)  {
		clearFile(this.categoriesDirectory);
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(this.categoriesDirectory), "UTF-8")) {
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").setPrettyPrinting();
			Gson gson = gsonBuilder.create();
			gson.toJson(categories, writer);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "Saving " + this.categoriesDirectory + " is successful. :)";
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
	
	/******************************INTERNAL***********************************************/
	private boolean fileExists(String directory) {
		File file = new File(directory);
		if (file.exists()) return true;
		else return false; 
	}

	private String printToFile(String filename, ArrayList<Task> tasks)  {
		clearFile(filename);
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")) {
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").setPrettyPrinting();
			Gson gson = gsonBuilder.create();
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
		ArrayList<Task> tasks = new ArrayList<Task>();
		try {
			br = new BufferedReader(new FileReader(filename));
			GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss");
			Gson gson = gsonBuilder.create();
			tasks = gson.fromJson(br, new TypeToken<ArrayList<Task>>() {}.getType());
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

	/*public class DateSerializer implements JsonSerializer<Date>{
		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			String str = formatter.format(src);
			return new JsonPrimitive(str);
		}
	}

	public class DateDeserializer implements JsonDeserializer<Date> {
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException	 {
			String date = json.getAsString();
			return new Date(json.getAsJsonPrimitive().getAsString());
		}
	}*/
	
	/*public class CustomSerializer implements JsonSerializer<ArrayList<Task>> {
		private static TreeMap<String, Class> map = new TreeMap<String, Class>();
		
		@Override 
		public JsonElement serialize(ArrayList<Task> src, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray ja = new JsonArray();
			for (Task task: src) {
				
			}
		}
	}*/
	
	/*public class CustomDeserializer implements JsonDeserializer<ArrayList<Task>> {
		@Override
		public ArrayList<Task> deserialize(JsonElement json, Type typeofT, JsonDeserializationContext context)
				throws JsonParseException {
			ArrayList<Task> list = new ArrayList<Task>();
			JsonArray ja = json.getAsJsonArray();
			for (JsonElement je: ja) {
				String type = je.getAsJsonObject().get("type").getAsString();
				System.out.println("=====CustomDeserializer=====" + type);
				if (type.equals("DEADLINED")) {
					JsonObject jsonObj = je.getAsJsonObject();
					String endDateTimeStr = jsonObj.get("endDateTime").getAsString();
					TASK_TYPE task_type = TASK_TYPE.EVENT;
					String name = jsonObj.get("name").getAsString();
					boolean flag = jsonObj.get("flag").getAsBoolean();
					boolean isComplete = jsonObj.get("isComplete").getAsBoolean();
					String tag = null;
					try {
						tag = jsonObj.get("tag").getAsString();
					} catch (NullPointerException e) {
					}
					
					Date endDateTime = null;
					try {
						endDateTime = formatter.parse(endDateTimeStr);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					DeadlinedTask task = new DeadlinedTask(task_type, name, tag, endDateTime);
					list.add(task);
				}
				else if (type.equals("EVENT")) {
					JsonObject jsonObj = je.getAsJsonObject();
					String startDateTimeStr = jsonObj.get("startDateTime").getAsString();
					String endDateTimeStr = jsonObj.get("endDateTime").getAsString();
					TASK_TYPE task_type = TASK_TYPE.EVENT;
					String name = jsonObj.get("name").getAsString();
					boolean flag = jsonObj.get("flag").getAsBoolean();
					boolean isComplete = jsonObj.get("isComplete").getAsBoolean();
					String tag = null;
					try {
						tag = jsonObj.get("tag").getAsString();
					} catch (NullPointerException e) {
					}
					Date startDateTime = null;
					Date endDateTime = null;
					try {
						startDateTime = formatter.parse(startDateTimeStr);
						endDateTime = formatter.parse(endDateTimeStr);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Event event = new Event(task_type, name, tag, startDateTime, endDateTime);
					list.add(event);
				}
				else {
					list.add(context.deserialize(je, Task.class));
				}
			}
			return list;
		}
	}*/
}
