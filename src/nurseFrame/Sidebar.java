package nurseFrame;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {
	
	private NurseFrame nurseFrame;
	
	public Sidebar(NurseFrame nurseFrame) {
		
		this.nurseFrame = nurseFrame;
		
    	this.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" + 
                "-fx-min-width: 50px;" +
                "-fx-min-height: 815px;"
        );
        
    	Label spacingLabel = new Label("");
    	Button button1 = new Button("메인");
    	Button button2 = new Button("조회");
    	Button button3 = new Button("매출");
    	Button button4 = new Button("대기");
    	
        button1.setOnAction(e -> nurseFrame.showPanel(nurseFrame.getMainPanel()));
        button2.setOnAction(e -> nurseFrame.showPanel(nurseFrame.getOtherPanel1()));
        button3.setOnAction(e -> nurseFrame.showPanel(nurseFrame.getOtherPanel2()));
        button4.setOnAction(e -> nurseFrame.showWaitingRoom());

        this.getChildren().addAll(spacingLabel, button1, button2, button3, button4);
    	
    	button1.setStyle(
    		"-fx-min-width: 50px;" + 
    	    "-fx-min-height: 50px;" + 
    	    "-fx-background-color: green;" + 
    	    "-fx-border-color: transparent;" +
    	    "-fx-background-radius: 90;" + //배경 둥글게
    		"-fx-border-radius: 90;" + //테두리도 같이 둥글게
    	    "-fx-text-fill: white;"
    	);
    	
    	button2.setStyle(
    		"-fx-min-width: 50px;" + 
            "-fx-min-height: 50px;" + 
    		"-fx-background-color: blue;" + 
            "-fx-border-color: transparent;" +
            "-fx-background-radius: 90;" + //배경 둥글게
    		"-fx-border-radius: 90;" +//테두리도 같이 둥글게
    		"-fx-text-fill: white;"
    	);
    	
    	button3.setStyle(
        		"-fx-min-width: 50px;" + 
                "-fx-min-height: 50px;" + 
        		"-fx-background-color: orange;" + 
                "-fx-border-color: transparent;" +
                "-fx-background-radius: 90;" + //배경 둥글게
        		"-fx-border-radius: 90;" + //테두리도 같이 둥글게
        		"-fx-text-fill: white;"
    	);
    	
    	button4.setStyle(
    			"-fx-min-width: 50px;" + 
    	        "-fx-min-height: 50px;" + 
    	        "-fx-background-color: lightgray;" + 
    	        "-fx-border-color: gray;" + 
    	        "-fx-border-style: dotted;" + 
    	        "-fx-border-width: 2px;" +
    	        "-fx-background-radius: 90;" + //배경 둥글게
    	        "-fx-border-radius: 90;" + //테두리도 같이 둥글게
    	        "-fx-text-fill: black;"
    	);
    	
    	this.setMargin(button1, new Insets(5, 0, 5, 0));
		this.setMargin(button2, new Insets(5, 0, 5, 0));
		this.setMargin(button3, new Insets(5, 0, 5, 0));
		this.setMargin(button4, new Insets(5, 0, 5, 0));
    	
    	
	}
}
