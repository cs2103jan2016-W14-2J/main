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
import javafx.scene.input.MouseEvent;
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
	private TreeMap<String, Category> tagMap;
	private UIListener listen;
	private PieChart.Data floatingData;
	private PieChart.Data ongoingData;
	private PieChart.Data completedData;
	private PieChart.Data overdueData;
	
	private int intOverdueTasks = 0;
	private int intCompletedTasks =0;
	private int intFloatingTasks = 0;
	private int intOngoingTasks =0 ;
	
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
		tagMap = new TreeMap<String, Category>();
		listen = new UIListener();
		floatingData = new PieChart.Data("Floating Tasks",intFloatingTasks);
		ongoingData= new PieChart.Data("On-going Tasks",intOngoingTasks);
		completedData= new PieChart.Data("Completed Tasks",intCompletedTasks);
		overdueData= new PieChart.Data("Overdue Tasks",intOverdueTasks);
		
	}
	public void build(UIRightBox rightBox) 
	{
		this.rightBox = rightBox;
	    addChartTitle();
		updateChart();
		//updateTag();
		listen.chartListener(chart,rightBox);
		listData.addAll(floatingData,ongoingData,completedData,overdueData);
		usc.cssLeftBoxComponents(leftBox,chart,titledPane,lblCategory,listView);
		leftBox.getChildren().addAll(chart,lblCategory,listView);
	}
	public void updateLeftBox()
	{
		//updateTag();
		updateChart();
	}
	public VBox getRoot()
	{
		return leftBox;
	}
	private void addChartTitle() 
	{
		chart.setTitle("Task");			
	}
	private void updateTag() 
	{
		/*tagMap = logic.getCategories();
		for(int x=0;x<tagMap.size();x++)
		{
			if(tagMap.get(x).getName()!=null)
			{
				//list.add(tagMap);
			}
		}
		listView = new ListView<String>(list);*/
	}
	
	public void updateChart() 
	{		
		intOverdueTasks = rightBox.overdueTasksSize();
		intCompletedTasks = rightBox.completedTasksSize();
		intFloatingTasks = rightBox.floatingTasksSize();
		intOngoingTasks = rightBox.getOngoingSize();
		
		floatingData.setPieValue(intFloatingTasks);
		ongoingData.setPieValue(intOngoingTasks);
		completedData.setPieValue(intCompletedTasks);
		overdueData.setPieValue(intOverdueTasks);
		
		
		
		if(intOverdueTasks==0)
		{
			listData.remove(overdueData);
		}
		else
		{
			if(!listData.contains(overdueData))
			{
				listData.add(overdueData);
			}
		}
		if(intCompletedTasks==0)
		{
			listData.remove(completedData);
		}
		else
		{
			if(!listData.contains(completedData))
			{
				listData.add(completedData);
			}		
		}
		if(intFloatingTasks==0)
		{
			listData.remove(floatingData);
		}
		else
		{
			if(!listData.contains(floatingData))
			{
				listData.add(floatingData);
			}	
		}
		if(intOngoingTasks==0)
		{
			listData.remove(ongoingData);
		}
		else
		{
			if(!listData.contains(ongoingData))
			{
				listData.add(ongoingData);
			}
		}
		
		
		
	
		
	}

	

	


}
