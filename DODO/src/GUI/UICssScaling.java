package GUI;

import org.controlsfx.glyphfont.FontAwesome;

import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
/*
 *@author Chiang Jia Feng
 *@Description: All scaling and css will be done here
 */
public class UICssScaling 
{
	String cssCellComponents;
	public UICssScaling()
	{
		cssCellComponents = "cellComponents.css";
	}
	public void setCssAndScalingForCell(HBox cellRoot, Label lblIndex, Label lblTag, Label lblName, VBox vbStartAndEnd, CheckBox chkFlag, Tooltip toolTip)
	{
		cellRoot.setPrefSize(100, 55);
		lblIndex.setPrefSize(100, 500);
		lblTag.setPrefSize(125, 500);
		lblName.setPrefSize(800, 500);
		vbStartAndEnd.setPrefSize(400, 500);
		chkFlag.setPrefSize(70, 50);
		
		lblIndex.setFont(Font.font ("Verdana", 20));
		lblTag.setFont(Font.font ("Verdana", 20));
		lblName.setFont(Font.font ("Verdana", 20));
		
		lblIndex.setAlignment(Pos.CENTER);
		vbStartAndEnd.setAlignment(Pos.CENTER);
		chkFlag.setAlignment(Pos.CENTER);
		
		cellRoot.setId("CellRoot");
		lblIndex.setId("CellIndex");
		lblTag.setId("CellTag");
		lblName.setId("CellName");
		chkFlag.setId("CellFlag");
		
		if(vbStartAndEnd.getChildren().size()==1)
		{
			vbStartAndEnd.getChildren().get(0).setId("CellStart");
			((Label) vbStartAndEnd.getChildren().get(0)).getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
			((Label) vbStartAndEnd.getChildren().get(0)).setFont(Font.font ("Verdana", 20));
		}
		else if(vbStartAndEnd.getChildren().size()==2)
		{
			vbStartAndEnd.getChildren().get(0).setId("CellStart");
			vbStartAndEnd.getChildren().get(1).setId("CellEnd");
			((Label) vbStartAndEnd.getChildren().get(0)).getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
			((Label) vbStartAndEnd.getChildren().get(1)).getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
			((Label) vbStartAndEnd.getChildren().get(0)).setFont(Font.font ("Verdana", 20));
			((Label) vbStartAndEnd.getChildren().get(1)).setFont(Font.font ("Verdana", 20));
		}
		chkFlag.getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
		AlignmentCheck(cellRoot, lblIndex,lblTag,lblName,vbStartAndEnd,chkFlag, toolTip);
	}
	private void AlignmentCheck(HBox cellRoot, Label lblIndex, Label lblTag, Label lblName, VBox vbStartAndEnd, CheckBox chkFlag, Tooltip toolTip)
	{
		cellRoot.styleProperty().set("-fx-border-color: black;");
		lblIndex.styleProperty().set("-fx-border-color: black;");
		lblTag.styleProperty().set("-fx-border-color: black;");
		lblName.styleProperty().set("-fx-border-color: black;");
		vbStartAndEnd.styleProperty().set("-fx-border-color: black;");
		chkFlag.styleProperty().set("-fx-border-color: black;");
		toolTip.styleProperty().set("-fx-border-color: black;");
	}
	public void cssLeftBoxComponents(VBox leftBox, PieChart chart, TitledPane titledPane,Label lblCategory, ListView<String> listView)
	{
		leftBox.setPrefSize(500, 1800);
		titledPane.setPrefSize(500, 888);
		chart.setPrefSize(500, 888);
		listView.setPrefSize(500, 888);
		lblCategory.setPrefSize(500, 50);
		
		lblCategory.setFont(Font.font("Cambria", 25));
		
		lblCategory.setAlignment(Pos.CENTER);
		
		AlignmentCheck(lblCategory);
		
	}
	private void AlignmentCheck(Label lblCategory) {
		lblCategory.styleProperty().set("-fx-border-color: black;");


	}






}
