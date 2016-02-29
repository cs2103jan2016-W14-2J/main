import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/*
 *@author Chiang Jia Feng
 *@Description: GUI (intialize the root(VBox))
 */
public class GUI{
	
	private final double sceneWidth = 1640;  //primaryStage scene size
	private final double sceneHeight = 750; 
	private Stage primaryStage;
	private HBox root = new HBox(); //main root
	private LeftBox leftBox = new LeftBox(); 
	private CenterBox centerBox = new CenterBox();
	private Scene scene = new Scene(root,sceneWidth,sceneHeight,Color.WHITE);	
	public static Logic logic;
	public String strDBdir = "";
	public String strDBname = "";
	
	public void start(Stage primaryStage) 
	{		
		CenterBox.logic=this.logic;
		root.getChildren().add(leftBox.leftBox(primaryStage,centerBox));
		root.getChildren().add(centerBox.centerBox(primaryStage,leftBox));
		EscCloseForm(primaryStage);
		initRoot(primaryStage);
	}
	public void initRoot(Stage primaryStage) 
	{
		this.primaryStage = primaryStage;
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	private void EscCloseForm(Stage primaryStage) 
	{
		primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		        @Override
		        public void handle(KeyEvent t) {
		          if(t.getCode()==KeyCode.ESCAPE)
		          {
		        	  primaryStage.close();
		          }
		        }
		    });		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) 
	          {
	        	  Task<Void> task = new Task<Void>() {
	     	         @Override protected Void call() throws Exception {
	     	        	 logic.save();
						return null;
	     	         }
	     	     };
	     	    Thread th = new Thread(task);
	     	    th.setDaemon(true);
	     	    th.start();
	            System.out.println("Stage is closing");
	          }
	      });        
		primaryStage.close();
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
}
