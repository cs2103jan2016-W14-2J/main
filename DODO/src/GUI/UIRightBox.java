package GUI;


import Command.*;
import Logic.*;
import Parser.*;
import Storage.*;
import Task.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/*
 *@author Chiang Jia Feng
 *@Description: rightBox (Tabpane and Textfield)
 */

public class UIRightBox{
	private VBox rightBox = new VBox(); //current box
	private UILeftBox leftBox;
	private final double textfieldWidth = 55; //centerBox txtfield size
	private final double textfieldHeight = 1233;
	private final double tabPaneWidth = 1000; //centerBox tabPane size
	private final double tabPaneHeight = 1450;
	private final double titledPaneWidth = 1000; //centerBox titledPane size
	private final double titledPaneHeight = 1450;
	private final double dialogWidth = 500; 
	private final double dialogHeight = 500;

	private TabPane tabPane = new TabPane();
	private TextField mainTextField = new TextField();
	private Logic logic;
	private Stage primaryStage;

	private final String onGoingTab = "Ongoing Tasks";
	private final String completedTab = "Completed Tasks";
	private final String overDueTab = "Overdue Tasks";
	private final String floatingTab = "Floating Tasks";
	
	private TitledPane titledPaneOnGoingTask= new TitledPane();
	private TitledPane titledPaneCompletedTask = new TitledPane();
	private TitledPane titledPaneOverdueTask = new TitledPane();
	private TitledPane titledPaneFloatingTask = new TitledPane();
	
	private ObservableList<Task> ongoingTasks =  FXCollections.observableArrayList();
	private ObservableList<Task> floatingTasks=  FXCollections.observableArrayList();
	private ObservableList<Task> completedTasks=  FXCollections.observableArrayList();
	private ObservableList<Task> overdueTasks=  FXCollections.observableArrayList();
	public enum TASK_TYPE 
	{
		onGoingTask,floatingTask,completedTask,overdueTask;
	}	
	public UIRightBox(Logic logic) 
	{
		this.logic = logic;
	}

	public VBox UIRightBox(UILeftBox leftBox)
	{
		setLeftBox(leftBox);
		addTabPane();
		addTabs();
		addMainTextfield();
    	updateNewListItems();
		updateTitledPane(); 
		
		return rightBox;	
	}
	
	private void setLeftBox(UILeftBox leftBox)
	{
		this.leftBox = leftBox;		
	}

