package GUI;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import Logic.Logic;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//@@author A0125372L
public class UIConfiguration extends Application 
{
	private final String CONFIG_FIRSTLINE_DIRECTORY_KNOWN = "DBDIR = %1$s";
	private final String CONFIG_SECONDLINE_FILENAME_KNOWN = "FILENAME = %1$s";
	private final String CONFIG_SEPARATOR = System.getProperty("line.separator");
	private final String DIALOG_TITLE = "SAVE DATABASE"; 
	private final int PARAM_FOR_DIR = 8;	//include space
	private final int PARAM_FOR_NAME = 11; //include space
	private String PARAM_CONFIG_DEFAULT_FILENAME = "Config.txt";
	private String PARAM_DB_DEFAULT_FILENAME = "DODOdatabase";
	private File configFile = new File(PARAM_CONFIG_DEFAULT_FILENAME);
	private FileChooser fileChooser = new FileChooser();
	private ArrayList<String> listOfConfigs = new ArrayList<String>();
	private File dbFile=null;
	private Stage primaryStage;
	private String strDBdir = "";
	private String strDBname = "";
	private Logic logic;
	private boolean launch=false;
	private UIMainController gui;
	
	@Override
	public void start(Stage primaryStage) throws IOException 
	{
		Image applicationIcon = new Image(getClass().getResourceAsStream("logo.png"));
		primaryStage.getIcons().add(applicationIcon);
		
        this.primaryStage = primaryStage;
        logic = Logic.getInstance();
		gui = new UIMainController(logic);
		gui.setDBdir(strDBdir);
		gui.setDBname(strDBname);
		launch=true;
		System.out.println( "                                                                              "+ strDBdir);
		gui.start(primaryStage);
	}
	public boolean getLaunch()
	{
		return launch;
	}


	
	

	/**
	 * Accessor
	 */	
	public String getDbDir()
	{
		return strDBdir;
	}
	public String strDbName()
	{
		return strDBname;
	}
	public static void main(String[] args) {
		launch(args);
	}

	public static File openDialogBox()
	{
		  DirectoryChooser directoryChooser = new DirectoryChooser();
          File selectedDirectory = directoryChooser.showDialog(new Stage());
           
          if(selectedDirectory == null){
              return null;
          }else{
              return selectedDirectory;
          }		
		
	}
	
}
