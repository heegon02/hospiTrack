//db patient 테이블 불러와서 조회하고 수정, 추가 가능하게 해주는 클래스.

package application;

import java.time.LocalDate;

public class Patient {
	private int patientId;
	private String name;
    private double weight;
    private double height;
    private String bloodType;
    private LocalDate birthDate;
    private String phoneNumber;
    private String gender;
    private String address;
    
    public Patient() {}
    
    public Patient(String name, double weight, double height, String bloodType,
                   LocalDate birthDate, String phoneNumber, String gender, String address) {
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.bloodType = bloodType;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
    }

    // getter
    public int getPatientId() {return patientId;}
    public String getName() { return name; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public String getBloodType() { return bloodType; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    
    // setter 추가
    public void setName(String name) { this.name = name; }
    public void setWeight(double weight) {this.weight = weight; }
    public void setHeight(double height) {this.height = height; }
    public void setBloodType(String bloodType) {this.bloodType = bloodType; }
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber; }
    public void setGender(String gender) {this.gender = gender; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setPatientId(int patientId) {this.patientId = patientId; }
}
