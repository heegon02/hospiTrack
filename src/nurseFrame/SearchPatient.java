//환자 검색 기능
package nurseFrame;

import application.Patient;
import application.PatientDAO;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;

public class SearchPatient extends VBox {

	//필드
    private TextField nameField;
    private Button searchBtn;
    private TableView<Patient> tableView;
    
    private Memo memo;
    private MedicalRecords medicalRecords;
    private PrescriptionDetail prescriptionDetail;
    
    // 선택된 환자의 ID 저장용
    private Integer selectedPatientId;

    public interface PatientSelectedListener {
        void onPatientSelected(int patientId);
    }
    private PatientSelectedListener listener;
    public void setOnPatientSelected(PatientSelectedListener listener) {
        this.listener = listener;
    }

    public SearchPatient() {
        this.setSpacing(10);
        this.setPadding(new Insets(20));

        Label titleLabel = new Label("환자 검색");
        titleLabel.setStyle(
            "-fx-font-size: 14px;" + 
        	"-fx-font-weight: bold;"
        );
        
        nameField = new TextField();
        nameField.setPromptText("검색할 이름 입력");

        searchBtn = new Button("검색");
        
        HBox searchBox = new HBox(10, nameField, searchBtn);
        searchBox.setSpacing(10);

        // 👉 검색 결과 표시용 TableView
        tableView = new TableView<>();
        tableView.setMinHeight(200);
        tableView.setPrefHeight(300);
        tableView.setMaxHeight(300);

        TableColumn<Patient, String> nameCol = new TableColumn<>("이름");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        
        TableColumn<Patient, String> SCol = new TableColumn<>("성별(M/F)");
        SCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGender()));
        
        TableColumn<Patient, String> birthCol = new TableColumn<>("생년월일");
        birthCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getBirthDate() != null ? data.getValue().getBirthDate().toString() : ""
        ));

        tableView.getColumns().addAll(nameCol, SCol, birthCol);

        // 검색 버튼 이벤트
        searchBtn.setOnAction(e -> searchPatients());
        nameField.setOnAction(e -> searchPatients());

        // 환자 선택 이벤트
        tableView.setRowFactory(tv -> {
            TableRow<Patient> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Patient selected = row.getItem();
                    selectedPatientId = selected.getPatientId();

                    if (listener != null) {
                        listener.onPatientSelected(selectedPatientId);
                    }
                    
                    if (memo != null) {
                    	memo.setPatientId(selectedPatientId);
                    }
                    

                    if (prescriptionDetail != null) {
                        prescriptionDetail.setPatientId(selectedPatientId);
                    }
                }
            });
            return row;
        });

      //이 클래스의 영역 스타일 지정.
    	this.setStyle(
    		"-fx-background-color: #ffffff;"+ //배경색 지정
    		"-fx-background-radius: 15;" + //배경 둥글게
    		"-fx-border-radius: 15;" + //테두리도 같이 둥글게
    		"-fx-border-color: lightgray;" //테두리 색
    	); 
        
        this.getChildren().addAll(titleLabel, searchBox, tableView);
    }
    
    // Memo 연결
    public void setMemo(Memo memo) {
        this.memo = memo;
    }

    // MedicalRecords 연결
    public void setMedicalRecords(MedicalRecords medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    // PrescriptionDetail 연결
    public void setPrescriptionDetail(PrescriptionDetail prescriptionDetail) {
        this.prescriptionDetail = prescriptionDetail;
    }
    
    

    // 검색 메소드
    private void searchPatients() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            return;
        }

        try {
            List<Patient> patients = PatientDAO.searchPatientsByName(name);
            tableView.getItems().setAll(patients); // 검색 결과 채우기
        } catch (SQLException ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("DB 오류");
            error.setHeaderText("검색 실패");
            error.setContentText(ex.getMessage());
            error.showAndWait();
        }
    }

    public Integer getSelectedPatientId() {
        return selectedPatientId;
    }
}
