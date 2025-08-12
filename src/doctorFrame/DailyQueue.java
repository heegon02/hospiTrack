package doctorFrame;

import java.time.LocalDateTime;

public class DailyQueue {
    private int id;
    private int patientId;
    private String patientName;
    private LocalDateTime registrationTime;
    private String status; // "대기", "진료중", "완료"
    private int appointmentId;
    
    public DailyQueue(int id, int patientId, String patientName, 
                     LocalDateTime registrationTime, String status, int appointmentId) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.registrationTime = registrationTime;
        this.status = status;
        this.appointmentId = appointmentId;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public LocalDateTime getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(LocalDateTime registrationTime) { 
        this.registrationTime = registrationTime; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getAppointmentId() {return appointmentId; }
    public void setAppointmentId(int appointmentId) {this.appointmentId = appointmentId; }

} 