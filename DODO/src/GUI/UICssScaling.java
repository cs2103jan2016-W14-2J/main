package GUI;

import java.util.ArrayList;

import org.controlsfx.glyphfont.FontAwesome;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
/*
 *@author Chiang Jia Feng
 *@Description: All scaling and css will be done here
 */
public class UICssScaling 
{
	String cssCellComponents;
	String cssTabPane;
	String cssTagBack;
	String cssTopBar;
	public UICssScaling()
	{
		cssCellComponents = "cellComponents.css";
		cssTabPane = "tabPane.css";
		cssTagBack = "tagBack.css";
		cssTopBar = "topBarColor.css";
	}
	public void setCssAndScalingForCell(HBox cellRoot,Label lblIndex, Label lblName, ArrayList<Label> ListTag, VBox vbStartAndEnd, CheckBox chkFlag, Tooltip toolTip)
	{
		cellRoot.setPrefSize(100, 55);
		lblName.setPrefSize(800, 500);
		lblIndex.setMinSize(100, 55);
		vbStartAndEnd.setPrefSize(400, 500);
		chkFlag.setPrefSize(70, 50);
		
		for(int x=0;x<ListTag.size();x++)
		{
			ListTag.get(x).getStylesheets().add(this.getClass().getResource(cssTagBack).toExternalForm());
			ListTag.get(x).setFont(Font.font ("Verdana", 20));
			//lblListTag.get(x).setId("CellTag");
			//lblListTag.get(x).styleProperty().set("-fx-border-color: black;");
			ListTag.get(x).setPadding(new Insets(10, 10, 10, 10));
		}
		
		lblIndex.setFont(Font.font ("Verdana", 20));
		lblName.setFont(Font.font ("Verdana", 20));
		lblName.setAlignment(Pos.CENTER_LEFT);
		vbStartAndEnd.setAlignment(Pos.CENTER);
		chkFlag.setAlignment(Pos.CENTER);
		
		cellRoot.setId("CellRoot");
		lblIndex.setId("CellIndex");
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
		AlignmentCheck(cellRoot, lblName,lblIndex,vbStartAndEnd,chkFlag, toolTip);
	}
	private void AlignmentCheck(HBox cellRoot,Label lblName, Label lblIndex, VBox vbStartAndEnd, CheckBox chkFlag, Tooltip toolTip)
	{
		cellRoot.styleProperty().set("-fx-border-color: black;");
		/*lblIndex.styleProperty().set("-fx-border-color: black;");
		lblName.styleProperty().set("-fx-border-color: black;");
		vbStartAndEnd.styleProperty().set("-fx-border-color: black;");
		chkFlag.styleProperty().set("-fx-border-color: black;");
		toolTip.styleProperty().set("-fx-border-color: black;");*/
	}
	public void cssLeftBoxComponents(Label lblLogo, VBox leftBox, PieChart chart, TitledPane titledPane,Label lblCategory, ListView<String> listView)
	{
		lblLogo.setUserData("TopBar");
		lblLogo.setPrefWidth(1000);
		lblLogo.getStylesheets().add(this.getClass().getResource(cssTopBar).toExternalForm());

		leftBox.setPrefSize(500, 1800);
		titledPane.setPrefSize(500, 888);
		chart.setPrefSize(500, 500);
		listView.setPrefSize(500, 888);
		lblCategory.setPrefSize(500, 50);
		
		lblCategory.setFont(Font.font("Cambria", 25));
		
		lblCategory.setAlignment(Pos.CENTER);
		lblCategory.getStylesheets().add(this.getClass().getResource(cssTopBar).toExternalForm());
		AlignmentCheck(lblCategory,leftBox,lblLogo);
		
	}
	private void AlignmentCheck(Label lblCategory,VBox leftBox, Label lblLogo)
	{
		lblLogo.styleProperty().set("-fx-border-color: black;");
		lblCategory.styleProperty().set("-fx-border-color: black;");
		leftBox.styleProperty().set("-fx-border-color: black;");
	}
	public void cssWelcomePage(HBox root, Label welcomeLabel) 
	{
		root.styleProperty().set("-fx-border-color: black;");
		root.setPrefSize(500, 500);
		welcomeLabel.setFont(Font.font("Cambria", 100));
		welcomeLabel.setTranslateX(455);
		welcomeLabel.setPrefSize(500, 500);
	}
	public void setCssAndScalingForRightBox(TabPane tabPane, TextField mainTextField) 
	{
		tabPane.getStylesheets().add(this.getClass().getResource(cssTabPane).toExternalForm());
		mainTextField.setFont(Font.font("Cambria", 25));
	}
	public void cssTag(Label lblTagging)
	{
		lblTagging.getStylesheets().add(this.getClass().getResource(cssTagBack).toExternalForm());
	}
	public void cssPrimaryStage(Stage primaryStage)
	{
		
	}





}
