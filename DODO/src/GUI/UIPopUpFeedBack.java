package GUI;

import org.controlsfx.control.PopOver;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.stage.Window;

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
	public void createPopUpFeedBack(String strFeedBack, Scene scene)
	{
		vbPop.getChildren().clear();
		feedBackLabel.setText(strFeedBack);
		vbPop.getChildren().add(feedBackLabel);
		vbPop.setPrefSize(1380, 70);
		caret = findCaret(mainTextField);
        screenLoc = findScreenLocation(caret);
		popUpFeedBack.show(mainTextField, screenLoc.getX()-10, screenLoc.getY() + 70);
	}
}
