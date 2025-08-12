package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DailyQueueDAO {
    
    // 환자 접수 정보 DTO
    public static class QueueInfo {
        private int queueNumber;
        private String name;
        private String visitTime;
        private String status;
        private int appointmentId;
        private int patientId; // ★ 추가

        // 전체 필드용 생성자
        public QueueInfo(int queueNumber, String name, String visitTime, String status, int appointmentId, int patientId) {
            this.queueNumber = queueNumber;
            this.name = name;
            this.visitTime = visitTime;
            this.status = status;
            this.appointmentId = appointmentId;
            this.patientId = patientId;
        }

        public int getQueueNumber() { return queueNumber; }
        public String getName() { return name; }
        public String getVisitTime() { return visitTime; }
        public String getStatus() { return status; }
        public int getAppointmentId() {return appointmentId; } 
        public int getPatientId() { return patientId; } // ★ 추가
        public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    }

    // 오늘 접수 순서 받아오기
    public static List<QueueInfo> getTodayQueue() {
        List<QueueInfo> list = new ArrayList<>();
        String sql = "SELECT dq.queue_number, p.name, a.visit_datetime, a.status, a.appointment_id, p.patient_id " +
                     "FROM daily_queue dq " +
                     "JOIN patients p ON dq.patient_id = p.patient_id " +
                     "JOIN appointments a ON dq.appointment_id = a.appointment_id " +
                     "WHERE dq.queue_date = CURDATE() " +
                     "ORDER BY dq.queue_number ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int queueNum = rs.getInt("queue_number");
                String name = rs.getString("name");
                String visitTime = rs.getString("visit_datetime");
                String status = rs.getString("status");
                int appointmentId = rs.getInt("appointment_id");
                int patientId = rs.getInt("patient_id"); // ★ 추가

                QueueInfo q = new QueueInfo(queueNum, name, visitTime, status, appointmentId, patientId);

                list.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
