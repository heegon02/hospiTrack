package application;

import nurseFrame.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {

    public static List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT appointment_id, visit_datetime FROM appointments WHERE patient_id = ? ORDER BY visit_datetime DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("appointment_id");
                String datetime = rs.getString("visit_datetime");
                appointments.add(new Appointment(id, datetime));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }
    
    //테스트
    public static Integer getLatestAppointmentIdByPatientId(int patientId) {
        String sql = "SELECT appointment_id FROM appointments WHERE patient_id = ? ORDER BY visit_datetime DESC LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("appointment_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // 기록이 없을 경우
    }

}
