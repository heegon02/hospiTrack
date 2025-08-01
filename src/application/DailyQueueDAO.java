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

        public QueueInfo(int queueNumber, String name, String visitTime, String status) {
            this.queueNumber = queueNumber;
            this.name = name;
            this.visitTime = visitTime;
            this.status = status;
        }

        public int getQueueNumber() { return queueNumber; }
        public String getName() { return name; }
        public String getVisitTime() { return visitTime; }
        public String getStatus() { return status; }
    }

    // 오늘 접수 순번 불러오기
    public static List<QueueInfo> getTodayQueue() {
        List<QueueInfo> list = new ArrayList<>();
        String sql = "SELECT dq.queue_number, p.name, a.visit_datetime, a.status " +
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

                list.add(new QueueInfo(queueNum, name, visitTime, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
