package GUI;

import java.util.ArrayList;

import Logic.Logic;
import Task.Category;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//@@author A0125372L
public class UICellComponents {
	private UIUtility utility;
	private Label lblIndex;
	private ArrayList<Label> lblListTag;
	private Label lblName;
	private VBox vbStartAndEnd;
	private Label lblStart = null;
	private Label lblEnd = null;
	private CheckBox chkFlag;
	private Tooltip toolTip;
	private HBox cellRoot;
	private HBox hbIndexAndName;
	private Label lblTagging;
	private static final int WIDTH_HBOX  = 1000;
	private static final int HEIGHT_HBOX  = 50;
	private static final int WIDTH_LABEL  = 1500;
	private static final int HEIGHT_LABEL = 50;

	public UICellComponents(Logic logic, String strIndex, ArrayList<Category> strTagging, String strName, String startString, String endString, boolean Flag) {
		utility = new UIUtility(logic.getCategories().size(),logic.getCategories());
		cellRoot = new HBox();
		lblIndex = new Label(strIndex);
		lblListTag = new ArrayList<Label>();
		checkForTag(strTagging,logic.getCategories().size());
		lblName = new Label(strName);
		vbStartAndEnd = new VBox();
		hbIndexAndName = new HBox(lblIndex, lblName);
		VBox vbNameAndTag = new VBox();
		checkForStartEnd(startString, endString);
		chkFlag = new CheckBox();
		checkFlag(Flag);
		toolTip = new Tooltip(strName);
		utility.setCssAndScalingForCell(cellRoot, lblIndex, lblName, lblListTag, vbStartAndEnd, chkFlag, toolTip);
		vbNameAndTag.getChildren().add(hbIndexAndName);
		FlowPane flowPane = new FlowPane();
		flowPane.getChildren().addAll(lblListTag);
		vbNameAndTag.getChildren().addAll(flowPane);
		cellRoot.getChildren().addAll(chkFlag, vbNameAndTag, vbStartAndEnd);
	}

	private void checkFlag(boolean Flag) {
		if (Flag) {
			chkFlag.setSelected(true);
		} else {
			chkFlag.setSelected(false);
		}
	}

	private void checkForStartEnd(String startString, String endString) {
		if (startString != null && endString != null) {
			lblStart = new Label(startString.toString());
			lblEnd = new Label(endString.toString());
			vbStartAndEnd.getChildren().addAll(lblStart, lblEnd);
		} else if (endString != null) {
			lblEnd = new Label(endString.toString());
			vbStartAndEnd.getChildren().add(lblEnd);
		}
	}

	private void checkForTag(ArrayList<Category> strTagging, int categoriesSize) {
		if (strTagging.size() != 0) {
			for (int x = 0; x < strTagging.size(); x++) {
				lblTagging = new Label(strTagging.get(x).getName());
				//logic.getCategories().size()
				utility.assignUserData(lblTagging,categoriesSize);
				lblListTag.add(lblTagging);
			}
		} else {
			lblListTag.add(new Label(""));
		}
	}

	public UICellComponents(HBox hbLblTitle, Label lblTitle, String strTitle) {
		lblTitle.setText(strTitle);
		hbLblTitle.setPrefSize(WIDTH_HBOX, HEIGHT_HBOX);
		lblTitle.setPrefSize(WIDTH_LABEL, HEIGHT_LABEL);
		lblTitle.setMaxSize(WIDTH_LABEL, HEIGHT_LABEL);
	}

	public Tooltip getToolTip() {
		return toolTip;
	}

	public HBox getCellRoot() {
		return cellRoot;
	}

	public CheckBox getCheckFlag() {
		return chkFlag;
	}
}
