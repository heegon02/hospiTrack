package doctorFrame;

import java.time.LocalDate;

public class Patient {
    private int id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private int age;
    private double height;
    private double weight;
    private String bloodType;
    
    public Patient(int id, String name, String gender, LocalDate birthDate, 
                   int age, double height, double weight, String bloodType) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
} 