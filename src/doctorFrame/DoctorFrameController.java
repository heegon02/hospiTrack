package doctorFrame;

import nurseFrame.Memo;
import nurseFrame.InquiryDate;
import nurseFrame.MedicalRecords;
import application.NursingRecordDAO;

import doctorFrame.DailyQueueDAO;
import doctorFrame.PatientDAO;
import doctorFrame.UserDAO;
import doctorFrame.MedicalRecordDAO;
import doctorFrame.AppointmentDAO;
import doctorFrame.MessageDAO;
import doctorFrame.DatabaseConnection;
import doctorFrame.DailyQueue;
import doctorFrame.Patient;
import doctorFrame.User;
import doctorFrame.Appointment;
import doctorFrame.AppointmentDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import nurseFrame.PrescriptionDetail;
import nurseFrame.SearchPatient;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.animation.Animation;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class DoctorFrameController implements Initializable {
    
    @FXML private Text programNameText;
    @FXML private Text userInfoText;
   
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, String> statusColumn;
  
    //탭
    
    @FXML
    private Button btnConsult;

    @FXML
    private Button btnPatientManage;

    @FXML
    private Button btnStatistics;

    @FXML
    private VBox tabConsult;

    @FXML
    private VBox tabPatientManage;

    @FXML
    private VBox tabStatistics;
    //
   

    private void showConsultTab() {
        tabConsult.setVisible(true);
        tabConsult.setManaged(true);

        tabPatientManage.setVisible(false);
        tabPatientManage.setManaged(false);

        tabStatistics.setVisible(false);
        tabStatistics.setManaged(false);

        // 버튼 스타일 변경 (선택된 버튼 강조)
        btnConsult.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPatientManage.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnStatistics.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
    }

    private void showPatientManageTab() {
        tabConsult.setVisible(false);
        tabConsult.setManaged(false);

        tabPatientManage.setVisible(true);
        tabPatientManage.setManaged(true);

        tabStatistics.setVisible(false);
        tabStatistics.setManaged(false);
        
        loadPatientManagementUI();

        btnConsult.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnPatientManage.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnStatistics.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
    }

    private void showStatisticsTab() {
        tabConsult.setVisible(false);
        tabConsult.setManaged(false);

        tabPatientManage.setVisible(false);
        tabPatientManage.setManaged(false);

        tabStatistics.setVisible(true);
        tabStatistics.setManaged(true);

        btnConsult.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnPatientManage.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnStatistics.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
    }

    
    private void loadPatientManagementUI() {
        System.out.println("loadPatientManagementUI 호출됨");

        tabPatientManage.getChildren().clear();
        
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

        // 두 컴포넌트를 좌우로 배치
        HBox hbox = new HBox(10, iqd, vbox3);

        iqd.setPrefSize(300, 630);
        vbox3.setPrefSize(300, 630);
        hbox.setPrefSize(900, 700);

        tabPatientManage.getChildren().add(hbox);
    }
    // 환자 대기열 관련
    @FXML private VBox patientQueueVBox;
    @FXML private Text queueCountText;
    
    // 환자 정보 관련
    @FXML private TextField patientNameField;
    @FXML private TextField patientGenderField;
    @FXML private TextField patientBirthField;
    @FXML private TextField patientAgeField;
    @FXML private TextField patientHeightField;
    @FXML private TextField patientWeightField;
    @FXML private TextField patientBloodTypeField;
    
    // 환자 진료 기록 관련
    @FXML private VBox medicalRecordsVBox;

    @FXML private Button refreshQueueButton;
    
    // 의사 처방 내역 관련
    @FXML private TextArea medicationArea;
    @FXML private TextArea symptomsArea;
    @FXML private TextArea opinionArea;
    @FXML private Button completeTreatmentButton;
    
    // 처방 입력 관련
    @FXML private ComboBox<String> diagnosisComboBox;
    @FXML private TextField diagnosisCodeField;
    @FXML private ComboBox<String> medicationComboBox;
    @FXML private TextField dailyDosageField;
    @FXML private TextField totalDaysField;
    @FXML private ComboBox<String> injectionComboBox;
    @FXML private Button enterButton;
    
    // 간호사 메모 관련
    @FXML private VBox nurseMemoVBox;
    @FXML private TextArea sendToNurseArea;
    @FXML private Button sendToNurseButton;
    
    private Memo nurseMemo;
    private MedicalRecords medicalRecords;
    private DailyQueueDAO dailyQueueDAO;
    private PatientDAO patientDAO;
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private UserDAO userDAO;
    private MedicalRecordDAO medicalRecordDAO;
    private AppointmentDAO appointmentDAO;
    private MessageDAO messageDAO;
    private User currentUser;
    private Patient selectedPatient;
    private DailyQueue selectedQueue;
    //prescriptionDetail
    private PrescriptionDetail prescriptionDetail;
    private Integer currentPatientId = 1;
    private SearchPatient searchPatient;
    
    private ObservableList<DailyQueue> queueList = FXCollections.observableArrayList();
    private List<Appointment> currentAppointments = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 기존 초기화 코드들
        initializeDAOs();
        loadCurrentUser();
        setupUI();
        loadTodayQueue();
        setupEventHandlers();

        medicalRecords = new MedicalRecords();
        medicalRecordsVBox.getChildren().add(medicalRecords);

        nurseMemo = new Memo();
        nurseMemoVBox.getChildren().add(nurseMemo);

        medicalRecords.setOnAppointmentDateSelected(appointmentId -> {
            // 클릭 이벤트 삽입.
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            List<DailyQueue> latestQueueList = dailyQueueDAO.getTodayQueue();
            List<Appointment> latestAppointmentList = appointmentDAO.getTodayAppointments();

            Platform.runLater(() -> {
                queueList.clear();
                queueList.addAll(latestQueueList);

                appointmentList.clear();
                appointmentList.addAll(latestAppointmentList);

                updateQueueDisplay();
            });
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // --- 여기부터 탭 초기화 및 버튼 이벤트 추가 ---
        showConsultTab();  // 초기 탭 설정(진료 탭 보여주기)

        btnConsult.setOnAction(e -> showConsultTab());
        btnPatientManage.setOnAction(e -> showPatientManageTab());
        btnStatistics.setOnAction(e -> showStatisticsTab());
    }

   
    private void initializeDAOs() {
        dailyQueueDAO = new DailyQueueDAO();
        patientDAO = new PatientDAO();
        messageDAO = new MessageDAO();
        userDAO = new UserDAO();
        medicalRecordDAO = new MedicalRecordDAO();
        appointmentDAO = new AppointmentDAO();
    }
  
    private void loadCurrentUser() {
        // 임시로 사용자 ID 1을 사용 (실제로는 로그인 시스템에서 가져와야 함)
        currentUser = userDAO.getUserById(1);
        if (currentUser != null) {
            userInfoText.setText(currentUser.getDepartment() + " / " + currentUser.getName());
        }
        programNameText.setText("hospitrack");
    }
    
    private void setupUI() {
        // 환자 정보 필드들을 읽기 전용으로 설정
        patientNameField.setEditable(false);
        patientGenderField.setEditable(false);
        patientBirthField.setEditable(false);
        patientAgeField.setEditable(false);
        patientHeightField.setEditable(false);
        patientWeightField.setEditable(false);
        patientBloodTypeField.setEditable(false);
        
        // 진단명 드롭다운 설정 - DB에서 가져오기
        loadDiagnosisData();
        
        // 약물 드롭다운 설정 - DB에서 가져오기
        loadMedicationData();
        
        // 주사 드롭다운 설정 - DB에서 가져오기
        loadInjectionData();
        
        // 진료 완료 버튼 비활성화
        completeTreatmentButton.setDisable(true);
    }
    
    private void loadDiagnosisData() {
        // treatments 테이블에서 진단명 가져오기
        String sql = "SELECT DISTINCT name, code FROM treatments ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                diagnosisComboBox.getItems().add(rs.getString("name"));
            }
            
            diagnosisComboBox.setOnAction(e -> updateDiagnosisCode());
            
        } catch (SQLException e) {
            System.err.println("Error loading diagnosis data: " + e.getMessage());
            // 기본값 설정
            diagnosisComboBox.getItems().addAll("감기", "고혈압", "당뇨", "두통", "복통");
        }
    }
    
    private void loadMedicationData() {
        // medications 테이블에서 약물명 가져오기 (type이 '약'인 것만)
        String sql = "SELECT DISTINCT name FROM medications WHERE type = '약' ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                medicationComboBox.getItems().add(rs.getString("name"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading medication data: " + e.getMessage());
            // 기본값 설정
            medicationComboBox.getItems().addAll("타이레놀", "아스피린", "이부프로펜", "비타민C");
        }
    }
    
    private void loadInjectionData() {
        // medications 테이블에서 주사명 가져오기 (type이 '주사'인 것만)
        String sql = "SELECT DISTINCT name FROM medications WHERE type = '주사' ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                injectionComboBox.getItems().add(rs.getString("name"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading injection data: " + e.getMessage());
            // 기본값 설정
            injectionComboBox.getItems().addAll("인슐린", "항생제", "진통제");
        }
    }
    
    private void updateDiagnosisCode() {
        String selectedDiagnosis = diagnosisComboBox.getValue();
        if (selectedDiagnosis != null) {
            // treatments 테이블에서 진단코드 가져오기
            String sql = "SELECT code FROM treatments WHERE name = ? LIMIT 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, selectedDiagnosis);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    diagnosisCodeField.setText(rs.getString("code"));
                }
                
            } catch (SQLException e) {
                System.err.println("Error getting diagnosis code: " + e.getMessage());
                // 기본값 설정
                switch (selectedDiagnosis) {
                    case "감기": diagnosisCodeField.setText("A01"); break;
                    case "고혈압": diagnosisCodeField.setText("B01"); break;
                    case "당뇨": diagnosisCodeField.setText("C01"); break;
                    case "두통": diagnosisCodeField.setText("D01"); break;
                    case "복통": diagnosisCodeField.setText("E01"); break;
                }
            }
        }
    }
    
    private void loadTodayQueue() {
        List<DailyQueue> todayQueue = dailyQueueDAO.getTodayQueue();
        queueList.clear();
        queueList.addAll(todayQueue);
        
        updateQueueDisplay();
    }
    
    private void updateQueueDisplay() {
        patientQueueVBox.getChildren().clear();
        queueCountText.setText(String.valueOf(queueList.size()));
        
        for (DailyQueue queue : queueList) {
            VBox patientItem = createPatientQueueItem(queue);
            patientQueueVBox.getChildren().add(patientItem);
        }
    }
    
    private VBox createPatientQueueItem(DailyQueue queue) {
        VBox item = new VBox(5);
        item.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white;");
        
        Text nameText = new Text(queue.getPatientName());
        Text timeText = new Text(queue.getRegistrationTime().format(
            DateTimeFormatter.ofPattern("HH:mm")));
        Text statusText = new Text(queue.getStatus());
        
        HBox statusButtons = new HBox(5);
        Button waitingBtn = new Button("진료대기");
        Button inProgressBtn = new Button("진료중");
        Button completedBtn = new Button("진료완료");
        
        waitingBtn.setOnAction(e -> updatePatientStatus(queue, "대기"));
        inProgressBtn.setOnAction(e -> updatePatientStatus(queue, "진료중"));
        completedBtn.setOnAction(e -> updatePatientStatus(queue, "완료"));
        
        statusButtons.getChildren().addAll(waitingBtn, inProgressBtn, completedBtn);
        
        item.getChildren().addAll(nameText, timeText, statusText, statusButtons);
        
        // 환자 클릭 시 정보 로드 및 선택 표시
        item.setOnMouseClicked(e -> {
            // 이전 선택 해제
            for (javafx.scene.Node node : patientQueueVBox.getChildren()) {
                if (node instanceof VBox) {
                    node.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white;");
                }
            }
            
            // 현재 선택 표시
            item.setStyle("-fx-padding: 10; -fx-border-color: #4CAF50; -fx-border-width: 2; -fx-background-color: #E8F5E8;");
            
            loadPatientInfo(queue);
        });
        
        return item;
    }
    
    private void updatePatientStatus(DailyQueue queue, String status) {
        dailyQueueDAO.updateQueueStatus(queue, status); // DailyQueue 객체와 상태 전달

        queue.setStatus(status);

        // "완료" 또는 "취소" 시 queue에서 제거
        if (status.equals("완료") || status.equals("취소")) {
            dailyQueueDAO.removeFromQueue(queue.getId());
            queueList.remove(queue);
        }

        updateQueueDisplay();
    }

    
    private void loadPatientInfo(DailyQueue queue) {
        selectedQueue = queue;
        selectedPatient = patientDAO.getPatientById(queue.getPatientId());
        
        if (selectedPatient != null) {
            patientNameField.setText(selectedPatient.getName());
            patientGenderField.setText(selectedPatient.getGender());
            patientBirthField.setText(selectedPatient.getBirthDate().toString());
            patientAgeField.setText(String.valueOf(selectedPatient.getAge()));
            patientHeightField.setText(String.valueOf(selectedPatient.getHeight()));
            patientWeightField.setText(String.valueOf(selectedPatient.getWeight()));
            patientBloodTypeField.setText(selectedPatient.getBloodType());
            
            completeTreatmentButton.setDisable(false);
            medicalRecords.setPatientId(selectedPatient.getId());
            loadAppointmentButtons(selectedPatient.getId());
            loadTodayNursingNote(selectedPatient.getId());
        }
    }
    
    private void setupEventHandlers() {
        // Enter 버튼 클릭 시 처방 내역에 추가
        enterButton.setOnAction(e -> addPrescriptionToHistory());
        
        // 진료 완료 버튼 클릭 시
        completeTreatmentButton.setOnAction(e -> completeTreatment());
        
        // 진료 기록 추가 버튼
       // addRecordButton.setOnAction(e -> addMedicalRecord());
        
        // 간호사에게 전송 버튼
        sendToNurseButton.setOnAction(e -> sendMessageToNurse());
        
        // 새로고침 버튼 클릭 시
       // refreshQueueButton.setOnAction(e -> refreshQueueData());
    }
    
    private void addPrescriptionToHistory() {
        StringBuilder prescription = new StringBuilder();
        StringBuilder symptoms = new StringBuilder();
        
        // 진단 정보를 환자 증상 및 기타 메모에 추가
        if (diagnosisComboBox.getValue() != null) {
            symptoms.append("진단: ").append(diagnosisComboBox.getValue())
                   .append(" (").append(diagnosisCodeField.getText()).append(")\n");
        }
        
        // 약물 정보 추가
        if (medicationComboBox.getValue() != null) {
            prescription.append("약물: ").append(medicationComboBox.getValue());
            if (!dailyDosageField.getText().isEmpty()) {
                prescription.append(" - 일일 ").append(dailyDosageField.getText()).append("회");
            }
            if (!totalDaysField.getText().isEmpty()) {
                prescription.append(" - 총 ").append(totalDaysField.getText()).append("일");
            }
            prescription.append("\n");
        }
        
        // 주사 정보 추가
        if (injectionComboBox.getValue() != null) {
            prescription.append("주사: ").append(injectionComboBox.getValue()).append("\n");
        }
        
        // 처방 내역에 추가
        medicationArea.appendText(prescription.toString());
        
        // 환자 증상 및 기타 메모에 추가
        symptomsArea.appendText(symptoms.toString());
        
        // 입력 필드 초기화
        diagnosisComboBox.setValue(null);
        diagnosisCodeField.clear();
        medicationComboBox.setValue(null);
        dailyDosageField.clear();
        totalDaysField.clear();
        injectionComboBox.setValue(null);
    }
    
    private void loadTodayNursingNote(int patientId) {
        nurseMemoVBox.getChildren().clear(); // 기존 메모 초기화
        
        try {
            String note = NursingRecordDAO.getTodayNursingNote(patientId);
            
            if (note != null && !note.isEmpty()) {
                TextArea memoArea = new TextArea(note);
                memoArea.setEditable(false);
                memoArea.setWrapText(true);
                memoArea.setPrefHeight(80);
                nurseMemoVBox.getChildren().add(memoArea);
            } else {
                Label noMemoLabel = new Label("오늘 간호사 메모가 없습니다.");
                nurseMemoVBox.getChildren().add(noMemoLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("간호사 메모 불러오기 중 오류 발생");
            nurseMemoVBox.getChildren().add(errorLabel);
        }
    }

    
    private void completeTreatment() {
        if (selectedQueue != null && selectedPatient != null) {
            // 1. 기존 appointment_id를 가져오기 (DailyQueue에서)
            int appointmentId = selectedQueue.getAppointmentId();

            // 2. 기존 medical_record가 있는지 확인
            MedicalRecord existingRecord = medicalRecordDAO.getMedicalRecordByAppointmentId(appointmentId);

            String medicationText = medicationArea.getText();
            String symptomMemoText = symptomsArea.getText();
            String opinionText = opinionArea.getText();

            if (existingRecord != null) {
                // 3. 기존 레코드 업데이트
                existingRecord.setMedicationOrInjection(medicationText);
                existingRecord.setPatientSymptomsMemo(symptomMemoText);
                existingRecord.setMedicalOpinion(opinionText);

                medicalRecordDAO.updateMedicalRecord(existingRecord);
            } else {
                // 4. 새 레코드 생성 (기존 appointment_id 사용)
                if (!medicationText.isEmpty() || !symptomMemoText.isEmpty() || !opinionText.isEmpty()) {
                    medicalRecordDAO.saveMedicalRecord(
                        appointmentId, // 기존 appointmentId 사용
                        null,
                        medicationText,
                        symptomMemoText,
                        opinionText
                    );
                }
            }

            // 5. 상태를 '완료'로 변경
            dailyQueueDAO.updateQueueStatus(selectedQueue, "완료");
            appointmentDAO.updateAppointmentStatus(appointmentId, "완료"); // appointment 테이블도 업데이트

            // 6. 큐에서 제거
            dailyQueueDAO.removeFromQueue(selectedQueue.getId());

            // 7. UI 초기화
            clearPatientInfo();
            loadTodayQueue();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("진료 완료");
            alert.setHeaderText(null);
            alert.setContentText("진료가 완료되었습니다.");
            alert.showAndWait();
        }
    }


    private void loadAppointmentButtons(int patientId) {
        medicalRecordsVBox.getChildren().clear();
        currentAppointments = appointmentDAO.getAppointmentsByPatient(patientId);
        for (Appointment appt : currentAppointments) {
            Button dateBtn = new Button(appt.getAppointmentDate().toString());

            // 클릭 이벤트에 로그 출력 추가
            dateBtn.setOnAction(e -> {
                System.out.println("Clicked date button: " + appt.getAppointmentDate());
                showMedicalRecordsForDate(patientId, appt.getAppointmentDate());
            });

            medicalRecordsVBox.getChildren().add(dateBtn);
        }
    }



    private void showMedicalRecordsForDate(int patientId, LocalDate date) {
        List<MedicalRecord> records = medicalRecordDAO.getMedicalRecords(patientId, date);

        // 세 영역 모두 초기화
        medicationArea.clear();
        symptomsArea.clear();
        opinionArea.clear();

        if (records.isEmpty()) {
            medicationArea.setText("해당 날짜에 진료 기록이 없습니다.");
            symptomsArea.setText("");
            opinionArea.setText("");
        } else {
            for (MedicalRecord rec : records) {
                // 1. 제약 및 주사
                medicationArea.appendText("● " + rec.getMedicationOrInjection() + "\n");

                // 2. 환자 증상 및 기타 메모 (병명 포함)
              
                symptomsArea.appendText("증상 메모: " + rec.getPatientSymptomsMemo() + "\n\n");

                // 3. 소견서
                opinionArea.appendText("● " + rec.getMedicalOpinion() + "\n\n");
            }
        }
    }



    
    
    private void clearPatientInfo() {
        patientNameField.clear();
        patientGenderField.clear();
        patientBirthField.clear();
        patientAgeField.clear();
        patientHeightField.clear();
        patientWeightField.clear();
        patientBloodTypeField.clear();
        
        medicationArea.clear();
        symptomsArea.clear();
        opinionArea.clear();
        
        selectedPatient = null;
        selectedQueue = null;
        completeTreatmentButton.setDisable(true);
    }
    
    private void sendMessageToNurse() {
        System.out.println("sendMessageToNurse() 메서드 호출됨");
        String message = sendToNurseArea.getText();
        System.out.println("입력된 메시지: " + message);
        
        if (!message.isEmpty()) {
            System.out.println("메시지가 비어있지 않음");
            // 현재 로그인한 의사의 ID로 메시지 저장
            if (currentUser != null) {
                System.out.println("현재 사용자 ID: " + currentUser.getId());
                System.out.println("현재 사용자 이름: " + currentUser.getName());
                System.out.println("전송할 메시지: " + message);
                
                boolean success = messageDAO.saveMessage(currentUser.getId(), message);
                
                System.out.println("메시지 저장 결과: " + success);
                
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("메시지 전송");
                    alert.setHeaderText(null);
                    alert.setContentText("간호사에게 메시지가 전송되었습니다.");
                    alert.showAndWait();
                    
                    sendToNurseArea.clear();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("메시지 전송 실패");
                    alert.setHeaderText(null);
                    alert.setContentText("메시지 전송에 실패했습니다. 다시 시도해주세요.");
                    alert.showAndWait();
                }
            } else {
                System.out.println("currentUser가 null입니다!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("오류");
                alert.setHeaderText(null);
                alert.setContentText("사용자 정보를 찾을 수 없습니다.");
                alert.showAndWait();
            }
        } else {
            System.out.println("메시지가 비어있음");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("메시지 전송");
            alert.setHeaderText(null);
            alert.setContentText("전송할 메시지를 입력해주세요.");
            alert.showAndWait();
        }
    }
    
    
    private void refreshQueueData() {
        // 대기열 데이터 새로고침
        loadTodayQueue();
        
        // 새로고침 완료 메시지 표시
//        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
//        alert.setTitle("새로고침 완료");
//        alert.setHeaderText(null);
//        alert.setContentText("환자 대기열이 새로고침되었습니다.");
//        alert.showAndWait();
    }
} 