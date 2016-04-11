package GUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;

import Logic.Logic;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

//@@author A0125372L
public class UIMainController {
	
	private HBox root;
	private UILeftBox leftBox; 
	private UIRightBox rightBox;
	private Logic logic;
	private Stage primaryStage;
	private final double sceneWidth = 1800;  //1900
	private final double sceneHeight = 750;  //900
	private Scene scene;	
	private String strDBdir = "";
	private String strDBname = "";
	private UIListener listen;
	private Timer timer;
	public UIMainController(Logic logic)
	{
		this.logic = logic;

		root = new HBox();
		scene = new Scene(root,sceneWidth,sceneHeight,Color.WHITE);
		
		leftBox = new UILeftBox(this.logic,this.root,scene);
		rightBox = new UIRightBox(this.logic,this.root,scene);
		
		timer = new Timer();
		
		leftBox.build(rightBox);
		rightBox.build(leftBox);
		
		
		
	}

	public void start(Stage primaryStage) 
	{		
		primaryStage.sizeToScene();
		setPrimaryStage(primaryStage);
		addLeftAndRightBox();
		listen = new UIListener(root,primaryStage,rightBox,leftBox,logic);
		listen.assignHelpSheetListener();
		show();

		

		

		Calendar now = Calendar.getInstance();	
		
		timer.schedule( new TimerTask() {
		    public void run() {
		    	Platform.runLater(new Runnable() {
                    @Override
                    public void run() 
                    {
                		logic.update();
                		rightBox.testMethod();
                		try {
                			Thread.sleep(1000);
                		} catch (Exception e) {
                			e.printStackTrace();
                		}

                    }
                });
		    }
		 }, 0, 60*200);
		
	  
		
	}
	public void addLeftAndRightBox() 
	{
		//root.getChildren().addAll(leftBox.getRoot(),rightBox.getRoot());
		root.getChildren().addAll(leftBox.getRoot(),rightBox.getRoot());
	}

	private void setPrimaryStage(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
	}

	public Scene getScene()
	{
		return scene;
	}
	public void show() 
	{
		root.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
            	char c =ke.getCharacter().charAt(0);
                if(Character.isAlphabetic(c))
                {
                	System.out.println(c);
                	rightBox.setTextField(Character.toString(c));
                	rightBox.getTextField().requestFocus();
                	rightBox.setCursorTextField();
                }
            }
        });
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	

	public void setDBname(String strDBname) {
		this.strDBname = strDBname;
	}
	public void setDBdir(String strDBdir) {
		this.strDBdir = strDBdir;
		
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
	/**
	 * Mutators
	 */	
	public String setDbDir()
	{
		return strDBdir;
	}
	public String setDbName()
	{
		return strDBname;
	}
	public HBox getRoot() 
	{
		return root;
	}
	public UIRightBox getRight()
	{
		return rightBox;
	}
	public UILeftBox getLeft()
	{
		return leftBox;
	}
	
}
