package GUI;

import java.util.ArrayList;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;

public class UIListener {
	private final String TASK_STATUSdotEMPTY = "TASK_STATUS.EMPTY";
	
	public UIListener()
	{
		
	}
	public void chartListener(PieChart chart, UIRightBox rightBox)
	{
		int intOverdueTasks = rightBox.getOverdueTasksSize();
		int intCompletedTasks = rightBox.getCompletedTasksSize();
		int intFloatingTasks = rightBox.getFloatingTasksSize();
		int intOngoingTasks = rightBox.getOngoingSize();
		
		Tab chartTab = new Tab("Chart Data");
		chartTab.setUserData(TASK_STATUSdotEMPTY);
		VBox vb = new VBox();
		HBox hb = new HBox();
		
		NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
		LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
		lineChart.setTitle("TASKS MONITORING");
		XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        //populating the series with data
        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));
        lineChart.getData().add(series);
		hb.getChildren().add(lineChart);
		vb.getChildren().add(hb);
		
		chart.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent)
		    {
		    	chartTab.setContent(vb);
		    	rightBox.getTabPane().getTabs().add(chartTab);
		    	rightBox.getTabPane().getSelectionModel().select(chartTab);		    	
		    }
		});
	}
	public void transparentHelpSheet(Stage primaryStage, HBox root, UIRightBox rightBox) 
	{
		
		PopOver transparentPo = new PopOver();
    	Pane pane = new Pane();
    	transparentPo.arrowSizeProperty().set(10);
    	transparentPo.arrowLocationProperty().set(ArrowLocation.TOP_CENTER);
    	transparentPo.detachableProperty().set(false);
    	transparentPo.setContentNode(pane);
		transparentPo.setOpacity(0.6);
		Label lblF1 = new Label("F1");
		Label lblF2 = new Label("F2");
		Label lblF3 = new Label("F3");
		Label lblF4 = new Label("F4");
		Label lblF5 = new Label("F5");
		Label lblF6 = new Label("F6");
		Label lblF7 = new Label("F7");
		Label lblF8 = new Label("F8");

		
    	ObservableList<Label> listLbl =FXCollections.observableArrayList(lblF1,lblF2,lblF3,lblF4,lblF5,lblF6,lblF7,lblF8);
		
				root.setOnKeyPressed(new EventHandler<KeyEvent>() {
					public void handle(KeyEvent ke) {
					if (ke.getCode().equals(KeyCode.ALT)) 
					{
						int numberOfTabs = rightBox.getTotalTabs();
						rightBox.getTabPane().layout();
						System.out.println(rightBox.getTabPane().getChildrenUnmodifiable().get(1).getLayoutX());
				        
						double rightBoxX = rightBox.getRoot().getLayoutX();
						double rightBoxY = rightBox.getRoot().getLayoutY();
						pane.setPrefSize(root.getWidth(), root.getHeight());						
						for(int x=0,y=0;x<numberOfTabs;x++,y+=150)
						{
							listLbl.get(x).setFont(Font.font("Cambria", 25));
							listLbl.get(x).setLayoutX(rightBoxX+y);
							listLbl.get(x).setLayoutY(rightBoxY);

							pane.getChildren().add(listLbl.get(x));
						}
						
						/*int numberOfTabs = rightBox.getTotalTabs();
						double rightBoxX = rightBox.getRoot().getLayoutX();
						double rightBoxY = rightBox.getRoot().getLayoutY();
						pane.setPrefSize(root.getWidth(), root.getHeight());
						HBox hb = new HBox(new Label("welcome"));
						hb.styleProperty().set("-fx-border-color: black;");
						hb.setLayoutX(rightBoxX);
						hb.setLayoutY(rightBoxY);
						pane.getChildren().add(hb);*/
						
						transparentPo.show(primaryStage,primaryStage.getX(),primaryStage.getY());
					}
				}
				});
				root.setOnKeyReleased(new EventHandler<KeyEvent>() {
					public void handle(KeyEvent ke) {
					if (ke.getCode().equals(KeyCode.ALT)) 
					{
						pane.getChildren().removeAll(listLbl);

						transparentPo.hide();
					}
				}
				});
			
	
	}

}
