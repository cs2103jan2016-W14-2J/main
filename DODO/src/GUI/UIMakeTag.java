package GUI;

import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Random;

import Logic.Logic;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

public class UIMakeTag {

	private ArrayList<Integer> tagColorIndex;
	private ArrayList<String> uniqueListCategory;
	private int numberOfUniqueTag;
	private Logic logic;

	public UIMakeTag(Logic logic)
	{
		this.logic = logic;
		numberOfUniqueTag = logic.getCategories().size();
		uniqueListCategory = logic.getCategories();
	}
	public void assignUserData(Label lbl)
	{
		//lbl.setUserData(value);
		numberOfUniqueTag = logic.getCategories().size();
		for(int x=0;x<numberOfUniqueTag;x++)
		{
			if(lbl.getText().equals(logic.getCategories().get(x)))
			{//+Integer.toString(x)
				lbl.setId("color"+Integer.toString(x));
			}
		}
	
	}
	public HBox getTag(String Content)
	{
		HBox root = new HBox();
		Label lbl = new Label(Content);
		root.getChildren().add(lbl);
		return root;
	}
	
	
}








































































/*public HBox getTag(String Content)
{
	HBox root = new HBox();
	Label lbl = new Label(Content);
	
	int R = (int)(Math.random()*256);
	int G = (int)(Math.random()*256);
	int B= (int)(Math.random()*256);
	Color color = new Color(R, G, B); //random color, but can be bright or dull
	java.awt.Color awtColor = color;
	Random random = new Random();
	final float hue = random.nextFloat();
	final float saturation = 0.5f;//1.0 for brilliant, 0.0 for dull
	final float luminance = 0.5f; //1.0 for brighter, 0.0 for black
	color = Color.getHSBColor(hue, saturation, luminance);
	int a = awtColor.getAlpha();
	double opacity = a / 255.0 ;
	javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(R, G, B, opacity);
	root.setBackground(new Background(new BackgroundFill(fxColor, null, null)));
	root.getChildren().add(lbl);
	return root;
}*/