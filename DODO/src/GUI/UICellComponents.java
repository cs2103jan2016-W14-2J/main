package GUI;

import java.util.Date;

import org.controlsfx.glyphfont.FontAwesome;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class UICellComponents
{
	private UIRightBox rightBox;
	private UICssScaling usc;
	private Label lblIndex;
	private Label lblTag;
	private Label lblName;
	private VBox vbStartAndEnd;
	private Label lblStart=null;
	private Label lblEnd=null;
    private CheckBox chkFlag;
	private Tooltip toolTip;
	private HBox cellRoot;

	public UICellComponents(String strIndex,String strTag, String strName, Date startTime,Date endTime,boolean Flag)
	{
		this.rightBox = rightBox;
		usc = new UICssScaling();
		cellRoot = new HBox();
		lblIndex = new Label(strIndex);
		lblTag = new Label(strTag);
		lblName = new Label(strName);
		vbStartAndEnd = new VBox();
		if(startTime!=null && endTime!=null)
		{
			lblStart = new Label(startTime.toString());
			lblEnd = new Label(endTime.toString());
			vbStartAndEnd.getChildren().addAll(lblStart,lblEnd);
		}
		else if(endTime!=null)
		{
			lblEnd = new Label(endTime.toString());
			vbStartAndEnd.getChildren().add(lblEnd);
		}
		chkFlag = new CheckBox();
		if(Flag)
		{
			chkFlag.setSelected(true);
		}
		else
		{
			chkFlag.setSelected(false);
		}
	
	
		toolTip = new Tooltip(strName);
		usc.setCssAndScalingForCell(cellRoot,lblIndex,lblTag,lblName,vbStartAndEnd,chkFlag,toolTip);
		cellRoot.getChildren().addAll(lblIndex,lblTag,lblName,vbStartAndEnd,chkFlag);
		
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
