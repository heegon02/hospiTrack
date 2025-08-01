package application;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class AppointmentDAO {

    // 진료 접수 처리 (appointments + daily_queue 삽입)
    public static void registerAppointment(int patientId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. appointments 테이블에 새 접수 삽입
            String insertAppointment = "INSERT INTO appointments (patient_id, visit_datetime, status) VALUES (?, NOW(), '대기')";
            pstmt = conn.prepareStatement(insertAppointment, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, patientId);
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            int appointmentId = -1;
            if (rs.next()) {
                appointmentId = rs.getInt(1);
            }

            // 2. 오늘 날짜의 마지막 접수 순번 조회
            String getMaxQueue = "SELECT IFNULL(MAX(queue_number), 0) FROM daily_queue WHERE queue_date = CURDATE()";
            pstmt = conn.prepareStatement(getMaxQueue);
            rs = pstmt.executeQuery();
            int nextQueueNum = 1;
            if (rs.next()) {
                nextQueueNum = rs.getInt(1) + 1;
            }

            // 3. daily_queue 테이블에 삽입
            String insertQueue = "INSERT INTO daily_queue (queue_date, patient_id, appointment_id, queue_number) VALUES (CURDATE(), ?, ?, ?)";
            pstmt = conn.prepareStatement(insertQueue);
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, appointmentId);
            pstmt.setInt(3, nextQueueNum);
            pstmt.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.setAutoCommit(true);
            if (conn != null) conn.close();
        }
    }
}
