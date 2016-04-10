package GUI;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;

import Logic.Logic;
import Task.Category;
import Task.TASK_TYPE;
import Task.Task;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;

//@@author A0125372L
public class UIRightBox {
	private Logger logger; 

	private final double textfieldWidth = 55; // centerBox txtfield size
	private final double textfieldHeight = 1233;
	private final double tabPaneWidth = 1500; // centerBox tabPane size
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

	private ArrayList<Task> allTasks;
	private ArrayList<Task> ongoingTasks;
	private ArrayList<Task> floatingTasks;
	private ArrayList<Task> completedTasks;
	private ArrayList<Task> overdueTasks;
	private ArrayList<Task> searchTasks;

	private PopOver popUpCell = new PopOver();

	private String strFeedBack;
	private Label feedBackLabel;
	
	private UICssScaling ucs;
	private VBox rightBox;
	private UILeftBox leftBox;
	private Logic logic;
	
	private UIWelcomePage uiWelcome;
	private UI_TAB currentTask;
	private UIListener listen;
	private	HBox mainControllerRoot; 
	private Pagination pagination;
	private UIPopUpFeedBack uiPopUpFeedBack;
	private UIMakeTag uiMakeTag;
	private PopOver popUpFeedBack = new PopOver();
	private Scene scene;
	
	private Tab tabWelcome;
	private Tab tabAll;
	private Tab tabFloating;
	private Tab tabOngoing;
	private Tab tabCompleted;
	private Tab tabOverdue;
	private Tab tabSearch;

	private StackPane spAllTask = new StackPane();
	private StackPane vbOnGoingTask = new StackPane();
	private StackPane vbCompletedTask = new StackPane();
	private StackPane vbOverdueTask = new StackPane();
	private StackPane vbFloatingTask = new StackPane();
	private StackPane vbSearchTask = new StackPane();
	
	private HBox welcomeHB;
	private VBox allVB;
	private VBox floatingVB;
	private VBox ongoingVB;
	private VBox completedVB;
	private VBox overdueVB;
	private VBox searchVB;
	
	private int intAllOverdueHashCode=0;
	private int intAllOngoingHashCode=0;
	private int intAllFloatingHashCode=0;
	private int intAllCompletedHashCode=0;
	private ArrayList<Integer> intIndexAllTabCell = new ArrayList<Integer>();
	ArrayList<Integer> intHashCode = new ArrayList<Integer>();
	
	private int intCellSwitching=0;
	
