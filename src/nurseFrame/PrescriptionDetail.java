package nurseFrame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.DBUtil;

public class PrescriptionDetail extends VBox {
	
    private Integer patientId;

    private TextField medicationField;
    private TextField symptomsField;
    private TextField opinionField;
	
	public PrescriptionDetail() {
		
		//타이틀
		Label title = new Label("의사 처방 내역");
		title.setStyle(
			"-fx-font-family : 'Arial';" +
		    "-fx-font-weight: bold;" + 
			"-fx-font-size: 16px;"
		);
		
		//제약 및 주사
		Label medicationLabel = new Label("제약 및 주사");
		medicationLabel.setStyle(
			"-fx-font-family: 'Arial';" + 
			"-fx-font-weight: bold;" + 
			"-fx-font-size: 12px;"
		);
		medicationField = new TextField();
		medicationField.setPrefSize(280, 200);
		
		//환자 증상 및 기타 메모
		Label symptomsLabel = new Label("환자 증상 및 기타 메모");
		symptomsLabel.setStyle(
			"-fx-font-family : 'Arial';" + 
			"-fx-font-weight: bold;" + 
			"-fx-font-size: 12px;"
		);
		symptomsField = new TextField();
		symptomsField.setPrefSize(280, 200);
		
		//소견서
		Label opinionLabel = new Label("소견서");
		opinionLabel.setStyle(
				"-fx-font-family : 'Arial';" + 
				"-fx-font-weight: bold;" + 
				"-fx-font-size: 12px;"
		);
		opinionField = new TextField();
		opinionField.setPrefSize(280, 200);
		
		
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
        
        // 👉 실제로 컴포넌트 추가
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
	
	
	// 👉 환자 ID를 설정하고, DB에서 처방 내역 불러오기
	public void setPatientId(Integer patientId) {
	    this.patientId = patientId;
	    loadPrescriptionDetails();
	}
	
	private void loadPrescriptionDetails() {
	    if (patientId == null) return;

	    String sql = "SELECT m.name AS medication, mr.patient_symptoms_memo, mr.medical_opinion " +
	                 "FROM medical_records mr " +
	                 "LEFT JOIN prescriptions p ON p.patient_id = mr.record_id " +
	                 "LEFT JOIN medications m ON p.medication_id = m.medication_id " +
	                 "WHERE mr.patient_id = ? " +
	                 "ORDER BY mr.created_at DESC LIMIT 1"; // 최근 기록 1개만 불러오기 (예시)

	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, patientId);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            medicationField.setText(rs.getString("medication"));
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
}
