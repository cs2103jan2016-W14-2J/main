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

	ArrayList<Integer> tagMapping;
	ArrayList<String> tagList;
	UICssScaling ucs = new UICssScaling();
	ArrayList<Label> taskLblTag;

	public UIMakeTag()
	{
		taskLblTag = new ArrayList<Label>();
		tagMapping = new ArrayList<Integer>();
		tagList = new ArrayList<String>();
	}

	public HBox getTag(String Content)
	{
		HBox root = new HBox();
		Label lbl = new Label(Content);
		root.getChildren().add(lbl);
		return root;
	}
	public void tagMapping(ArrayList<String> categories) 
	{
		tagMapping.clear();
		tagList = categories;
		if(categories!=null);
		{
			for(int x=0;x<categories.size();x++)
			{
				
				tagMapping.add(x);
			}
		}
		
	}
	public void MatchTagToCategories(Logic logic ,ArrayList<String> listOfTags)
	{			
		tagMapping.clear();
		for(int x=0;x<logic.getCategories().size();x++)
		{
			tagMapping.add(x);
		}
	}
	public ArrayList<Label> assignTagUserData(Logic logic, ArrayList<String> strTag)
	{
		for(int x=0;x<logic.getCategories().size();x++)
		{
			for(int y=0;y<strTag.size();y++)
			{
					if(tagList.size()!=0&& strTag.size()!=0 &&tagList.get(x).equals(strTag.get(y)))
					{
						System.out.println("inininininininininininininininininininininininininininininininin");
						Label lbl = new Label(strTag.get(y));
						lbl.setUserData("color"+tagMapping.get(x));
						ucs.setTagBackground(lbl);
						taskLblTag.add(lbl);
					}
			}
		}		
		
		return taskLblTag;
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