	private void addTabPane() 
	{
		initTabPane();
		rightBox.getChildren().add(tabPane);	
	}
	private void addMainTextfield() 
	{
		initMainTextfield();
		textFieldListener();
		rightBox.getChildren().add(mainTextField);
	}
	private void initMainTextfield() {
		mainTextField.setPrefSize(textfieldHeight, textfieldWidth);		
	}
	private void addTabs()
	{
		floatingTab();
		ongoingTab();
		completedTab();
		overdueTab();
	}
	private void overdueTab() {
		Tab tab = new Tab(overDueTab);
		tab.setContent(titledPaneOverdueTask);
		tabPane.getTabs().add(tab);	
	}
	private void completedTab() {
		Tab tab = new Tab(completedTab);
		tab.setContent(titledPaneCompletedTask);
		tabPane.getTabs().add(tab);	
	}
	private void ongoingTab() {
		Tab tab = new Tab(onGoingTab);
		tab.setContent(titledPaneOnGoingTask);
		tabPane.getTabs().add(tab);		
	}
	private void floatingTab() 
	{
		Tab tab = new Tab(floatingTab);
		tab.setContent(titledPaneFloatingTask);
		tabPane.getTabs().add(tab);		
		

	}
	private void addListToTitledPane(TitledPane titledPane,ObservableList<Task>listTask, TASK_TYPE typeOfTask) 
	{		
		initTitledPane(titledPane);
		final ListView<Task> listViewLabel = new ListView<Task>(listTask);
		
		new Thread(new Runnable() 
		{
		    @Override public void run() 
		    { 
			listViewLabel.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() 
			{
		          public ListCell<Task> call(ListView<Task> param) 
		          {
		            ListCell<Task> cell = new ListCell<Task>() 
		            {		            
		              @Override
		              public void updateItem(Task item, boolean empty) 
		              {
		            	super.updateItem(item, empty);
		                if (item != null) 
		                {
		                    UICellComponents lsg = new UICellComponents(Integer.toString(this.getIndex()+1),item.getName(),item.getDescription());
		                	setTooltip(lsg.getToolTip());
		                    setGraphic(lsg.getCellRoot());
		                }
		              }
		            };
            		//cell.setStyle("-fx-control-background: Transparent");
		            
		            return cell;
		          }
		        });
			
			listViewLabel.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
		          public void changed(ObservableValue<? extends Task> observable,
		        		  Task oldValue, Task newValue) {
		            System.out.println("selection changed");  
		          }
		        });
			
			listViewLabel.setOnKeyPressed(new EventHandler<KeyEvent>() 
			{
		        public void handle(KeyEvent ke) {
		        	if (ke.getCode().equals(KeyCode.ENTER))
		            {
		        		createDisappearPane();
		        		rightBox.setEffect(new BoxBlur());
		        		leftBox.setEffect(new BoxBlur());
		            }
		        }
		    });		
			
			    }
		}).start();
		
		
		titledPane.setContent(listViewLabel);
		
		
	}

	private void initTitledPane(TitledPane titledPane) {
		titledPane.setPrefSize(titledPaneHeight, titledPaneWidth);
	}
	private void initTabPane() {
		tabPane.setPrefSize(tabPaneHeight,tabPaneWidth);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);		
	}
	private void textFieldListener() {
		mainTextField.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	logic.run(mainTextField.getText());
	            	System.out.println("GUIIIIIIIIIIIIII "+ mainTextField.getText());
	            	updateNewListItems();
	            	mainTextField.setText("");
	            }
	        }
	    });	
	}
	private void updateNewListItems() 
	{
		ongoingTasks.clear();
		floatingTasks.clear();
		completedTasks.clear();
		overdueTasks.clear();

		
		ongoingTasks.addAll(logic.getOngoingTasks());
		floatingTasks.addAll(logic.getFloatingTasks());
		completedTasks.addAll(logic.getCompletedTasks());
		overdueTasks.addAll(logic.getOverdueTasks());
		System.out.println("292 floatingTasks "+floatingTasks);
		updateTitledPane();
	}
	protected void updateTitledPane() {
		addListToTitledPane(titledPaneFloatingTask,floatingTasks,TASK_TYPE.floatingTask);
		addListToTitledPane(titledPaneOnGoingTask,ongoingTasks,TASK_TYPE.onGoingTask);
		addListToTitledPane(titledPaneCompletedTask,completedTasks,TASK_TYPE.completedTask);
		addListToTitledPane(titledPaneOverdueTask,overdueTasks,TASK_TYPE.overdueTask);		
	}
	public TextField getTextField()
	{
		return mainTextField;
	}
	
	private void createDisappearPane() 
	{
	    Stage dialog = new Stage();
	    VBox dialogVBox = new VBox();
		dialog.initModality(Modality.APPLICATION_MODAL);
	    dialog.initOwner(primaryStage);
	    dialogVBox.getChildren().add(new Text("This is a Dialog"));
	    Scene dialogScene = new Scene(dialogVBox, dialogWidth, dialogHeight);
	    dialog.setScene(dialogScene);
	    dialog.show();
	    dialog.setOnCloseRequest(new EventHandler<WindowEvent>() 
	    {
	        public void handle(WindowEvent we) 
	        {
	        	rightBox.setEffect(null);
		        leftBox.setEffect(null);
	        }
	    });  
	    dialog.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
	        @Override
	        public void handle(KeyEvent t) {
	          if(t.getCode()==KeyCode.ESCAPE)
	          {
		          rightBox.setEffect(null);
		          leftBox.setEffect(null);
	        	  dialog.close();
	          }
	        }
	    });		
	}
}
