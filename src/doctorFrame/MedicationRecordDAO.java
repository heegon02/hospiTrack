package doctorFrame;

import doctorFrame.DatabaseConnection;
import doctorFrame.MedicationRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicationRecordDAO {
    
    public void saveMedicationRecord(int patientId, int nurseId, String medicationName, String dosage, String instructions) {
        String sql = "INSERT INTO medication_records (patient_id, nurse_id, medication_name, dosage, instructions, medication_time) VALUES (?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, nurseId);
            pstmt.setString(3, medicationName);
            pstmt.setString(4, dosage);
            pstmt.setString(5, instructions);
            pstmt.executeUpdate();
            
            System.out.println("Medication record saved successfully");
            
        } catch (SQLException e) {
            System.err.println("Error saving medication record: " + e.getMessage());
        }
    }
    
    public List<MedicationRecord> getMedicationRecordsByPatient(int patientId) {
        String sql = "SELECT * FROM medication_records WHERE patient_id = ? ORDER BY medication_time DESC";
        List<MedicationRecord> records = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MedicationRecord record = new MedicationRecord(
                    rs.getInt("medication_id"),
                    rs.getInt("prescription_id"),
                    rs.getInt("patient_id"),
                    rs.getInt("nurse_id"),
                    rs.getTimestamp("medication_time").toLocalDateTime(),
                    rs.getString("medication_name"),
                    rs.getString("dosage"),
                    rs.getString("instructions")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error getting medication records: " + e.getMessage());
        }
        return records;
    }
} 