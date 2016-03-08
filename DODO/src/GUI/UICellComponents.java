package GUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class UICellComponents{
	
	private Label lblIndex;
	private Label lblName;
	private Label lblDescription;
    private CheckBox chkBox;
	private Tooltip toolTip;
	private HBox cellRoot;
	private final double labelWidthIndex = 250; //centerBox titledPane size
	private final double labelHeightIndex = 100;
	private final double labelWidthName = 1250; //centerBox titledPane size
	private final double labelHeightName = 1000;
	public final double cellWidth = 100;
	public final double cellHeight = 35;
   

	public UICellComponents(String strIndex, String strName, String strDescription)
	{
		cellRoot = new HBox();
		lblIndex = new Label(strIndex);
		lblName = new Label(strName);
		lblDescription = new Label(strDescription);
		chkBox = new CheckBox();
		toolTip = new Tooltip();
		toolTip.setText(strName);
		setComponentsSetting();
		setComponentsCSS();
		addAllComponents();
	}
	private void addAllComponents() 
	{
		cellRoot.getChildren().addAll(lblIndex,lblName,lblDescription,chkBox);	
		
	}
	private void setComponentsCSS() 
	{
		setCellCosmetic();
	}
	private void setCellCosmetic()
	{
		setCellRootCosmetics();
	}
	private void setCellRootCosmetics() 
	{
		cellRoot.setStyle
		("-fx-padding:5;" 
		+ "-fx-border-style:solid;"
				+"fx-border-width:2;"
		+"fx-border-inserts:5;"
				+"fx-border-radius:5;"
		+"fx-border-color:blue;"
		+"fx-border-radius: 10 10 0 0;"
		 +"fx-background-radius: 10 10 0 0;");
	}
	private void setComponentsSetting() 
	{
		setLblIndexSetting();
		setCellRootSetting();
		setChkBoxSetting();
	}
	private void setChkBoxSetting() {
		chkBox.getStylesheets().add(this.getClass().getResource("checkBox.css").toExternalForm());
    	chkBox.setAlignment(Pos.CENTER_RIGHT);
        chkBox.setFocusTraversable(false);		
	}
	private void setCellRootSetting() 
	{
		cellRoot.setPrefSize(cellWidth,cellHeight);
		
	}
	private void setLblIndexSetting() 
	{
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
