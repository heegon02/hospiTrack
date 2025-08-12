package doctorFrame;

import doctorFrame.DatabaseConnection;
import doctorFrame.MedicalRecord;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {

    /**
     * appointment_id로 기존 medical_record 조회
     */
    public MedicalRecord getMedicalRecordByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM medical_records WHERE appointment_id = ? ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                MedicalRecord record = new MedicalRecord();
                record.setRecordId(rs.getInt("record_id"));
                record.setAppointmentId(rs.getInt("appointment_id"));
                record.setTreatmentId(rs.getInt("treatment_id"));
                record.setMedicationOrInjection(rs.getString("medication_or_injection"));
                record.setPatientSymptomsMemo(rs.getString("patient_symptoms_memo"));
                record.setMedicalOpinion(rs.getString("medical_opinion"));
                // created_at과 updated_at 설정
                if (rs.getTimestamp("created_at") != null) {
                    record.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                if (rs.getTimestamp("updated_at") != null) {
                    record.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
                return record;
            }
        } catch (SQLException e) {
            System.err.println("Error getting medical record by appointment ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * 기존 medical_record 업데이트
     */
    public boolean updateMedicalRecord(MedicalRecord record) {
        String sql = "UPDATE medical_records SET " +
                    "medication_or_injection = ?, " +
                    "patient_symptoms_memo = ?, " +
                    "medical_opinion = ?, " +
                    "updated_at = CURRENT_TIMESTAMP " +
                    "WHERE record_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, record.getMedicationOrInjection());
            pstmt.setString(2, record.getPatientSymptomsMemo());
            pstmt.setString(3, record.getMedicalOpinion());
            pstmt.setInt(4, record.getRecordId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating medical record: " + e.getMessage());
            return false;
        }
    }

    public void saveMedicalRecord(int appointmentId, Integer treatmentId,
                                  String medicationOrInjection,
                                  String patientSymptomsMemo,
                                  String medicalOpinion) {
        String sql = "INSERT INTO medical_records " +
                     "(appointment_id, treatment_id, medication_or_injection, patient_symptoms_memo, medical_opinion, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointmentId);

            if (treatmentId != null) {
                pstmt.setInt(2, treatmentId);
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }

            pstmt.setString(3, medicationOrInjection);
            pstmt.setString(4, patientSymptomsMemo);
            pstmt.setString(5, medicalOpinion);

            pstmt.executeUpdate();
            System.out.println("✅ 의사 처방 내역이 medical_records 테이블에 성공적으로 저장되었습니다.");

        } catch (SQLException e) {
            System.err.println("❌ 의사 처방 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public List<MedicalRecord> getMedicalRecords(int patientId, LocalDate date) {
        List<MedicalRecord> records = new ArrayList<>();

        String sql = "SELECT mr.record_id, mr.appointment_id, mr.treatment_id, " +
                     "mr.medication_or_injection, mr.patient_symptoms_memo, mr.medical_opinion, " +
                     "mr.created_at, mr.updated_at " +
                     "FROM medical_records mr " +
                     "JOIN appointments a ON mr.appointment_id = a.appointment_id " +
                     "WHERE a.patient_id = ? AND DATE(a.visit_datetime) = ? " +
                     "ORDER BY mr.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MedicalRecord record = new MedicalRecord(
                    rs.getInt("record_id"),
                    rs.getInt("appointment_id"),
                    rs.getInt("treatment_id"),
                    rs.getString("medication_or_injection"),
                    rs.getString("patient_symptoms_memo"),
                    rs.getString("medical_opinion"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime()
                );
                records.add(record);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching medical records by patient and date: " + e.getMessage());
        }

        return records;
    }
}