	public UIRightBox(Logic logic, HBox root, Scene scene)
	{
		mainControllerRoot = root;
		rightBox = new VBox(); 
		this.scene = scene;
		this.logic = logic;
		ucs = new UICssScaling();
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

		
		uiWelcome = new UIWelcomePage();
		tabPane = new TabPane();
		mainTextField = new TextField();
		listen = new UIListener();
		uiPopUpFeedBack = new UIPopUpFeedBack(popUpFeedBack, mainTextField);
		
	}
	//add list -> add vbtab ->
	void build(UILeftBox leftBox) 
	{
		this.leftBox = leftBox;
		createGreetingTab();
		testMethod();
		
		tabPane.setPrefSize(tabPaneHeight, tabPaneWidth);
		//tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		rightBox.getChildren().add(tabPane);
		
		mainTextField.setPrefSize(textfieldHeight, textfieldWidth);
		textFieldListener();
		rightBox.getChildren().add(mainTextField);
		leftBox.updateLeftBox();
		ucs.setCssAndScalingForRightBox(tabPane,mainTextField);
		
		mainControllerRoot.setOnKeyReleased(new EventHandler<KeyEvent>() 
		{
			public void handle(KeyEvent ke) 
			{
				if (ke.getCode().equals(KeyCode.TAB)) 
				{
				    pagination.setStyle("");
				}
			}
		});
		mainControllerRoot.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
			public void handle(KeyEvent ke) 
			{
				if (ke.getCode().equals(KeyCode.ALT)) 
				{
					pagination.setStyle("");
				}
				if (ke.getCode().equals(KeyCode.TAB)) 
				{
					if(getCurrentTab().equals(UI_TAB.WELCOME))
					{
						pagination.requestFocus();
					    pagination.setStyle("-fx-border-color:black;");
					}
					if(getCurrentTab().equals(UI_TAB.ALL))
					{
						spAllTask.getChildren().get(0).requestFocus();
					}
					if(getCurrentTab().equals(UI_TAB.FLOATING))
					{
						vbFloatingTask.getChildren().get(0).requestFocus();
					}
					if(getCurrentTab().equals(UI_TAB.ONGOING))
					{
						vbOnGoingTask.getChildren().get(0).requestFocus();
					}
					if(getCurrentTab().equals(UI_TAB.COMPLETED))
					{
						vbCompletedTask.getChildren().get(0).requestFocus();
					}
					if(getCurrentTab().equals(UI_TAB.OVERDUE))
					{
						vbOverdueTask.getChildren().get(0).requestFocus();
					}
					if(getCurrentTab().equals(UI_TAB.SEARCH))
					{
						vbSearchTask.getChildren().get(0).requestFocus();
					}
					//SET ALL THE SELECTION HERE
				}
			}
		});
		
		allVB.getChildren().add(spAllTask);
		floatingVB.getChildren().add(vbFloatingTask);
		ongoingVB.getChildren().add(vbOnGoingTask);
		completedVB.getChildren().add(vbCompletedTask);
		overdueVB.getChildren().add(vbOverdueTask);
		searchVB.getChildren().add(vbSearchTask);
		
		 
		chooseTab();
	}
	private void createGreetingTab() {
		pagination = new Pagination(1, 0);
		tabWelcome.setUserData(UI_TAB.WELCOME);
		tabPane.getTabs().add(tabWelcome);
		tabWelcome.setContent(welcomeHB);
		uiWelcome.setRoot(welcomeHB,pagination);
	}
	private void chooseTab() 
	{
		if(tabPane.getTabs().contains(tabAll))
		{
			tabPane.getSelectionModel().select(tabAll);
		}		
		else
		{
			tabPane.getSelectionModel().select(tabWelcome);
		}
	}
	private ArrayList<Task> getAll()
	{
		intIndexAllTabCell.clear();
		intHashCode.clear();
		ArrayList<Task> allTask = new ArrayList<Task>();
		
		Task stubTask1 = new Task();
		intAllOverdueHashCode = stubTask1.hashCode();
		allTask.add(stubTask1); //add title for overdueTasks
		allTask.addAll(overdueTasks);
		
		Task stubTask2 = new Task();
		intAllOngoingHashCode = stubTask2.hashCode();
		allTask.add(stubTask2);// add title for ongoingTasks
		allTask.addAll(ongoingTasks);
		
		Task stubTask3 = new Task();
		intAllFloatingHashCode = stubTask3.hashCode();
		allTask.add(stubTask3);// add title for floatingTasks
		allTask.addAll(floatingTasks);
		
		Task stubTask4 = new Task();
		intAllCompletedHashCode = stubTask4.hashCode();
		allTask.add(stubTask4);// add title for completedTasks
		allTask.addAll(completedTasks);

		for(int x=0,y=0;x<allTask.size();x++,y++)
		{
			int currentHashCode = allTask.get(x).hashCode();
			if(intAllOverdueHashCode ==currentHashCode)
			{
				intIndexAllTabCell.add(-1);
				y--;
			}
			else if(intAllOngoingHashCode ==currentHashCode)
			{
				intIndexAllTabCell.add(-1);
				y--;
			}
			else if(intAllFloatingHashCode ==currentHashCode)
			{
				intIndexAllTabCell.add(-1);
				y--;
			}
			else if(intAllCompletedHashCode ==currentHashCode)
			{
				intIndexAllTabCell.add(-1);
				y--;
			}
			else
			{
				intIndexAllTabCell.add(y);
				intHashCode.add(currentHashCode);
			}
			
		}
		
		System.out.println(intIndexAllTabCell);
		System.out.println(intHashCode);
		
		
		
		return allTask;
	}
	public void testMethod() 
	{
		
		allTasks.clear();
	    ongoingTasks.clear();
		floatingTasks.clear();
		completedTasks.clear();
		overdueTasks.clear();
		searchTasks.clear(); 	 
			
		floatingTasks.addAll(logic.getFloatingTasks());
		ongoingTasks.addAll(logic.getOngoingTasks());
		completedTasks.addAll(logic.getCompletedTasks());
		overdueTasks.addAll(logic.getOverdueTasks());
		searchTasks.addAll(logic.getSearchResults());
		allTasks.addAll(getAll());
		//allTasks.addAll(logic.getAll());

		//tabPane.getTabs().clear();
		updateTabMap();
		if(tabMap[0]==true)
		{
			if(!tabPane.getTabs().contains(tabAll))
			{
				tabAll = new Tab(allTab);
				tabPane.getTabs().add(tabAll);
			}
			tabAll.setContent(allVB);
			addListToTitledPane(spAllTask, FXCollections.observableArrayList(allTasks),UI_TAB.ALL);
			tabAll.setUserData(UI_TAB.ALL);
		}
		else
		{
			tabPane.getTabs().remove(tabAll);
			tabAll=null;

		}
		if(tabMap[1]==true)
		{
			if(!tabPane.getTabs().contains(tabFloating))
			{
				tabFloating = new Tab(floatingTab);
				tabPane.getTabs().add(tabFloating);
			}
			tabFloating.setContent(floatingVB);
			addListToTitledPane(vbFloatingTask, FXCollections.observableArrayList(floatingTasks),UI_TAB.FLOATING);
			tabFloating.setUserData(UI_TAB.FLOATING);
		}
		else
		{
			tabPane.getTabs().remove(tabFloating);
			tabFloating=null;

		}
		if(tabMap[2]==true)
		{
			
			if(!tabPane.getTabs().contains(tabOngoing))
			{
				tabOngoing = new Tab(onGoingTab);
				tabPane.getTabs().add(tabOngoing);
			}
			tabOngoing.setContent(ongoingVB);
			addListToTitledPane(vbOnGoingTask, FXCollections.observableArrayList(ongoingTasks),UI_TAB.ONGOING);
			tabOngoing.setUserData(UI_TAB.ONGOING);

		}
		else
		{
			tabPane.getTabs().remove(tabOngoing);
			tabOngoing=null;

		}
		if(tabMap[3]==true)
		{
			
			if(!tabPane.getTabs().contains(tabCompleted))
			{
				tabCompleted = new Tab(completedTab);
				tabPane.getTabs().add(tabCompleted);
			}
			tabCompleted.setContent(completedVB);
			addListToTitledPane(vbCompletedTask, FXCollections.observableArrayList(completedTasks),UI_TAB.COMPLETED);
			tabCompleted.setUserData(UI_TAB.COMPLETED);
		}
		else
		{
			tabPane.getTabs().remove(tabCompleted);
			tabCompleted=null;

		}
		if(tabMap[4]==true)
		{

			if(!tabPane.getTabs().contains(tabOverdue))
			{
				tabOverdue = new Tab(overDueTab);
				tabPane.getTabs().add(tabOverdue);
			}
			tabOverdue.setContent(overdueVB);
			addListToTitledPane(vbOverdueTask, FXCollections.observableArrayList(overdueTasks),UI_TAB.OVERDUE);
			tabOverdue.setUserData(UI_TAB.OVERDUE);
		}
		else
		{
			tabPane.getTabs().remove(tabOverdue);
			tabOverdue=null;

		}
		if(tabMap[5]==true)
		{


			if(!tabPane.getTabs().contains(tabSearch))
			{
				tabSearch = new Tab(searchTab);
				tabPane.getTabs().add(tabSearch);
			}
			tabSearch.setContent(searchVB);
			addListToTitledPane(vbSearchTask, FXCollections.observableArrayList(searchTasks),UI_TAB.SEARCH);
			tabSearch.setUserData(UI_TAB.SEARCH);
		}
		else
		{
			tabPane.getTabs().remove(tabSearch);
			tabSearch=null;
		}
		if(tabPane.getTabs().size()>1)
		{
			tabPane.getTabs().remove(tabWelcome);
			
		}
		else if(tabPane.getTabs().size()==0)
		{
			tabPane.getTabs().add(tabWelcome);
		}
	}
	private void updateTabMap() {
		
		//change here
		if(logic.getAll().size()!=0)
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
	private void addListToTitledPane(StackPane spTask, ObservableList<Task> listTask, UI_TAB typeOfTask) {
		spTask.setPrefSize(titledPaneHeight, titledPaneWidth);		
		final ListView<Task> listViewLabel = new ListView<Task>(listTask);

		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				listViewLabel.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
					public ListCell<Task> call(ListView<Task> param) {
						ListCell<Task> cell = new ListCell<Task>() {
							@Override
							public void updateItem(Task item, boolean empty) {
								super.updateItem(item, empty);

								if (item != null) {
									if(intAllCompletedHashCode==item.hashCode()||intAllOverdueHashCode==item.hashCode() || intAllOngoingHashCode==item.hashCode() ||intAllFloatingHashCode==item.hashCode() )
									{	
										UICellComponents lsg = null;

										HBox hbLblTitle = new HBox();
										Label lblTitle = new Label();
										hbLblTitle.getChildren().add(lblTitle);

										String strTitle="";
										if(intAllOverdueHashCode==item.hashCode())
										{
											lblTitle.setId("Overdue");
											strTitle = "Over-Due Tasks (" + logic.getOverdueTasks().size()+")"; 
										}
										else if(intAllOngoingHashCode==item.hashCode())
										{
											lblTitle.setId("Ongoing");
											strTitle = "On-Going Tasks (" + logic.getOngoingTasks().size()+")"; 
										}
										else if(intAllFloatingHashCode==item.hashCode())
										{
											lblTitle.setId("Floating");
											strTitle = "Floating Tasks (" + logic.getFloatingTasks().size()+")"; 
										}
										else if(intAllCompletedHashCode==item.hashCode())
										{
											lblTitle.setId("Completed");
											strTitle = "Completed Tasks (" + logic.getCompletedTasks().size()+")"; 
										}
										lsg = new UICellComponents(hbLblTitle, lblTitle,strTitle);

										
										ucs.cssAllTitle(lblTitle);
										setGraphic(hbLblTitle);
										
										
									}
									else
									{
										ArrayList<Category> strTagging = logic.mapCategories(item.getCategories());

										UICellComponents lsg = null;
										String currentIndex;
										if(typeOfTask.equals(UI_TAB.ALL))
										{
											currentIndex=Integer.toString(intIndexAllTabCell.get(this.getIndex())  + 1);
										}
										else
										{
											currentIndex=Integer.toString(this.getIndex()  + 1);

										}

										if (item.getType() == TASK_TYPE.DEADLINED) 
										{
											lsg = new UICellComponents(logic,currentIndex, strTagging, item.getName(), null, item.getEndString(), item.getFlag());
											logger.info("DeadlinedTask");  
										} 
										else if (item.getType() == TASK_TYPE.EVENT) 
										{											
											lsg = new UICellComponents(logic,currentIndex, strTagging, item.getName(), item.getStartString(),item.getEndString(), item.getFlag());
											logger.info("Event");  
											
										}
										else if (item.getType() == TASK_TYPE.FLOATING) 
										{
											lsg = new UICellComponents(logic,currentIndex, strTagging ,item.getName(), null, null, item.getFlag());
											logger.info("Task");  
										}
										lsg.getCheckFlag().selectedProperty().addListener(new ChangeListener<Boolean>() 
										{
										    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
										        if(oldValue ==false)
										        {
										        	setTextFieldAndEnter("flag "+ currentIndex);
										        	//System.out.println("flag ");
										        }
										        else if(oldValue==true) 
										        {
										        	setTextFieldAndEnter("unflag "+ currentIndex);
										        //	System.out.println("unflag ");
										        }
										        
										    }
										});
										
										setTooltip(lsg.getToolTip());
										setGraphic(lsg.getCellRoot());
									}
									
									
								}
							}
						};
						// cell.setStyle("-fx-control-background: Transparent");
					
						return cell;
					}
				});
				listViewLabel.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
					public void changed(ObservableValue<? extends Task> observable, Task oldValue, Task newValue) 
					{
						
						
					}
				});
				
				listViewLabel.setOnKeyPressed(new EventHandler<KeyEvent>() {
					public void handle(KeyEvent ke) {
						if (ke.getCode().equals(KeyCode.ENTER)) {
							createDisappearPane(listViewLabel);
						}
						if (ke.getCode().equals(KeyCode.ESCAPE)) 
						{
							Platform.runLater(new Runnable() 
	    	                {
	    	                    @Override
	    	                    public void run() 
	    	                    {
	    	                      assert(logic.save()!=null); 
	    	   		        	  logic.save();
	    	   	     	          System.exit(0);
	    	                    }
	    	                });
						}
					}
				});
				
			}
			
		}).start();			
		
		spTask.getChildren().clear();
		spTask.getChildren().add(listViewLabel);
		
		
	}
	private void createDisappearPane(ListView<Task> listViewLabel) 
	{		
		//listViewLabel.getSelectionModel().getSelectedIndex()
		int x = intIndexAllTabCell.get(listViewLabel.getSelectionModel().getSelectedIndex());
		if(listViewLabel.getSelectionModel().getSelectedItem().hashCode()==intAllOverdueHashCode|| 
				listViewLabel.getSelectionModel().getSelectedItem().hashCode()==intAllOngoingHashCode||
				listViewLabel.getSelectionModel().getSelectedItem().hashCode()==intAllFloatingHashCode||
				listViewLabel.getSelectionModel().getSelectedItem().hashCode()==intAllCompletedHashCode)
		{
			
		}
		else
		{
			System.out.println(x);
			int index=x+1;
			VBox root = new VBox();
			HBox hbTitle = new HBox();
			ScrollPane sp = new ScrollPane();
			VBox vbBody = new VBox();
			
			hbTitle.styleProperty().set("-fx-border-color: black;");
			vbBody.styleProperty().set("-fx-border-color: black;");
			String strTitle = "Popup of Index " +index;
			
			Label titleForPopUp = new Label(strTitle);
			titleForPopUp.setFont(Font.font("Cambria", 25));
			hbTitle.getChildren().add(titleForPopUp);
			hbTitle.setAlignment(Pos.CENTER);
			
			root.setPrefSize(500, 500);
			popUpCell.arrowSizeProperty().set(0);
			String moreName=null;
			
			if(getCurrentTab().equals(UI_TAB.ALL))
			{
				moreName = allTasks.get(x).getName();
			}
			if(getCurrentTab().equals(UI_TAB.ONGOING))
			{
				moreName =ongoingTasks.get(x).getName();
	
			}
			if(getCurrentTab().equals(UI_TAB.FLOATING))
			{
				moreName =floatingTasks.get(x).getName();
	
			}
			if(getCurrentTab().equals(UI_TAB.COMPLETED))
			{
				moreName =completedTasks.get(x).getName();
	
			}
			if(getCurrentTab().equals(UI_TAB.OVERDUE))
			{
				moreName = overdueTasks.get(x).getName();
	
			}
			if(getCurrentTab().equals(UI_TAB.SEARCH))
			{
				moreName = searchTasks.get(x).getName();
	
			}
		    Label lbl = new Label(moreName);
		    lbl.setPrefWidth(465);
		    lbl.setWrapText(true);
		    sp.setContent(lbl);
		    sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		    sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
	
		    sp.setPrefHeight(1000);
			vbBody.getChildren().add(sp);
			vbBody.setPrefHeight(1000);
			root.getChildren().addAll(hbTitle,vbBody);
	
			popUpCell.setY(listViewLabel.getHeight());
		    popUpCell.setContentNode(root);
		    popUpCell.show(listViewLabel);
		}
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

	private void textFieldListener() {
		//listen.rightBoxListener(mainTextField);
		mainTextField.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
			public void handle(KeyEvent ke)
	        {		
				if (ke.getCode().equals(KeyCode.UP))
				{
					
					System.out.println("test");
					
					String prevCmd ="";
					prevCmd =  logic.getPreviousCommand();
					mainTextField.setText(prevCmd);
				}
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
		uiPopUpFeedBack.createPopUpFeedBack(strFeedBack,scene);
		
		mainTextField.requestFocus();
	    
		//uiMakeTag.tagMapping(logic.getCategories());
		testMethod();
		leftBox.updateTag();

		
	
    	mainTextField.setText("");
    	if(currentTask == UI_TAB.ALL)
 		{
 			tabPane.getSelectionModel().select(tabAll);
 		}
    	else if(currentTask == UI_TAB.COMPLETED)
		{
			tabPane.getSelectionModel().select(tabCompleted);
		}
		else if(currentTask == UI_TAB.ONGOING)
		{
			tabPane.getSelectionModel().select(tabOngoing);
		}
		else if(currentTask == UI_TAB.FLOATING)
		{
			tabPane.getSelectionModel().select(tabFloating);
		}
		else if(currentTask == UI_TAB.OVERDUE)
		{
			tabPane.getSelectionModel().select(tabOverdue);
		}	  
		else if(currentTask == UI_TAB.SEARCH)
		{
			tabPane.getSelectionModel().select(tabSearch);
		}	
		else
		{
			tabPane.getSelectionModel().select(null);
		}
		leftBox.updateLeftBox();
		
		if(tabPane.getTabs().size()>1)
		{
			tabPane.getTabs().remove(tabWelcome);
			
		}
		else if(tabPane.getTabs().size()==0)
		{
			tabPane.getTabs().add(tabWelcome);
		}
		
		
	}
	public static UI_TAB getCurrentTab() 
	{
		return (UI_TAB) tabPane.getSelectionModel().getSelectedItem().getUserData();
	}
	public TextField getMainTextField()
	{
		return mainTextField;
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