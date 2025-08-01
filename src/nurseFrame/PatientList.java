package nurseFrame;

import application.DailyQueueDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.util.List;

public class PatientList extends VBox {

    private Label titleLabel;

    //constructor
    public PatientList() {
        titleLabel = new Label("오늘의 환자 진료 순서"); 
        titleLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;"
        );

        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setAlignment(Pos.TOP_LEFT);
        this.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: lightgray;"
        );

        this.getChildren().addAll(titleLabel);
        this.setMinSize(350, 740);

        loadPatientQueue();
    }

    private void loadPatientQueue() {
        List<DailyQueueDAO.QueueInfo> queueList = DailyQueueDAO.getTodayQueue();

        for (DailyQueueDAO.QueueInfo q : queueList) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label numLabel = new Label("No." + q.getQueueNumber());
            numLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label nameLabel = new Label(q.getName());
            nameLabel.setStyle("-fx-font-size: 14px;");

            Label timeLabel = new Label(q.getVisitTime().substring(11,16)); // "HH:mm"만 추출
            timeLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

            Label statusLabel = new Label("[" + q.getStatus() + "]");
            statusLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");

            row.getChildren().addAll(numLabel, nameLabel, timeLabel, statusLabel);
            this.getChildren().add(row);
        }
    }
    
    public void reloadQueue() {
        this.getChildren().clear();

        Label titleLabel = new Label("오늘의 환자 진료 순서");
        titleLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;"
        );
        this.getChildren().add(titleLabel);

        List<DailyQueueDAO.QueueInfo> queueList = DailyQueueDAO.getTodayQueue();
        for (DailyQueueDAO.QueueInfo q : queueList) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label numLabel = new Label("No." + q.getQueueNumber());
            numLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label nameLabel = new Label(q.getName());
            nameLabel.setStyle("-fx-font-size: 14px;");

            Label timeLabel = new Label(q.getVisitTime().substring(11,16));
            timeLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

            Label statusLabel = new Label("[" + q.getStatus() + "]");
            statusLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");

            row.getChildren().addAll(numLabel, nameLabel, timeLabel, statusLabel);
            this.getChildren().add(row);
        }
    }

    
}
