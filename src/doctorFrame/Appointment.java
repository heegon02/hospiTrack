package doctorFrame;

import java.time.LocalDate;

public class Appointment {
    private int appointmentId;
    private int patientId;
    private LocalDate appointmentDate;

    public Appointment(int appointmentId, int patientId, LocalDate appointmentDate) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.appointmentDate = appointmentDate;
    }

    public int getAppointmentId() { return appointmentId; }
    public int getPatientId() { return patientId; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
}