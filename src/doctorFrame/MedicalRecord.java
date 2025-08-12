package doctorFrame;

import java.time.LocalDateTime;

public class MedicalRecord {
    private int recordId;
    private int appointmentId;
    private int treatmentId;
    private String medicationOrInjection;
    private String patientSymptomsMemo;
    private String medicalOpinion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 생성자 추가 (DAO에서 필요)
    public MedicalRecord() {
    }

    public MedicalRecord(int recordId, int appointmentId, int treatmentId,
                        String medicationOrInjection, String patientSymptomsMemo,
                        String medicalOpinion, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.recordId = recordId;
        this.appointmentId = appointmentId;
        this.treatmentId = treatmentId;
        this.medicationOrInjection = medicationOrInjection;
        this.patientSymptomsMemo = patientSymptomsMemo;
        this.medicalOpinion = medicalOpinion;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getRecordId() {
        return recordId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public String getMedicationOrInjection() {
        return medicationOrInjection;
    }

    public String getPatientSymptomsMemo() {
        return patientSymptomsMemo;
    }

    public String getMedicalOpinion() {
        return medicalOpinion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters (업데이트를 위해 필요)
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public void setMedicationOrInjection(String medicationOrInjection) {
        this.medicationOrInjection = medicationOrInjection;
    }

    public void setPatientSymptomsMemo(String patientSymptomsMemo) {
        this.patientSymptomsMemo = patientSymptomsMemo;
    }

    public void setMedicalOpinion(String medicalOpinion) {
        this.medicalOpinion = medicalOpinion;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}