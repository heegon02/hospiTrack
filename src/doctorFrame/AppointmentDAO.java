package doctorFrame;

import doctorFrame.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
	
	/**
	 * appointment 상태 업데이트
	 */
	public boolean updateAppointmentStatus(int appointmentId, String status) {
	    String sql = "UPDATE appointments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE appointment_id = ?";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setString(1, status);
	        pstmt.setInt(2, appointmentId);
	        
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	        
	    } catch (SQLException e) {
	        System.err.println("Error updating appointment status: " + e.getMessage());
	        return false;
	    }
	}

    // 예약 저장 (기본 status = '대기', created_at 자동 처리)
	public int saveAppointment(int patientId, LocalDate date) {
	    String sql = "INSERT INTO appointments (patient_id, visit_datetime, status, created_at) VALUES (?, ?, '대기', NOW())";

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        pstmt.setInt(1, patientId);
	        pstmt.setDate(2, Date.valueOf(date));

	        int affectedRows = pstmt.executeUpdate();

	        if (affectedRows == 0) {
	            throw new SQLException("Appointment 생성 실패, 영향 받은 행이 없습니다.");
	        }

	        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getInt(1); // 생성된 appointment_id 반환
	            } else {
	                throw new SQLException("Appointment 생성 실패, ID를 가져올 수 없습니다.");
	            }
	        }

	    } catch (SQLException e) {
	        System.err.println("Error saving appointment: " + e.getMessage());
	        return -1;  // 실패 시 -1 반환
	    }
	}


    // 환자별 예약 조회
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY visit_datetime DESC";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getInt("patient_id"),
                    rs.getDate("visit_datetime").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments: " + e.getMessage());
        }
        return list;
    }

    // 오늘 대기 상태인 예약 조회 (3초마다 UI 갱신용)
    public List<Appointment> getTodayAppointments() {
        String sql = "SELECT * FROM appointments WHERE status = ? AND DATE(visit_datetime) = CURDATE() ORDER BY created_at ASC";
        List<Appointment> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "대기");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getInt("patient_id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's waiting appointments: " + e.getMessage());
        }

        return list;
    }
}
