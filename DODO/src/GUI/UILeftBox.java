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

	private VBox leftBox;
	private UIRightBox rightBox;
	private Logic logic;
	private ListView<String> list;
	private TitledPane titledPane;
	private ObservableList<PieChart.Data> listData;	
	private PieChart chart; 
	public UILeftBox(Logic logic)
	{
		this.logic=logic;
	}
	public UILeftBox()
	{
		list = new ListView<>(); 
		leftBox = new VBox();
		listData  = FXCollections.observableArrayList();
		titledPane = new TitledPane();
		chart= new PieChart(listData);
	}
	public VBox UILeftBox(UIRightBox rightBox) 
	{
		setRightBox(rightBox);
		addGraph();
		addLabelCategories();
		addTagCategories();
		setComponentsCSS();
		return leftBox;	
	}
	private void setComponentsCSS() 
	{
		setInternalVBoxCosmetic();
		setChartCosmetic();
		getTasks();
		
	}
	public void getTasks() 
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
	private void setInternalVBoxCosmetic() 
	{
		leftBox.styleProperty().set("-fx-border-color: black;");
	}
	private void setChartCosmetic() {
		chart.setPrefSize(500, 888);
		chart.styleProperty().set("-fx-border-color: black;");
        chart.setTitle("Task");		
	}
	private void setRightBox(UIRightBox rightBox) {
		this.rightBox = rightBox;
	}
	private void addLabelCategories() 
	{
		ObservableList<String> list =FXCollections.observableArrayList("fku","fku","fku","fku","fku");
		ListView<String> lv = new ListView<String>(list);
		Label lbl = new Label("Tag Categories");
		lbl.setAlignment(Pos.CENTER_LEFT);
		lv.setPrefHeight(888);
		lbl.setFont(Font.font("Cambria", 25));
		leftBox.getChildren().addAll(lbl,lv);
	}
	private void addGraph() 
	{
        leftBox.getChildren().add(chart);
	}
	private void addTagCategories() 
	{
		addTagList(titledPane);
	}
	private void addTagList(TitledPane titledPane)
	{
		titledPane.setFocusTraversable(false);
		titledPane.setContent(list);
		leftBox.getChildren().add(list);
	}
	public void setEffect(Effect object)
	{
		leftBox.setEffect(object);
	}

}
