package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NursingRecordDAO {

    // 간호 기록 저장
    public static void addNursingRecord(Integer patientId, String note) throws SQLException {
        String sql = "INSERT INTO nursing_records (patient_id, nursing_note) VALUES (?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            pstmt.setString(2, note);
            pstmt.executeUpdate();
        }
    }
}
