package GUI;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Date;

import Task.DeadlinedTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class UICellComponents{
	
	private Label lblIndex;
	private Label lblName;
	private Label lblDescription;
	private Label lblStartDateTime;
	private Label lblEndDateTime;
    private CheckBox chkBox;
	private Tooltip toolTip;
	private HBox cellRoot;
	private VBox vbStartAndEnd;
	public final double cellWidth = 100;
	public final double cellHeight = 35;
	private final double labelWidthIndex = 100; //centerBox titledPane size
	private final double labelHeightIndex = 1000;
	private final double labelWidthName = 600; //centerBox titledPane size
	private final double labelHeightName = 1000;
	public final double labelWidthEndDT = 250;
	public final double labelHeightEndDT = 1000;
	public final double labelIndexXproperty = 50;

	public UICellComponents(String strIndex, String strName, String strDescription)
	{

		cellRoot = new HBox();
		lblIndex = new Label(strIndex);
		lblName = new Label(strName);
		lblDescription = new Label(strDescription);
		lblEndDateTime = new Label("");
		chkBox = new CheckBox();
		toolTip = new Tooltip();
		toolTip.setText(strName);
		setComponentsSetting();
		setComponentsCSS();
		ObservableList<Object> objList = FXCollections.observableArrayList();
		cellRoot.getChildren().addAll(lblIndex,lblName,lblDescription,chkBox);
	}
	public UICellComponents(String strIndex, String strName, String strDescription, Date endDateTime) {
		cellRoot = new HBox();
		lblIndex = new Label(strIndex);
		lblName = new Label(strName);
		lblDescription = new Label(strDescription);
		lblEndDateTime = new Label(endDateTime.toString());
		chkBox = new CheckBox();
		toolTip = new Tooltip();
		toolTip.setText(strName);
		setComponentsSetting();
		setComponentsCSS();
		ObservableList<Object> objList = FXCollections.observableArrayList();
		cellRoot.getChildren().addAll(lblIndex,lblName,lblDescription,lblEndDateTime,chkBox);
	}
	public UICellComponents(String strIndex, String strName, String strDescription, Date startDateTime , Date endDateTime) {
		cellRoot = new HBox();
		vbStartAndEnd = new VBox();
		vbStartAndEnd.setPrefSize(labelWidthEndDT, labelHeightEndDT);
		lblIndex = new Label(strIndex);
		lblName = new Label(strName);
		lblDescription = new Label(strDescription);
		lblEndDateTime = new Label(endDateTime.toString());
		lblStartDateTime = new Label(startDateTime.toString());
		vbStartAndEnd.getChildren().addAll(lblStartDateTime,lblEndDateTime);
		chkBox = new CheckBox();
		toolTip = new Tooltip();
		toolTip.setText(strName);
		setComponentsSetting();
		setComponentsCSS();
		cellRoot.getChildren().addAll(lblIndex,lblName,lblDescription,vbStartAndEnd,chkBox);
		
		
	
	}

	private void setComponentsCSS() 
	{
		setCellCosmetic();
	}
	private void setCellCosmetic()
	{
		setLabelCosmetics();
		setCellRootCosmetics();
	}
	private void setLabelCosmetics() {
		lblIndex.setTextFill(Color.BLACK);
		lblName.setTextFill(Color.BLACK);
	}
	private void setCellRootCosmetics() 
	{
		cellRoot.setStyle
		("-fx-padding:3;" + "-fx-border-style:solid;"+"-fx-border-width:2;"
		+"-fx-border-inserts:5;"+"-fx-border-color: #090a0c,linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),linear-gradient(#20262b, #191d22), radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));"
		+"-fx-border-radius: 10 10 10 10;"+"-fx-background-radius: 10 1 0 0; "
		+"-fx-background-radius: 5;");

/*		cellRoot.setStyle("-fx-background-color: #090a0c,"
				+ "linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),"
				+ "linear-gradient(#20262b, #191d22),"
				+ "radial-gradient(center 50% 0%, radius 100%, "
				+ "rgba(114,131,148,0.9), rgba(255,255,255,0));");*/
		

	}
	private void setComponentsSetting() 
	{
		setEndDateTimeSetting();
		setLblSetting();
		setCellRootSetting();
		setChkBoxSetting();
	}
	private void setEndDateTimeSetting() 
	{

		//lblEndDateTime.setPrefSize(labelWidthEndDT,labelHeightEndDT);

	}
	private void setChkBoxSetting() {
		chkBox.translateXProperty().set(300);
		chkBox.getStylesheets().add(this.getClass().getResource("checkBox.css").toExternalForm());
    	chkBox.setAlignment(Pos.CENTER_RIGHT);
        chkBox.setFocusTraversable(false);		
	}
	private void setCellRootSetting() 
	{
		cellRoot.setPrefSize(cellWidth,cellHeight);
	}
	private void setLblSetting() 
	{

		lblIndex.translateXProperty().set(labelIndexXproperty);
		lblIndex.setPrefSize(labelWidthIndex, labelHeightIndex);
		lblName.setPrefSize(labelWidthName, labelHeightName);	
		//lblDescription.setPrefSize(labelHeightIndex, labelHeight);

	}
	public CheckBox getFlag()
	{
		return chkBox;
	}
	public Tooltip getToolTip()
	{
		return toolTip;
	}
	public HBox getCellRoot()
	{
		return cellRoot;
	}

	
	
	
}
