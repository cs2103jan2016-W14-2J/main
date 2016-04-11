package GUI;

import java.util.ArrayList;

import Logic.Logic;
import Task.Category;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
//@@author A0125372L
public class UIMakeTag {

	private ArrayList<Integer> tagColorIndex;
	private ArrayList<Category> uniqueListCategory;
	private ArrayList <String> ascii = new ArrayList <String> (26);
	private ArrayList <String> ascii1 = new ArrayList <String> (26);
	private int numberOfUniqueTag;
	private Logic logic;

	public UIMakeTag(Logic logic)
	{
		this.logic = logic;
		numberOfUniqueTag = logic.getCategories().size();
		uniqueListCategory = logic.getCategories();
		for (char c = 'A'; c <= 'Z'; c++)
		{
		    ascii.add (String.valueOf (c));
		}
		for (char c = 'a'; c <= 'z'; c++)
		{
		    ascii1.add (String.valueOf (c));
		}
	}
	public void assignUserData(Label lbl)
	{
		
		//lbl.setUserData(value);
		numberOfUniqueTag = logic.getCategories().size();
		
			//if(lbl.getText().equals(logic.getCategories().get(x).getName()))
		for(int y=0;y<26;y++)
		{
			
			if(lbl.getText().subSequence(0, 1).equals(ascii.get(y)) || lbl.getText().subSequence(0, 1).equals(ascii1.get(y)))
			{//+Integer.toString(x)
				lbl.setId("color"+Integer.toString(y));
				lbl.setUserData("color"+Integer.toString(y));
				return;
			}
			
		}	
		lbl.setId("noncolor");
		lbl.setUserData("noncolor");

		
	
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