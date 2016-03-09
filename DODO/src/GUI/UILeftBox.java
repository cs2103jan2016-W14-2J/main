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
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
	private Node popupContent;
	private DatePicker  checkInDatePicker = new DatePicker();
	private Label checkInlabel = new Label("Check-In Date:");
	private DatePickerSkin datePickerSkin = new DatePickerSkin(new DatePicker(LocalDate.now()));
	private ListView<String> list = new ListView<>(); 
	private TitledPane titledPane = new TitledPane();
	private VBox internalVBox = new VBox();
	private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList( new PieChart.Data("Completed", 13), new PieChart.Data("Incomplete", 25));	
	private ObservableList<String> arrayImgView = FXCollections.observableArrayList(new String("test"),new String("test1"),new String("test2"));
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
	}
	private void setPopupContentCosmetic() 
	{
		popupContent.styleProperty().set("-fx-border-color: black;");
	}	
	private void setInternalVBoxCosmetic() 
	{
		internalVBox.styleProperty().set("-fx-border-color: black;");
	}
	private void setChartCosmetic() {
		chart.styleProperty().set("-fx-border-color: black;");
        chart.setTitle("Completed Task Statistic");		
	}
	private void setRightBox(UIRightBox rightBox) {
		this.rightBox = rightBox;
		
	}
	private void addLabelCategories() 
	{
		leftBox.getChildren().add(internalVBox);
		ImageView imgView = new ImageView();
		Image img = new Image(UICellComponents.class.getResourceAsStream("tag2.png"));
		imgView.setImage(img);
		
		Label lbl = new Label("Tag Categories");
		lbl.translateXProperty().set(80);
		lbl.translateYProperty().set(10);

		lbl.setFont(Font.font("Cambria", 20));
		HBox hbox = new HBox(5);
		hbox.getChildren().addAll(imgView,lbl);
		hbox.styleProperty().set("-fx-background-color: white;");
		internalVBox.getChildren().add(hbox);
	}
	public void setEffect(Effect object)
	{
		leftBox.setEffect(object);
	}	private void addGraph() 
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
	    popupContent = datePickerSkin.getPopupContent();
		leftBox.getChildren().add(popupContent);
	}
	private void addTagList(TitledPane titledPane)
	{
		list.setItems(arrayImgView);
		titledPane.setContent(list);
		internalVBox.getChildren().add(list);
	}
}
