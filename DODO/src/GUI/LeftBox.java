package GUI;

import Command.*;
import Logic.*;
import Parser.*;
import Storage.*;
import Task.*;

import java.time.LocalDate;

import com.sun.javafx.scene.control.skin.DatePickerSkin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
 *@author Chiang Jia Feng
 *@Description: LeftBox (Graph, tag categories and calendar)
 */
public class LeftBox {

	private Stage primaryStage;
	CenterBox centerBox;
	public VBox leftBox = new VBox();
	DatePicker  checkInDatePicker = new DatePicker();
	Label checkInlabel = new Label("Check-In Date:");
    DatePickerSkin datePickerSkin = new DatePickerSkin(new DatePicker(LocalDate.now()));
    ListView<String> list = new ListView<>(); 
	TitledPane titledPane = new TitledPane();
	
	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList( new PieChart.Data("Completed", 13), new PieChart.Data("Incomplete", 25));	
	ObservableList<String> arrayImgView = FXCollections.observableArrayList(new String("test"),new String("test1"),new String("test2"));
    
	final PieChart chart = new PieChart(pieChartData);

	public void blurEffect()
	{
		System.out.println("blur at leftbox");
		leftBox.setEffect(new BoxBlur());
		titledPane.setEffect(new BoxBlur());
		chart.setEffect(new BoxBlur());
		checkInlabel.setEffect(new BoxBlur());
	}
    
	private void addGraph() 
	{
        chart.setTitle("Completed Task Statistic");
        leftBox.getChildren().add(chart);
	}
	private void addTagCategories() 
	{
		addTagList(titledPane);
	}
	@SuppressWarnings("restriction")
	private void addCalendar() 
	{
		Node popupContent = datePickerSkin.getPopupContent();
        leftBox.getChildren().add(popupContent);
	}
	private void addTagList(TitledPane titledPane)
	{
		list.setItems(arrayImgView);
		titledPane.setContent(list);
		leftBox.getChildren().add(list);
	}
	public VBox leftBox(Stage primaryStage, CenterBox cb) 
	{
		this.centerBox = centerBox;
		addGraph();
		addTagCategories();
		addCalendar();
		return leftBox;	
	}
}
