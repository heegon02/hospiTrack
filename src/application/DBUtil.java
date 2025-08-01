package application;

import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_db?serverTimezone=UTC";
    private static final String USER = "root";   // 네 DB 사용자
    private static final String PASSWORD = "1234"; // 네 DB 비밀번호

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
