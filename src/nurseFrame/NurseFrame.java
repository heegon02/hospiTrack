//간호사 메인 프레임
package nurseFrame;

import application.LoginDAO;
import application.MedicalRecordDAO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import java.util.List;

public class NurseFrame extends Application {

    private VBox superMainContainer;
    private HBox mainContainer0;
    private HBox mainContainer;
    private VBox container0;
    private VBox container1;
    private VBox container2;
    private VBox container3;
    private VBox container4;

    private HBox mainPanel;   // container1~4를 묶는 패널
    private HBox otherPanel1; // 버튼2 화면
    private HBox otherPanel2; // 버튼3 화면

    private LoginDAO.User currentUser;
    private StackPane contentArea; // 카드 레이아웃 영역
    private MessageDAO messageDAO;
    private Timeline messageCheckTimer;

    public NurseFrame(LoginDAO.User user) {
        this.currentUser = user;
    }

    public HBox getMainPanel() { return mainPanel; }
    public HBox getOtherPanel1() { return otherPanel1; }
    public HBox getOtherPanel2() { return otherPanel2; }

    @Override
    public void start(Stage stage) {
    		
    	

        //슈퍼메인컨테이너 (세로로 배치)
        superMainContainer = new VBox(0);

        //메인컨테이너0 (상단 프로그램 이름)
        mainContainer0 = new HBox(0);
        mainContainer0.setStyle(
            "-fx-min-height: 50px;" +
            "-fx-min-width: 1600px;"
        );
        mainContainer0.setAlignment(Pos.CENTER_LEFT);

        //프로그램 이름
        Label programTitle = new Label("      hospiTrack");
        programTitle.setStyle(
            "-fx-font-family: '고딕';"+
            "-fx-text-fill: white;"+
            "-fx-font-size: 19px;" +
            "-fx-min-height: 50px;" +
            "-fx-min-width: 1480px;"
        );

        //로그인한 사용자 이름
        Label nurseName = new Label(currentUser.getName());
        nurseName.setStyle("-fx-font-size: 15px;" + "-fx-text-fill: white;" );

        mainContainer0.setStyle("-fx-background-color: green;");
        mainContainer0.getChildren().addAll(programTitle, nurseName);

        //메인컨테이너 (좌: sidebar, 우: contentArea)
        mainContainer = new HBox(10);

        // sidebar 영역
        container0 = new VBox(2);
        container0.setPadding(new Insets(15));
        Sidebar sidebar = new Sidebar(this);
        container0.getChildren().add(sidebar);

        // contentArea (카드 레이아웃)
        contentArea = new StackPane();

        container1 = new VBox(10);
        container1.setPadding(new Insets(15));
        NewPatientAddButton newPatientPanel = new NewPatientAddButton();
        SearchPatient searchPanel = new SearchPatient();
        newPatientPanel.setSearchPatient(searchPanel);
        
        PatientInfoPanel infoPanel = new PatientInfoPanel();
        searchPanel.setOnPatientSelected(patientId -> infoPanel.loadPatientInfo(patientId));
        container1.getChildren().addAll(newPatientPanel, searchPanel, infoPanel);

        container2 = new VBox(10);
        container2.setPadding(new Insets(15));
        Memo memoPanel = new Memo();
        PrescriptionDetail detailPanel = new PrescriptionDetail();
        container2.getChildren().addAll(memoPanel, detailPanel);

        container3 = new VBox(10);
        container3.setPadding(new Insets(15));
        container3.setMinSize(300, 850);
        PatientList pl = new PatientList();
        infoPanel.setPatientList(pl);
        container3.getChildren().add(pl);

        container4 = new VBox(10);
        container4.setPadding(new Insets(15));
        container4.setMinSize(400, 850);
        MedicalRecords mr = new MedicalRecords();
        container4.getChildren().add(mr);
        
        pl.setPatientSelectedListener(patientId -> {
            infoPanel.loadPatientInfo(patientId);

            Integer latestAppointmentId = MedicalRecordDAO.getLatestAppointmentIdByPatientId(patientId);

            if (latestAppointmentId != null) {
                detailPanel.setAppointmentId(latestAppointmentId);
            } else {
                detailPanel.setAppointmentId(null);
            }
        });

        
        pl.setMedicalRecords(mr);
        mr.setOnAppointmentDateSelected(appointmentId -> {detailPanel.setAppointmentId(appointmentId);});
        pl.setMemo(memoPanel);
        
        
        // Memo, MedicalRecords, PrescriptionDetail 연결
        searchPanel.setMemo(memoPanel);
        searchPanel.setMedicalRecords(mr);
        searchPanel.setPrescriptionDetail(detailPanel);
        

        mainPanel = new HBox(10);
        mainPanel.setPadding(new Insets(15));
        mainPanel.getChildren().addAll(container1, container2, container3, container4);

        
        otherPanel1 = new HBox(10);
        otherPanel1.setPadding(new Insets(15));
        InquiryDate iqd = new InquiryDate();
        VBox vbox3 = new VBox(10);
        PrescriptionDetail detailPanel2 = new PrescriptionDetail();
        Memo memo2 = new Memo();
        memo2.getSubmitButton().setVisible(false);
        memo2.getSubmitButton().setManaged(false);
        vbox3.getChildren().addAll(memo2, detailPanel2);
        
        iqd.setOnAppointmentSelected(appointmentId -> {
        	memo2.setPatientId(appointmentId);
        	detailPanel2.setAppointmentId(appointmentId);
        });
        
        otherPanel1.getChildren().addAll(iqd, vbox3);
        
        otherPanel2 = new HBox(10);
        otherPanel2.setPadding(new Insets(15));
        otherPanel2.getChildren().addAll();
        
        contentArea.getChildren().addAll(mainPanel, otherPanel1, otherPanel2);

        // 기본은 mainPanel 보이기
        showPanel(mainPanel);

        // 메인컨테이너에 sidebar + contentArea 넣기
        mainContainer.getChildren().addAll(container0, contentArea);

        superMainContainer.getChildren().addAll(mainContainer0, mainContainer);

        // 전체 프레임 세팅
        Scene scene = new Scene(superMainContainer, 1600, 930);
        stage.setScene(scene);
        stage.setTitle("간호사 전용 사용화면입니다.");
        stage.setMaximized(true);
        stage.show();
        
        // 메시지 체크 기능 초기화
        initializeMessageCheck();
    }

