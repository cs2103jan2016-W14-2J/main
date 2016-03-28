package GUI;

import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UIWelcomePage 
{
	 UICssScaling usc;
	 
	 
	 public UIWelcomePage()
	 {
		 usc = new UICssScaling();
	 }


	public void setRoot(HBox root) 
	{
		
		Label welcomeLabel = new Label("WELCOME");
			
		
		
		usc.cssWelcomePage(root,welcomeLabel);
		root.getChildren().addAll(welcomeLabel);
		
	}
	 
	 
	 
	 
	 
	 
	 
	 
}
