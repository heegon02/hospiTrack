//환자 기본 정보 출력 클래스.

package nurseFrame;

import javafx.application.Platform;
import application.Patient;
import application.PatientDAO;
import application.AppointmentDAO;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class PatientInfoPanel extends VBox {

    private PatientList patientList;

    public void setPatientList(PatientList patientList) {
        this.patientList = patientList;
    }

    private TextField nameField;
    private TextField weightField;
    private TextField heightField;
    private ComboBox<String> bloodTypeBox;
    private DatePicker birthDatePicker;
    private TextField phoneField;
    private ComboBox<String> genderBox;
    private Button updateButton;
    private Button receptionButton;

    private int currentPatientId = -1;

    public PatientInfoPanel() {
        this.setSpacing(10);
        this.setPadding(new Insets(20));

        // 이 클래스의 영역 스타일 지정
        this.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: lightgray;"
        );

        Label titleLabel = new Label("환자 기본 정보");
        titleLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 900;"
        );

        Label spacingLabel = new Label("          ");
        spacingLabel.setStyle("-fx-font-size: 5px;");

        // 입력 필드들
        nameField = new TextField();
        weightField = new TextField();
        heightField = new TextField();
        bloodTypeBox = new ComboBox<>();
        bloodTypeBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        birthDatePicker = new DatePicker();
        phoneField = new TextField();
        genderBox = new ComboBox<>();
        genderBox.getItems().addAll("M", "F");

        updateButton = new Button("정보 수정");
        updateButton.setPadding(new Insets(10, 30, 10, 30));

        receptionButton = new Button("진료 접수");
        receptionButton.setPadding(new Insets(10, 30, 10, 30));

        HBox underTwoHBox = new HBox(10, updateButton, receptionButton);

        // 라벨 + 입력창을 같은 줄에 배치
        this.getChildren().addAll(
            titleLabel,
            spacingLabel,
            createRow("이름", nameField),
            createRow("몸무게", weightField),
            createRow("키", heightField),
            createRow("혈액형", bloodTypeBox),
            createRow("생년월일", birthDatePicker),
            createRow("전화번호", phoneField),
            createRow("성별", genderBox),
            underTwoHBox
        );

        // 수정 버튼 이벤트
        updateButton.setOnAction(e -> updatePatientInfo());
        // 진료 접수 버튼 이벤트
        receptionButton.setOnAction(e -> updatePatientList());
    }

    private HBox createRow(String labelText, Control input) {
        Label label = new Label(labelText);
        label.setPrefWidth(70);
        label.setMinWidth(70);
        label.setMaxWidth(70);

        HBox row = new HBox(10, label, input);
        row.setFillHeight(true);
        return row;
    }

    // 선택된 환자 정보 DB에서 불러오기
    public void loadPatientInfo(int patientId) {
        try {
            Patient p = PatientDAO.getPatientBasicInfo(patientId);
            if (p != null) {
                currentPatientId = patientId;
                nameField.setText(p.getName());
                weightField.setText(String.valueOf(p.getWeight()));
                heightField.setText(String.valueOf(p.getHeight()));
                bloodTypeBox.setValue(p.getBloodType());
                birthDatePicker.setValue(p.getBirthDate());
                phoneField.setText(p.getPhoneNumber());
                genderBox.setValue(p.getGender());
            } else {
                clearFields();
                nameField.setText("환자 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            clearFields();
            nameField.setText("DB 오류: " + e.getMessage());
        }
    }

    // 환자 정보 업데이트
    private void updatePatientInfo() {
        if (currentPatientId == -1) {
            showAlert(Alert.AlertType.WARNING, "환자 선택 필요", "먼저 환자를 선택하세요.");
            return;
        }

        try {
            Patient p = new Patient();
            p.setPatientId(currentPatientId);
            p.setName(nameField.getText());
            p.setWeight(Double.parseDouble(weightField.getText()));
            p.setHeight(Double.parseDouble(heightField.getText()));
            p.setBloodType(bloodTypeBox.getValue());
            p.setBirthDate(birthDatePicker.getValue());
            p.setPhoneNumber(phoneField.getText());
            p.setGender(genderBox.getValue());

            PatientDAO.updatePatient(p);

            showAlert(Alert.AlertType.INFORMATION, "수정 완료", "환자 정보가 정상적으로 수정되었습니다.");

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "수정 실패", "DB 오류: " + ex.getMessage());
        }
    }
    
    private void updatePatientList() {
        if (currentPatientId == -1) {
            showAlert(Alert.AlertType.WARNING, "환자 선택 필요", "먼저 환자를 선택하세요.");
            return;
        }

        try {
            // 1. DB에 접수 등록
            AppointmentDAO.registerAppointment(currentPatientId);

         // UI 갱신 (JavaFX Application Thread 보장)
            if (patientList != null) {
                Platform.runLater(() -> {
                    patientList.reloadQueue();
              });
             } 
            showAlert(Alert.AlertType.INFORMATION, "접수 완료", "환자가 접수되었습니다."); }
            
            catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "접수 실패", "DB 오류: " + ex.getMessage());
        }
    }

    
    

    private void clearFields() {
        nameField.clear();
        weightField.clear();
        heightField.clear();
        bloodTypeBox.setValue(null);
        birthDatePicker.setValue(null);
        phoneField.clear();
        genderBox.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String header, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("알림");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
