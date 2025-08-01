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
        String sql = "SELECT dq.*, p.name as patient_name FROM daily_queue dq " +
                     "JOIN patients p ON dq.patient_id = p.patient_id " +
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
                    "대기" // 기본 상태
                );
                queue.add(dailyQueue);
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's queue: " + e.getMessage());
        }
        return queue;
    }
    
    public void updateQueueStatus(int queueId, String status) {
        String sql = "UPDATE daily_queue SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, queueId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating queue status: " + e.getMessage());
        }
    }
    
    public void removeFromQueue(int queueId) {
        String sql = "DELETE FROM daily_queue WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, queueId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error removing from queue: " + e.getMessage());
        }
    }
} 