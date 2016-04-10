package GUI;

import org.controlsfx.control.PopOver;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.stage.Window;
//@@author A0125372L
public class UIPopUpFeedBack {

	
	private Label feedBackLabel;
	private PopOver popUpFeedBack;
	private TextField mainTextField;
	private VBox vbPop;
	private Path caret;
	private Point2D screenLoc;
	
	public UIPopUpFeedBack(PopOver popUpFeedBack, TextField mainTextField)
	{
		this.popUpFeedBack = popUpFeedBack;
		this.mainTextField = mainTextField;
		feedBackLabel = new Label();
		vbPop = new VBox();
		feedBackLabel.setId("lblFeedBack");
		popUpFeedBack.consumeAutoHidingEventsProperty().set(false);
		popUpFeedBack.setAutoFix(true);
		popUpFeedBack.setContentNode(vbPop);
		popUpFeedBack.setArrowSize(0);
	}

	private Path findCaret(Parent root) 
	{
	    for (Node n : root.getChildrenUnmodifiable()) {
	      if (n instanceof Path) {
	        return (Path) n;
	      } else if (n instanceof Parent) {
	        Path p = findCaret((Parent) n);
	        if (p != null) {
	          return p;
	        }
	      }
	    }
	    return null;
	  }
	private Point2D findScreenLocation(Node node) 
	{
	    double x = 0;
	    double y = 0;
	    for (Node n = node; n != null; n=n.getParent()) {
	      Bounds parentBounds = n.getBoundsInParent();
	      x += parentBounds.getMinX();
	      y += parentBounds.getMinY();
	    }
	    Scene scene = node.getScene();
	    x += scene.getX();
	    y += scene.getY();
	    Window window = scene.getWindow();
	    x += window.getX();
	    y += window.getY();
	    Point2D screenLoc = new Point2D(x, y);
	    return screenLoc;
	  }
	
	public void createListenFeedBack(VBox root,TextField mainTextField,String strFeedBack, Scene scene)
	{
		
		
		
	}
	public void createPopUpFeedBack(VBox root,TextField mainTextField,String strFeedBack, Scene scene)
	{
		feedBackLabel.setText(strFeedBack);
		if(vbPop.getChildren().size()==0)
		{
			vbPop.getChildren().add(feedBackLabel);
		}
		
		vbPop.setPrefSize(1380, 70);
		caret = findCaret(mainTextField);
        screenLoc = findScreenLocation(caret);
        if(scene.getWidth()>1500 && scene.getHeight()>900)
        {
    		vbPop.setPrefSize(1500, 70);
    		if(!root.getChildren().contains(feedBackLabel))
    		{
    			System.out.println("here");
        		root.getChildren().set(1, feedBackLabel);
        		root.getChildren().add(mainTextField);

    		}
    		//popUpFeedBack.show(mainTextField, screenLoc.getX(), screenLoc.getY());
        }
        else
        {
        	if(root.getChildren().contains(feedBackLabel))
        	{
        		root.getChildren().remove(feedBackLabel);
        	}
        	
        	popUpFeedBack.show(mainTextField, screenLoc.getX()-10, screenLoc.getY() + 70);
        	

        	
        }
	}
}
