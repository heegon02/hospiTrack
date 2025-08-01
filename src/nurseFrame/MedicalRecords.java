package nurseFrame;

import application.MedicalRecordDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class MedicalRecords extends VBox {

    private Integer patientId;
    
    public MedicalRecords() {
        Label titleLabel = new Label("환자 진료 기록");
        titleLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;"
        );

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.setAlignment(Pos.TOP_LEFT);

        this.getChildren().add(titleLabel);
        
     // VBox 스타일링
        this.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: lightgray;"
        );
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setAlignment(Pos.TOP_LEFT);
        this.setMinSize(400, 740);
        
        
    }

    
    
}
