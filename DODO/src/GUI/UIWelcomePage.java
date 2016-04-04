package GUI;

import javafx.geometry.Pos;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UIWelcomePage 
{
	 private UICssScaling usc;
	 private Pagination pagination;
	 
	 public UIWelcomePage()
	 {
		 usc = new UICssScaling();
	 }

	 public VBox createPage(int pageIndex) 
	 {
		 VBox box = new VBox();
		 if(pageIndex==0)
		 {
			 BorderPane bp = new BorderPane();
			 bp.setPrefSize(400, 5000);
			 
			/* ImageView logoImgView = new ImageView();
			 Image logoImg = new Image(UIWelcomePage.class.getResourceAsStream("logo.png"));
			 logoImgView.setImage(logoImg);
			 bp.setCenter(logoImgView);*/
			 
			 ImageView logoImgView1 = new ImageView();
			 Image logoImg1 = new Image(UIWelcomePage.class.getResourceAsStream("PAGE2.png"));
			 logoImgView1.setImage(logoImg1);
			 bp.setCenter(logoImgView1);
			 BorderPane.setAlignment(logoImgView1, Pos.CENTER);
			 
			 box.getChildren().add(bp);
		 }
		 if(pageIndex==1)
		 {
			 
		 }
		return box;    
	 }
	public void setRoot(HBox root,Pagination pagination) 
	{

		
		
	    pagination.setPageFactory((Integer pageIndex) -> createPage(pageIndex));
	    AnchorPane anchor = new AnchorPane();
	    AnchorPane.setTopAnchor(pagination, 10.0);
	    AnchorPane.setRightAnchor(pagination, 10.0);
	    AnchorPane.setBottomAnchor(pagination, 10.0);
	    AnchorPane.setLeftAnchor(pagination, 10.0);
	    anchor.setPrefSize(5000, 1000);
	    anchor.getChildren().addAll(pagination);
		//usc.cssWelcomePage(root,welcomeLabel);
		root.getChildren().addAll(anchor);
		
	}
	 
	 
	 
	 
	 
	 
	 
	 
}
