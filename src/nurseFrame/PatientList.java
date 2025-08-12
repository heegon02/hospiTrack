// 오늘의 환자 진료 순서.

package nurseFrame;

import application.AppointmentDAO;
import application.DailyQueueDAO;
import application.VoicePlayer;
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

public class PatientList extends VBox {

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
            
            //진료실 입장 버튼
            Button enterButton = new Button("✅");
            enterButton.setStyle(
            		"-fx-font-size: 12px;" + 
            				"-fx-background-color: transparent;" + 
            				"-fx-border-color: transparent;" + 
            				"-fx-text-fill: blue;" + 
            				"-fx-font-weight: bold;"
            );
            // 버튼 더블클릭 이벤트
            enterButton.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String currentStatus = q.getStatus();  // 현재 환자의 상태 (예: "대기", "입장")
                    String newStatus = currentStatus.equals("대기") ? "입장" : "대기";
                    
                    System.out.println("상태 변경 시도 appointment_id = " + q.getAppointmentId() 
                                       + ", " + currentStatus + " → " + newStatus);
                    
                    boolean success = AppointmentDAO.updateStatus2(q.getAppointmentId(), newStatus);
                    if (success) {
                        reloadQueue(); // 상태 바뀌었으니 화면 새로고침
                        if ("입장".equals(newStatus)) {
                        	String name = q.getName();
                        	VoicePlayer.speak(name + "진료실 입장 바랍니다.");
                        }
                    } else {
                        Stage errorDialog = new Stage();
                        errorDialog.initModality(Modality.APPLICATION_MODAL);
                        errorDialog.setTitle("에러");

                        Label errorMsg = new Label("상태 변경에 실패했습니다.");
                        Button okBtn = new Button("확인");
                        okBtn.setOnAction(ev -> errorDialog.close());

                        VBox errorBox = new VBox(15, errorMsg, okBtn);
                        errorBox.setAlignment(Pos.CENTER);
                        errorBox.setPadding(new Insets(20));

                        errorDialog.setScene(new Scene(errorBox, 250, 120));
                        errorDialog.showAndWait();
                    }
                }
            });

            
            //진료 취소 버튼
            Button cancelButton = new Button("❌");
            cancelButton.setStyle(
            		"-fx-background-color: transparent;" + 
            		"-fx-border-color: transparent;" + 
            		"-fx-text-fill: red;" + 
            		"-fx-font-weight: bold;" + 
            		"-fx-font-size: 12px;"
            );
            
            cancelButton.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Stage dialog = new Stage();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.setTitle("진료 취소 확인");

                    Label message = new Label("정말 취소하시겠습니까?");
                    message.setStyle("-fx-font-size: 14px;");

                    Button yesBtn = new Button("예");
                    Button noBtn = new Button("아니오");

                    HBox buttonBox = new HBox(10, yesBtn, noBtn);
                    buttonBox.setAlignment(Pos.CENTER);

                    VBox vbox = new VBox(15, message, buttonBox);
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setPadding(new Insets(20));

                    Scene scene = new Scene(vbox, 300, 150);
                    dialog.setScene(scene);

                    // "예" 버튼 동작
                    yesBtn.setOnAction(e -> {
                        System.out.println("취소 시도 appointment_id = " + q.getAppointmentId()); // 디버깅 로그
                        boolean success = AppointmentDAO.cancelAppointment(q.getAppointmentId());
                        if (success) {
                            reloadQueue();
                        } else {
                            Stage errorDialog = new Stage();
                            errorDialog.initModality(Modality.APPLICATION_MODAL);
                            errorDialog.setTitle("에러");

                            Label errorMsg = new Label("진료 취소에 실패했습니다.");
                            Button okBtn = new Button("확인");
                            okBtn.setOnAction(ev -> errorDialog.close());

                            VBox errorBox = new VBox(15, errorMsg, okBtn);
                            errorBox.setAlignment(Pos.CENTER);
                            errorBox.setPadding(new Insets(20));

                            errorDialog.setScene(new Scene(errorBox, 250, 120));
                            errorDialog.showAndWait();
                        }
                        dialog.close();
                    });

                    noBtn.setOnAction(e -> dialog.close());
                    dialog.showAndWait();
                }
            });



            row.getChildren().addAll(cancelButton, numLabel, nameLabel, timeLabel, statusButton, enterButton);
            rowButton.setGraphic(row);
            
            
            
            
            this.getChildren().add(rowButton);
            
        }
    }

    // 재로딩할 때는 reload = true
    public void reloadQueue() {
        loadPatientQueue(true);
    }
}
