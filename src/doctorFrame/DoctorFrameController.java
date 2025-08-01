package doctorFrame;

import doctorFrame.DailyQueueDAO;
import doctorFrame.PatientDAO;
import doctorFrame.UserDAO;
import doctorFrame.MedicationRecordDAO;
import doctorFrame.AppointmentDAO;
import doctorFrame.DatabaseConnection;
import doctorFrame.DailyQueue;
import doctorFrame.Patient;
import doctorFrame.User;
import doctorFrame.Appointment;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

public class DoctorFrameController implements Initializable {
    
    @FXML private Text programNameText;
    @FXML private Text userInfoText;
    
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
    @FXML private Button addRecordButton;
    
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
    @FXML private TextArea nurseMemoArea;
    @FXML private TextArea sendToNurseArea;
    @FXML private Button sendToNurseButton;
    
    private DailyQueueDAO dailyQueueDAO;
    private PatientDAO patientDAO;
    private UserDAO userDAO;
    private MedicationRecordDAO medicationRecordDAO;
    private AppointmentDAO appointmentDAO;
    private User currentUser;
    private Patient selectedPatient;
    private DailyQueue selectedQueue;
    
    private ObservableList<DailyQueue> queueList = FXCollections.observableArrayList();
    private List<Appointment> currentAppointments = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeDAOs();
        loadCurrentUser();
        setupUI();
        loadTodayQueue();
        setupEventHandlers();
    }
    
    private void initializeDAOs() {
        dailyQueueDAO = new DailyQueueDAO();
        patientDAO = new PatientDAO();
        userDAO = new UserDAO();
        medicationRecordDAO = new MedicationRecordDAO();
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
        dailyQueueDAO.updateQueueStatus(queue.getId(), status);
        queue.setStatus(status);
        
        if (status.equals("완료")) {
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
            // 진료기록(appointments) 버튼 목록 표시
            loadAppointmentButtons(selectedPatient.getId());
        }
    }
    
    private void setupEventHandlers() {
        // Enter 버튼 클릭 시 처방 내역에 추가
        enterButton.setOnAction(e -> addPrescriptionToHistory());
        
        // 진료 완료 버튼 클릭 시
        completeTreatmentButton.setOnAction(e -> completeTreatment());
        
        // 진료 기록 추가 버튼
        addRecordButton.setOnAction(e -> addMedicalRecord());
        
        // 간호사에게 전송 버튼
        sendToNurseButton.setOnAction(e -> sendMessageToNurse());
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
    
    private void completeTreatment() {
        if (selectedQueue != null && selectedPatient != null) {
            // 오늘 날짜 appointments 저장
            appointmentDAO.saveAppointment(selectedPatient.getId(), LocalDate.now());
            // 처방 내역을 medication_records에 저장
            String medicationText = medicationArea.getText();
            if (!medicationText.isEmpty()) {
                // 간단한 처방 정보를 저장 (실제로는 더 복잡한 처방 시스템이 필요)
                medicationRecordDAO.saveMedicationRecord(
                    selectedPatient.getId(), 
                    1, // 임시 nurse_id
                    "처방약", 
                    medicationText, 
                    "의사 처방"
                );
            }
            
            // 진료 완료 처리
            dailyQueueDAO.updateQueueStatus(selectedQueue.getId(), "완료");
            dailyQueueDAO.removeFromQueue(selectedQueue.getId());
            
            // 환자 정보 초기화
            clearPatientInfo();
            
            // 대기열 다시 로드
            loadTodayQueue();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("진료 완료");
            alert.setHeaderText(null);
            alert.setContentText("진료가 완료되었습니다.");
            alert.showAndWait();
        }
    }
    
    private void addMedicalRecord() {
        Button todayButton = new Button(LocalDate.now().toString());
        todayButton.setOnAction(e -> {
            // 오늘 날짜 진료 기록 추가 로직
            System.out.println("오늘 날짜 진료 기록 추가");
        });
        medicalRecordsVBox.getChildren().add(todayButton);
    }

    private void loadAppointmentButtons(int patientId) {
        medicalRecordsVBox.getChildren().clear();
        currentAppointments = appointmentDAO.getAppointmentsByPatient(patientId);
        for (Appointment appt : currentAppointments) {
            Button dateBtn = new Button(appt.getAppointmentDate().toString());
            dateBtn.setOnAction(e -> showMedicationRecordsForDate(patientId, appt.getAppointmentDate()));
            medicalRecordsVBox.getChildren().add(dateBtn);
        }
    }

    private void showMedicationRecordsForDate(int patientId, LocalDate date) {
        // medication_records에서 해당 날짜의 기록을 조회해서 출력
        List<doctorFrame.MedicationRecord> records = medicationRecordDAO.getMedicationRecordsByPatient(patientId);
        medicationArea.clear();
        for (doctorFrame.MedicationRecord rec : records) {
            if (rec.getMedicationTime().toLocalDate().equals(date)) {
                medicationArea.appendText(rec.getMedicationName() + ": " + rec.getDosage() + "\n" + rec.getInstructions() + "\n\n");
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
        String message = sendToNurseArea.getText();
        if (!message.isEmpty()) {
            // 간호사에게 메시지 전송 로직 (실제로는 데이터베이스에 저장)
            System.out.println("간호사에게 메시지 전송: " + message);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("메시지 전송");
            alert.setHeaderText(null);
            alert.setContentText("간호사에게 메시지가 전송되었습니다.");
            alert.showAndWait();
            
            sendToNurseArea.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("메시지 전송");
            alert.setHeaderText(null);
            alert.setContentText("전송할 메시지를 입력해주세요.");
            alert.showAndWait();
        }
    }
} 