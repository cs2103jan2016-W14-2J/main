package GUI;

import java.util.ArrayList;

import Command.*;
import Logic.*;
import Parser.*;
import Storage.*;
import Task.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/*
 *@author Chiang Jia Feng
 *@Description: CenterBox (Tabpane and Textfield)
 */

public class CenterBox {

	private final double textfieldWidth = 100; //centerBox txtfield size
	private final double textfieldHeight = 1500;
	private final double tabPaneWidth = 1000; //centerBox tabPane size
	private final double tabPaneHeight = 1000;
	private final double titledPaneWidth = 1000; //centerBox titledPane size
	private final double titledPaneHeight = 1000;
	private final double dialogWidth = 500; 
	private final double dialogHeight = 500;


	private VBox centerVBox = new VBox();
	


	private TabPane tabPane = new TabPane();
	private TextField mainTextField = new TextField();
	public static Logic logic;
	private LeftBox leftBox;
	private Stage primaryStage;

	private final String onGoingTab = "On Going Tasks";
	private final String completedTab = "Completed Tasks";
	private final String overDueTab = "Overdue Tasks";
	private final String floatingTab = "Floating Tasks";
	
	private TitledPane titledPaneOnGoingTask= new TitledPane();
	private TitledPane titledPaneCompletedTask = new TitledPane();
	private TitledPane titledPaneOverdueTask = new TitledPane();
	private TitledPane titledPaneFloatingTask = new TitledPane();
	
	private	ArrayList<Task> ongoingTasks = new ArrayList<Task>();
	private ArrayList<Task> floatingTasks = new ArrayList<Task>();
	private ArrayList<Task> completedTasks = new ArrayList<Task>();
	private ArrayList<Task> overdueTasks = new ArrayList<Task>();

	public enum TASK_TYPE 
	{
		onGoingTask,floatingTask,completedTask,overdueTask;
	}
	
	public VBox centerBox(Stage primaryStage, LeftBox leftBox)
	{
		this.leftBox = leftBox;
		this.primaryStage = primaryStage;
		addTabpane();
		addMainTextfield();
		return centerVBox;	
	}
	private void addTabpane() 
	{
		initTabPane();
		addTabs();
		updateTitledPane(); 
		centerVBox.getChildren().add(tabPane);	

	}

	private void addMainTextfield() 
	{
		textFieldListener();
		mainTextField.setPrefSize(textfieldHeight, textfieldWidth);
		centerVBox.getChildren().add(mainTextField);
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
	private void addListToTitledPane(TitledPane titledPane,ArrayList<Task> listTask, TASK_TYPE typeOfTask) 
	{
		ListView<HBox> list = new ListView<>(); 
		titledPane.setPrefSize(titledPaneHeight, titledPaneWidth);
		addContent(list,listTask,typeOfTask);
		list.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
	        public void handle(KeyEvent ke) {
	        	if (ke.getCode().equals(KeyCode.ENTER))
	            {
	        		/*System.out.println("list component selected is as follows: "  + list.getSelectionModel().getSelectedItem().getChildren().get(0));
	        		System.out.println("list component selected is as follows: "  + list.getSelectionModel().getSelectedItem().getChildren().get(1));*/
		            createDisappearPane(list.getSelectionModel().getSelectedItem());
		            centerVBox.setEffect(new BoxBlur());
		            leftBox.leftVBox.setEffect(new BoxBlur());
	            }
	        }
	    });
		titledPane.setContent(list);
	}

	@SuppressWarnings("null")
	private void addContent(ListView<HBox> list, ArrayList<Task> listTask, TASK_TYPE typeOfTask) 
	{
		ObservableList<HBox> listOfSmallTasks = FXCollections.observableArrayList();
		new Thread(new Runnable() 
		{
		    @Override public void run() 
		    {
		    	if(logic.getFloatingTasks()!=null &&typeOfTask.equals(TASK_TYPE.floatingTask))
        		{
        			for(int x=0;x<logic.getFloatingTasks().size();x++)
        			{
        				HBox vb = new HBox();
        				vb.setPrefSize(50,100);
        				Label lbl1 = new Label(logic.getFloatingTasks().get(x).getName());
        				Label lbl2 = new Label(logic.getFloatingTasks().get(x).getDescription());
        				lbl1.setPrefWidth(100);
        				lbl2.setPrefWidth(250);
        				vb.getChildren().addAll(lbl1,lbl2);
        				listOfSmallTasks.add(vb);
        			}
        		}
        		if(logic.getOngoingTasks()!=null &&typeOfTask.equals(TASK_TYPE.onGoingTask))
        		{
        			for(int x=0;x<logic.getOngoingTasks().size();x++)
        			{
        				HBox vb = new HBox();
        				vb.setPrefSize(50,100);
        				Label lbl1 = new Label(logic.getOngoingTasks().get(x).getName());
        				Label lbl2 = new Label(logic.getOngoingTasks().get(x).getDescription());
        				lbl1.setPrefWidth(100);
        				lbl2.setPrefWidth(250);
        				vb.getChildren().addAll(lbl1,lbl2);
        				listOfSmallTasks.add(vb);
        			}
        		}
        		if(logic.getCompletedTasks()!=null &&typeOfTask.equals(TASK_TYPE.completedTask))
        		{
        			for(int x=0;x<logic.getCompletedTasks().size();x++)
        			{
        				HBox vb = new HBox();
        				vb.setPrefSize(50,100);
        				Label lbl1 = new Label(logic.getCompletedTasks().get(x).getName());
        				Label lbl2 = new Label(logic.getCompletedTasks().get(x).getDescription());
        				lbl1.setPrefWidth(100);
        				lbl2.setPrefWidth(250);
        				vb.getChildren().addAll(lbl1,lbl2);
        				listOfSmallTasks.add(vb);
        			}
        		}
        		if(logic.getOverdueTasks()!=null &&typeOfTask.equals(TASK_TYPE.overdueTask))
        		{
        			for(int x=0;x<logic.getOverdueTasks().size();x++)
        			{
        				HBox vb = new HBox();
        				vb.setPrefSize(50,100);
        				Label lbl1 = new Label(logic.getOverdueTasks().get(x).getName());
        				Label lbl2 = new Label(logic.getOverdueTasks().get(x).getDescription());
        				lbl1.setPrefWidth(100);
        				lbl2.setPrefWidth(250);
        				vb.getChildren().addAll(lbl1,lbl2);
        				listOfSmallTasks.add(vb);
        			}
        		}
        		
		    }
		}).start();
		list.setItems(listOfSmallTasks);
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

			private void updateNewListItems() 
			{
				ongoingTasks.addAll(logic.getOngoingTasks());
				floatingTasks.addAll(logic.getFloatingTasks());
				completedTasks.addAll(logic.getCompletedTasks());
				overdueTasks.addAll(logic.getOverdueTasks());
				updateTitledPane();
			}
	    });	
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
	
	private void createDisappearPane(HBox hbox) 
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
	        	centerVBox.setEffect(null);
	            leftBox.leftVBox.setEffect(null);
	            
	        }
	    });  
	}
}
