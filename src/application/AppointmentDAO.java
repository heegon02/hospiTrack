package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import nurseFrame.InquiryDate;

public class AppointmentDAO {
	
	// 진료 상태 업데이트
	public static boolean updateStatus(int appointmentId, String newStatus) {
	    String sql = "UPDATE appointments SET status=? WHERE appointment_id=?";
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, newStatus);
	        pstmt.setInt(2, appointmentId);
	        int result = pstmt.executeUpdate();
	        return result > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	// 환자 접수 등록
	public static boolean registerAppointment(int patientId) {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = DBUtil.getConnection();

	        // [추가] 오늘 이미 접수했는지 먼저 확인
	        String checkSql = "SELECT COUNT(*) FROM daily_queue WHERE queue_date = CURDATE() AND patient_id = ?";
	        pstmt = conn.prepareStatement(checkSql);
	        pstmt.setInt(1, patientId);
	        rs = pstmt.executeQuery();
	        if (rs.next() && rs.getInt(1) > 0) {
	            return false; // 이미 접수된 상태 → 새로 등록하지 않음
	        }
	        rs.close();
	        pstmt.close();

	        conn.setAutoCommit(false); // 트랜잭션 시작

	        // 1. appointments에 새 진료 일정 추가
	        String insertAppSql = "INSERT INTO appointments (patient_id, visit_datetime, status) VALUES (?, NOW(), '대기')";
	        pstmt = conn.prepareStatement(insertAppSql, Statement.RETURN_GENERATED_KEYS);
	        pstmt.setInt(1, patientId);
	        pstmt.executeUpdate();

	        rs = pstmt.getGeneratedKeys();
	        int appointmentId = 0;
	        if (rs.next()) {
	            appointmentId = rs.getInt(1);
	        }
	        rs.close();
	        pstmt.close();

	        // 2. daily_queue에서 오늘 날짜의 마지막 queue_number 가져오기
	        String maxQueueSql = "SELECT COALESCE(MAX(queue_number), 0) FROM daily_queue WHERE queue_date = CURDATE()";
	        pstmt = conn.prepareStatement(maxQueueSql);
	        rs = pstmt.executeQuery();
	        int nextQueueNum = 1;
	        if (rs.next()) {
	            nextQueueNum = rs.getInt(1) + 1;
	        }
	        rs.close();
	        pstmt.close();

	        // 3. daily_queue에 추가
	        String insertQueueSql = "INSERT INTO daily_queue (queue_date, patient_id, appointment_id, queue_number) VALUES (CURDATE(), ?, ?, ?)";
	        pstmt = conn.prepareStatement(insertQueueSql);
	        pstmt.setInt(1, patientId);
	        pstmt.setInt(2, appointmentId);
	        pstmt.setInt(3, nextQueueNum);
	        pstmt.executeUpdate();

	        conn.commit(); // 트랜잭션 성공
	        return true;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
	        return false;
	    } finally {
	        if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
	        if (pstmt != null) try { pstmt.close(); } catch (SQLException ignore) {}
	        if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignore) {}
	    }
	}


    // 환자 접수 취소
    public static boolean cancelAppointment(int appointmentId) {
        String sql = "UPDATE appointments SET status='취소' WHERE appointment_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 환자 진료실 입장
    public static boolean enterAppointment(int appointmentId) {
        String sql = "UPDATE appointments SET status = '입장' WHERE appointment_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            int rows = pstmt.executeUpdate();
            
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //환자 진료실 입장, 대기 전환용 메소드
    public static boolean updateStatus2(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, appointmentId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 이름 + 진료시간 가져오기
    public static List<String> getPatientNamesAndTimesByDate(String date) {
        List<String> result = new ArrayList<>();

        String sql = "SELECT p.name, DATE_FORMAT(a.visit_datetime, '%H:%i') AS visit_time " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE DATE(a.visit_datetime) = ? " +
                     "ORDER BY a.visit_datetime";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String time = rs.getString("visit_time");
                result.add(name + " - " + time); // "홍길동 - 10:30"
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    
    // 이름 + 진료시간 가져오기2 (InquiryDate 클래스에서 사용)
    public static List<InquiryDate.AppointmentRow> getPatientNamesAndTimesByDate2(String date) {
        List<InquiryDate.AppointmentRow> appointments = new ArrayList<>();
        String sql = "SELECT a.appointment_id, p.patient_id, p.name AS patient_name, TIME(a.visit_datetime) AS visit_time " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE DATE(a.visit_datetime) = ? " +
                     "ORDER BY a.visit_datetime";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int appointmentId = rs.getInt("appointment_id");
                    int patientId = rs.getInt("patient_id");
                    String name = rs.getString("patient_name");
                    String time = rs.getString("visit_time");
                    appointments.add(new InquiryDate.AppointmentRow(appointmentId, patientId, name, time));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }


    
    
}
