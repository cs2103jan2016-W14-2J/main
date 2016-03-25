package GUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.controlsfx.control.PopOver;

import Command.*;
import Logic.*;
import Parser.*;
import Storage.*;
import Task.*;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;

/*
 *@author Chiang Jia Feng
 *@Description: rightBox (Tabpane and Textfield)
 */

public class UIRightBox {
	 Logger logger = Logger.getLogger("MyLog");  

	private VBox rightBox = new VBox(); // current box
	private UILeftBox leftBox;
	private final double textfieldWidth = 55; // centerBox txtfield size
	private final double textfieldHeight = 1233;
	private final double tabPaneWidth = 1000; // centerBox tabPane size
	private final double tabPaneHeight = 1450;
	private final double titledPaneWidth = 1000; // centerBox titledPane size
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
	private final String allTab = "All Tasks";

	private TitledPane titledPaneOnGoingTask = new TitledPane();
	private TitledPane titledPaneCompletedTask = new TitledPane();
	private TitledPane titledPaneOverdueTask = new TitledPane();
	private TitledPane titledPaneFloatingTask = new TitledPane();

	private ArrayList<Task> ongoingTasks = new ArrayList<Task>();
	private ArrayList<Task> floatingTasks = new ArrayList<Task>();
	private ArrayList<Task> completedTasks = new ArrayList<Task>();
	private ArrayList<Task> overdueTasks = new ArrayList<Task>();

	private PopOver po = new PopOver();
	PopOver popUpFeedBack = new PopOver();

	Tab tabAll = new Tab(floatingTab);
	VBox allVB = new VBox();

	
	Tab tabFloating = new Tab(floatingTab);
	VBox floatingVB = new VBox();

	Tab tabOngoing = new Tab(onGoingTab);
	VBox ongoingVB = new VBox();

	Tab tabCompleted = new Tab(completedTab);
	VBox completedVB = new VBox();

	Tab tabOverdue = new Tab(overDueTab);
	VBox overdueVB = new VBox();

	private PopOver popOver;
	private String strFeedBack;
	private Label feedBackLabel;
	AnchorPane ap = new AnchorPane();
	public VBox UIRightBox(UILeftBox leftBox) {
		setLeftBox(leftBox);
		addTabPane();
		addTabs();
		addMainTextfield();
		updateNewListItems();
		updateTitledPane();
		return rightBox;
	}
	public static TASK_STATUS getCurrentTab() 
	{
		return (TASK_STATUS) tabPane.getSelectionModel().getSelectedItem().getUserData();
	}

	public UIRightBox(Logic logic) {
		this.logic = logic;
	}

	private void setLeftBox(UILeftBox leftBox) {
		this.leftBox = leftBox;
	}

	private void addTabPane() {
		initTabPane();
		rightBox.getChildren().add(tabPane);
	}

	private void addMainTextfield() {
		initMainTextfield();
		textFieldListener();
		rightBox.getChildren().add(mainTextField);
	}
	public TextField getMainTextField()
	{
		return mainTextField;
	}
	private void initMainTextfield() {
		mainTextField.setPrefSize(textfieldHeight, textfieldWidth);
	}

	private void addTabs() {
		floatingTab();
		ongoingTab();
		completedTab();
		overdueTab();
	}

	private void overdueTab() {
		overdueVB.getChildren().add(titledPaneOverdueTask);
		tabOverdue.setContent(overdueVB);
		tabPane.getTabs().add(tabOverdue);
		tabOverdue.setUserData(TASK_STATUS.OVERDUE);
	}

	private void completedTab() {
		completedVB.getChildren().add(titledPaneCompletedTask);
		tabCompleted.setContent(completedVB);
		tabPane.getTabs().add(tabCompleted);
		tabCompleted.setUserData(TASK_STATUS.COMPLETED);
	}

	private void ongoingTab() {
		ongoingVB.getChildren().add(titledPaneOnGoingTask);
		tabOngoing.setContent(ongoingVB);
		tabPane.getTabs().add(tabOngoing);
		tabOngoing.setUserData(TASK_STATUS.ONGOING);

	}

