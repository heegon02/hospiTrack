package doctorFrame;

import doctorFrame.DatabaseConnection;
import doctorFrame.DailyQueue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class DailyQueueDAO {

	public List<DailyQueue> getTodayQueue() {
		String sql = "SELECT dq.*, p.name AS patient_name, a.status, a.appointment_id " +
	             "FROM daily_queue dq " +
	             "JOIN patients p ON dq.patient_id = p.patient_id " +
	             "JOIN appointments a ON dq.appointment_id = a.appointment_id " +
	             "WHERE dq.queue_date = CURDATE() " +
	             "ORDER BY dq.queue_number ASC";

	    List<DailyQueue> queue = new ArrayList<>();

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            DailyQueue dailyQueue = new DailyQueue(
	                rs.getInt("queue_id"),
	                rs.getInt("patient_id"),
	                rs.getString("patient_name"),
	                rs.getTimestamp("created_at").toLocalDateTime(),
	                rs.getString("status"),  // appointments.status 가져오기
	                rs.getInt("appointment_id")
	            );
	            queue.add(dailyQueue);
	        }
	    } catch (SQLException e) {
	        System.err.println("Error getting today's queue: " + e.getMessage());
	    }
	    return queue;
	}

    
    public void updateQueueStatus(DailyQueue queue, String status) {
        queue.setStatus(status);

        String updateQueueSQL = "UPDATE daily_queue SET `status` = ? WHERE queue_id = ?";
        String updateAppointmentSQL = "UPDATE appointments SET `status` = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. daily_queue 상태 업데이트
            try (PreparedStatement pstmt1 = conn.prepareStatement(updateQueueSQL)) {
                pstmt1.setString(1, status);
                pstmt1.setInt(2, queue.getId());
                pstmt1.executeUpdate();
            }

            // 2. appointmentId 조회 (같은 conn 사용)
            int appointmentId = getAppointmentId(conn, queue.getPatientId(), queue.getRegistrationTime());
            System.out.println("appointmentId: " + appointmentId);

            if (appointmentId <= 0) {
                System.err.println("⚠ 유효하지 않은 appointmentId: " + appointmentId);
                conn.rollback();
                return;
            }

            // 3. appointments 상태 업데이트
            try (PreparedStatement pstmt2 = conn.prepareStatement(updateAppointmentSQL)) {
                pstmt2.setString(1, status);
                pstmt2.setInt(2, appointmentId);
                pstmt2.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ 환자 상태가 두 테이블에 모두 업데이트됨: " + status);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ 상태 업데이트 실패");
        }
    }


    
 // 기존 커넥션을 받아서 appointment_id 조회
    public int getAppointmentId(Connection conn, int patientId, LocalDateTime createdAt) {
        String sql = "SELECT appointment_id FROM appointments WHERE patient_id = ? AND created_at = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(createdAt));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("appointment_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching appointment_id: " + e.getMessage());
        }
        return -1; // 못 찾으면 -1 반환
    }

    public void removeFromQueue(int queueId) {
        String sql = "DELETE FROM daily_queue WHERE queue_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, queueId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error removing from queue: " + e.getMessage());
        }
    }
} 