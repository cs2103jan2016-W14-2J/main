package GUI;

import org.controlsfx.control.PopOver;

import Logic.Logic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
//@@author A0125372L
public class UIListener {
	private final String TASK_STATUSdotEMPTY = "TASK_STATUS.EMPTY";
	private Stage primaryStage;
	private UIRightBox rightBox;
	private UILeftBox leftBox;
	private HBox root;
	private Logic logic;
	
	public UIListener()
	{
		
	}
	public UIListener(HBox root,Stage primaryStage, UIRightBox rightBox, UILeftBox leftBox, Logic logic)
	{
		this.logic = logic;
		this.root = root;
		this.primaryStage =primaryStage;
		this.rightBox = rightBox;
		this.leftBox= leftBox;
	}
	public void rightBoxListener(TextField mainTextField)
	{

		mainTextField.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() 
		{

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.UP))
				{
					System.out.println("test");
					
					String prevCmd ="";
					prevCmd =  logic.getPreviousCommand();
					mainTextField.setText(prevCmd);
				}
			}
			
		});

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
	public void assignHelpSheetListener() 
	{
		
		PopOver transparentPo = new PopOver();
    	Pane pane = new Pane();
    	transparentPo.arrowSizeProperty().set(0);

    	transparentPo.detachableProperty().set(false);
    	transparentPo.setContentNode(pane);
		transparentPo.setOpacity(0.6);
		Label lblF1 = new Label("Ctrl"+'\n'+ "A");
		Label lblF2 = new Label("Ctrl"+'\n'+ "S");
		Label lblF3 = new Label("Ctrl"+'\n'+ "D");
		Label lblF4 = new Label("Ctrl"+'\n'+ "F");
		Label lblF5 = new Label("Ctrl"+'\n'+ "G");
		Label lblF6 = new Label("Ctrl"+'\n'+ "H");
		Label lblF7 = new Label("Ctrl"+'\n'+ "J");
		Label lblF8 = new Label("Ctrl"+'\n'+ "K");
		
		KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.A,KeyCombination.CONTROL_DOWN);
		KeyCombination keyComb2 = new KeyCodeCombination(KeyCode.S,KeyCombination.CONTROL_DOWN);
		KeyCombination keyComb3 = new KeyCodeCombination(KeyCode.D,KeyCombination.CONTROL_DOWN);
		KeyCombination keyComb4 = new KeyCodeCombination(KeyCode.F,KeyCombination.CONTROL_DOWN);
		KeyCombination keyComb5 = new KeyCodeCombination(KeyCode.G,KeyCombination.CONTROL_DOWN);
		KeyCombination keyComb6 = new KeyCodeCombination(KeyCode.H,KeyCombination.CONTROL_DOWN);
		KeyCombination keyComb7 = new KeyCodeCombination(KeyCode.J,KeyCombination.CONTROL_DOWN);
		KeyCombination keyComb8 = new KeyCodeCombination(KeyCode.K,KeyCombination.CONTROL_DOWN);

    	ObservableList<Label> listLbl =FXCollections.observableArrayList(lblF1,lblF2,lblF3,lblF4,lblF5,lblF6,lblF7,lblF8);
	
    	root.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
    		public void handle(KeyEvent ke) {
    			System.out.println(ke);
						if (ke.getCode().equals(KeyCode.ALT)) 
						{
							int numberOfTabs = rightBox.getTotalTabs();
							double rightBoxX = rightBox.getRoot().getLayoutX();
							double rightBoxY = rightBox.getRoot().getLayoutY();
							
							pane.setPrefSize(root.getWidth(), root.getHeight());	
							pane.getChildren().removeAll(listLbl);
							for(int x=0,y=50;x<numberOfTabs;x++,y+=200)
							{
								listLbl.get(x).setPrefSize(150, 100);
								listLbl.get(x).setFont(Font.font("Cambria", 30));
								listLbl.get(x).setLayoutX(rightBoxX+y);
								listLbl.get(x).setLayoutY(rightBoxY);
	
								pane.getChildren().add(listLbl.get(x));
							}

							transparentPo.show(primaryStage,primaryStage.getX(),primaryStage.getY());
							rightBox.getTabPane().requestFocus();
						}
						
						
						if (keyComb1.match(ke)) 
						{
							rightBox.getTabPane().getSelectionModel().select(0);

						}
						if (keyComb2.match(ke)) 
						{
							rightBox.getTabPane().getSelectionModel().select(1);
						}
						if (keyComb3.match(ke)) 
						{
							rightBox.getTabPane().getSelectionModel().select(2);							
						}
						if (keyComb4.match(ke)) 
						{
							rightBox.getTabPane().getSelectionModel().select(3);
						}
						if (keyComb5.match(ke)) 
						{
							rightBox.getTabPane().getSelectionModel().select(4);
						}
						if (keyComb6.match(ke)) 
						{
							rightBox.getTabPane().getSelectionModel().select(5);
						}
						if (keyComb7.match(ke)) 
						{
							rightBox.getTabPane().getSelectionModel().select(6);
						}
						if (keyComb8.match(ke)) 
						{
							rightBox.getTabPane().getSelectionModel().select(7);
						}
						/*if(ke.getCode().equals(KeyCode.SHIFT))
						{
							rightBox.getTabPane().getTabs().get(1).getContent(;
						}*/
					     if(ke.getCode()==KeyCode.ESCAPE)
					     {
					    	 logic.save();
					    	 primaryStage.close();
					     }
					         
					        
						ke.consume();
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
/*				primaryStage.maximizedProperty().addListener(new ChangeListener<Boolean>() {

				    @Override
				    public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1)
				    {   	
				    	//	private final double sceneWidth = 1800;  //1900
				    	// private final double sceneHeight = 750;  //900
				    	
				    }
				});*/
	
	}
	
	
	
	
	
	
}
