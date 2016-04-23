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
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

//@@author A0125372L
public class UIConfiguration extends Application 
{
	private Stage primaryStage;
	private String strDBdir = "";
	private String strDBname = "";
	private Logic logic;
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
		saveBeforeClose(primaryStage);
		gui.start(primaryStage);
	}
	private void saveBeforeClose(Stage primaryStage) 
	{
		primaryStage.setOnHiding(new EventHandler<WindowEvent>() 
		{
            public void handle(WindowEvent event) 
            {
                Platform.runLater(new Runnable() 
                {
                    @Override
                    public void run() 
                    {
	                    assert(logic.save()!=null); 
	     	        	logic.save();
	     	        	System.exit(0);
                    }
                });
            }
        });
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
