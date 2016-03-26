package GUI;

import java.awt.Robot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.controlsfx.control.PopOver;

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
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

/*
 *@author Chiang Jia Feng
 *@Description: rightBox (Tabpane and Textfield)
 */

public class UIRightBox {
	private Logger logger; 

	private final double textfieldWidth = 55; // centerBox txtfield size
	private final double textfieldHeight = 1233;
	private final double tabPaneWidth = 1000; // centerBox tabPane size
	private final double tabPaneHeight = 1450;
	private final double titledPaneWidth = 1000; // centerBox titledPane size
	private final double titledPaneHeight = 1450;

	private final String onGoingTab = "Ongoing Tasks";
	private final String completedTab = "Completed Tasks";
	private final String overDueTab = "Overdue Tasks";
	private final String floatingTab = "Floating Tasks";

	private static TabPane tabPane;
	private TextField mainTextField;
	
	private TitledPane titledPaneOnGoingTask;
	private TitledPane titledPaneCompletedTask;
	private TitledPane titledPaneOverdueTask;
	private TitledPane titledPaneFloatingTask;
	
	private ArrayList<Task> ongoingTasks;
	private ArrayList<Task> floatingTasks;
	private ArrayList<Task> completedTasks;
	private ArrayList<Task> overdueTasks;

	private PopOver po = new PopOver();
	private PopOver popUpFeedBack = new PopOver();

	private PopOver popOver;
	private String strFeedBack;
	private Label feedBackLabel;
	
	private UICssScaling usc;
	private VBox rightBox;
	private UILeftBox leftBox;
	private Logic logic;
	
	private Tab tabFloating;
	private Tab tabOngoing;
	private Tab tabCompleted;
	private Tab tabOverdue;

	private VBox floatingVB;
	private VBox ongoingVB;
	private VBox completedVB;
	private VBox overdueVB;
	
	private int prevOngoingTasks;
	private int prevFloatingTasks;
	private int prevOverdueTasks;
	private int prevCompletedTasks;
		
	public UIRightBox(Logic logic)
	{
		rightBox = new VBox(); 
		this.logic = logic;
		usc = new UICssScaling();
		logger = Logger.getLogger("MyLog"); 

		tabFloating = new Tab(floatingTab);
		tabOngoing = new Tab(onGoingTab);
		tabCompleted = new Tab(completedTab);
		tabOverdue = new Tab(overDueTab);
		
		floatingVB = new VBox();
		ongoingVB = new VBox();
		completedVB = new VBox();
		overdueVB = new VBox();
		 
		ongoingTasks = new ArrayList<Task>();
		floatingTasks = new ArrayList<Task>();
		completedTasks = new ArrayList<Task>();
		overdueTasks = new ArrayList<Task>();

		titledPaneOnGoingTask = new TitledPane();
		titledPaneCompletedTask = new TitledPane();
		titledPaneOverdueTask = new TitledPane();
		titledPaneFloatingTask = new TitledPane();

		tabPane = new TabPane();
		mainTextField = new TextField();

	}
	//add list -> add vbtab ->
	void build(UILeftBox leftBox) 
	{
		this.leftBox = leftBox;

		floatingTasks.addAll(logic.getFloatingTasks());
		ongoingTasks.addAll(logic.getOngoingTasks());
		completedTasks.addAll(logic.getCompletedTasks());
		overdueTasks.addAll(logic.getOverdueTasks());

		floatingVB.getChildren().add(titledPaneFloatingTask);
		ongoingVB.getChildren().add(titledPaneOnGoingTask);
		completedVB.getChildren().add(titledPaneCompletedTask);
		overdueVB.getChildren().add(titledPaneOverdueTask);

		tabFloating.setContent(floatingVB);
		tabOngoing.setContent(ongoingVB);
		tabCompleted.setContent(completedVB);
		tabOverdue.setContent(overdueVB);
		
		tabPane.getTabs().add(tabFloating);
		tabPane.getTabs().add(tabOngoing);
		tabPane.getTabs().add(tabCompleted);
		tabPane.getTabs().add(tabOverdue);

		addListToTitledPane(titledPaneFloatingTask, FXCollections.observableArrayList(floatingTasks),TASK_STATUS.FLOATING);
		addListToTitledPane(titledPaneOnGoingTask, FXCollections.observableArrayList(ongoingTasks),TASK_STATUS.ONGOING);
		addListToTitledPane(titledPaneCompletedTask, FXCollections.observableArrayList(completedTasks),TASK_STATUS.COMPLETED);
		addListToTitledPane(titledPaneOverdueTask, FXCollections.observableArrayList(overdueTasks),TASK_STATUS.OVERDUE);

		tabOngoing.setUserData(TASK_STATUS.ONGOING);
		tabCompleted.setUserData(TASK_STATUS.COMPLETED);
		tabOverdue.setUserData(TASK_STATUS.OVERDUE);
		tabFloating.setUserData(TASK_STATUS.FLOATING);
		
		tabPane.setPrefSize(tabPaneHeight, tabPaneWidth);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		rightBox.getChildren().add(tabPane);
		
		mainTextField.setPrefSize(textfieldHeight, textfieldWidth);
		textFieldListener();
		rightBox.getChildren().add(mainTextField);
		
		
		leftBox.updateChart();
	}
		
	
	public static TASK_STATUS getCurrentTab() 
	{
		return (TASK_STATUS) tabPane.getSelectionModel().getSelectedItem().getUserData();
	}
	
