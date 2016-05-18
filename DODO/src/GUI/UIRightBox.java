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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;

//@@author A0125372L
public class UIRightBox {
	private Logger logger;

	private final double titledPaneWidth = 1000; // centerBox titledPane size
	private final double titledPaneHeight = 1450;

	private final String MainTab = "Main";
	private final String allTab = "All Tasks";
	private final String onGoingTab = "Ongoing Tasks";
	private final String completedTab = "Completed Tasks";
	private final String overDueTab = "Overdue Tasks";
	private final String floatingTab = "Floating Tasks";
	private final String searchTab = "Search Tasks";

	private static TabPane tabPane;
	private TextField mainTextField;
	private boolean[] tabMap;

	private ArrayList<Task> allTasks;
	private ArrayList<Task> ongoingTasks;
	private ArrayList<Task> floatingTasks;
	private ArrayList<Task> completedTasks;
	private ArrayList<Task> overdueTasks;
	private ArrayList<Task> searchTasks;

	private PopOver popUpCell = new PopOver();

	private String strFeedBack;
	private Label feedBackLabel;

	private UIUtility utility;
	private VBox rightBox;
	private UILeftBox leftBox;
	private Logic logic;

	private UI_TAB uiTab;
	private HBox mainControllerRoot;
	private Pagination pagination;
	private PopOver popUpFeedBack = new PopOver();
	private Scene scene;

	private Tab tabMain;
	private Tab tabAll;
	private Tab tabFloating;
	private Tab tabOngoing;
	private Tab tabCompleted;
	private Tab tabOverdue;
	private Tab tabSearch;

	private StackPane spAllTask = new StackPane();
	private StackPane spOnGoingTask = new StackPane();
	private StackPane spCompletedTask = new StackPane();
	private StackPane spOverdueTask = new StackPane();
	private StackPane spFloatingTask = new StackPane();
	private StackPane spSearchTask = new StackPane();

	private HBox MainHB;
	private VBox allVB;
	private VBox floatingVB;
	private VBox ongoingVB;
	private VBox completedVB;
	private VBox overdueVB;
	private VBox searchVB;

	private int intAllOverdueHashCode = 0;
	private int intAllOngoingHashCode = 0;
	private int intAllFloatingHashCode = 0;
	private int intAllCompletedHashCode = 0;
	
	private ArrayList<Integer> intIndexAllTabCell = new ArrayList<Integer>();
	private ArrayList<Integer> intHashCode = new ArrayList<Integer>();
	private ArrayList<Task> oldOverdueList = new ArrayList<Task>();
	private ArrayList<Task> taskThatExpiredList = new ArrayList<Task>();
	private int numNewTaskExpired;
	private BorderPane mainPageBorderPane;

	private Stage owner = new Stage();

	private VBox vbPop;
	private Path caret;
	private Point2D screenLoc;
	
	public UIRightBox(Logic logic, HBox root, Scene scene) {
		mainControllerRoot = root;
		rightBox = new VBox();
		this.scene = scene;
		this.logic = logic;
		utility = new UIUtility(logic.getCategories().size(),logic.getCategories());
		logger = Logger.getLogger("MyLog");
		tabMap = new boolean[6];

		tabMain = new Tab(MainTab);
		tabAll = new Tab(allTab);
		tabFloating = new Tab(floatingTab);
		tabOngoing = new Tab(onGoingTab);
		tabCompleted = new Tab(completedTab);
		tabOverdue = new Tab(overDueTab);
		tabSearch = new Tab(searchTab);

		MainHB = new HBox();
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

		tabPane = new TabPane();
		mainTextField = new TextField();
		mainPageBorderPane = new BorderPane();
		createLog();
		utility.setCssAndScalingForRightBox(tabPane, mainTextField);

	}
	void build(UILeftBox leftBox) {
		this.leftBox = leftBox;
		createMainTab();
		buildList(UIFrom.UIRIGHT);
		textFieldListener();
		mainControllerListener();
		addAllChildren();
		leftBox.updateLeftBox();
		addContentForTabs();
		chooseTab();
	}

