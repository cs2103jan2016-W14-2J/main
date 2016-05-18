package GUI;

import java.util.ArrayList;
import Logic.Logic;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//@@author A0125372L
public class UILeftBox {

	private final String CATEGORY_HEADER = "Categories";
	private final String CHART_HEADER = "Task";
	private final String LOGO_HEADER = "DODO";
	private VBox leftBox;
	private UIRightBox rightBox;
	private Logic logic;
	private ListView<String> listView;
	private ObservableList<String> list;
	private TitledPane titledPane;
	private ObservableList<PieChart.Data> listData;
	private PieChart chart;
	private UIUtility utility;
	private Label lblCategory;
	private ArrayList<String> tagMap;
	private PieChart.Data floatingData;
	private PieChart.Data ongoingData;
	private PieChart.Data completedData;
	private PieChart.Data overdueData;
	private FlowPane flowpaneCategory;
	private int intOverdueTasks = 0;
	private int intCompletedTasks = 0;
	private int intFloatingTasks = 0;
	private int intOngoingTasks = 0;
	private Label lblLogo;
	final ScrollPane scroll = new ScrollPane();

	public UILeftBox(Logic logic, HBox root, Scene scene) {
		leftBox = new VBox();
		this.logic = logic;
		listView = new ListView<>();
		list = FXCollections.observableArrayList();
		listData = FXCollections.observableArrayList();
		titledPane = new TitledPane();
		lblLogo = new Label(LOGO_HEADER);
		chart = new PieChart(listData);
		lblCategory = new Label(CATEGORY_HEADER);
		tagMap = new ArrayList<String>();
		floatingData = new PieChart.Data("Floating Tasks", intFloatingTasks);
		ongoingData = new PieChart.Data("On-going Tasks", intOngoingTasks);
		completedData = new PieChart.Data("Completed Tasks", intCompletedTasks);
		overdueData = new PieChart.Data("Overdue Tasks", intOverdueTasks);
		flowpaneCategory = new FlowPane();
		flowpaneCategory.setHgap(20);
		flowpaneCategory.setPrefSize(500, 500);
		utility = new UIUtility(logic);

	}

	public void build(UIRightBox rightBox) {
		this.rightBox = rightBox;
		updateChart();
		updateTagScroll();
		updateTag();
		listData.addAll(floatingData, ongoingData, completedData, overdueData);
		utility.cssLeftBoxComponents(lblLogo, leftBox, chart, titledPane, lblCategory, listView);
		leftBox.getChildren().addAll(lblLogo, chart, lblCategory, scroll);
	}

	public void updateLeftBox() {
		updateChart();
		updateTag();
	}

	public VBox getRoot() {
		return leftBox;
	}

	private void updateTagScroll() {
		scroll.setPrefSize(500, 500);
		scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical
																	// scroll
																	// bar
		scroll.setContent(flowpaneCategory);
		scroll.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
				flowpaneCategory.setPrefWidth(bounds.getWidth());
				flowpaneCategory.setPrefHeight(bounds.getHeight());
			}
		});

	}

	public void updateTag() {
		clearFlowPane();
		if (logic.getCategories() != null) {
			for (int x = 0; x < logic.getCategories().size(); x++) {
				Label lbl = new Label(logic.getCategories().get(x).getName());
				utility.assignUserData(lbl);
				utility.cssTag(lbl);
				if (!flowpaneCategory.getChildren().contains(lbl)) {
					flowpaneCategory.getChildren().add(lbl);
				}
				lbl.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent mouseEvent) {
						rightBox.setTextFieldAndEnter("search #" + lbl.textProperty().getValue());
					}
				});

			}
		}
	}

	private void clearFlowPane() {
		flowpaneCategory.getChildren().clear();
	}

	public void updateChart() {
		updatePieValue();
		detectPie();
		utility.cssChart(chart);
	}

	private void detectPie() {
		if (intOverdueTasks == 0) {
			listData.remove(overdueData);
		} else {
			if (!listData.contains(overdueData)) {
				listData.add(overdueData);
			}
		}
		if (intCompletedTasks == 0) {
			listData.remove(completedData);
		} else {
			if (!listData.contains(completedData)) {
				listData.add(completedData);
			}
		}
		if (intFloatingTasks == 0) {
			listData.remove(floatingData);
		} else {
			if (!listData.contains(floatingData)) {
				listData.add(floatingData);
			}
		}
		if (intOngoingTasks == 0) {
			listData.remove(ongoingData);
		} else {

			if (!listData.contains(ongoingData)) {
				listData.add(ongoingData);
			}
		}
	}

	private void updatePieValue() {
		intOverdueTasks = rightBox.getOverdueTasksSize();
		intCompletedTasks = rightBox.getCompletedTasksSize();
		intFloatingTasks = rightBox.getFloatingTasksSize();
		intOngoingTasks = rightBox.getOngoingSize();

		floatingData.setPieValue(intFloatingTasks);
		ongoingData.setPieValue(intOngoingTasks);
		completedData.setPieValue(intCompletedTasks);
		overdueData.setPieValue(intOverdueTasks);
	}

}