	public TextField getMainTextField()
	{
		return mainTextField;
	}

	private void addListToTitledPane(TitledPane titledPane, ObservableList<Task> listTask, TASK_STATUS typeOfTask) {
		initTitledPane(titledPane);
		final ListView<Task> listViewLabel = new ListView<Task>(listTask);
		new Thread(new Runnable() {
			@Override
			public void run() {
				listViewLabel.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
					public ListCell<Task> call(ListView<Task> param) {
						ListCell<Task> cell = new ListCell<Task>() {
							@Override
							public void updateItem(Task item, boolean empty) {
								super.updateItem(item, empty);
								if (item != null) {
									
									String strTagging = item.getTag();
									String currentIndex=Integer.toString(this.getIndex() + 1);
									UICellComponents lsg = null;
									
									if ((item instanceof DeadlinedTask) == true) 
									{
										System.out.println("DeadlinedTask");
										lsg = new UICellComponents(currentIndex, strTagging,item.getName(), null, ((DeadlinedTask) item).getEndDateTime(), item.getFlag());
										logger.info("DeadlinedTask");  
									
									} 
									else if ((item instanceof Event) == true) 
									{
										System.out.println("in event" + ((Event) item).getEndDateTime());
										lsg = new UICellComponents(currentIndex, strTagging, item.getName(), ((Event) item).getStartDateTime(),((Event) item).getEndDateTime(), item.getFlag());
										logger.info("Event");  
									}
									else if ((item instanceof Task) == true) 
									{
										System.out.println("in Task");
										lsg = new UICellComponents(currentIndex,strTagging,item.getName(), null, null, item.getFlag());
										
										logger.info("Task");  
									}
									lsg.getCheckFlag().selectedProperty().addListener(new ChangeListener<Boolean>() {
									    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
									        if(oldValue ==false)
									        {
									        	setTextFieldAndEnter("flag "+ currentIndex);
									        	System.out.println("flag ");
									        }
									        else if(oldValue==true) 
									        {
									        	setTextFieldAndEnter("unflag "+ currentIndex);
									        	System.out.println("unflag ");
									        }
									        
									    }
									});
									
									setTooltip(lsg.getToolTip());
									setGraphic(lsg.getCellRoot());
								}
							}
						};
						// cell.setStyle("-fx-control-background: Transparent");

						return cell;
					}
				});
				
				listViewLabel.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
					public void changed(ObservableValue<? extends Task> observable, Task oldValue, Task newValue) {
						System.out.println("selection changed");
					}
				});
				listViewLabel.setOnKeyPressed(new EventHandler<KeyEvent>() {
					public void handle(KeyEvent ke) {
						if (ke.getCode().equals(KeyCode.ENTER)) {
							createDisappearPane(listViewLabel);
						}
					}
				});
				listViewLabel.setOnMouseClicked(new EventHandler<MouseEvent>()
				{
					@Override
					public void handle(MouseEvent me) {
							if(listViewLabel.getSelectionModel().getSelectedIndex()==-1)
							{
								mainTextField.setText("");
							}
							else
							{
								int indexToEdit = listViewLabel.getSelectionModel().getSelectedIndex()+1;
								mainTextField.setText("edit " + indexToEdit);
							}
					}
				});
				
				
				
			}
			
		}).start();			
		
		//leftBox.updateChart();
		listViewLabel.scrollTo(listTask.size()-1);
		titledPane.setContent(listViewLabel);
	}
	private void createLog()
	{
		    FileHandler fh;  
		    try {  

		        // This block configure the logger with handler and formatter  
		        fh = new FileHandler("MyLogFile.log");  
		        logger.addHandler(fh);
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter);  

		        // the following statement is used to log any messages  
		        logger.info("My first log");  

		    } catch (SecurityException e) {  
		        e.printStackTrace();  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    }  

		    logger.info("test1");  
	}
	

	private void initTitledPane(TitledPane titledPane) {
		titledPane.setPrefSize(titledPaneHeight, titledPaneWidth);
	}

	private void textFieldListener() {
		
		mainTextField.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
			public void handle(KeyEvent ke)
	        {		
				if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	runCommand();
	            }	
	        }
	    });	
	}
	protected void runCommand()
	{
		prevOngoingTasks= ongoingTasks.size();
		prevFloatingTasks = floatingTasks.size();
		prevCompletedTasks = completedTasks.size();
		prevOverdueTasks = overdueTasks.size();	
		
		strFeedBack = logic.run(mainTextField.getText());
    	VBox vbPop = new VBox();
    	feedBackLabel = new Label(strFeedBack);
		vbPop.getChildren().add(feedBackLabel);
		vbPop.setPrefSize(1380, 50);
		feedBackLabel.setId("lblFeedBack");
		popUpFeedBack.setAutoFix(true);
		popUpFeedBack.setContentNode(vbPop);
		Path caret = findCaret(mainTextField);
        Point2D screenLoc = findScreenLocation(caret);
		popUpFeedBack.show(mainTextField, screenLoc.getX(), screenLoc.getY() + 70);
	    mainTextField.requestFocus();
	    
	    ongoingTasks.clear();
		floatingTasks.clear();
		completedTasks.clear();
		overdueTasks.clear();
		
		ongoingTasks.addAll(logic.getOngoingTasks());
		floatingTasks.addAll(logic.getFloatingTasks());
		completedTasks.addAll(logic.getCompletedTasks());
		overdueTasks.addAll(logic.getOverdueTasks());
		
		tabFloating.setContent(floatingVB);
		tabPane.getTabs().add(tabFloating);
    	addListToTitledPane(titledPaneFloatingTask, FXCollections.observableArrayList(floatingTasks),TASK_STATUS.FLOATING);

    	tabOngoing.setContent(ongoingVB);
		tabPane.getTabs().add(tabOngoing);
    	addListToTitledPane(titledPaneOnGoingTask, FXCollections.observableArrayList(ongoingTasks),TASK_STATUS.ONGOING);

		tabCompleted.setContent(completedVB);
		tabPane.getTabs().add(tabCompleted);
    	addListToTitledPane(titledPaneCompletedTask, FXCollections.observableArrayList(completedTasks),TASK_STATUS.COMPLETED);

		tabOverdue.setContent(overdueVB);
		tabPane.getTabs().add(tabOverdue);
    	addListToTitledPane(titledPaneOverdueTask, FXCollections.observableArrayList(overdueTasks),TASK_STATUS.OVERDUE);
	
    	mainTextField.setText("");
    	if(prevCompletedTasks!=completedTasks.size())
		{
			tabPane.getSelectionModel().select(tabCompleted);
		}
		else if(prevOngoingTasks!=ongoingTasks.size())
		{
			tabPane.getSelectionModel().select(tabOngoing);
		}
		else if(prevFloatingTasks!=floatingTasks.size())
		{
			tabPane.getSelectionModel().select(tabFloating);
		}
		else if(prevOverdueTasks!=overdueTasks.size())
		{
			tabPane.getSelectionModel().select(tabOngoing);
		}	  
    	leftBox.updateChart();		
	}
	private Path findCaret(Parent parent) {
	    for (Node n : parent.getChildrenUnmodifiable()) {
	      if (n instanceof Path) {
	        return (Path) n;
	      } else if (n instanceof Parent) {
	        Path p = findCaret((Parent) n);
	        if (p != null) {
	          return p;
	        }
	      }
	    }
	    return null;
	  }

	  private Point2D findScreenLocation(Node node) {
	    double x = 0;
	    double y = 0;
	    for (Node n = node; n != null; n=n.getParent()) {
	      Bounds parentBounds = n.getBoundsInParent();
	      x += parentBounds.getMinX();
	      y += parentBounds.getMinY();
	    }
	    Scene scene = node.getScene();
	    x += scene.getX();
	    y += scene.getY();
	    Window window = scene.getWindow();
	    x += window.getX();
	    y += window.getY();
	    Point2D screenLoc = new Point2D(x, y);
	    return screenLoc;
	  }




	private void createDisappearPane(ListView<Task> listViewLabel) 
	{
		
		
		int x= listViewLabel.getSelectionModel().getSelectedIndex();
		VBox vbPop = new VBox();
		vbPop.setPrefSize(500, 2000);
		po.arrowSizeProperty().set(0);
		String moreName=null;
		
		if(getCurrentTab().equals(TASK_STATUS.ONGOING))
		{
			moreName =ongoingTasks.get(x).getName();
		}
		if(getCurrentTab().equals(TASK_STATUS.FLOATING))
		{
			moreName =floatingTasks.get(x).getName();
		}
		if(getCurrentTab().equals(TASK_STATUS.COMPLETED))
		{
			moreName =completedTasks.get(x).getName();
		}
		if(getCurrentTab().equals(TASK_STATUS.OVERDUE))
		{
			moreName = overdueTasks.get(x).getName();
		}
	    Label lbl = new Label(moreName);
	    lbl.setWrapText(true);
	    
	    lbl.setPrefSize(5000, 1000);
		vbPop.getChildren().add(lbl);

	    po.setY(listViewLabel.getHeight());
	    po.setContentNode(vbPop);
	    po.show(listViewLabel);
	}

	public int getOngoingSize() 
	{
		if(ongoingTasks==null)
		{return 0;}
		return ongoingTasks.size();
	}
	public int floatingTasksSize() 
	{
		if(floatingTasks==null)
		{return 0;}
		return floatingTasks.size();
	}
	public int completedTasksSize() 
	{
		if(completedTasks==null)
		{return 0;}
		return completedTasks.size();
	}
	public int overdueTasksSize() 
	{
		if(overdueTasks==null)
		{return 0;}
		return overdueTasks.size();
	}

	public void setCursorTextField() {
		mainTextField.positionCaret(1);		
	}

	public TextField getTextField() 
	{
		return mainTextField;
	}
	public void setTextField(String str) 
	{
		mainTextField.textProperty().set(str);
		mainTextField.setText(str);
	}
	public void setTextFieldAndEnter(String str) 
	{
		mainTextField.textProperty().set(str);
		mainTextField.setText(str);
		runCommand();
	}
	public String getFeedBack()
	{
		return strFeedBack;
	}
	public VBox getRoot() {
		return rightBox;
	}



}