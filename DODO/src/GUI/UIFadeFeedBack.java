package GUI;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class UIFadeFeedBack {

	public void run(String string)
	{
		
		Stage fadingStage = new Stage();
	    HBox dialogVBox = new HBox();
	    Label labelFeedBack = new Label(string);
	    dialogVBox.setStyle("-fx-padding:3;" + "-fx-border-style:solid;");
	    labelFeedBack.setFont(new Font("Arial", 30));
	    dialogVBox.getChildren().add(labelFeedBack);
	    fadingStage.initStyle(StageStyle.UNDECORATED);
	    
	    FadeTransition ft = new FadeTransition(Duration.millis(3000), dialogVBox);
	    ft.setFromValue(0.0);
	    ft.setToValue(1.0);
	    ft.play();
	    ft.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	fadingStage.hide();
                System.out.println("Hiding");
            }
        });
	    Scene scene = new Scene(dialogVBox, 1888, 50, Color.WHITE);
	    fadingStage.setScene(scene);
	    fadingStage.setX(20);
	    fadingStage.setY(888);
	    fadingStage.show();
	    
	    
	}
	
	
	
	
}
