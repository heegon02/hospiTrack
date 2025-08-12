package nurseFrame;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    
    // 읽지 않은 메시지 조회 (의사 이름 포함)
    public List<Message> getUnreadMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.name as sender_name FROM messages m " +
                    "JOIN users u ON m.sender_id = u.user_id " +
                    "WHERE m.is_read = false " +
                    "ORDER BY m.send_time DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("sender_id"),
                    rs.getString("sender_name"),
                    rs.getString("content"),
                    rs.getTimestamp("send_time").toLocalDateTime(),
                    rs.getBoolean("is_read")
                );
                messages.add(message);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unread messages: " + e.getMessage());
        }
        
        return messages;
    }
    
    // 메시지를 읽음 상태로 변경
    public boolean markMessageAsRead(int messageId) {
        String sql = "UPDATE messages SET is_read = true WHERE message_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, messageId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error marking message as read: " + e.getMessage());
            return false;
        }
    }
    
    // 데이터베이스 연결
    private Connection getConnection() throws SQLException {
        String URL = "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String USER = "root";
        String PASSWORD = "1234";
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
} 