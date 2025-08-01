//DB에서 patients 테이블 연결해서 데이터 입출력을 담당하는 클래스
//addPatient (환자 신규 등록 메소드)
//searchPatientsByName (환자 검색 메소드)
//getPatientBasicInfo (환자 기본 정보 조회 메소드)

package application;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
	
	
	//신규 환자 등록에 사용하는 메소드: addPatient
	public static void addPatient(Patient p) throws SQLException {
        String sql = "INSERT INTO patients (name, weight, height, blood_type, birth_date, phone_number, gender, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getName());
            pstmt.setDouble(2, p.getWeight());
            pstmt.setDouble(3, p.getHeight());
            pstmt.setString(4, p.getBloodType());
            pstmt.setDate(5, java.sql.Date.valueOf(p.getBirthDate()));
            pstmt.setString(6, p.getPhoneNumber());
            pstmt.setString(7, p.getGender());
            pstmt.setString(8, p.getAddress());

            pstmt.executeUpdate();
        }
    }
	
	
	//환자 검색에 사용하는 메소드: searchPatientByName
	public static List<Patient> searchPatientsByName(String name) throws SQLException {
	    String sql = "SELECT patient_id, name, gender, birth_date FROM patients WHERE name = ?";
	    List<Patient> patients = new ArrayList<>();

	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setString(1, name);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Patient p = new Patient();
	            p.setPatientId(rs.getInt("patient_id"));
	            p.setName(rs.getString("name"));
	            p.setGender(rs.getString("gender"));
	            p.setBirthDate(rs.getDate("birth_date").toLocalDate());
	            patients.add(p);
	        }
	    }
	    return patients;
	}
	
	
	
	
	//환자 기본 정보 출력에 사용하는 메소드: getPatientBasicInfo
	public static Patient getPatientBasicInfo(int patientId) throws SQLException {
	    String sql = "SELECT patient_id, name, weight, height, blood_type, birth_date, phone_number, gender " +
	                 "FROM patients WHERE patient_id = ?";

	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setInt(1, patientId);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            Patient p = new Patient();
	            p.setPatientId(rs.getInt("patient_id"));
	            p.setName(rs.getString("name"));
	            p.setWeight(rs.getDouble("weight"));
	            p.setHeight(rs.getDouble("height"));
	            p.setBloodType(rs.getString("blood_type"));
	            p.setBirthDate(rs.getDate("birth_date").toLocalDate());
	            p.setPhoneNumber(rs.getString("phone_number"));
	            p.setGender(rs.getString("gender"));
	            return p;
	        }
	    }
	    return null; // 해당 환자가 없으면 null 반환
	}


	
	// 환자 정보 수정 메소드
	public static void updatePatient(Patient p) throws SQLException {
	    String sql = "UPDATE patients SET name=?, weight=?, height=?, blood_type=?, birth_date=?, phone_number=?, gender=? WHERE patient_id=?";

	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setString(1, p.getName());
	        pstmt.setDouble(2, p.getWeight());
	        pstmt.setDouble(3, p.getHeight());
	        pstmt.setString(4, p.getBloodType());
	        pstmt.setDate(5, java.sql.Date.valueOf(p.getBirthDate()));
	        pstmt.setString(6, p.getPhoneNumber());
	        pstmt.setString(7, p.getGender());
	        pstmt.setInt(8, p.getPatientId());

	        pstmt.executeUpdate();
	    }
	}
	
	
	
	
	
	
	
}
