package application;

import java.sql.*;

public class LoginDAO {

    // User 클래스 정의
    public static class User {
        private int userId;
        private String username;
        private String name;
        private String role;

        public User(int userId, String username, String name, String role) {
            this.userId = userId;
            this.username = username;
            this.name = name;
            this.role = role;
        }

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getName() { return name; }
        public String getRole() { return role; }
    }

    // 로그인 처리 메서드
    public User login(String username, String password, String role) {
        String sql = "SELECT user_id, username, name, role FROM users WHERE username = ? AND password = ? AND role = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String uname = rs.getString("username");
                String name = rs.getString("name");
                String userRole = rs.getString("role");

                return new User(userId, uname, name, userRole);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // 로그인 실패
    }
}
