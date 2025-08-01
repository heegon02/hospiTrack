package doctorFrame;

import doctorFrame.DatabaseConnection;
import doctorFrame.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PatientDAO {
    
    public Patient getPatientById(int patientId) {
        String sql = "SELECT *, TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) as age FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Patient(
                    rs.getInt("patient_id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getInt("age"),
                    rs.getDouble("height"),
                    rs.getDouble("weight"),
                    rs.getString("blood_type")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting patient: " + e.getMessage());
        }
        return null;
    }
    
    public Patient getPatientByName(String patientName) {
        String sql = "SELECT * FROM patients WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Patient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getInt("age"),
                    rs.getDouble("height"),
                    rs.getDouble("weight"),
                    rs.getString("blood_type")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting patient by name: " + e.getMessage());
        }
        return null;
    }
} 