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
public class UICellComponents
{
	private UIRightBox rightBox;
	private UICssScaling usc;
	private Label lblIndex;
	private ArrayList<Label> lblListTag;
	private Label lblName;
	private VBox vbStartAndEnd;
	private Label lblStart=null;
	private Label lblEnd=null;
    private CheckBox chkFlag;
	private Tooltip toolTip;
	private HBox cellRoot;
	private UIMakeTag makeTag;
	private HBox hbIndexAndName;
	private Label lblTagging;

	public UICellComponents(Logic logic,String strIndex,ArrayList<Category> strTagging, String strName, String startString,String endString,boolean Flag)
	{
		this.rightBox = rightBox;
		makeTag = new UIMakeTag(logic);
		usc = new UICssScaling();
		cellRoot = new HBox();
		lblIndex = new Label(strIndex);
		lblListTag = new ArrayList<Label>();
		checkForTag(strTagging);
		lblName = new Label(strName);
		vbStartAndEnd = new VBox();
		hbIndexAndName = new HBox(lblIndex,lblName);
		VBox vbNameAndTag = new VBox();
		checkForStartEnd(startString, endString);
		chkFlag = new CheckBox();
		checkFlag(Flag);
		toolTip = new Tooltip(strName);
		usc.setCssAndScalingForCell(cellRoot,lblIndex,lblName,lblListTag, vbStartAndEnd,chkFlag,toolTip);
		vbNameAndTag.getChildren().add(hbIndexAndName);
		FlowPane fp = new FlowPane();
		fp.getChildren().addAll(lblListTag);
		vbNameAndTag.getChildren().addAll(fp);
		cellRoot.getChildren().addAll(chkFlag,vbNameAndTag,vbStartAndEnd);
	}
	private void checkFlag(boolean Flag) {
		if(Flag)
		{
			chkFlag.setSelected(true);
		}
		else
		{
			chkFlag.setSelected(false);
		}
	}
	private void checkForStartEnd(String startString, String endString) {
		if(startString!=null && endString!=null)
		{
			lblStart = new Label(startString.toString());
			lblEnd = new Label(endString.toString());
			vbStartAndEnd.getChildren().addAll(lblStart,lblEnd);
		}
		else if(endString!=null)
		{
			lblEnd = new Label(endString.toString());
			vbStartAndEnd.getChildren().add(lblEnd);
		}
	}
	private void checkForTag(ArrayList<Category> strTagging) {
		if(strTagging.size()!=0)
		{
			for(int x=0;x<strTagging.size();x++)
			{
				lblTagging = new Label(strTagging.get(x).getName());
				makeTag.assignUserData(lblTagging);
				lblListTag.add(lblTagging);
			}
		}
		else
		{
			lblListTag.add(new Label(""));
		}
	}
	public UICellComponents(HBox hbLblTitle, Label lblTitle, String strTitle) 
	{
		lblTitle.setText(strTitle);	
		hbLblTitle.setPrefSize(1000,50);
		lblTitle.setPrefSize(1500,50);
		lblTitle.setMaxSize(1500, 50);
	}
	public Tooltip getToolTip()
	{
		return toolTip;
	}
	public HBox getCellRoot()
	{
		return cellRoot;
	}
	public CheckBox getCheckFlag() {
		return chkFlag;
	}
}
