package nurseFrame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.DBUtil;

public class PrescriptionDetail extends VBox {
    
    private Integer patientId;
    private Integer appointmentId;
    
    private TextArea medicationField;
    private TextArea symptomsField;
    private TextArea opinionField;
    
    public PrescriptionDetail() {
        
        // 타이틀
        Label title = new Label("의사 처방 내역");
        title.setStyle(
            "-fx-font-family : 'Arial';" +
            "-fx-font-weight: bold;" + 
            "-fx-font-size: 16px;"
        );
        
        // 제약 및 주사
        Label medicationLabel = new Label("제약 및 주사");
        medicationLabel.setStyle(
            "-fx-font-family: 'Arial';" + 
            "-fx-font-weight: bold;" + 
            "-fx-font-size: 12px;"
        );
        medicationField = new TextArea();
        medicationField.setPrefSize(280, 200);
        medicationField.setEditable(false);
        
        // 환자 증상 및 기타 메모
        Label symptomsLabel = new Label("환자 증상 및 기타 메모");
        symptomsLabel.setStyle(
            "-fx-font-family : 'Arial';" + 
            "-fx-font-weight: bold;" + 
            "-fx-font-size: 12px;"
        );
        symptomsField = new TextArea();
        symptomsField.setPrefSize(280, 200);
        symptomsField.setEditable(false);
        
        // 소견서
        Label opinionLabel = new Label("소견서");
        opinionLabel.setStyle(
            "-fx-font-family : 'Arial';" + 
            "-fx-font-weight: bold;" + 
            "-fx-font-size: 12px;"
        );
        opinionField = new TextArea();
        opinionField.setPrefSize(280, 200);
        opinionField.setEditable(false);
        
        
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
        
        // 실제로 컴포넌트 추가
        this.getChildren().addAll(
            title,
            medicationLabel, medicationField,
            symptomsLabel, symptomsField,
            opinionLabel, opinionField
        );
        
        this.setPrefSize(300, 600);
        this.setMinSize(300, 600);
        this.setMaxSize(300, 600);
    }
    
    
    // 환자 ID를 설정하고 최신 처방 + 진료 기록 불러오기 (기존 기능)
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
        loadPrescriptionDetails();
    }
    
    // appointmentId 기준으로 처방 내역과 진료 기록 모두 불러오기 (복수 처방 포함)
    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
        loadPrescriptionDetails();
        if (appointmentId == null) {
            medicationField.setText("");
            symptomsField.setText("");
            opinionField.setText("");
        } else {
            loadPrescriptionDetailByAppointment();
        }
    }
    
    
    // patientId 기준 최신 1건 조회 (기존)
    private void loadPrescriptionDetails() {
        if (patientId == null) return;

        String sql =
        	    "SELECT patient_symptoms_memo, medical_opinion, medication_or_injection " +
        	    "FROM medical_records " +
        	    "WHERE appointment_id = ? " +
        	    "ORDER BY created_at DESC LIMIT 1";


        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                medicationField.setText(rs.getString("medication_or_injection")); // 치료 내용이 사실상 처방
                symptomsField.setText(rs.getString("patient_symptoms_memo"));
                opinionField.setText(rs.getString("medical_opinion"));
            } else {
                medicationField.setText("");
                symptomsField.setText("");
                opinionField.setText("");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    // appointmentId 기준 처방 내역 + 진료 기록 모두 조회
    private void loadPrescriptionDetailByAppointment() {
        if (appointmentId == null) return;

        String sql =
            "SELECT patient_symptoms_memo, medical_opinion, medication_or_injection " +
            "FROM medical_records " +
            "WHERE appointment_id = ? " +
            "ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                medicationField.setText(rs.getString("medication_or_injection"));  // 수정된 부분
                symptomsField.setText(rs.getString("patient_symptoms_memo"));
                opinionField.setText(rs.getString("medical_opinion"));
            } else {
                medicationField.setText("진료 기록 없음");
                symptomsField.setText("");
                opinionField.setText("");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
