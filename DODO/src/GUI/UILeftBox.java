package GUI;

import java.util.ArrayList;

import Logic.Logic;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
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
	private UICssScaling usc;
	private Label lblCategory;
	private ArrayList<String> tagMap;
	private UIListener listen;
	private PieChart.Data floatingData;
	private PieChart.Data ongoingData;
	private PieChart.Data completedData;
	private PieChart.Data overdueData;
	private FlowPane flowpaneCategory;
	private int intOverdueTasks = 0;
	private int intCompletedTasks = 0;
	private int intFloatingTasks = 0;
	private int intOngoingTasks = 0;
	private UIMakeTag makeTag;
	private Label lblLogo;
	final ScrollPane scroll = new ScrollPane();

	public UILeftBox(Logic logic, HBox root, Scene scene) {
		leftBox = new VBox();
		this.logic = logic;
		usc = new UICssScaling();
		listView = new ListView<>();
		list = FXCollections.observableArrayList();
		listData = FXCollections.observableArrayList();
		titledPane = new TitledPane();
		lblLogo = new Label(LOGO_HEADER);
		chart = new PieChart(listData);
		lblCategory = new Label(CATEGORY_HEADER);
		tagMap = new ArrayList<String>();
		listen = new UIListener();
		floatingData = new PieChart.Data("Floating Tasks", intFloatingTasks);
		ongoingData = new PieChart.Data("On-going Tasks", intOngoingTasks);
		completedData = new PieChart.Data("Completed Tasks", intCompletedTasks);
		overdueData = new PieChart.Data("Overdue Tasks", intOverdueTasks);
		
		flowpaneCategory = new FlowPane();
		flowpaneCategory.setHgap(20);
		flowpaneCategory.setPrefSize(500, 500);

		makeTag = new UIMakeTag(logic);
	}

	public void build(UIRightBox rightBox) {
		this.rightBox = rightBox;
		updateChart();
		updateTagScroll();
		updateTag();
		listen.chartListener(chart, rightBox);
		listData.addAll(floatingData, ongoingData, completedData, overdueData);
		usc.cssLeftBoxComponents(lblLogo,leftBox, chart, titledPane, lblCategory, listView);
		leftBox.getChildren().addAll(lblLogo,chart, lblCategory, scroll);
	}

	private void updateTagScroll() 
	{
			scroll.setPrefSize(500, 500);
	        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);    // Vertical scroll bar
	        scroll.setContent(flowpaneCategory);
	        scroll.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
	            @Override
	            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
	            	flowpaneCategory.setPrefWidth(bounds.getWidth());
	            	flowpaneCategory.setPrefHeight(bounds.getHeight());
	            }
	        });		
	        
	}

	public void updateLeftBox() {
		updateChart();
		updateTag();
	}

	public VBox getRoot() {
		return leftBox;
	}


	public void updateTag() 
	{
		//System.out.println("127 + ##############################################################################################"+logic.getCategories().size());
		
		flowpaneCategory.getChildren().clear();
		flowpaneCategory.getChildren().removeAll();
		if(logic.getCategories()!=null)
		{
			for (int x = 0; x < logic.getCategories().size(); x++)
			{
				Label lbl = new Label(logic.getCategories().get(x).getName());
				makeTag.assignUserData(lbl);
				//HBox root = makeTag.getTag(tagMap.get(x));
				usc.cssTag(lbl);
				if(!flowpaneCategory.getChildren().contains(lbl))
				{
					flowpaneCategory.getChildren().add(lbl);			
				}
			}
		}
	}
	public void updateChart() {
		intOverdueTasks = rightBox.getOverdueTasksSize();
		intCompletedTasks = rightBox.getCompletedTasksSize();
		intFloatingTasks = rightBox.getFloatingTasksSize();
		intOngoingTasks = rightBox.getOngoingSize();

		floatingData.setPieValue(intFloatingTasks);
		ongoingData.setPieValue(intOngoingTasks);
		completedData.setPieValue(intCompletedTasks);
		overdueData.setPieValue(intOverdueTasks);

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

}
