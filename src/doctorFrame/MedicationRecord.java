package doctorFrame;

import java.time.LocalDateTime;

public class MedicationRecord {
    private int medicationId;
    private int prescriptionId;
    private int patientId;
    private int nurseId;
    private LocalDateTime medicationTime;
    private String medicationName;
    private String dosage;
    private String instructions;
    
    public MedicationRecord(int medicationId, int prescriptionId, int patientId, int nurseId, 
                          LocalDateTime medicationTime, String medicationName, String dosage, String instructions) {
        this.medicationId = medicationId;
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.nurseId = nurseId;
        this.medicationTime = medicationTime;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.instructions = instructions;
    }
    
    // Getters and Setters
    public int getMedicationId() { return medicationId; }
    public void setMedicationId(int medicationId) { this.medicationId = medicationId; }
    
    public int getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(int prescriptionId) { this.prescriptionId = prescriptionId; }
    
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    
    public int getNurseId() { return nurseId; }
    public void setNurseId(int nurseId) { this.nurseId = nurseId; }
    
    public LocalDateTime getMedicationTime() { return medicationTime; }
    public void setMedicationTime(LocalDateTime medicationTime) { this.medicationTime = medicationTime; }
    
    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }
    
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
} 