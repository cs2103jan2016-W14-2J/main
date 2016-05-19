 package GUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;

import Logic.Logic;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.controlsfx.control.Notifications;

//@@author A0125372L
public class UIMainController {

	private static final double sceneWidth = 1800; // 1900
	private static final double sceneHeight = 750; // 900
	private static final Label lblF1 = new Label("Ctrl" + '\n' + "A");
	private static final Label lblF2 = new Label("Ctrl" + '\n' + "S");
	private static final Label lblF3 = new Label("Ctrl" + '\n' + "D");
	private static final Label lblF4 = new Label("Ctrl" + '\n' + "F");
	private static final Label lblF5 = new Label("Ctrl" + '\n' + "G");
	private static final Label lblF6 = new Label("Ctrl" + '\n' + "H");
	private static final Label lblF7 = new Label("Ctrl" + '\n' + "J");
	private static final Label lblF8 = new Label("Ctrl" + '\n' + "K");
	private static final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
	private static final KeyCombination keyComb2 = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
	private static final KeyCombination keyComb3 = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN);
	private static final KeyCombination keyComb4 = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
	private static final KeyCombination keyComb5 = new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN);
	private static final KeyCombination keyComb6 = new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN);
	private static final KeyCombination keyComb7 = new KeyCodeCombination(KeyCode.J, KeyCombination.CONTROL_DOWN);
	private static final KeyCombination keyComb8 = new KeyCodeCombination(KeyCode.K, KeyCombination.CONTROL_DOWN);
	private static final ObservableList<Label> listLbl = FXCollections.observableArrayList(lblF1, lblF2, lblF3, lblF4, lblF5, lblF6, lblF7, lblF8);
	private PopOver transparentPo;
	private Pane pane;
	private HBox root;
	private UILeftBox leftBox;
	private UIRightBox rightBox;
	private Logic logic;
	private Stage primaryStage;
	private Scene scene;
	private String strDBdir = "";
	private String strDBname = "";
	private Timer timer;

	public UIMainController(Logic logic) {
		this.logic = logic;
		root = new HBox();
		scene = new Scene(root, sceneWidth, sceneHeight, Color.WHITE);
		leftBox = new UILeftBox(this.logic, this.root, scene);
		rightBox = new UIRightBox(this.logic, this.root, scene);
		timer = new Timer();
		transparentPo = new PopOver();
		pane = new Pane();
		leftBox.build(rightBox);
		rightBox.build(leftBox);
	}

	public void start(Stage primaryStage) {
		primaryStage.sizeToScene();
		setPrimaryStage(primaryStage);
		addLeftAndRightBox();
		transparentPopOverSetting(transparentPo, pane);
		assignHelpSheetListener();
		show();
		new JFXPanel();
		timer.schedule(new TimerTask() 
		{
			public void run() 
			{
				Platform.runLater(new Runnable() 
				{
					@Override
					public void run() 
					{
						try 
						{
							logic.update();
							rightBox.buildList(UIFrom.THREAD);				       
						} 
						catch (Exception e) 
						{
							System.out.println("Some Error");
						}

					}
				});
			}
		}, 0, 60 * 1);
	}

	public void addLeftAndRightBox() {
		root.getChildren().addAll(leftBox.getRoot(), rightBox.getRoot());
	}

	private void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public Scene getScene() {
		return scene;
	}

	public void show() {
		root.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				char c = ke.getCharacter().charAt(0);
				if (Character.isAlphabetic(c)) {
					rightBox.setTextField(Character.toString(c));
					rightBox.getTextField().requestFocus();
					rightBox.setCursorTextField();
				}
			}
		});
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void assignHelpSheetListener() {
		root.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ESCAPE) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							assert (logic.save() != null);
							logic.save();
							System.exit(0);
						}
					});
				}
				if (ke.getCode().equals(KeyCode.ALT)) {
					int numberOfTabs = rightBox.getTotalTabs();
					double rightBoxX = rightBox.getRoot().getLayoutX();
					double rightBoxY = rightBox.getRoot().getLayoutY();

					pane.setPrefSize(root.getWidth(), root.getHeight());
					pane.getChildren().removeAll(listLbl);
					for (int x = 0, y = 25; x < numberOfTabs; x++, y += 220) {
						// listLbl.get(x).setPrefSize(150, 100);
						listLbl.get(x).setFont(Font.font("Cambria", 30));
						listLbl.get(x).setLayoutX(rightBoxX + y);
						listLbl.get(x).setLayoutY(rightBoxY);
						pane.getChildren().add(listLbl.get(x));
					}
					transparentPo.show(primaryStage, primaryStage.getX(), primaryStage.getY());
					rightBox.getTabPane().requestFocus();
				}
				if (keyComb1.match(ke)) {
					rightBox.getTabPane().getSelectionModel().select(0);
				}
				if (keyComb2.match(ke)) {
					rightBox.getTabPane().getSelectionModel().select(1);
				}
				if (keyComb3.match(ke)) {
					rightBox.getTabPane().getSelectionModel().select(2);
				}
				if (keyComb4.match(ke)) {
					rightBox.getTabPane().getSelectionModel().select(3);
				}
				if (keyComb5.match(ke)) {
					rightBox.getTabPane().getSelectionModel().select(4);
				}
				if (keyComb6.match(ke)) {
					rightBox.getTabPane().getSelectionModel().select(5);
				}
				if (keyComb7.match(ke)) {
					rightBox.getTabPane().getSelectionModel().select(6);
				}
				if (keyComb8.match(ke)) {
					rightBox.getTabPane().getSelectionModel().select(7);
				}
				ke.consume();
			}
		});
		root.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.ALT)) {
					pane.getChildren().removeAll(listLbl);
					transparentPo.hide();
				}
			}
		});
	}

	private void transparentPopOverSetting(PopOver transparentPo, Pane pane) {
		transparentPo.arrowSizeProperty().set(0);
		transparentPo.detachableProperty().set(false);
		transparentPo.setContentNode(pane);
		transparentPo.setOpacity(0.6);
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
	public String getDbDir() {
		return strDBdir;
	}

	public String strDbName() {
		return strDBname;
	}

	/**
	 * Mutators
	 */
	public String setDbDir() {
		return strDBdir;
	}

	public String setDbName() {
		return strDBname;
	}

	public HBox getRoot() {
		return root;
	}

	public UIRightBox getRight() {
		return rightBox;
	}

	public UILeftBox getLeft() {
		return leftBox;
	}

}