	private void floatingTab() {
		TitledPane tp = new TitledPane();
		floatingVB.getChildren().add(tp);
		tabFloating.setContent(titledPaneFloatingTask);
		tabPane.getTabs().add(tabFloating);
		tabFloating.setUserData(TASK_STATUS.FLOATING);
		/*
		 * Tab tab = new Tab(floatingTab); VBox vb = new VBox();
		 * tab.setContent(vb); tab.setContent(titledPaneFloatingTask);
		 * tabPane.getTabs().add(tab); tab.setUserData(TASK_STATUS.FLOATING);
		 */

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
									/*
									 * floatingTab(); ongoingTab();
									 * completedTab(); overdueTab();
									 */
									//add tagging string here and checkbox.
									String strTagging = item.getTag();
									UICellComponents lsg = null;
									System.out.println("at the line 219 try complete command cannt work BRO LOOK HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE" + item.getComplete());
									System.out.println("ITEM.GETFLAG GET THE FLAG FOR THAT TASK TO CHECK IS IT FLAGGED " + item.getFlag());
									if ((item instanceof DeadlinedTask) == true) 
									{
										System.out.println("DeadlinedTask");
										lsg = new UICellComponents(Integer.toString(this.getIndex() + 1), strTagging,
												item.getName(), null, ((DeadlinedTask) item).getEndDateTime(), item.getFlag());
										logger.info("DeadlinedTask");  
									} 
									else if ((item instanceof Event) == true) 
									{
										System.out.println("in event" + ((Event) item).getEndDateTime());
										lsg = new UICellComponents(Integer.toString(this.getIndex() + 1), strTagging,
												item.getName(), ((Event) item).getStartDateTime(),
												((Event) item).getEndDateTime(), item.getFlag());
										logger.info("Event");  

									}
									else if ((item instanceof Task) == true) 
									{
										System.out.println("in Task");
										lsg = new UICellComponents(Integer.toString(this.getIndex() + 1), strTagging,
												item.getName(), null, null, item.getFlag());
										logger.info("Task");  
									}
									
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
		
		leftBox.getTasks();

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

	private void initTabPane() {
		tabPane.setPrefSize(tabPaneHeight, tabPaneWidth);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
	}

	private void textFieldListener() {
		mainTextField.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        private int prevOngoingTasks;
			private int prevFloatingTasks;
			private int prevOverdueTasks;
			private int prevCompletedTasks;
			
			public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	chkCurrentContent();
	            	strFeedBack = logic.run(mainTextField.getText());
	            	VBox vbPop = new VBox();
	            	feedBackLabel = new Label(strFeedBack);
	        		vbPop.getChildren().add(feedBackLabel);
	        		vbPop.setPrefSize(1380, 50);
	        		feedBackLabel.setId("lblFeedBack");
	        		popUpFeedBack.setAutoFix(true);
	        		popUpFeedBack.setContentNode(vbPop);
	        		//popUpFeedBack.show(mainTextField);
	        		Path caret = findCaret(mainTextField);
	                Point2D screenLoc = findScreenLocation(caret);
	        		popUpFeedBack.show(mainTextField, screenLoc.getX(), screenLoc.getY() + 70);
	        		/*FadeTransition ft = new FadeTransition(Duration.millis(2000),feedBackLabel);
	        	    ft.setFromValue(1.0);
	        	    ft.setToValue(0.0);
	        	    ft.play();
	        	    ft.setOnFinished(new EventHandler<ActionEvent>() 
	        	    {
	                    public void handle(ActionEvent t) 
	                    {
	                    	gate=true;
	                    	popUpFeedBack.hide();
	                        System.out.println("Hiding");
	                    }
	                });*/	        		
	        	    mainTextField.requestFocus();
	            	updateNewListItems();
	            	mainTextField.setText("");
	            	updateTabSelection();
	            	//System.out.println("tab enter " + tabPane.getSelectionModel().getSelectedItem().getUserData());
	            }
	        }
			 

			private void updateTabSelection() 
			{
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
			}

			private void chkCurrentContent() 
			{
				prevOngoingTasks= ongoingTasks.size();
				prevFloatingTasks = floatingTasks.size();
				prevCompletedTasks = completedTasks.size();
				prevOverdueTasks = overdueTasks.size();				
			}
	    });	
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
	private void updateNewListItems() {
		ongoingTasks.clear();
		floatingTasks.clear();
		completedTasks.clear();
		overdueTasks.clear();

		ongoingTasks.addAll(logic.getOngoingTasks());
		floatingTasks.addAll(logic.getFloatingTasks());
		completedTasks.addAll(logic.getCompletedTasks());
		overdueTasks.addAll(logic.getOverdueTasks());
		System.out.println("292 floatingTasks " + floatingTasks);
		updateTitledPane();
	}

	protected void updateTitledPane() {
		// having an array to store all arrays.
		// each array are sorted according to date
		/*
		 * tabFloating.setContent(floatingVB);
		 * tabFloating.setUserData(TASK_STATUS.FLOATING);
		 * floatingVB.getChildren().clear(); for(int x=0;x<10;x++) { TitledPane
		 * tp = new TitledPane(); floatingVB.getChildren().add(tp);
		 * addListToTitledPane(tp,FXCollections.observableArrayList(
		 * floatingTasks),TASK_TYPE.floatingTask);
		 * 
		 * }
		 */

		addListToTitledPane(titledPaneFloatingTask, FXCollections.observableArrayList(floatingTasks),
				TASK_STATUS.FLOATING);
		addListToTitledPane(titledPaneOnGoingTask, FXCollections.observableArrayList(ongoingTasks),
				TASK_STATUS.ONGOING);
		addListToTitledPane(titledPaneCompletedTask, FXCollections.observableArrayList(completedTasks),
				TASK_STATUS.COMPLETED);
		addListToTitledPane(titledPaneOverdueTask, FXCollections.observableArrayList(overdueTasks),
				TASK_STATUS.OVERDUE);
		
		
		if(floatingTasks.size()==0 && ongoingTasks.size()==0 && completedTasks.size()==0 && overdueTasks.size()==0)
		{
			titledPaneFloatingTask.setContent(new Label("empty"));
			titledPaneOnGoingTask.setContent(new Label("empty"));
			titledPaneCompletedTask.setContent(new Label("empty"));
			titledPaneOverdueTask.setContent(new Label("empty"));
		}
		
		
		
		
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
		return ongoingTasks.size();
	}
	public int floatingTasksSize() 
	{
		return floatingTasks.size();
	}
	public int completedTasksSize() 
	{
		return completedTasks.size();
	}
	public int overdueTasksSize() 
	{
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
	public String getFeedBack()
	{
		return strFeedBack;
	}
}