    public void showPanel(Node panel) {
        contentArea.getChildren().setAll(panel);
    }
    
    // 메시지 체크 기능 초기화
    private void initializeMessageCheck() {
        messageDAO = new MessageDAO();
        
        // 10초마다 새 메시지 체크
        messageCheckTimer = new Timeline(
            new KeyFrame(Duration.seconds(5), event -> checkNewMessages())
        );
        messageCheckTimer.setCycleCount(Timeline.INDEFINITE);
        messageCheckTimer.play();
        
        // 초기 메시지 체크
        checkNewMessages();
    }
    
    // 새 메시지 체크 및 팝업 표시
    private void checkNewMessages() {
        Platform.runLater(() -> {
            try {
                List<Message> unreadMessages = messageDAO.getUnreadMessages();
                
                for (Message message : unreadMessages) {
                    showMessagePopup(message);
                    // 메시지를 읽음 상태로 변경
                    messageDAO.markMessageAsRead(message.getMessageId());
                }
            } catch (Exception e) {
                System.err.println("메시지 체크 중 오류: " + e.getMessage());
            }
        });
    }
    
    // 메시지 팝업 표시
    private void showMessagePopup(Message message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("새 메시지");
        alert.setHeaderText("의사 " + message.getSenderName() + "님이 보낸 메시지");
        alert.setContentText("시간: " + message.getSendTime().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                "\n\n내용:\n" + message.getContent());

        alert.getButtonTypes().setAll(ButtonType.OK);

        // DialogPane 전체 스타일
        alert.getDialogPane().setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #87CEEB, #B0E0E6);" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;" +
            "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #333333;"
        );

        // 내부 header와 content 패널 투명 처리
        alert.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color: transparent;");
        alert.getDialogPane().lookup(".content").setStyle("-fx-background-color: transparent;");

        // 버튼 스타일
        alert.getDialogPane().lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: #064273;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8 20 8 20;"
        );

        alert.showAndWait();
    }
    
    public void showWaitingRoom() {
        Stage waitingStage = new Stage();
        waitingStage.setTitle("대기실 화면");
        
        StackPane root = new StackPane();
        
        HBox container = new HBox(10);
        
        VBox vbox1 = new VBox(10);
        WaitingRoomPatientList wrpl = new WaitingRoomPatientList();
        vbox1.getChildren().addAll(wrpl);
        
        VBox vbox2 = new VBox(10);
        WaitingRoomYoutube youtube = new WaitingRoomYoutube();
        HBox.setHgrow(youtube, Priority.ALWAYS);
        HBox.setHgrow(vbox2, Priority.ALWAYS); 
        VBox.setVgrow(youtube, Priority.ALWAYS); 
        
        vbox2.getChildren().addAll(youtube);
        
        container.getChildren().addAll(vbox1, vbox2);
        
        HBox.setMargin(vbox1, new Insets(10, 10, 10, 10));
        HBox.setMargin(vbox2, new Insets(10, 10, 10, 10));
        
        
        
        root.getChildren().addAll(container); //여기에 내부 패널 추가
        Scene scene = new Scene(root, 1600, 850); 
        waitingStage.setScene(scene);
        waitingStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
