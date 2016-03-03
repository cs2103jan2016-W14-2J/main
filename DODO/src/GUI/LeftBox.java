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

	public VBox leftVBox = new VBox();
	private Stage primaryStage;
	private CenterBox centerBox;
	private DatePicker  checkInDatePicker = new DatePicker();
	private Label checkInlabel = new Label("Check-In Date:");
	private DatePickerSkin datePickerSkin = new DatePickerSkin(new DatePicker(LocalDate.now()));
	private ListView<String> list = new ListView<>(); 
	private TitledPane titledPane = new TitledPane();
	
	private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList( new PieChart.Data("Completed", 13), new PieChart.Data("Incomplete", 25));	
	private ObservableList<String> arrayImgView = FXCollections.observableArrayList(new String("test"),new String("test1"),new String("test2"));
    
	final PieChart chart = new PieChart(pieChartData);

	public VBox leftBox(Stage primaryStage, CenterBox cb) 
	{
		this.centerBox = centerBox;
		addGraph();
		addTagCategories();
		addCalendar();
		return leftVBox;	
	}
	private void blurEffect()
	{
		System.out.println("blur at leftbox");
		leftVBox.setEffect(new BoxBlur());
		titledPane.setEffect(new BoxBlur());
		chart.setEffect(new BoxBlur());
		checkInlabel.setEffect(new BoxBlur());
	}
    
	private void addGraph() 
	{
        chart.setTitle("Completed Task Statistic");
        leftVBox.getChildren().add(chart);
	}
	private void addTagCategories() 
	{
		addTagList(titledPane);
	}
	@SuppressWarnings("restriction")
	private void addCalendar() 
	{
		Node popupContent = datePickerSkin.getPopupContent();
		leftVBox.getChildren().add(popupContent);
	}
	private void addTagList(TitledPane titledPane)
	{
		list.setItems(arrayImgView);
		titledPane.setContent(list);
		leftVBox.getChildren().add(list);
	}


}
