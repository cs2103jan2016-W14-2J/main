package GUI;

import org.controlsfx.glyphfont.FontAwesome;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
		
		cellRoot.setId("CellRoot");
		lblIndex.setId("CellIndex");
		lblTag.setId("CellTag");
		lblName.setId("CellName");
		chkFlag.setId("CellFlag");
		
		if(vbStartAndEnd.getChildren().size()==1)
		{
			vbStartAndEnd.getChildren().get(0).setId("CellStart");
			((Label) vbStartAndEnd.getChildren().get(0)).getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
		}
		else if(vbStartAndEnd.getChildren().size()==2)
		{
			vbStartAndEnd.getChildren().get(0).setId("CellStart");
			vbStartAndEnd.getChildren().get(1).setId("CellEnd");
			((Label) vbStartAndEnd.getChildren().get(0)).getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
			((Label) vbStartAndEnd.getChildren().get(1)).getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
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
}
