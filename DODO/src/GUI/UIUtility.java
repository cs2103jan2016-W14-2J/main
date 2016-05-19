package GUI;

import java.util.ArrayList;

import org.controlsfx.control.PopOver;

import Logic.Logic;
import Task.Category;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

//@@author A0125372L
public class UIUtility {
	private String cssCellComponents;
	private String cssTabPane;
	private String cssTagBack;
	private String cssTopBar;
	private ArrayList<Integer> tagColorIndex;
	private ArrayList<Category> uniqueListCategory;
	private ArrayList<String> ascii = new ArrayList<String>(26);
	private ArrayList<String> ascii1 = new ArrayList<String>(26);
	private int numberOfUniqueTag;
	private static final double NUMBER_FONT_SIZE_25 =25;	
	private static final double NUMBER_FONT_SIZE_100 =100;	
	
	private static final double Y_FLAG_TRANSLATION =20;	
	
	private static final double WIDTH_CELLROOT =1000;	
	private static final double HEIGHT_CELLROOT =100;	
	
	private static final double WIDTH_LABELNAME =800;	
	private static final double HEIGHT_LABELNAME  =500;
	
	private static final double WIDTH_LABELINDEX =100;	
	private static final double HEIGHT_LABELINDEX =55;

	private static final double WIDTH_VBTIME =400;	
	private static final double HEIGHT_VBTIME =500;
	
	private static final double WIDTH_FLAG =70;	
	private static final double HEIGHT_FLAG =50;
	
	private static final String FONT_NAME = "Cambria";
	private static final String BLACK_NAME = "-fx-border-color: black;";
	
	private static final double WIDTH_LBL_LOGO =1000;	
	private static final double WIDTH_LEFTBOX =500;
	
	private static final double HEIGHT_LEFTBOX =1800;
	
	private static final double WIDTH_CHART =500;
	private static final double HEIGHT_CHART =500;
	
	private static final double WIDTH_LISTVIEW =500;
	private static final double HEIGHT_LISTVIEW =888;

	private static final double WIDTH_LBLCATEGORY =500;
	private static final double HEIGHT_LBLCATEGORY =50;
	
	private static final double WIDTH_ROOT =500;
	private static final double HEIGHT_ROOT =500;
	
	public UIUtility(int numberOfUniqueTag, ArrayList<Category> uniqueListCategory) {
		cssCellComponents = "cellComponents.css";
		cssTabPane = "tabPane.css";
		cssTagBack = "tagBack.css";
		cssTopBar = "topBarColor.css";
		
/*		numberOfUniqueTag = logic.getCategories().size();
		uniqueListCategory = logic.getCategories();*/
		this.numberOfUniqueTag = numberOfUniqueTag;
		this.uniqueListCategory = uniqueListCategory;
		for (char c = 'A'; c <= 'Z'; c++) {
			ascii.add(String.valueOf(c));
		}
		for (char c = 'a'; c <= 'z'; c++) {
			ascii1.add(String.valueOf(c));
		}
	}

	public void setCssAndScalingForCell(HBox cellRoot, Label lblIndex, Label lblName, ArrayList<Label> ListTag, VBox vbStartAndEnd, CheckBox chkFlag, Tooltip toolTip) {
		chkFlag.translateYProperty().set(Y_FLAG_TRANSLATION);
		cellRoot.setPrefSize(WIDTH_CELLROOT, HEIGHT_CELLROOT);
		lblName.setPrefSize(WIDTH_LABELNAME, HEIGHT_LABELNAME);
		lblIndex.setMinSize(WIDTH_LABELINDEX, HEIGHT_LABELINDEX);
		vbStartAndEnd.setPrefSize(WIDTH_VBTIME, HEIGHT_VBTIME);
		chkFlag.setPrefSize(WIDTH_FLAG, HEIGHT_FLAG);

		for (int x = 0; x < ListTag.size(); x++) 
		{
			ListTag.get(x).getStylesheets().add(this.getClass().getResource(cssTagBack).toExternalForm());
			ListTag.get(x).setFont(Font.font("Cambria", NUMBER_FONT_SIZE_25));
			ListTag.get(x).setPadding(new Insets(10, 10, 10, 10));
		}

		lblIndex.setFont(Font.font(FONT_NAME, NUMBER_FONT_SIZE_25));
		lblName.setFont(Font.font(FONT_NAME, NUMBER_FONT_SIZE_25));
		lblName.setAlignment(Pos.CENTER_LEFT);
		vbStartAndEnd.setAlignment(Pos.CENTER);
		chkFlag.setAlignment(Pos.CENTER);

		cellRoot.setId("CellRoot");
		lblIndex.setId("CellIndex");
		lblName.setId("CellName");
		chkFlag.setId("CellFlag");

		lblName.getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
		lblIndex.getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());

