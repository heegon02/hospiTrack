package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {

    // 특정 환자의 진료 기록 날짜 리스트 조회
    public static List<Date> getMedicalRecordDatesByPatientId(int patientId) throws SQLException {
        List<Date> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT DATE(created_at) AS record_date FROM medical_records "
                   + "JOIN appointments a ON medical_records.appointment_id = a.appointment_id "
                   + "WHERE a.patient_id = ? ORDER BY record_date DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                dates.add(rs.getDate("record_date"));
            }
        }
        return dates;
    }
}
