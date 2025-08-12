package doctorFrame;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    
    // 메시지 저장
    public boolean saveMessage(int senderId, String content) {
        String sql = "INSERT INTO messages (sender_id, content, send_time, is_read) VALUES (?, ?, ?, ?)";
        
        System.out.println("MessageDAO.saveMessage 호출됨");
        System.out.println("senderId: " + senderId);
        System.out.println("content: " + content);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, senderId);
            pstmt.setString(2, content);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setBoolean(4, false);  // 기본적으로 읽지 않은 상태
            
            int result = pstmt.executeUpdate();
            System.out.println("SQL 실행 결과: " + result + "행이 영향받음");
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // 읽지 않은 메시지 조회
    public List<Message> getUnreadMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.name as sender_name FROM messages m " +
                    "JOIN users u ON m.sender_id = u.user_id " +
                    "WHERE m.is_read = false " +
                    "ORDER BY m.send_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("sender_id"),
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
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, messageId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error marking message as read: " + e.getMessage());
            return false;
        }
    }
    
    // 모든 메시지 조회 (최신순)
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages ORDER BY send_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("sender_id"),
                    rs.getString("content"),
                    rs.getTimestamp("send_time").toLocalDateTime(),
                    rs.getBoolean("is_read")
                );
                messages.add(message);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all messages: " + e.getMessage());
        }
        
        return messages;
    }
} 