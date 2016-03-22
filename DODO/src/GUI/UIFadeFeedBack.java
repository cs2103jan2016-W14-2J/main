package GUI;

import org.controlsfx.control.PopOver;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class UIFadeFeedBack {

	public void run(String string)
	{
		
		/*PopOver po = new PopOver();
		VBox vbPop = new VBox();
		vbPop.getChildren().add(new Button());
		vbPop.setPrefSize(500, 500);
		po.arrowSizeProperty().set(0);
	    po.setContentNode(vbPop);
	    po.show(vbPop);*/

	    
	    
	   /* 
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
	    */
	    
	}
	
	
	
	
}
