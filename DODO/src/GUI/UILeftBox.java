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

	private VBox leftBox = new VBox(); //current box
	private UIRightBox rightBox;
	private Logic logic;
	private Node content;
	private DatePicker dp = new DatePicker(LocalDate.now());
	private DatePickerSkin datePickerSkin = new DatePickerSkin(dp);
	private ListView<String> list = new ListView<>(); 
	private TitledPane titledPane = new TitledPane();
	private VBox internalVBox = new VBox();
	private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();	
	private PieChart chart = new PieChart(pieChartData);
	public UILeftBox(Logic logic)
	{
		this.logic=logic;
	}
	public VBox UIleftBox(UIRightBox rightBox) 
	{
		setRightBox(rightBox);
		addGraph();
		addLabelCategories();
		addTagCategories();
		addCalendar();
		setComponentsCSS();
		return leftBox;	
	}
	private void setComponentsCSS() 
	{
		setPopupContentCosmetic();
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
		
		pieChartData.clear();
		if(intOngoingTasks!=0)
		{			
			pieChartData.addAll(new PieChart.Data("On-going Tasks",intOngoingTasks));
		}
		if(intOverdueTasks!=0)
		{
			pieChartData.addAll(new PieChart.Data("Overdue Tasks",intOverdueTasks));
		}
		if(intFloatingTasks!=0)
		{
			pieChartData.addAll(new PieChart.Data("Floating Tasks",intFloatingTasks));
		}
		if(intCompletedTasks!=0)
		{
			pieChartData.addAll(new PieChart.Data("Completed Tasks",intCompletedTasks));
		}
	}
	private void setPopupContentCosmetic() 
	{
		content.styleProperty().set("-fx-border-color: black;");
	}	
	private void setInternalVBoxCosmetic() 
	{
		internalVBox.styleProperty().set("-fx-border-color: black;");
	}
	private void setChartCosmetic() {
		chart.setPrefSize(500, 1500);
		chart.styleProperty().set("-fx-border-color: black;");
        chart.setTitle("Task");		
	}
	private void setRightBox(UIRightBox rightBox) {
		this.rightBox = rightBox;
	}
	private void addLabelCategories() 
	{
		
		
		
		leftBox.getChildren().add(internalVBox);
		/*ImageView imgView = new ImageView();
		Image img = new Image(UICellComponents.class.getResourceAsStream("tag2.png"));
		imgView.setImage(img);*/
		BorderPane bp = new BorderPane();
		
		ObservableList<String> list =FXCollections.observableArrayList("fku","fku","fku","fku","fku");

		ListView<String> lv = new ListView<String>(list);
		
		Label lbl = new Label("Tag Categories");
		lv.setPrefHeight(888);

		lbl.setFont(Font.font("Cambria", 25));
		bp.setCenter(lbl);
		bp.styleProperty().set("-fx-background-color: white;");
		internalVBox.getChildren().addAll(bp,lv);
	}
	private void addGraph() 
	{
        leftBox.getChildren().add(chart);
	}
	private void addTagCategories() 
	{
		addTagList(titledPane);
	}
	@SuppressWarnings("restriction")
	private void addCalendar() 
	{
		StackPane sp = new StackPane();
		//calendar tab not working
		System.out.println(datePickerSkin.getNode() + "test test test test test test test test test test test test test test test test");
		datePickerSkin.getDisplayNode();
		datePickerSkin.getPopupContent().setFocusTraversable(false);
		content = datePickerSkin.getPopupContent();
	    
	    sp.getChildren().add(content);

		//leftBox.getChildren().add(popupContent);
	    sp.focusTraversableProperty().set(false);
	    sp.focusTraversableProperty().setValue(false);
	    
		leftBox.getChildren().add(sp);
		sp.setDisable(true);
		datePickerSkin.getNode().focusTraversableProperty().set(false);
		datePickerSkin.getNode().focusTraversableProperty().setValue(false);
		datePickerSkin.getNode().setFocusTraversable(false);
		datePickerSkin.getDisplayNode().setFocusTraversable(false);
		datePickerSkin.getSkinnable().setFocusTraversable(false);
		datePickerSkin.getChildren().get(0).setFocusTraversable(false);
		datePickerSkin.getChildren().get(0).setDisable(true);
		content.setFocusTraversable(false);
		content.focusTraversableProperty().set(false);
		content.focusTraversableProperty().setValue(false);
		
		
		
		
	}
	private void addTagList(TitledPane titledPane)
	{
		titledPane.setFocusTraversable(false);
		//list.setItems(arrayImgView);
		titledPane.setContent(list);
		internalVBox.getChildren().add(list);
	}
	public void setEffect(Effect object)
	{
		leftBox.setEffect(object);
	}

}
