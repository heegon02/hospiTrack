package doctorFrame;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Appointment {
    private int appointmentId;
    private int patientId;
    private LocalDate appointmentDate;

    private LocalDateTime createdAt;
    private String status;

    // 기존 생성자
    public Appointment(int appointmentId, int patientId, LocalDate appointmentDate) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.appointmentDate = appointmentDate;
    }

    // 새 생성자 (createdAt, status 포함)
    public Appointment(int appointmentId, int patientId, LocalDateTime createdAt, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.createdAt = createdAt;
        this.status = status;
    }

    // Getter들
    public int getAppointmentId() {
        return appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    
    // 필요하면 Setter도 추가 가능
}
