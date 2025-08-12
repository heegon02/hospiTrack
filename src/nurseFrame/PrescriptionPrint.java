package nurseFrame;

import application.DBUtil;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class PrescriptionPrint {

	public static void printPrescription(Integer appointmentId) {
	    if (appointmentId == null) return;

	    String sql = "SELECT p.name AS patient_name, p.birth_date, p.gender, " +
	                 "m.patient_symptoms_memo, m.medical_opinion, m.medication_or_injection, " +
	                 "a.visit_datetime " +
	                 "FROM patients p " +
	                 "JOIN appointments a ON p.patient_id = a.patient_id " +
	                 "LEFT JOIN medical_records m ON a.appointment_id = m.appointment_id " +
	                 "WHERE a.appointment_id = ? " +
	                 "ORDER BY m.created_at DESC LIMIT 1";

	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, appointmentId);
	        ResultSet rs = pstmt.executeQuery();

	        String patientName = "정보 없음";
	        String birthDate = "정보 없음";
	        String gender = "정보 없음";
	        String symptoms = "정보 없음";
	        String opinion = "정보 없음";
	        String medication = "정보 없음";
	        String date = "정보 없음";

	        if (rs.next()) {
	            patientName = rs.getString("patient_name");
	            birthDate = rs.getString("birth_date");
	            gender = rs.getString("gender");
	            symptoms = rs.getString("patient_symptoms_memo");
	            opinion = rs.getString("medical_opinion");
	            medication = rs.getString("medication_or_injection");
	            date = rs.getString("visit_datetime");
	        }

	        createPDF(patientName, birthDate, gender, symptoms, opinion, medication, date);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private static void createPDF(String name, String birth, String gender,
	        String symptoms, String opinion, String medication, String date) throws Exception {

	    String userDesktop = System.getProperty("user.home") + "/Desktop";
	    File file = new File(userDesktop, "prescription.pdf");

	    Document document = new Document(PageSize.A4, 20, 20, 20, 20);
	    PdfWriter.getInstance(document, new FileOutputStream(file));
	    document.open();

	    // 폰트 설정
	    Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK);
	    Font hospitalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLUE);
	    Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	    Font valueFont = new Font(Font.FontFamily.HELVETICA, 10);
	    Font smallFont = new Font(Font.FontFamily.HELVETICA, 8);
	    Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

	    // ===== 상단 헤더 영역 =====
	    PdfPTable headerTable = new PdfPTable(3);
	    headerTable.setWidthPercentage(100);
	    headerTable.setWidths(new float[]{3f, 4f, 3f});
	    headerTable.setSpacingAfter(10);

	    // 왼쪽: 병원 로고 영역
	    PdfPCell logoCell = new PdfPCell();
	    logoCell.setBorder(Rectangle.BOX);
	    logoCell.setFixedHeight(60f);
	    logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    logoCell.addElement(new Paragraph("LOGO", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.GRAY)));
	    headerTable.addCell(logoCell);

	    // 중앙: 병원 정보
	    PdfPCell hospitalInfoCell = new PdfPCell();
	    hospitalInfoCell.setBorder(Rectangle.NO_BORDER);
	    hospitalInfoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    
	    Paragraph hospitalName = new Paragraph("서울대학교병원", titleFont);
	    hospitalName.setAlignment(Element.ALIGN_CENTER);
	    hospitalInfoCell.addElement(hospitalName);
	    
	    Paragraph hospitalAddr = new Paragraph("서울특별시 종로구 대학로 101", hospitalFont);
	    hospitalAddr.setAlignment(Element.ALIGN_CENTER);
	    hospitalInfoCell.addElement(hospitalAddr);
	    
	    Paragraph hospitalTel = new Paragraph("TEL: 02-2072-2114  FAX: 02-742-3152", smallFont);
	    hospitalTel.setAlignment(Element.ALIGN_CENTER);
	    hospitalTel.setSpacingBefore(5);
	    hospitalInfoCell.addElement(hospitalTel);
	    
	    headerTable.addCell(hospitalInfoCell);

	    // 오른쪽: 처방전 정보
	    PdfPCell prescInfoCell = new PdfPCell();
	    prescInfoCell.setBorder(Rectangle.BOX);
	    prescInfoCell.setPadding(8);
	    
	    Paragraph prescNo = new Paragraph("처방전 번호", labelFont);
	    prescInfoCell.addElement(prescNo);
	    Paragraph prescNoVal = new Paragraph("2025080800001", valueFont);
	    prescNoVal.setSpacingAfter(5);
	    prescInfoCell.addElement(prescNoVal);
	    
	    Paragraph prescDate = new Paragraph("발행일자", labelFont);
	    prescInfoCell.addElement(prescDate);
	    Paragraph prescDateVal = new Paragraph("2025.08.08", valueFont);
	    prescInfoCell.addElement(prescDateVal);
	    
	    headerTable.addCell(prescInfoCell);
	    document.add(headerTable);

	    // ===== 처방전 제목 =====
	    Paragraph prescTitle = new Paragraph("처    방    전", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD));
	    prescTitle.setAlignment(Element.ALIGN_CENTER);
	    prescTitle.setSpacingAfter(15);
	    document.add(prescTitle);

	    // ===== 환자 정보 테이블 =====
	    PdfPTable patientTable = new PdfPTable(8);
	    patientTable.setWidthPercentage(100);
	    patientTable.setWidths(new float[]{1.5f, 3f, 1.5f, 2f, 1f, 1.5f, 1.5f, 2.5f});
	    patientTable.setSpacingAfter(10);

	    // 첫 번째 행
	    addBorderedCell(patientTable, "환자성명", labelFont, true);
	    addBorderedCell(patientTable, name, valueFont, false);
	    addBorderedCell(patientTable, "주민등록번호", labelFont, true);
	    addBorderedCell(patientTable, birth.replace("-", "").substring(2) + "-*******", valueFont, false);
	    addBorderedCell(patientTable, "성별", labelFont, true);
	    addBorderedCell(patientTable, gender, valueFont, false);
	    addBorderedCell(patientTable, "나이", labelFont, true);
	    addBorderedCell(patientTable, calculateAge(birth) + "세", valueFont, false);

	    // 두 번째 행
	    addBorderedCell(patientTable, "전화번호", labelFont, true);
	    addBorderedCell(patientTable, "010-****-****", valueFont, false);
	    addBorderedCell(patientTable, "진료과", labelFont, true);
	    addBorderedCell(patientTable, "내과", valueFont, false);
	    addBorderedCell(patientTable, "진료의", labelFont, true);
	    addBorderedCell(patientTable, "김○○", valueFont, false);
	    addBorderedCell(patientTable, "면허번호", labelFont, true);
	    addBorderedCell(patientTable, "제12345호", valueFont, false);

	    // 세 번째 행
	    addBorderedCell(patientTable, "진료일시", labelFont, true);
	    addBorderedCell(patientTable, date, valueFont, false);
	    addBorderedCell(patientTable, "질병분류기호", labelFont, true);
	    addBorderedCell(patientTable, "K59.1", valueFont, false);
	    addBorderedCell(patientTable, "처방일수", labelFont, true);
	    addBorderedCell(patientTable, "7일", valueFont, false);
	    addBorderedCell(patientTable, "투약일수", labelFont, true);
	    addBorderedCell(patientTable, "7일", valueFont, false);

	    document.add(patientTable);

	    // ===== 상병명 =====
	    PdfPTable diseaseTable = new PdfPTable(2);
	    diseaseTable.setWidthPercentage(100);
	    diseaseTable.setWidths(new float[]{1.5f, 8.5f});
	    diseaseTable.setSpacingAfter(10);
	    
	    addBorderedCell(diseaseTable, "상병명", headerFont, true);
	    addBorderedCell(diseaseTable, symptoms, valueFont, false);
	    
	    document.add(diseaseTable);

	 // PrescriptionPrint.java의 createPDF 메서드에서 medication 처리 부분을 이렇게 수정하세요:

	 // ===== 처방 내역 ===== (기존 코드 200번째 줄 근처)
	 PdfPTable medicationTable = new PdfPTable(7);
	 medicationTable.setWidthPercentage(100);
	 medicationTable.setWidths(new float[]{0.8f, 4f, 1.2f, 1f, 1f, 1f, 1f});
	 medicationTable.setSpacingAfter(10);

	 // 처방 테이블 헤더
	 addBorderedCell(medicationTable, "번호", headerFont, true);
	 addBorderedCell(medicationTable, "의약품명", headerFont, true);
	 addBorderedCell(medicationTable, "의약품코드", headerFont, true);
	 addBorderedCell(medicationTable, "1회투약량", headerFont, true);
	 addBorderedCell(medicationTable, "1일투약횟수", headerFont, true);
	 addBorderedCell(medicationTable, "총투약일수", headerFont, true);
	 addBorderedCell(medicationTable, "용법", headerFont, true);

	 // 처방 내역 (medication null 체크 추가)
	 String[] medications;
	 if (medication != null && !medication.equals("정보 없음") && !medication.trim().isEmpty()) {
	     medications = medication.split(",|\\n");
	 } else {
	     medications = new String[]{"처방된 약물 없음"}; // 기본값 설정
	 }

	 for (int i = 0; i < Math.min(medications.length, 3); i++) {
	     addBorderedCell(medicationTable, String.valueOf(i + 1), valueFont, false);
	     addBorderedCell(medicationTable, medications[i].trim(), valueFont, false);
	     addBorderedCell(medicationTable, "641900" + (i + 1) + "10", smallFont, false);
	     addBorderedCell(medicationTable, "1T", valueFont, false);
	     addBorderedCell(medicationTable, "3", valueFont, false);
	     addBorderedCell(medicationTable, "7", valueFont, false);
	     addBorderedCell(medicationTable, "식후 30분", smallFont, false);
	 }

	 // 빈 행 추가 (최소 5행 확보)
	 for (int i = medications.length; i < 5; i++) {
	     for (int j = 0; j < 7; j++) {
	         addBorderedCell(medicationTable, "", valueFont, false);
	     }
	 }

	 document.add(medicationTable);

	    // ===== 의사 소견 =====
	    PdfPTable opinionTable = new PdfPTable(1);
	    opinionTable.setWidthPercentage(100);
	    opinionTable.setSpacingAfter(15);
	    
	    PdfPCell opinionHeaderCell = new PdfPCell(new Phrase("의사소견 및 특기사항", headerFont));
	    opinionHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    opinionHeaderCell.setPadding(5);
	    opinionHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    opinionTable.addCell(opinionHeaderCell);
	    
	    PdfPCell opinionContentCell = new PdfPCell(new Phrase(opinion, valueFont));
	    opinionContentCell.setPadding(8);
	    opinionContentCell.setMinimumHeight(50);
	    opinionTable.addCell(opinionContentCell);
	    
	    document.add(opinionTable);

	    // ===== 하단 서명 및 인증 영역 =====
	    PdfPTable signatureTable = new PdfPTable(3);
	    signatureTable.setWidthPercentage(100);
	    signatureTable.setWidths(new float[]{3f, 3f, 4f});
	    
	    // 처방의 서명
	    PdfPCell signCell = new PdfPCell();
	    signCell.setBorder(Rectangle.BOX);
	    signCell.setPadding(10);
	    signCell.setFixedHeight(80f);
	    
	    Paragraph signTitle = new Paragraph("처방의 서명", labelFont);
	    signTitle.setAlignment(Element.ALIGN_CENTER);
	    signCell.addElement(signTitle);
	    
	    Paragraph signName = new Paragraph("김○○  (인)", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
	    signName.setAlignment(Element.ALIGN_CENTER);
	    signName.setSpacingBefore(20);
	    signCell.addElement(signName);
	    signatureTable.addCell(signCell);
	    
	    // 조제기관란
	    PdfPCell pharmacyCell = new PdfPCell();
	    pharmacyCell.setBorder(Rectangle.BOX);
	    pharmacyCell.setPadding(10);
	    pharmacyCell.setFixedHeight(80f);
	    
	    Paragraph pharmacyTitle = new Paragraph("조제기관", labelFont);
	    pharmacyTitle.setAlignment(Element.ALIGN_CENTER);
	    pharmacyCell.addElement(pharmacyTitle);
	    
	    Paragraph pharmacyName = new Paragraph("약국명:", valueFont);
	    pharmacyName.setSpacingBefore(10);
	    pharmacyCell.addElement(pharmacyName);
	    
	    Paragraph pharmacistName = new Paragraph("약사명:", valueFont);
	    pharmacistName.setSpacingBefore(5);
	    pharmacyCell.addElement(pharmacistName);
	    signatureTable.addCell(pharmacyCell);
	    
	    // 환자확인란
	    PdfPCell confirmCell = new PdfPCell();
	    confirmCell.setBorder(Rectangle.BOX);
	    confirmCell.setPadding(10);
	    confirmCell.setFixedHeight(80f);
	    
	    Paragraph confirmTitle = new Paragraph("환자 확인란", labelFont);
	    confirmTitle.setAlignment(Element.ALIGN_CENTER);
	    confirmCell.addElement(confirmTitle);
	    
	    Paragraph confirmNote = new Paragraph("※ 이 처방전은 교부일을 포함하여 3일 이내에 약국에 제출하여야 합니다.", 
	                                         new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.RED));
	    confirmNote.setSpacingBefore(10);
	    confirmCell.addElement(confirmNote);
	    signatureTable.addCell(confirmCell);
	    
	    document.add(signatureTable);

	    // ===== 하단 안내문 =====
	    Paragraph footer = new Paragraph("※ 의약분업 예외 사유: □ 의료업자가 직접 조제  □ 응급의료  □ 자가치료용", smallFont);
	    footer.setAlignment(Element.ALIGN_CENTER);
	    footer.setSpacingBefore(10);
	    document.add(footer);

	    document.close();

	    // 파일 존재 및 크기 체크
	    if (!file.exists() || file.length() == 0) {
	        System.out.println("PDF 파일 생성 실패");
	        return;
	    }

	    System.out.println("PDF 생성 완료: " + file.getAbsolutePath() + ", 크기: " + file.length());

	    try {
	        String[] cmd = {"cmd", "/c", "start", "\"\"", "\"" + file.getAbsolutePath() + "\""};
	        Runtime.getRuntime().exec(cmd);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	// 테이블 셀 생성 헬퍼 메소드
	private static void addBorderedCell(PdfPTable table, String text, Font font, boolean isHeader) {
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setBorder(Rectangle.BOX);
	    cell.setPadding(3);
	    if (isHeader) {
	        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    }
	    table.addCell(cell);
	}

	// 나이 계산 헬퍼 메소드
	private static int calculateAge(String birthDate) {
	    try {
	        if (birthDate == null || birthDate.equals("정보 없음")) return 0;
	        
	        String[] parts = birthDate.split("-");
	        int birthYear = Integer.parseInt(parts[0]);
	        int currentYear = java.time.LocalDate.now().getYear();
	        
	        return currentYear - birthYear;
	    } catch (Exception e) {
	        return 0;
	    }
	}

	
}
