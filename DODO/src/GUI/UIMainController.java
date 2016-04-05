package GUI;

import Logic.Logic;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
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
	public UIMainController(Logic logic)
	{
		this.logic = logic;

		root = new HBox();
		scene = new Scene(root,sceneWidth,sceneHeight,Color.WHITE);
		
		leftBox = new UILeftBox(this.logic,this.root,scene);
		rightBox = new UIRightBox(this.logic,this.root,scene);
		
		leftBox.build(rightBox);
		rightBox.build(leftBox);
		
		Stage newStage = new Stage();
	}

	public void start(Stage primaryStage) 
	{		
		primaryStage.sizeToScene();
		setPrimaryStage(primaryStage);
		addLeftAndRightBox();
		setEscCloseForm();
		listen = new UIListener(root,primaryStage,rightBox,leftBox,logic);
		listen.assignHelpSheetListener();
		
	
		show();
		
		
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
	          //  System.out.println("Stage is closing");
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
