package nurseFrame;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {
	
	
	Sidebar() {
		
    	this.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" + 
                "-fx-min-width: 50px;" +
                "-fx-min-height: 815px;"
        );
        
    	Button button1 = new Button("메인");
    	Button button2 = new Button("조회");
    	Button button3 = new Button("매출");
    	
    	button1.setStyle(
    		"-fx-min-width: 50px;" + 
    	    "-fx-min-height: 50px;" + 
    	    "-fx-background-color: white;" + 
    	    "-fx-border-color: lightgray;" +
    	    "-fx-background-radius: 90;" + //배경 둥글게
    		"-fx-border-radius: 90;"  //테두리도 같이 둥글게
    	);
    	
    	button2.setStyle(
    		"-fx-min-width: 50px;" + 
            "-fx-min-height: 50px;" + 
    		"-fx-background-color: white;" + 
            "-fx-border-color: lightgray;" +
            "-fx-background-radius: 90;" + //배경 둥글게
    		"-fx-border-radius: 90;"  //테두리도 같이 둥글게
    	);
    	
    	button3.setStyle(
        		"-fx-min-width: 50px;" + 
                "-fx-min-height: 50px;" + 
        		"-fx-background-color: white;" + 
                "-fx-border-color: lightgray;" +
                "-fx-background-radius: 90;" + //배경 둥글게
        		"-fx-border-radius: 90;"  //테두리도 같이 둥글게
        );
    	
    	this.getChildren().addAll(button1, button2, button3);
    	
    	this.setMargin(button1, new Insets(5, 0, 5, 0));
		this.setMargin(button2, new Insets(5, 0, 5, 0));
		this.setMargin(button3, new Insets(5, 0, 5, 0));
    	
    	
	}
}
