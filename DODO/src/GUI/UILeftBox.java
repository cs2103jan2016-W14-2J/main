package GUI;

import Command.*;
import Logic.*;
import Parser.*;
import Storage.*;
import Task.*;

import java.time.LocalDate;
import java.util.TreeMap;

import com.sun.javafx.scene.control.skin.DatePickerSkin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*
 *@author Chiang Jia Feng
 *@Description: leftBox (Graph, tag categories and calendar)
 */
public class UILeftBox {

	private final String CATEGORY_HEADER ="Categories";
	private final String CHART_HEADER ="Task";
	
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
	

	public UILeftBox(Logic logic )
	{
		
		leftBox = new VBox();
		this.logic = logic;
		usc = new UICssScaling();
		listView = new ListView<>(); 
		list = FXCollections.observableArrayList();
		listData  = FXCollections.observableArrayList();
		titledPane = new TitledPane();
		chart= new PieChart(listData);
		lblCategory = new Label(CATEGORY_HEADER);
				
	}
	
	public void build(UIRightBox rightBox) {
		this.rightBox = rightBox;
		addTagContent();
	    addTagListView();
	    addChartTitle();
		usc.cssLeftBoxComponents(leftBox,chart,titledPane,lblCategory,listView);
		leftBox.getChildren().addAll(chart,lblCategory,listView);		
	}

	public VBox getRoot()
	{
		return leftBox;
	}
	private void addChartTitle() {
		chart.setTitle("Task");			
	}
	private void addTagListView() {
		listView = new ListView<String>(list);
	}
	private void addTagContent() {
		list.addAll("fku","fku","fku","fku","fku");
	}

	public void updateChart() 
	{
		int intOverdueTasks = rightBox.overdueTasksSize();
		int intCompletedTasks = rightBox.completedTasksSize();
		int intFloatingTasks = rightBox.floatingTasksSize();
		int intOngoingTasks = rightBox.getOngoingSize();
		
		listData.clear();
		
		if(intOngoingTasks!=0)
		{			
			listData.addAll(new PieChart.Data("On-going Tasks",intOngoingTasks));
		}
		if(intOverdueTasks!=0)
		{
			listData.addAll(new PieChart.Data("Overdue Tasks",intOverdueTasks));
		}
		if(intFloatingTasks!=0)
		{
			listData.addAll(new PieChart.Data("Floating Tasks",intFloatingTasks));
		}
		if(intCompletedTasks!=0)
		{
			listData.addAll(new PieChart.Data("Completed Tasks",intCompletedTasks));
		}
	}

	

	


}