	private void textFieldListener() {
		mainTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.UP)) {

					String prevCmd = "";
					prevCmd = logic.getPreviousCommand();
					mainTextField.setText(prevCmd);
				}
				if (ke.getCode().equals(KeyCode.ENTER)) {
					runCommand(userInputCommand());
				}

			}

		});

	}

	private void mainControllerListener() {
		mainControllerRoot.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.TAB)) {
					pagination.setStyle("");
				}
			}
		});
		mainControllerRoot.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.ALT)) {
					pagination.setStyle("");
				}
				if (ke.getCode().equals(KeyCode.TAB)) {
					if (getCurrentTab().equals(UI_TAB.MAIN)) {
						pagination.requestFocus();
						pagination.setStyle("-fx-border-color:black;");
					}
					if (getCurrentTab().equals(UI_TAB.ALL)) {
						spAllTask.getChildren().get(0).requestFocus();
					}
					if (getCurrentTab().equals(UI_TAB.FLOATING)) {
						spFloatingTask.getChildren().get(0).requestFocus();
					}
					if (getCurrentTab().equals(UI_TAB.ONGOING)) {
						spOnGoingTask.getChildren().get(0).requestFocus();
					}
					if (getCurrentTab().equals(UI_TAB.COMPLETED)) {
						spCompletedTask.getChildren().get(0).requestFocus();
					}
					if (getCurrentTab().equals(UI_TAB.OVERDUE)) {
						spOverdueTask.getChildren().get(0).requestFocus();
					}
					if (getCurrentTab().equals(UI_TAB.SEARCH)) {
						spSearchTask.getChildren().get(0).requestFocus();
					}
					// SET ALL THE SELECTION HERE
				}
			}
		});
	}

	private void addContentForTabs() {
		allVB.getChildren().add(spAllTask);
		floatingVB.getChildren().add(spFloatingTask);
		ongoingVB.getChildren().add(spOnGoingTask);
		completedVB.getChildren().add(spCompletedTask);
		overdueVB.getChildren().add(spOverdueTask);
		searchVB.getChildren().add(spSearchTask);
	}

	private void addAllChildren() {
		rightBox.getChildren().addAll(tabPane, mainTextField);
	}

	private void createMainTab() {
		pagination = new Pagination(2, 0);
		tabMain.setUserData(UI_TAB.MAIN);
		tabPane.getTabs().add(tabMain);
		tabMain.setContent(MainHB);
		setMainPageRoot(MainHB, pagination);
	}
	public VBox createPage(int pageIndex) {
		VBox box = new VBox();
		if (pageIndex == 0)
		{
			ImageView logoImgView = new ImageView(); 
			Image logoImg = new Image(UIRightBox.class.getResourceAsStream("logo.png"));
    		logoImgView.setImage(logoImg); 
    		mainPageBorderPane.setCenter(logoImgView);
			BorderPane.setAlignment(logoImgView, Pos.CENTER);
			box.getChildren().add(mainPageBorderPane);
		}
		if (pageIndex == 1) 
		{
			mainPageBorderPane.setPrefSize(400, 5000);
			ImageView logoImgView1 = new ImageView();
			Image logoImg1 = new Image(UIRightBox.class.getResourceAsStream("PAGE2.png"));
			logoImgView1.setImage(logoImg1);
			mainPageBorderPane.setCenter(logoImgView1);
			BorderPane.setAlignment(logoImgView1, Pos.CENTER);
			box.getChildren().add(mainPageBorderPane);
		}
		return box;
	}

	public void setMainPageRoot(HBox root, Pagination pagination) {

		pagination.setPageFactory((Integer pageIndex) -> createPage(pageIndex));
		AnchorPane anchor = new AnchorPane();
		AnchorPane.setTopAnchor(pagination, 10.0);
		AnchorPane.setRightAnchor(pagination, 10.0);
		AnchorPane.setBottomAnchor(pagination, 10.0);
		AnchorPane.setLeftAnchor(pagination, 10.0);
		anchor.setPrefSize(5000, 1000);
		anchor.getChildren().addAll(pagination);
		// usc.cssWelcomePage(root,welcomeLabel);
		root.getChildren().addAll(anchor);

	}

	private void chooseTab() {
		if (tabPane.getTabs().contains(tabAll)) {
			tabPane.getSelectionModel().select(tabAll);
		} else {
			tabPane.getSelectionModel().select(tabMain);
		}
	}

	private ArrayList<Task> getAll() {
		clearCellIndicationList();
		ArrayList<Task> allTask = addTitleStub();
		addTitleStubToIndicationList(allTask);
		return allTask;
	}

	private void addTitleStubToIndicationList(ArrayList<Task> allTask) {
		for (int x = 0, y = 0; x < allTask.size(); x++, y++) {
			int currentHashCode = allTask.get(x).hashCode();
			if (intAllOverdueHashCode == currentHashCode) {
				intIndexAllTabCell.add(-1);
				y--;
			} else if (intAllOngoingHashCode == currentHashCode) {
				intIndexAllTabCell.add(-1);
				y--;
			} else if (intAllFloatingHashCode == currentHashCode) {
				intIndexAllTabCell.add(-1);
				y--;
			} else if (intAllCompletedHashCode == currentHashCode) {
				intIndexAllTabCell.add(-1);
				y--;
			} else {
				intIndexAllTabCell.add(y);
				intHashCode.add(currentHashCode);
			}
		}
	}

	private ArrayList<Task> addTitleStub() {
		ArrayList<Task> allTask = new ArrayList<Task>();
		Task stubTask1 = new Task();
		intAllOverdueHashCode = stubTask1.hashCode();
		allTask.add(stubTask1); // add title for overdueTasks
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
		return allTask;
	}

	private void clearCellIndicationList() {
		intIndexAllTabCell.clear();
		intHashCode.clear();
	}

	public boolean buildList(UIFrom from) {

		
		if (from.equals(UIFrom.THREAD)) {
			if (chkChangeOfTask()) {
				System.out.println("thread block");
				oldOverdueList.clear();
				oldOverdueList.addAll(overdueTasks);
				return true;
			}
			else
			{
		        StackPane root = new StackPane();
		        Label content = new Label();
				Platform.runLater(() ->
				{
					
					numNewTaskExpired = Math.abs(oldOverdueList.size()-logic.getOverdueTasks().size());
		            for(int x=0;x<logic.getOverdueTasks().size();x++)
		            {
		            	boolean gate = true;
		            	for(int y=0;y<oldOverdueList.size();y++)
		            	{
		            		if(logic.getOverdueTasks().get(x).equals(oldOverdueList.get(y)))
		            		{
		            			gate=false;
		            		}
		            	}
		            	if(gate==true)
		            	{
		            		taskThatExpiredList.add(logic.getOverdueTasks().get(x));

		            	}
		            }
		            
		            
		            content.setText("You have " + numNewTaskExpired + " task(s) expired." + "\t" + "The latest expired task is : "+taskThatExpiredList);
		            
		            System.out.println(oldOverdueList);
		            System.out.println(logic.getOverdueTasks());
		            
		            root.setPrefSize(500, 500);
		            
		            root.setStyle("-fx-background-color: TRANSPARENT");
		            root.getChildren().add(content);
		            Scene scene = new Scene(root, 500, 500);
		            scene.setFill(Color.TRANSPARENT);
		            owner.setScene(scene);
		            owner.show();

		        });
			}
		}
		
		clearAllTasks();
		updateAllTasks();
		updateTabMap();
		generateTabs();
		updateLeftChart();


		return true;
	}
	private void updateLeftChart() {
		leftBox.updateChart();
	}
	private boolean chkChangeOfTask() {
		return floatingTasks.equals(logic.getFloatingTasks()) && ongoingTasks.equals(logic.getOngoingTasks())&& completedTasks.equals(logic.getCompletedTasks())&& overdueTasks.equals(logic.getOverdueTasks());
	}

	private void generateTabs() {
		if (tabMap[0] == true) {
			if (!tabPane.getTabs().contains(tabAll)) {
				tabAll = new Tab(allTab);
				tabPane.getTabs().add(tabAll);
			}
			tabAll.setContent(allVB);
			updateList(spAllTask, FXCollections.observableArrayList(allTasks), UI_TAB.ALL);
			tabAll.setUserData(UI_TAB.ALL);
		} else {
			tabPane.getTabs().remove(tabAll);
			tabAll = null;

		}
		if (tabMap[1] == true) {
			if (!tabPane.getTabs().contains(tabFloating)) {
				tabFloating = new Tab(floatingTab);
				tabPane.getTabs().add(tabFloating);
			}
			tabFloating.setContent(floatingVB);
			updateList(spFloatingTask, FXCollections.observableArrayList(floatingTasks), UI_TAB.FLOATING);
			tabFloating.setUserData(UI_TAB.FLOATING);
		} else {
			tabPane.getTabs().remove(tabFloating);
			tabFloating = null;
		}
		if (tabMap[2] == true) {
			if (!tabPane.getTabs().contains(tabOngoing)) {
				tabOngoing = new Tab(onGoingTab);
				tabPane.getTabs().add(tabOngoing);
			}
			tabOngoing.setContent(ongoingVB);
			updateList(spOnGoingTask, FXCollections.observableArrayList(ongoingTasks), UI_TAB.ONGOING);
			tabOngoing.setUserData(UI_TAB.ONGOING);
		} else {
			tabPane.getTabs().remove(tabOngoing);
			tabOngoing = null;

		}
		if (tabMap[3] == true) {

			if (!tabPane.getTabs().contains(tabCompleted)) {
				tabCompleted = new Tab(completedTab);
				tabPane.getTabs().add(tabCompleted);
			}
			tabCompleted.setContent(completedVB);
			updateList(spCompletedTask, FXCollections.observableArrayList(completedTasks), UI_TAB.COMPLETED);
			tabCompleted.setUserData(UI_TAB.COMPLETED);
		} else {
			tabPane.getTabs().remove(tabCompleted);
			tabCompleted = null;

		}
		if (tabMap[4] == true) {

			if (!tabPane.getTabs().contains(tabOverdue)) {
				tabOverdue = new Tab(overDueTab);
				tabPane.getTabs().add(tabOverdue);
			}
			tabOverdue.setContent(overdueVB);
			updateList(spOverdueTask, FXCollections.observableArrayList(overdueTasks), UI_TAB.OVERDUE);
			tabOverdue.setUserData(UI_TAB.OVERDUE);
		} else {
			tabPane.getTabs().remove(tabOverdue);
			tabOverdue = null;

		}
		if (tabMap[5] == true) {
			if (!tabPane.getTabs().contains(tabSearch)) {
				tabSearch = new Tab(searchTab);
				tabPane.getTabs().add(tabSearch);
			}
			tabSearch.setContent(searchVB);
			updateList(spSearchTask, FXCollections.observableArrayList(searchTasks), UI_TAB.SEARCH);
			tabSearch.setUserData(UI_TAB.SEARCH);
		} else {
			tabPane.getTabs().remove(tabSearch);
			tabSearch = null;
		}
	}

	private void updateAllTasks() {
		floatingTasks.addAll(logic.getFloatingTasks());
		ongoingTasks.addAll(logic.getOngoingTasks());
		completedTasks.addAll(logic.getCompletedTasks());
		overdueTasks.addAll(logic.getOverdueTasks());
		searchTasks.addAll(logic.getSearchResults());
		allTasks.addAll(getAll());
	}

	private void clearAllTasks() {

		allTasks.clear();
		ongoingTasks.clear();
		floatingTasks.clear();
		completedTasks.clear();
		overdueTasks.clear();
		searchTasks.clear();
	}

	private void updateTabMap() {
		if (logic.getAll().size() != 0) {
			tabMap[0] = true;
		} else {
			tabMap[0] = false;

		}
		if (logic.getFloatingTasks().size() != 0) {
			tabMap[1] = true;
		} else {
			tabMap[1] = false;

		}
		if (logic.getOngoingTasks().size() != 0) {
			tabMap[2] = true;
		} else {
			tabMap[2] = false;

		}
		if (logic.getCompletedTasks().size() != 0) {
			tabMap[3] = true;
		} else {
			tabMap[3] = false;
		}
		if (logic.getOverdueTasks().size() != 0) {
			tabMap[4] = true;
		} else {
			tabMap[4] = false;

		}
		if (logic.getSearchResults().size() != 0) {
			tabMap[5] = true;
		} else {
			tabMap[5] = false;
		}
	}

	private void updateList(StackPane spTask, ObservableList<Task> listTask, UI_TAB typeOfTask) {
		spTask.setPrefSize(titledPaneHeight, titledPaneWidth);
		final ListView<Task> listView = new ListView<Task>(listTask);
		new Thread(new Runnable() {
			@Override
			public void run() {
				listView.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
					public ListCell<Task> call(ListView<Task> param) {
						ListCell<Task> cell = new ListCell<Task>() {
							@Override
							public void updateItem(Task item, boolean empty) {
								super.updateItem(item, empty);
								if (item != null) {
									if (intAllCompletedHashCode == item.hashCode()
											|| intAllOverdueHashCode == item.hashCode()
											|| intAllOngoingHashCode == item.hashCode()
											|| intAllFloatingHashCode == item.hashCode()) {
										UICellComponents lsg = null;

										HBox hbLblTitle = new HBox();
										Label lblTitle = new Label();
										hbLblTitle.getChildren().add(lblTitle);

										String strTitle = "";
										if (intAllOverdueHashCode == item.hashCode()) {
											lblTitle.setId("Overdue");
											strTitle = "Over-Due Tasks (" + logic.getOverdueTasks().size() + ")";
										} else if (intAllOngoingHashCode == item.hashCode()) {
											lblTitle.setId("Ongoing");
											strTitle = "On-Going Tasks (" + logic.getOngoingTasks().size() + ")";
										} else if (intAllFloatingHashCode == item.hashCode()) {
											lblTitle.setId("Floating");
											strTitle = "Floating Tasks (" + logic.getFloatingTasks().size() + ")";
										} else if (intAllCompletedHashCode == item.hashCode()) {
											lblTitle.setId("Completed");
											strTitle = "Completed Tasks (" + logic.getCompletedTasks().size() + ")";
										}
										lsg = new UICellComponents(hbLblTitle, lblTitle, strTitle);

										utility.cssAllTitle(lblTitle);
										setGraphic(hbLblTitle);
									} else {
										ArrayList<Category> strTagging = logic.mapCategories(item.getCategories());

										UICellComponents lsg = null;
										String currentIndex;
										if (typeOfTask.equals(UI_TAB.ALL)) {
											currentIndex = Integer
													.toString(intIndexAllTabCell.get(this.getIndex()) + 1);
										} else {
											currentIndex = Integer.toString(this.getIndex() + 1);
										}
										if (item.getType() == TASK_TYPE.DEADLINED) {
											lsg = new UICellComponents(logic, currentIndex, strTagging, item.getName(),
													null, item.getEndString(), item.getFlag());
											logger.info("DeadlinedTask");
										} else if (item.getType() == TASK_TYPE.EVENT) {
											lsg = new UICellComponents(logic, currentIndex, strTagging, item.getName(),
													item.getStartString(), item.getEndString(), item.getFlag());
											logger.info("Event");

										} else if (item.getType() == TASK_TYPE.FLOATING) {
											lsg = new UICellComponents(logic, currentIndex, strTagging, item.getName(),
													null, null, item.getFlag());
											logger.info("Task");
										}
										lsg.getCheckFlag().selectedProperty()
												.addListener(new ChangeListener<Boolean>() {
											public void changed(ObservableValue<? extends Boolean> observable,
													Boolean oldValue, Boolean newValue) {
												if (oldValue == false) {
													setTextFieldAndEnter("flag " + currentIndex);
													// System.out.println("flag
													// ");
												} else if (oldValue == true) {
													setTextFieldAndEnter("unflag " + currentIndex);
													// System.out.println("unflag
													// ");
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
				listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
					public void changed(ObservableValue<? extends Task> observable, Task oldValue, Task newValue) {

					}
				});

				listView.setOnKeyPressed(new EventHandler<KeyEvent>() {
					public void handle(KeyEvent ke) {
						if (ke.getCode().equals(KeyCode.ENTER)) {
							createExtraInfo(listView);
						}
						if (ke.getCode().equals(KeyCode.ESCAPE)) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									assert (logic.save() != null);
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
		spTask.getChildren().add(listView);
	}

	private void createExtraInfo(ListView<Task> listView) {
		int x;
		int intCorrectIndex = 0;
		String strTitle = "";
		VBox root = new VBox();
		HBox hbTitle = new HBox();
		ScrollPane sp = new ScrollPane();
		VBox vbBody = new VBox();

		if (getCurrentTab().equals(UI_TAB.ALL)) {
			x = intIndexAllTabCell.get(listView.getSelectionModel().getSelectedIndex());
			if (x != -1) {
				int showIndex = x + 1;
				strTitle = "Popup of Index " + showIndex;
			}
		} else {
			x = listView.getSelectionModel().getSelectedIndex() + 1;
			intCorrectIndex = listView.getSelectionModel().getSelectedIndex();
			strTitle = "Popup of Index " + x;
		}
		Label titleForPopUp = new Label(strTitle);
		hbTitle.getChildren().add(titleForPopUp);

		String moreName = null;
		if (getCurrentTab().equals(UI_TAB.ALL)) {
			try {
				moreName = logic.getAll().get(x).getName();
			} catch (Exception e) {

			}
		} else {
			if (getCurrentTab().equals(UI_TAB.ONGOING)) {
				moreName = ongoingTasks.get(intCorrectIndex).getName();

			}
			if (getCurrentTab().equals(UI_TAB.FLOATING)) {
				moreName = floatingTasks.get(intCorrectIndex).getName();

			}
			if (getCurrentTab().equals(UI_TAB.COMPLETED)) {
				moreName = completedTasks.get(intCorrectIndex).getName();

			}
			if (getCurrentTab().equals(UI_TAB.OVERDUE)) {
				moreName = overdueTasks.get(intCorrectIndex).getName();

			}
			if (getCurrentTab().equals(UI_TAB.SEARCH)) {
				moreName = searchTasks.get(intCorrectIndex).getName();

			}
		}
		Label lbl = new Label(moreName);
		sp.setContent(lbl);
		utility.cssExtraInfo(vbBody, titleForPopUp, hbTitle, root, popUpCell, lbl, sp);
		root.getChildren().addAll(hbTitle, vbBody);
		popUpCell.setY(listView.getHeight());
		popUpCell.setContentNode(root);
		popUpCell.show(listView);

	}

	private void createLog() {
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

	private void runCommand(String input) {
		inputLogic(input);
		updateTabStatus();
		createPopupFeedback();
		buildList(UIFrom.COMMAND);
		updateLeftboxTag();
		emptyTextField();
		selectTabAndCell();
	}

	public void selectTabAndCell() {
		if (uiTab == UI_TAB.ALL) {
			tabPane.getSelectionModel().select(tabAll);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((ListView) spCompletedTask.getChildren().get(0)).getSelectionModel()
							.select(logic.getLastModifiedIndex());
					((ListView) spCompletedTask.getChildren().get(0)).scrollTo(logic.getLastModifiedIndex());
					((ListView) spCompletedTask.getChildren().get(0)).getFocusModel()
							.focus(logic.getLastModifiedIndex());
				}
			});

		} else if (uiTab == UI_TAB.COMPLETED) {
			tabPane.getSelectionModel().select(tabCompleted);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((ListView) spCompletedTask.getChildren().get(0)).getSelectionModel()
							.select(logic.getLastModifiedIndex());
					((ListView) spCompletedTask.getChildren().get(0)).scrollTo(logic.getLastModifiedIndex());
					((ListView) spCompletedTask.getChildren().get(0)).getFocusModel()
							.focus(logic.getLastModifiedIndex());
				}
			});

		} else if (uiTab == UI_TAB.ONGOING) {
			tabPane.getSelectionModel().select(tabOngoing);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((ListView) spOnGoingTask.getChildren().get(0)).getSelectionModel()
							.select(logic.getLastModifiedIndex());
					((ListView) spOnGoingTask.getChildren().get(0)).scrollTo(logic.getLastModifiedIndex());
					((ListView) spOnGoingTask.getChildren().get(0)).getFocusModel().focus(logic.getLastModifiedIndex());
				}
			});
		} else if (uiTab == UI_TAB.FLOATING) {
			tabPane.getSelectionModel().select(tabFloating);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((ListView) spFloatingTask.getChildren().get(0)).getSelectionModel()
							.select(logic.getLastModifiedIndex());
					((ListView) spFloatingTask.getChildren().get(0)).scrollTo(logic.getLastModifiedIndex());
					((ListView) spFloatingTask.getChildren().get(0)).getFocusModel()
							.focus(logic.getLastModifiedIndex());
				}
			});
		} else if (uiTab == UI_TAB.OVERDUE) {
			tabPane.getSelectionModel().select(tabOverdue);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((ListView) spOverdueTask.getChildren().get(0)).getSelectionModel()
							.select(logic.getLastModifiedIndex());
					((ListView) spOverdueTask.getChildren().get(0)).scrollTo(logic.getLastModifiedIndex());
					((ListView) spOverdueTask.getChildren().get(0)).getFocusModel().focus(logic.getLastModifiedIndex());
				}
			});
		} else if (uiTab == UI_TAB.SEARCH) {
			tabPane.getSelectionModel().select(tabSearch);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((ListView) spSearchTask.getChildren().get(0)).getSelectionModel()
							.select(logic.getLastModifiedIndex());
					((ListView) spSearchTask.getChildren().get(0)).scrollTo(logic.getLastModifiedIndex());
					((ListView) spSearchTask.getChildren().get(0)).getFocusModel().focus(logic.getLastModifiedIndex());
				}
			});
		} else {
			tabPane.getSelectionModel().select(null);
		}
	}

	private void inputLogic(String input) {
		strFeedBack = logic.run(input);
	}

	private void updateTabStatus() {
		uiTab = logic.getStatus();
	}

	private void createPopupFeedback() {
		createPopUpFeedBack(rightBox, mainTextField, strFeedBack, scene);
	}

	private void emptyTextField() {
		mainTextField.setText("");
	}

	private void updateLeftboxTag() {
		leftBox.updateLeftBox();

	}

	public static UI_TAB getCurrentTab() {
		return (UI_TAB) tabPane.getSelectionModel().getSelectedItem().getUserData();
	}

	public TextField getMainTextField() {
		return mainTextField;
	}

	public int getOngoingSize() {
		if (ongoingTasks == null) {
			return 0;
		}
		return ongoingTasks.size();
	}

	public int getFloatingTasksSize() {
		if (floatingTasks == null) {
			return 0;
		}
		return floatingTasks.size();
	}

	public int getCompletedTasksSize() {
		if (completedTasks == null) {
			return 0;
		}
		return completedTasks.size();
	}

	public int getOverdueTasksSize() {
		if (overdueTasks == null) {
			return 0;
		}
		return overdueTasks.size();
	}

	public TextField getTextField() {
		return mainTextField;
	}

	public String getFeedBack() {
		return strFeedBack;
	}

	public VBox getRoot() {
		return rightBox;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public ArrayList<Tab> getListTabs() {
		ArrayList<Tab> tabList = new ArrayList<Tab>();
		for (int x = 0; x < tabPane.getTabs().size(); x++) {
			tabList.addAll(tabPane.getTabs());
		}
		return tabList;
	}

	public int getTotalTabs() {
		return tabPane.getTabs().size();
	}

	public void setCursorTextField() {
		mainTextField.positionCaret(1);
	}

	public void setTextField(String str) {
		mainTextField.textProperty().set(str);
		mainTextField.setText(str);
	}

	public void setTextFieldAndEnter(String str) {
		mainTextField.textProperty().set(str);
		mainTextField.setText(str);
		runCommand(userInputCommand());
	}
	public String userInputCommand()
	{
		System.out.println(mainTextField.getText().toString());
		return mainTextField.getText().toString();
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
		for (Node n = node; n != null; n = n.getParent()) {
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

	public void createPopUpFeedBack(VBox root, TextField mainTextField, String strFeedBack, Scene scene) {
		feedBackLabel = new Label();
		vbPop = new VBox();
		feedBackLabel.setId("lblFeedBack");
		popUpFeedBack.consumeAutoHidingEventsProperty().set(false);
		popUpFeedBack.setAutoFix(true);
		popUpFeedBack.setContentNode(vbPop);
		popUpFeedBack.setArrowSize(0);
		feedBackLabel.setText(strFeedBack);
		if (vbPop.getChildren().size() == 0) {
			vbPop.getChildren().add(feedBackLabel);
		}

		vbPop.setPrefSize(1380, 70);
		caret = findCaret(mainTextField);
		screenLoc = findScreenLocation(caret);
		if (scene.getWidth() > 1500 && scene.getHeight() > 900) {
			readjustmentFeedback(root, mainTextField);
		} else {
			if (root.getChildren().contains(feedBackLabel)) {
				root.getChildren().remove(feedBackLabel);
			}
			defaultFeedback(mainTextField);
		}
	}

	private void defaultFeedback(TextField mainTextField) {
		popUpFeedBack.show(mainTextField, screenLoc.getX() - 10, screenLoc.getY() + 70);
	}

	private void readjustmentFeedback(VBox root, TextField mainTextField) {
		vbPop.setPrefSize(1500, 70);
		if (!root.getChildren().contains(feedBackLabel)) {
			System.out.println("here");
			root.getChildren().set(1, feedBackLabel);
			root.getChildren().add(mainTextField);

		}
	}
}