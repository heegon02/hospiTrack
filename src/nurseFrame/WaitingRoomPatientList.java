// 오늘의 환자 진료 순서.

package nurseFrame;

import application.AppointmentDAO;
import application.DailyQueueDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;

public class WaitingRoomPatientList extends VBox {

    private Label titleLabel;
    
    private Memo memo;
    private MedicalRecords medicalRecords;
    private PrescriptionDetail prescriptionDetail;
    private PatientSelectedListener patientSelectedListener;
    public interface PatientSelectedListener {
    	void onPatientSelected(int patientId);
    }
    
    public void setMemo(Memo memo) {
        this.memo = memo;
    }

    public void setMedicalRecords(MedicalRecords medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public void setPrescriptionDetail(PrescriptionDetail prescriptionDetail) {
        this.prescriptionDetail = prescriptionDetail;
    }

    public void setPatientSelectedListener(PatientSelectedListener listener) {
        this.patientSelectedListener = listener;
    }
    

    //생성자함수
    public WaitingRoomPatientList() {
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
        
        // 주기적 갱신
        Timeline timeline = new Timeline (
        	new KeyFrame(Duration.seconds(3), e -> reloadQueue())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // 환자 리스트 로딩 메소드 (reload가 true면 기존 내용 지우고 다시 그림)
    public void loadPatientQueue(boolean reload) {
        if (reload) {
            this.getChildren().clear();
            this.getChildren().add(titleLabel);
        }

        List<DailyQueueDAO.QueueInfo> queueList = DailyQueueDAO.getTodayQueue();
        for (DailyQueueDAO.QueueInfo q : queueList) {
            
        	
        	
        	//상태가 '취소'면 스킵
        	if ("취소".equals(q.getStatus())) {
        		continue;
        	}
        	
            Button rowButton = new Button();
            rowButton.setMinWidth(310); //환자 버튼의 좌우 넓이 고정.
            rowButton.setStyle(
            	"-fx-background-color: white;" + 
            	"-fx-border-color: lightgray;" + 
            	"-fx-alignment: CENTER_LEFT;"
            );
            
            // 상태가 '입장'이면 테두리 굵고 붉은색으로 변경
            if ("입장".equals(q.getStatus())) {
                rowButton.setStyle(
                    "-fx-background-color: white;" + 
                    "-fx-border-color: red;" + 
                    "-fx-border-width: 3;" +  // 테두리 굵기
                    "-fx-border-style: dashed;" +
                    "-fx-alignment: CENTER_LEFT;"
                );
            }
            
            if ("진료중".equals(q.getStatus())) {
                rowButton.setStyle(
                    "-fx-background-color: white;" + 
                    "-fx-border-color: orange;" + 
                    "-fx-border-width: 2;" +  // 테두리 굵기
                    "-fx-border-style: dashed;" +
                    "-fx-alignment: CENTER_LEFT;"
                );
            }
            
            rowButton.setOnMouseEntered(e -> rowButton.setStyle(
            		"-fx-background-color: white;" + 
                    "-fx-border-color: lightgray;" + 
                    "-fx-alignment: CENTER_LEFT;" + 
            		"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 2);"
            ));
            
            rowButton.setOnMouseExited(e -> rowButton.setStyle(
            		"-fx-background-color: white;" + 
                    "-fx-border-color: lightgray;" + 
                    "-fx-alignment: CENTER_LEFT;"
            ));
            
         // 환자 선택 버튼 (rowButton) 이벤트
            rowButton.setOnAction(e -> {
                int selectedPatientId = q.getPatientId();  
                System.out.println("[환자 선택됨] patient_id = " + selectedPatientId);

                // 환자 관련 패널들에 ID 전달
                if (memo != null) {
                    memo.setPatientId(selectedPatientId);
                }
                if (medicalRecords != null) {
                    medicalRecords.setPatientId(selectedPatientId);
                }
                if (prescriptionDetail != null) {
                    prescriptionDetail.setPatientId(selectedPatientId);
                }
                if (patientSelectedListener != null) {
                    patientSelectedListener.onPatientSelected(selectedPatientId);
                }
            });
            
            
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            
            //접수 순서
            Label numLabel = new Label("No." + q.getQueueNumber());
            numLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            HBox.setHgrow(numLabel, Priority.ALWAYS);
            numLabel.setMaxWidth(Double.MAX_VALUE);

            //환자 이름
            Label nameLabel = new Label(q.getName());
            nameLabel.setStyle("-fx-font-size: 14px;");

            //접수 시간
            Label timeLabel = new Label(q.getVisitTime().substring(11, 16));
            timeLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            HBox.setHgrow(timeLabel, Priority.ALWAYS);
            timeLabel.setMaxWidth(Double.MAX_VALUE);

            
            //상태
            Button statusButton = new Button(q.getStatus());
            statusButton.setStyle(
            		"-fx-text-fill: blue;" +
            		"-fx-font-size: 12px;" + 
            		"-fx-background-color: transparent;" + 
            		"-fx-border-color: lightgray;" + 
            		"-fx-border-radius: 50");
            HBox.setHgrow(statusButton, Priority.ALWAYS);
            statusButton.setMaxWidth(Double.MAX_VALUE);

            row.getChildren().addAll(numLabel, nameLabel, timeLabel, statusButton);
            rowButton.setGraphic(row);
            
            
            
            
            this.getChildren().add(rowButton);
            
        }
    }

    // 재로딩할 때는 reload = true
    public void reloadQueue() {
        loadPatientQueue(true);
    }
}