		if (vbStartAndEnd.getChildren().size() == 1) 
		{
			vbStartAndEnd.getChildren().get(0).setId("CellStart");
			((Label) vbStartAndEnd.getChildren().get(0)).getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
			
		}
		else if (vbStartAndEnd.getChildren().size() == 2) 
		{
			vbStartAndEnd.getChildren().get(0).setId("CellStart");
			vbStartAndEnd.getChildren().get(1).setId("CellEnd");
			((Label) vbStartAndEnd.getChildren().get(0)).getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
			((Label) vbStartAndEnd.getChildren().get(1)).getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
		}
		chkFlag.getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
		AlignmentCheck(cellRoot, lblName, lblIndex, vbStartAndEnd, chkFlag, toolTip);
	}

	private void AlignmentCheck(HBox cellRoot, Label lblName, Label lblIndex, VBox vbStartAndEnd, CheckBox chkFlag,
			Tooltip toolTip) {
		cellRoot.styleProperty().set(BLACK_NAME);
		/*
		 * lblIndex.styleProperty().set(BLACK_NAME);
		 * lblName.styleProperty().set(BLACK_NAME);
		 * vbStartAndEnd.styleProperty().set(BLACK_NAME);
		 * chkFlag.styleProperty().set(BLACK_NAME);
		 * toolTip.styleProperty().set(BLACK_NAME);
		 */
	}
	

	
	public void cssLeftBoxComponents(Label lblLogo, VBox leftBox, PieChart chart, Label lblCategory, ListView<String> listView) {
		lblLogo.setUserData("TopBar");
		lblLogo.setPrefWidth(WIDTH_LBL_LOGO);
		lblLogo.getStylesheets().add(this.getClass().getResource(cssTopBar).toExternalForm());

		leftBox.setPrefSize(WIDTH_LEFTBOX, HEIGHT_LEFTBOX);
		chart.setPrefSize(WIDTH_CHART, HEIGHT_CHART);
		listView.setPrefSize(WIDTH_LISTVIEW, HEIGHT_LISTVIEW);
		lblCategory.setPrefSize(WIDTH_LBLCATEGORY, HEIGHT_LBLCATEGORY);

		lblCategory.setFont(Font.font(FONT_NAME, NUMBER_FONT_SIZE_25));

		lblCategory.setAlignment(Pos.CENTER);
		lblCategory.getStylesheets().add(this.getClass().getResource(cssTopBar).toExternalForm());
		AlignmentCheck(lblCategory, leftBox, lblLogo);

	}

	private void AlignmentCheck(Label lblCategory, VBox leftBox, Label lblLogo) {
		lblLogo.styleProperty().set(BLACK_NAME);
		lblCategory.styleProperty().set(BLACK_NAME);
		leftBox.styleProperty().set(BLACK_NAME);
	}

	public void cssWelcomePage(HBox root, Label welcomeLabel) {
		root.styleProperty().set(BLACK_NAME);
		root.setPrefSize(500, 500);
		welcomeLabel.setFont(Font.font("Cambria", NUMBER_FONT_SIZE_100));
		welcomeLabel.setTranslateX(455);
		welcomeLabel.setPrefSize(500, 500);
	}

	public void setCssAndScalingForRightBox(TabPane tabPane, TextField mainTextField) {
		tabPane.setPrefSize(1450, 1500);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getStylesheets().add(this.getClass().getResource(cssTabPane).toExternalForm());
		mainTextField.setPrefSize(1233, 55);
		mainTextField.setFont(Font.font(FONT_NAME, NUMBER_FONT_SIZE_25));
	}

	public void cssTag(Label lblTagging) 
	{
		lblTagging.getStylesheets().add(this.getClass().getResource(cssTagBack).toExternalForm());
	}

	public void cssAllTitle(Label lblTitle) 
	{
		lblTitle.getStylesheets().add(this.getClass().getResource(cssCellComponents).toExternalForm());
	}

	public void cssChart(PieChart chart) {
		chart.setLegendVisible(false);
		for (int x = 0; x < chart.getData().size(); x++)
		{
			System.out.println(chart.getData().get(x).nameProperty().get());
			if (chart.getData().get(x).nameProperty().get().equals("Floating Tasks")) 
			{
				chart.getData().get(x).getNode().setStyle(" -fx-pie-color: #f0c55d;");
			}
			else if (chart.getData().get(x).nameProperty().get().equals("On-going Tasks")) 
			{
				chart.getData().get(x).getNode().setStyle(" -fx-pie-color: #d47d9c;");
			} 
			else if (chart.getData().get(x).nameProperty().get().equals("Completed Tasks")) 
			{
				chart.getData().get(x).getNode().setStyle(" -fx-pie-color: #568cb4");
			} 
			else if (chart.getData().get(x).nameProperty().get().equals("Overdue Tasks")) 
			{
				chart.getData().get(x).getNode().setStyle(" -fx-pie-color: #060f4d");
			}
		}
	}

	public void setCssAndScalingForCell(HBox hbLblTitle, Label lblTitle) {
		hbLblTitle.setPrefSize(1000, 50);
		lblTitle.setPrefSize(1500, 50);
		lblTitle.setMaxSize(1500, 50);
	}

	public void cssExtraInfo(VBox vbBody, Label titleForPopUp, HBox hbTitle, VBox root, PopOver popUpCell, Label lbl,
			ScrollPane sp) {

		sp.setPrefHeight(1000);
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);

		lbl.setPrefWidth(465);
		lbl.setWrapText(true);

		hbTitle.styleProperty().set(BLACK_NAME);
		hbTitle.setAlignment(Pos.CENTER);

		vbBody.styleProperty().set(BLACK_NAME);
		vbBody.getChildren().add(sp);
		vbBody.setPrefHeight(1000);

		titleForPopUp.setFont(Font.font(FONT_NAME, NUMBER_FONT_SIZE_25));

		popUpCell.arrowSizeProperty().set(0);

		root.setPrefSize(WIDTH_ROOT,HEIGHT_ROOT);

	}

	public void cssStackPaneOfTab(StackPane spTask) {
		spTask.setPrefSize(1450, 1000);
	}
	public void assignUserData(Label lbl,int numberOfUniqueTag) {
		this.numberOfUniqueTag = numberOfUniqueTag;
		for (int y = 0; y < 26; y++) {
			if (lbl.getText().subSequence(0, 1).equals(ascii.get(y))
					|| lbl.getText().subSequence(0, 1).equals(ascii1.get(y))) {
				lbl.setId("color" + Integer.toString(y));
				lbl.setUserData("color" + Integer.toString(y));
				return;
			}
		}
		lbl.setId("noncolor");
		lbl.setUserData("noncolor");
	}

	public HBox getTag(String Content) {
		HBox root = new HBox();
		Label lbl = new Label(Content);
		root.getChildren().add(lbl);
		return root;
	}
}
