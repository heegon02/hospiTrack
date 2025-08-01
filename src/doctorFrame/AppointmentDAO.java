package doctorFrame;

import doctorFrame.DatabaseConnection;
import doctorFrame.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    public void saveAppointment(int patientId, LocalDate date) {
        String sql = "INSERT INTO appointments (patient_id, appointment_date) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
        }
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getInt("patient_id"),
                    rs.getDate("appointment_date").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments: " + e.getMessage());
        }
        return list;
    }
}