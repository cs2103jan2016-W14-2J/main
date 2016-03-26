package GUI;

import Command.*;
import Logic.*;
import Parser.*;
import Storage.*;
import Task.*;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/*
 *@author Chiang Jia Feng
 *@Description: GUI (intialize the root(VBox))
 */
public class UIMainController {
	
	private HBox root = new HBox(); //main root
	private UILeftBox leftBox; 
	private UIRightBox rightBox;
	private Logic logic;
	private Stage primaryStage;
	private final double sceneWidth = 1800;  
	private final double sceneHeight = 750; 
	private Scene scene = new Scene(root,sceneWidth,sceneHeight,Color.WHITE);	
	private String strDBdir = "";
	private String strDBname = "";
	

	public UIMainController(Logic logic)
	{
		this.logic=logic;
		leftBox = new UILeftBox(logic);
		rightBox = new UIRightBox(logic);
	}
	public void start(Stage primaryStage) 
	{		
		leftBox = new UILeftBox();
		//rightBox = new UIRightBox();

		this.primaryStage = primaryStage;
		addLeftAndRightBox();
		setEscCloseForm();
		show();
		System.out.println(root.getChildren().size());
	}
	public void addLeftAndRightBox() 
	{
		root.getChildren().addAll(leftBox.UILeftBox(rightBox),rightBox.UIRightBox(leftBox));
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

	public void setEscCloseForm() 
	{
		saveBeforeClose(primaryStage);
		escListener(primaryStage);
		primaryStage.close();
	}
	private void saveBeforeClose(Stage primaryStage) 
	{
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() 
		{
	          public void handle(WindowEvent we) 
	          {
	        	  Task<Void> task = new Task<Void>() {
	     	         @Override protected Void call() throws Exception 
	     	         {
	     	        	 assert(logic.save()!=null); 
	     	        	 logic.save();
	     	        	 return null;
	     	         }
	     	     };
	     	    startThread(task);
	            System.out.println("Stage is closing");
	          }

			private void startThread(Task<Void> task) {
				Thread th = new Thread(task);
	     	    th.setDaemon(true);
	     	    th.start();				
			}
	      });    		
	}
	private void escListener(Stage primaryStage) 
	{
		primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() 
		{
		        @Override
		        public void handle(KeyEvent t) {
		          if(t.getCode()==KeyCode.ESCAPE)
		          {
	     	          logic.save();
		        	  primaryStage.close();
		          }
		        }
		    });			
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
