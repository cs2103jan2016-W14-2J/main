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

	
	private final String onGoingTab = "On Going Tasks";
	private final String completedTaskTab = "Completed Tasks";
	private final String overDueTab = "Overdue Tasks";
	private final String resultTab = "Result";
	
	private final double textfieldWidth = 100; //centerBox txtfield size
	private final double textfieldHeight = 1500;
	private final double tabPaneWidth = 1000; //centerBox tabPane size
	private final double tabPaneHeight = 1000;
	private final double titledPaneWidth = 1000; //centerBox titledPane size
	private final double titledPaneHeight = 1000;
	private final double dialogWidth = 500; //centerBox txtfield size
	private final double dialogHeight = 500; //centerBox txtfield size

	private VBox centerBox = new VBox();;
	private TitledPane titledPaneAllTask = new TitledPane();
	private TitledPane titledPaneCompletedTask = new TitledPane();
	private TitledPane titledPaneOverdueTask = new TitledPane();
	//private Pane resultPane = new Pane();
	private TabPane tabPane = new TabPane();
	private TextField mainTextField = new TextField();
	public static Logic logic;
	private LeftBox leftBox;
	private Stage primaryStage;

	private	ArrayList<Task> ongoingTasks = new ArrayList<Task>();
	private ArrayList<Task> floatingTasks = new ArrayList<Task>();
	private ArrayList<Task> completedTasks = new ArrayList<Task>();
	private ArrayList<Task> overdueTasks = new ArrayList<Task>();

	
	
	public VBox centerBox(Stage primaryStage, LeftBox leftBox)
	{
		this.leftBox = leftBox;
		this.primaryStage = primaryStage;
		addTabpane();
		addMainTextfield();
		return centerBox;	
	}
	private void addTabpane() 
	{
		initTabPane();
		addTab();
		centerBox.getChildren().add(tabPane);	

	}
	private void addMainTextfield() 
	{
		textFieldListener();
		mainTextField.setPrefSize(textfieldHeight, textfieldWidth);
		centerBox.getChildren().add(mainTextField);
	}
	/**
	 * TAB IS FIXED
	 * 
	 */	
	private void addTab()
	{
		Tab tab = new Tab(onGoingTab);
		tab.setContent(titledPaneAllTask);
		addListToTitledPane(titledPaneAllTask,ongoingTasks);
		tabPane.getTabs().add(tab);
		
		/*Tab tab1 = new Tab(completedTaskTab);
		tab1.setContent(titledPaneCompletedTask);
		addListToTitledPane(titledPaneCompletedTask);
		tabPane.getTabs().add(tab1);
	
		
		Tab tab3 = new Tab(overDueTab);
		tab3.setContent(titledPaneOverdueTask);
		addListToTitledPane(titledPaneOverdueTask);
		tabPane.getTabs().add(tab3);*/
	
	/*	Tab tab4 = new Tab(resultTab);
		tab4.setContent(resultPane);
		tabPane.getTabs().add(tab4);*/
	
	
	}
	private void addListToTitledPane(TitledPane titledPane,ArrayList<Task> listTask) 
	{
		titledPane.setPrefSize(titledPaneHeight, titledPaneWidth);
		ListView<HBox> list = new ListView<>(); 
		addContent(list);
		list.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
	        public void handle(KeyEvent ke) {
	        	if (ke.getCode().equals(KeyCode.ENTER))
	            {
		            createDisappearPane(list.getSelectionModel().getSelectedItem());
		           
		            centerBox.setEffect(new BoxBlur());
		            leftBox.leftBox.setEffect(new BoxBlur());
	            }
	        }
	    });
		
		titledPane.setContent(list);
	}

	@SuppressWarnings("null")
	private void addContent(ListView<HBox> list) 
	{
		ObservableList<HBox> arrayImgView = FXCollections.observableArrayList();
		
		
		//for(int x=0;x<logic.getOngoingTasks().size();x++)
		/*for(int x=0;x<10000;x++)
		{
			HBox vb = new HBox();
			vb.setPrefSize(50,100);
			Label lbl = new Label("task a");
			Label lbl1 = new Label("date");
			Label lbl2 = new Label("time");
			lbl.setPrefWidth(100);
			lbl1.setPrefWidth(250);
			lbl2.setPrefWidth(500);

			//lbl.setText(logic.getOngoingTasks().get(0).getTaskDescription());
			vb.getChildren().addAll(lbl,lbl1,lbl2);
			arrayImgView.add(vb);
		}*/
		new Thread(new Runnable() {
		    @Override public void run() {
		        Platform.runLater(new Runnable() {
		            @Override public void run() {
		        		
		            		if(logic.getOngoingTasks()!=null)
		            		{
		            			for(int x=0;x<logic.getFloatingTasks().size();x++)
		            			{
		            				HBox vb = new HBox();
		            				vb.setPrefSize(50,100);
		            				
		            				System.out.println("[DEBUG/CentreBox] task: " + logic.getFloatingTasks().get(x));
		            				Label lbl1 = new Label(logic.getFloatingTasks().get(x).getName());
		            				Label lbl2 = new Label(logic.getFloatingTasks().get(x).getName());
		            				lbl1.setPrefWidth(100);
		            				lbl2.setPrefWidth(250);
		            				vb.getChildren().addAll(lbl1,lbl2);
		            				arrayImgView.add(vb);
		            			}
		            		}
		            }
		        });
		    }
		}).start();
		/*new Thread(new Runnable() {
		    @Override public void run() {
		        Platform.runLater(new Runnable() {
		            @Override public void run() {
		            	for(int x=0;x<5000;x++)
		        		{
		        			HBox vb = new HBox();
		        			vb.setPrefSize(50,100);
		        			Label lbl = new Label("task a");
		        			Label lbl1 = new Label("date");
		        			Label lbl2 = new Label("time");
		        			lbl.setPrefWidth(100);
		        			lbl1.setPrefWidth(250);
		        			lbl2.setPrefWidth(500);

		        			//lbl.setText(logic.getOngoingTasks().get(0).getTaskDescription());
		        			vb.getChildren().addAll(lbl,lbl1,lbl2);
		        			arrayImgView.add(vb);
		        			
		        		}
		            }
		        });
		    }
		}).start();*/
		//arrayImgView = FXCollections.observableArrayList(new VBox(),new VBox(),new VBox());
		//ObservableList<String> arrayImgView = FXCollections.observableArrayList(new String("test"),new String("test1"),new String("test2"));	
		list.setItems(arrayImgView);
	}
	private void addListToTitledPane1(TitledPane titledPane) 
	{
		titledPane.setPrefSize(titledPaneHeight, titledPaneWidth);
		ListView<HBox> list = new ListView<>(); 
		addContent(list);
		list.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
	        public void handle(KeyEvent ke) {
	        	if (ke.getCode().equals(KeyCode.ENTER))
	            {
		            createDisappearPane(list.getSelectionModel().getSelectedItem());
		            centerBox.setEffect(new BoxBlur());
		            leftBox.leftBox.setEffect(new BoxBlur());
	            }
	        }
	    });
		titledPane.setContent(list);
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
	        	//get frm ly
            	//get will return string
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	//logic.run(mainTextField.getText());
	            	//updateTabs();
	            	logic.run(mainTextField.getText());
	            	System.out.println("GUIIIIIIIIIIIIII "+ mainTextField.getText());
	            	updateNewListItems();
	            	mainTextField.setText("");
	            }
	        }

			private void updateNewListItems() 
			{
				ongoingTasks.addAll(logic.getOngoingTasks());
            	System.out.println("247 " + floatingTasks.size());
				floatingTasks.addAll(logic.getFloatingTasks());
				completedTasks.addAll(logic.getCompletedTasks());
				overdueTasks.addAll(logic.getOverdueTasks());
				addListToTitledPane(titledPaneAllTask,floatingTasks);

			
			}
	    });	
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
	    dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
	        public void handle(WindowEvent we) {
	        	centerBox.setEffect(null);
	            leftBox.leftBox.setEffect(null);
	        }
	    });  
	}
}