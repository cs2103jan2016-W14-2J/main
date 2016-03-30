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
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
	 
	private final String welcomeTab = "Welcome"; 
	private final String allTab = "All Tasks";
	private final String onGoingTab = "Ongoing Tasks";
	private final String completedTab = "Completed Tasks";
	private final String overDueTab = "Overdue Tasks";
	private final String floatingTab = "Floating Tasks";
	private final String searchTab = "Search Tasks";

	private static TabPane tabPane;
	private TextField mainTextField;
	private	boolean[] tabMap;

	private TitledPane titledPaneAllTask;
	private TitledPane titledPaneOnGoingTask;
	private TitledPane titledPaneCompletedTask;
	private TitledPane titledPaneOverdueTask;
	private TitledPane titledPaneFloatingTask;
	private TitledPane titledPaneSearchTask;

	private ArrayList<Task> allTasks;
	private ArrayList<Task> ongoingTasks;
	private ArrayList<Task> floatingTasks;
	private ArrayList<Task> completedTasks;
	private ArrayList<Task> overdueTasks;
	private ArrayList<Task> searchTasks;

	private PopOver po = new PopOver();
	private PopOver popUpFeedBack = new PopOver();

	private PopOver popOver;
	private String strFeedBack;
	private Label feedBackLabel;
	
	private UICssScaling usc;
	private VBox rightBox;
	private UILeftBox leftBox;
	private Logic logic;
	
	private Tab tabWelcome;
	private Tab tabAll;
	private Tab tabFloating;
	private Tab tabOngoing;
	private Tab tabCompleted;
	private Tab tabOverdue;
	private Tab tabSearch;

	private HBox welcomeHB;
	private VBox allVB;
	private VBox floatingVB;
	private VBox ongoingVB;
	private VBox completedVB;
	private VBox overdueVB;
	private VBox searchVB;
	
	private UIWelcomePage uiWelcome;
	private TASK_STATUS currentTask;
	private UIListener listen;
	private	HBox mainControllerRoot; 
	
	public UIRightBox(Logic logic, HBox root)
	{
		mainControllerRoot = root;
		rightBox = new VBox(); 
		this.logic = logic;
		usc = new UICssScaling();
		logger = Logger.getLogger("MyLog"); 
		tabMap = new boolean[6];

		tabWelcome = new Tab(welcomeTab);
		tabAll = new Tab(allTab);
		tabFloating = new Tab(floatingTab);
		tabOngoing = new Tab(onGoingTab);
		tabCompleted = new Tab(completedTab);
		tabOverdue = new Tab(overDueTab);
		tabSearch = new Tab(searchTab);
		
		welcomeHB = new HBox();
		allVB = new VBox();
		floatingVB = new VBox();
		ongoingVB = new VBox();
		completedVB = new VBox();
		overdueVB = new VBox();
		searchVB = new VBox();
		 
		allTasks = new ArrayList<Task>();
		ongoingTasks = new ArrayList<Task>();
		floatingTasks = new ArrayList<Task>();
		completedTasks = new ArrayList<Task>();
		overdueTasks = new ArrayList<Task>();
		searchTasks = new ArrayList<Task>();

		titledPaneAllTask = new TitledPane();
		titledPaneOnGoingTask = new TitledPane();
		titledPaneCompletedTask = new TitledPane();
		titledPaneOverdueTask = new TitledPane();
		titledPaneFloatingTask = new TitledPane();
		titledPaneSearchTask = new TitledPane();

		
		uiWelcome = new UIWelcomePage();
		tabPane = new TabPane();
		mainTextField = new TextField();
		
		listen = new UIListener();
		
	}
	//add list -> add vbtab ->
	void build(UILeftBox leftBox) 
	{
		this.leftBox = leftBox;
		setGreetingTab();
		testMethod();
		
		tabPane.setPrefSize(tabPaneHeight, tabPaneWidth);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		rightBox.getChildren().add(tabPane);
		
		mainTextField.setPrefSize(textfieldHeight, textfieldWidth);
		textFieldListener();
		rightBox.getChildren().add(mainTextField);
		leftBox.updateLeftBox();
		usc.setCssAndScalingForRightBox(tabPane);
		

		mainControllerRoot.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
			public void handle(KeyEvent ke) 
			{
				if (ke.getCode().equals(KeyCode.TAB)) 
				{
					/*if(getCurrentTab().equals(TASK_STATUS.WELCOME))
					{
					}*/
					if(getCurrentTab().equals(TASK_STATUS.ALL))
					{
						titledPaneAllTask.getContent().requestFocus();
					}
					if(getCurrentTab().equals(TASK_STATUS.FLOATING))
					{
						titledPaneFloatingTask.getContent().requestFocus();
					}
					if(getCurrentTab().equals(TASK_STATUS.ONGOING))
					{
						titledPaneOnGoingTask.getContent().requestFocus();
					}
					if(getCurrentTab().equals(TASK_STATUS.COMPLETED))
					{
						titledPaneCompletedTask.getContent().requestFocus();
					}
					if(getCurrentTab().equals(TASK_STATUS.OVERDUE))
					{
						titledPaneOverdueTask.getContent().requestFocus();
					}
					if(getCurrentTab().equals(TASK_STATUS.SEARCH))
					{
						titledPaneSearchTask.getContent().requestFocus();
					}
					

					//SET ALL THE SELECTION HERE
				}
			}
		});
	}
	
	
	private void testMethod() 
	{
		floatingTasks.addAll(logic.getFloatingTasks());
		ongoingTasks.addAll(logic.getOngoingTasks());
		completedTasks.addAll(logic.getCompletedTasks());
		overdueTasks.addAll(logic.getOverdueTasks());
		searchTasks.addAll(logic.getSearchResults());
		allTasks(); //allTasks.addAll(logic.getAllTasks());

		//tabPane.getTabs().clear();
		updateTabMap();
		if(tabMap[0]==true)
		{
			allVB.getChildren().remove(titledPaneAllTask);
			allVB.getChildren().add(titledPaneAllTask);
			if(!tabPane.getTabs().contains(tabAll))
			{
				tabAll = new Tab(allTab);
				tabPane.getTabs().add(tabAll);
			}
			tabAll.setContent(allVB);
			addListToTitledPane(titledPaneAllTask, FXCollections.observableArrayList(allTasks),TASK_STATUS.ALL);
			tabAll.setUserData(TASK_STATUS.ALL);
		}
		if(tabMap[1]==true)
		{
			floatingVB.getChildren().remove(titledPaneFloatingTask);
			floatingVB.getChildren().add(titledPaneFloatingTask);
			if(!tabPane.getTabs().contains(tabFloating))
			{
				tabFloating = new Tab(floatingTab);
				tabPane.getTabs().add(tabFloating);
			}
			tabFloating.setContent(floatingVB);
			addListToTitledPane(titledPaneFloatingTask, FXCollections.observableArrayList(floatingTasks),TASK_STATUS.FLOATING);
			tabFloating.setUserData(TASK_STATUS.FLOATING);
		}
		else
		{
			tabPane.getTabs().remove(tabFloating);
			floatingVB.getChildren().remove(titledPaneFloatingTask);
			tabFloating=null;

		}
		if(tabMap[2]==true)
		{
			ongoingVB.getChildren().remove(titledPaneOnGoingTask);
			ongoingVB.getChildren().add(titledPaneOnGoingTask);
			
			if(!tabPane.getTabs().contains(tabOngoing))
			{
				tabOngoing = new Tab(onGoingTab);
				tabPane.getTabs().add(tabOngoing);
			}
			tabOngoing.setContent(ongoingVB);
			addListToTitledPane(titledPaneOnGoingTask, FXCollections.observableArrayList(ongoingTasks),TASK_STATUS.ONGOING);
			tabOngoing.setUserData(TASK_STATUS.ONGOING);

		}
		else
		{
			tabPane.getTabs().remove(tabOngoing);
			ongoingVB.getChildren().remove(titledPaneFloatingTask);
			tabOngoing=null;

		}
		if(tabMap[3]==true)
		{
			completedVB.getChildren().remove(titledPaneCompletedTask);
			completedVB.getChildren().add(titledPaneCompletedTask);
			
			if(!tabPane.getTabs().contains(tabCompleted))
			{
				tabCompleted = new Tab(completedTab);
				tabPane.getTabs().add(tabCompleted);
			}
			tabCompleted.setContent(completedVB);
			addListToTitledPane(titledPaneCompletedTask, FXCollections.observableArrayList(completedTasks),TASK_STATUS.COMPLETED);
			tabCompleted.setUserData(TASK_STATUS.COMPLETED);
		}
		else
		{
			tabPane.getTabs().remove(tabCompleted);
			completedVB.getChildren().remove(titledPaneFloatingTask);
			tabCompleted=null;

		}
		if(tabMap[4]==true)
		{
			overdueVB.getChildren().remove(titledPaneOverdueTask);	
			overdueVB.getChildren().add(titledPaneOverdueTask);
			if(!tabPane.getTabs().contains(tabOverdue))
			{
				tabOverdue = new Tab(overDueTab);
				tabPane.getTabs().add(tabOverdue);
			}
			tabOverdue.setContent(overdueVB);
			addListToTitledPane(titledPaneOverdueTask, FXCollections.observableArrayList(overdueTasks),TASK_STATUS.OVERDUE);
			tabOverdue.setUserData(TASK_STATUS.OVERDUE);
		}
		else
		{
			tabPane.getTabs().remove(tabOverdue);
			overdueVB.getChildren().remove(titledPaneFloatingTask);
			tabOverdue=null;

		}
		if(tabMap[5]==true)
		{
			System.out.println(searchTasks.get(0).getName());

			searchVB.getChildren().remove(titledPaneSearchTask);	
			searchVB.getChildren().add(titledPaneSearchTask);
			if(!tabPane.getTabs().contains(tabSearch))
			{
				tabSearch = new Tab(searchTab);
				tabPane.getTabs().add(tabSearch);
			}
			tabSearch.setContent(searchVB);
			addListToTitledPane(titledPaneSearchTask, FXCollections.observableArrayList(overdueTasks),TASK_STATUS.SEARCH);
			tabSearch.setUserData(TASK_STATUS.SEARCH);
		}
		else
		{
			tabPane.getTabs().remove(tabSearch);
			searchVB.getChildren().remove(titledPaneSearchTask);
			tabSearch=null;
		}
		
		

		//addListToTitledPane(titledPaneAllTask, FXCollections.observableArrayList(allTasks),TASK_STATUS.FLOATING);
		//addListToTitledPane(titledPaneSearchTask, FXCollections.observableArrayList(searchTasks),TASK_STATUS.FLOATING);
		
		//tabSearch.setUserData(TASK_STATUS.FLOATING);
		
		//tabAll.setUserData(TASK_STATUS.FLOATING);
		
	}
	private void updateTabMap() {
		
		//change here
		if(logic.getFloatingTasks().size()!=0)
		{
			tabMap[0] = true;
		}
		else 
		{
			tabMap[0] = false;

		}
		if(logic.getFloatingTasks().size()!=0)
		{
			tabMap[1] = true;
		}
		else 
		{
			tabMap[1] = false;

		}
		if(logic.getOngoingTasks().size()!=0)
		{
			tabMap[2] = true;
		}
		else 
		{
			tabMap[2] = false;

		}
		if(logic.getCompletedTasks().size()!=0)
		{
			tabMap[3] = true;
		}
		else 
		{
			tabMap[3] = false;

		}
		if(logic.getOverdueTasks().size()!=0)
		{
			tabMap[4] = true;
		}
		else 
		{
			tabMap[4] = false;

		}
		if(logic.getSearchResults().size()!=0)
		{
			tabMap[5] = true;
		}
		else 
		{
			tabMap[5] = false;

		}
		
	}
	private void allTasks() {
		allTasks.addAll(logic.getFloatingTasks());
		allTasks.addAll(logic.getOngoingTasks());
		allTasks.addAll(logic.getCompletedTasks());
		allTasks.addAll(logic.getOverdueTasks());		
		
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
									lsg.getCheckFlag().selectedProperty().addListener(new ChangeListener<Boolean>() 
									{
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


	
	private Path findCaret(Parent root) {
	    for (Node n : root.getChildrenUnmodifiable()) {
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
		
		if(getCurrentTab().equals(TASK_STATUS.ALL))
		{
			moreName = allTasks.get(x).getName();
		}
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
		if(getCurrentTab().equals(TASK_STATUS.SEARCH))
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
		strFeedBack = logic.run(mainTextField.getText());
		System.out.println(logic.getStatus()+"...........................................................................................................................................................................................................................................................................................................................................................................................");
		currentTask = logic.getStatus();
		
    	VBox vbPop = new VBox();
    	feedBackLabel = new Label(strFeedBack);
		vbPop.getChildren().add(feedBackLabel);
		vbPop.setPrefSize(1380, 50);
		feedBackLabel.setId("lblFeedBack");
		popUpFeedBack.consumeAutoHidingEventsProperty().set(false);
		popUpFeedBack.setAutoFix(true);
		popUpFeedBack.setContentNode(vbPop);
		Path caret = findCaret(mainTextField);
        Point2D screenLoc = findScreenLocation(caret);
		popUpFeedBack.show(mainTextField, screenLoc.getX(), screenLoc.getY() + 70);
	    mainTextField.requestFocus();
	    
	    allTasks.clear();
	    ongoingTasks.clear();
		floatingTasks.clear();
		completedTasks.clear();
		overdueTasks.clear();
		
		testMethod();
		
		
	
    	mainTextField.setText("");
    	if(currentTask == TASK_STATUS.ALL)
 		{
 			tabPane.getSelectionModel().select(tabAll);
 		}
    	else if(currentTask == TASK_STATUS.COMPLETED)
		{
			tabPane.getSelectionModel().select(tabCompleted);
		}
		else if(currentTask == TASK_STATUS.ONGOING)
		{
			tabPane.getSelectionModel().select(tabOngoing);
		}
		else if(currentTask == TASK_STATUS.FLOATING)
		{
			tabPane.getSelectionModel().select(tabFloating);
		}
		else if(currentTask == TASK_STATUS.OVERDUE)
		{
			tabPane.getSelectionModel().select(tabOverdue);
		}	  
		else if(currentTask == TASK_STATUS.SEARCH)
		{
			tabPane.getSelectionModel().select(tabSearch);
		}	
		else
		{
			tabPane.getSelectionModel().select(null);
		}
		leftBox.updateLeftBox();
	}
	public static TASK_STATUS getCurrentTab() 
	{
		if(tabPane.getSelectionModel().getSelectedItem()==null )
		{
			//return TASK_STATUS.WELCOME;
			return TASK_STATUS.OVERDUE;
		}	
		System.out.println((TASK_STATUS) tabPane.getSelectionModel().getSelectedItem().getUserData());
		
		return (TASK_STATUS) tabPane.getSelectionModel().getSelectedItem().getUserData();
	}

	public TextField getMainTextField()
	{
		return mainTextField;
	}
	
	
	
	private void setGreetingTab() 
	{
		String str = "TASK_STATUS.WELCOME";
		tabWelcome.setUserData(TASK_STATUS.FLOATING);
		tabPane.getTabs().add(tabWelcome);
		tabWelcome.setContent(welcomeHB);
		uiWelcome.setRoot(welcomeHB);
	}
	
	public int getOngoingSize() 
	{
		if(ongoingTasks==null)
		{return 0;}
		return ongoingTasks.size();
	}
	public int getFloatingTasksSize() 
	{
		if(floatingTasks==null)
		{return 0;}
		return floatingTasks.size();
	}
	public int getCompletedTasksSize() 
	{
		if(completedTasks==null)
		{return 0;}
		return completedTasks.size();
	}
	public int getOverdueTasksSize() 
	{
		if(overdueTasks==null)
		{return 0;}
		return overdueTasks.size();
	}

	public TextField getTextField() 
	{
		return mainTextField;
	}
	public String getFeedBack()
	{
		return strFeedBack;
	}
	public VBox getRoot() 
	{
		return rightBox;
	}
	public TabPane getTabPane() 
	{
		return tabPane;
	}
	public ArrayList<Tab> getListTabs()
	{
		ArrayList<Tab> tabList = new ArrayList<Tab>();
		for(int x=0;x<tabPane.getTabs().size();x++)
		{
			tabList.addAll(tabPane.getTabs());
		}
		return tabList;
	}
	public int getTotalTabs()
	{
		return tabPane.getTabs().size();
	}
	public void setCursorTextField() {
		mainTextField.positionCaret(1);		
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

	
}