package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//NursingRecordDAO.java
public class NursingRecordDAO {

 // 오늘 날짜의 환자 메모 가져오기
 public static String getTodayNursingNote(int patientId) throws SQLException {
     String sql = "SELECT nursing_note FROM nursing_records " +
                  "WHERE patient_id = ? AND DATE(created_at) = CURDATE() " +
                  "LIMIT 1";
     try (Connection conn = DBUtil.getConnection();
          PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setInt(1, patientId);
         ResultSet rs = pstmt.executeQuery();
         if (rs.next()) {
             return rs.getString("nursing_note");
         }
     }
     return null;
 }

 // 오늘 날짜의 메모가 있으면 UPDATE, 없으면 INSERT
 public static void upsertNursingRecord(int patientId, String note) throws SQLException {
     String checkSql = "SELECT nursing_id FROM nursing_records " +
                       "WHERE patient_id = ? AND DATE(created_at) = CURDATE() LIMIT 1";
     try (Connection conn = DBUtil.getConnection();
          PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
         checkStmt.setInt(1, patientId);
         ResultSet rs = checkStmt.executeQuery();
         if (rs.next()) {
             int nursingId = rs.getInt("nursing_id");
             String updateSql = "UPDATE nursing_records SET nursing_note = ?, updated_at = NOW() WHERE nursing_id = ?";
             try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                 updateStmt.setString(1, note);
                 updateStmt.setInt(2, nursingId);
                 updateStmt.executeUpdate();
             }
         } else {
             String insertSql = "INSERT INTO nursing_records (patient_id, nursing_note) VALUES (?, ?)";
             try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                 insertStmt.setInt(1, patientId);
                 insertStmt.setString(2, note);
                 insertStmt.executeUpdate();
             }
         }
     }
 }
}
