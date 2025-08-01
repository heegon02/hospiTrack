package nurseFrame;

import application.DailyQueueDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;

public class PatientList extends VBox {

    private Label titleLabel;

    //생성자함수
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

        this.setMinSize(350, 815);
        this.setMaxSize(350, 815);

        // 초기 로딩 시에는 reload = false
        this.getChildren().add(titleLabel);
        loadPatientQueue(false);
    }

    // 환자 리스트 로딩 메소드 (reload가 true면 기존 내용 지우고 다시 그림)
    public void loadPatientQueue(boolean reload) {
        if (reload) {
            this.getChildren().clear();
            this.getChildren().add(titleLabel);
        }

        List<DailyQueueDAO.QueueInfo> queueList = DailyQueueDAO.getTodayQueue();
        for (DailyQueueDAO.QueueInfo q : queueList) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label numLabel = new Label("No." + q.getQueueNumber());
            numLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            HBox.setHgrow(numLabel, Priority.ALWAYS);
            numLabel.setMaxWidth(Double.MAX_VALUE);

            Label nameLabel = new Label(q.getName());
            nameLabel.setStyle("-fx-font-size: 14px;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            nameLabel.setMaxWidth(Double.MAX_VALUE);

            Label timeLabel = new Label(q.getVisitTime().substring(11, 16));
            timeLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            HBox.setHgrow(timeLabel, Priority.ALWAYS);
            timeLabel.setMaxWidth(Double.MAX_VALUE);

            Label statusLabel = new Label("[" + q.getStatus() + "]");
            statusLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px; ");

            row.getChildren().addAll(numLabel, nameLabel, timeLabel, statusLabel);
            
            Button rowButton = new Button();
            rowButton.setGraphic(row);
            
            rowButton.setStyle(
            	"-fx-background-color: white;" + 
                "-fx-border-color: lightgray;" + 
            	"-fx-min-width: 310;"
            );
            
            this.getChildren().add(rowButton);
            
        }
    }

    // 재로딩할 때는 reload = true
    public void reloadQueue() {
        loadPatientQueue(true);
    }
}
