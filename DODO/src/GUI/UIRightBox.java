package GUI;


import java.util.ArrayList;

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

	private static TabPane tabPane = new TabPane();
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
	
	/*private ObservableList<Task> ongoingTasks =  FXCollections.observableArrayList();
	private ObservableList<Task> floatingTasks=  FXCollections.observableArrayList();
	private ObservableList<Task> completedTasks=  FXCollections.observableArrayList();
	private ObservableList<Task> overdueTasks=  FXCollections.observableArrayList();*/
	private ArrayList<Task> ongoingTasks =  new ArrayList<Task>();
	private ArrayList<Task> floatingTasks=  new ArrayList<Task>();
	private ArrayList<Task> completedTasks=  new ArrayList<Task>();
	private ArrayList<Task> overdueTasks=  new ArrayList<Task>();
	
	private UIFadeFeedBack fb = new UIFadeFeedBack();
	
	
	Tab tabFloating = new Tab(floatingTab);
	VBox floatingVB =new VBox();
	
	Tab tabOngoing = new Tab(onGoingTab);
	VBox ongoingVB = new VBox();
	
	Tab tabCompleted = new Tab(completedTab);
	VBox completedVB = new VBox();
	
	Tab tabOverdue = new Tab(overDueTab);
	VBox overdueVB = new VBox();
	
	public static TASK_STATUS getCurrentTab()
	{
		return (TASK_STATUS) tabPane.getSelectionModel().getSelectedItem().getUserData();
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
	

	private void overdueTab() 
	{
		overdueVB.getChildren().add(titledPaneOverdueTask);
		tabOverdue.setContent(overdueVB);
		tabPane.getTabs().add(tabOverdue);	
		tabOverdue.setUserData(TASK_STATUS.OVERDUE);
	}
	private void completedTab() 
	{
		completedVB.getChildren().add(titledPaneCompletedTask);
		tabCompleted.setContent(completedVB);
		tabPane.getTabs().add(tabCompleted);	
		tabCompleted.setUserData(TASK_STATUS.COMPLETED);
	}
	private void ongoingTab()
	{
		ongoingVB.getChildren().add(titledPaneOnGoingTask);
		tabOngoing.setContent(ongoingVB);
		tabPane.getTabs().add(tabOngoing);		
		tabOngoing.setUserData(TASK_STATUS.ONGOING);

	}
	private void floatingTab() 
	{
		TitledPane tp = new TitledPane();
		floatingVB.getChildren().add(tp);
		tabFloating.setContent(titledPaneFloatingTask);
		tabPane.getTabs().add(tabFloating);		
		tabFloating.setUserData(TASK_STATUS.FLOATING);
		/*Tab tab = new Tab(floatingTab);
		VBox vb = new VBox();
		tab.setContent(vb);
		tab.setContent(titledPaneFloatingTask);
		tabPane.getTabs().add(tab);		
		tab.setUserData(TASK_STATUS.FLOATING);
		*/

	}

	private void addListToTitledPane(TitledPane titledPane,ObservableList<Task>listTask, TASK_STATUS typeOfTask) 
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
		                	UICellComponents lsg=null;
		                    if ((item instanceof DeadlinedTask) == true)
		                    {
		                    	System.out.println("DeadlinedTask");
		                    	lsg = new UICellComponents(
			                    		Integer.toString(this.getIndex()+1),
			                    		"tagging",item.getName(),
			                    		null,
			                    		((DeadlinedTask)item).getEndDateTime(),
			                    		"flag");
		                    }
		                    else if ((item instanceof Event) == true)
		                    {
		                    	System.out.println("in event");
		                    	lsg = new UICellComponents(
			                    		Integer.toString(this.getIndex()+1),
			                    		"tagging",item.getName(),
			                    		((Event)item).getStartDateTime(),
			                    		((Event)item).getEndDateTime(),
			                    		"flag");
		                    }
		                    else if ((item instanceof Task) == true)
		                    {
		                    	System.out.println("in Task");
		                    	lsg = new UICellComponents(
			                    		Integer.toString(this.getIndex()+1),
			                    		"tagging",item.getName(),
			                    		null,
			                    		null,
			                    		"flag");
		                    }
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
	private void initTitledPane(TitledPane titledPane)
	{
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
	            	fb.run(logic.run(mainTextField.getText()));
	            	System.out.println("GUIIIIIIIIIIIIII "+ mainTextField.getText());
	            	updateNewListItems();
	            	mainTextField.setText("");
	            	//System.out.println("tab enter " + tabPane.getSelectionModel().getSelectedItem().getUserData());
	            	
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
		//having an array to store all arrays.
		//each array are sorted according to date
		/*tabFloating.setContent(floatingVB);
		tabFloating.setUserData(TASK_STATUS.FLOATING);
		floatingVB.getChildren().clear();
		for(int x=0;x<10;x++)
		{
			TitledPane tp = new TitledPane();
			floatingVB.getChildren().add(tp);
			addListToTitledPane(tp,FXCollections.observableArrayList(floatingTasks),TASK_TYPE.floatingTask);

		}*/
		
		addListToTitledPane(titledPaneFloatingTask,FXCollections.observableArrayList(floatingTasks),TASK_STATUS.FLOATING);
		addListToTitledPane(titledPaneOnGoingTask,FXCollections.observableArrayList(ongoingTasks),TASK_STATUS.ONGOING);
		addListToTitledPane(titledPaneCompletedTask,FXCollections.observableArrayList(completedTasks),TASK_STATUS.COMPLETED);
		addListToTitledPane(titledPaneOverdueTask,FXCollections.observableArrayList(overdueTasks),TASK_STATUS.OVERDUE);		
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
