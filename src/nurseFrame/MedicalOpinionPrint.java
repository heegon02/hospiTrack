package nurseFrame;

import application.DBUtil;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MedicalOpinionPrint {

    public static void printMedicalOpinion(Integer appointmentId) {
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

            createOpinionPDF(patientName, birthDate, gender, symptoms, opinion, medication, date);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createOpinionPDF(String name, String birth, String gender,
            String symptoms, String opinion, String medication, String date) throws Exception {

        String userDesktop = System.getProperty("user.home") + "/Desktop";
        File file = new File(userDesktop, "medical_opinion.pdf");

        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // 폰트 설정
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD, BaseColor.BLACK);
        Font hospitalFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 11);
        Font contentFont = new Font(Font.FontFamily.HELVETICA, 11);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.GRAY);

        // ===== 상단 헤더 =====
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{7f, 3f});
        headerTable.setSpacingAfter(20);

        // 병원 정보
        PdfPCell hospitalCell = new PdfPCell();
        hospitalCell.setBorder(Rectangle.NO_BORDER);
        
        Paragraph hospitalName = new Paragraph("서울대학교병원", hospitalFont);
        hospitalName.setSpacingAfter(5);
        hospitalCell.addElement(hospitalName);
        
        Paragraph hospitalInfo = new Paragraph("서울특별시 종로구 대학로 101", smallFont);
        hospitalCell.addElement(hospitalInfo);
        Paragraph hospitalContact = new Paragraph("TEL: 02-2072-2114", smallFont);
        hospitalCell.addElement(hospitalContact);
        
        headerTable.addCell(hospitalCell);

        // 문서 번호
        PdfPCell docInfoCell = new PdfPCell();
        docInfoCell.setBorder(Rectangle.NO_BORDER);
        docInfoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        Paragraph docNumber = new Paragraph("문서번호: OP-" + System.currentTimeMillis() % 100000, smallFont);
        docInfoCell.addElement(docNumber);
        Paragraph issueDate = new Paragraph("발급일: " + currentDate, smallFont);
        docInfoCell.addElement(issueDate);
        
        headerTable.addCell(docInfoCell);
        document.add(headerTable);

        // ===== 제목 =====
        Paragraph title = new Paragraph("의  료  소  견  서", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);

        // ===== 환자 기본 정보 =====
        Paragraph patientInfoTitle = new Paragraph("환자 정보", sectionFont);
        patientInfoTitle.setSpacingAfter(10);
        document.add(patientInfoTitle);

        PdfPTable patientTable = new PdfPTable(4);
        patientTable.setWidthPercentage(100);
        patientTable.setWidths(new float[]{2f, 3f, 2f, 3f});
        patientTable.setSpacingAfter(20);

        addCleanCell(patientTable, "성      명", labelFont, name, valueFont);
        addCleanCell(patientTable, "생년월일", labelFont, birth, valueFont);
        addCleanCell(patientTable, "성      별", labelFont, gender, valueFont);
        addCleanCell(patientTable, "진료일자", labelFont, date, valueFont);

        document.add(patientTable);

        // ===== 진단 내용 =====
        Paragraph diagnosisTitle = new Paragraph("진단 및 증상", sectionFont);
        diagnosisTitle.setSpacingAfter(10);
        document.add(diagnosisTitle);

        PdfPTable diagnosisTable = new PdfPTable(1);
        diagnosisTable.setWidthPercentage(100);
        diagnosisTable.setSpacingAfter(20);

        PdfPCell diagnosisCell = new PdfPCell();
        diagnosisCell.setBorder(Rectangle.BOX);
        diagnosisCell.setPadding(15);
        diagnosisCell.setMinimumHeight(80);
        diagnosisCell.addElement(new Paragraph(symptoms, contentFont));
        diagnosisTable.addCell(diagnosisCell);

        document.add(diagnosisTable);

        // ===== 의사 소견 =====
        Paragraph opinionTitle = new Paragraph("의사 소견", sectionFont);
        opinionTitle.setSpacingAfter(10);
        document.add(opinionTitle);

        PdfPTable opinionTable = new PdfPTable(1);
        opinionTable.setWidthPercentage(100);
        opinionTable.setSpacingAfter(20);

        PdfPCell opinionCell = new PdfPCell();
        opinionCell.setBorder(Rectangle.BOX);
        opinionCell.setPadding(15);
        opinionCell.setMinimumHeight(100);
        opinionCell.addElement(new Paragraph(opinion, contentFont));
        opinionTable.addCell(opinionCell);

        document.add(opinionTable);

        // ===== 치료 및 처방 내역 =====
        if (medication != null && !medication.equals("정보 없음") && !medication.trim().isEmpty()) {
            Paragraph treatmentTitle = new Paragraph("치료 및 처방 내역", sectionFont);
            treatmentTitle.setSpacingAfter(10);
            document.add(treatmentTitle);

            PdfPTable treatmentTable = new PdfPTable(1);
            treatmentTable.setWidthPercentage(100);
            treatmentTable.setSpacingAfter(30);

            PdfPCell treatmentCell = new PdfPCell();
            treatmentCell.setBorder(Rectangle.BOX);
            treatmentCell.setPadding(15);
            treatmentCell.setMinimumHeight(60);
            treatmentCell.addElement(new Paragraph(medication, contentFont));
            treatmentTable.addCell(treatmentCell);

            document.add(treatmentTable);
        } else {
            document.add(new Paragraph(" ", contentFont));
            document.add(new Paragraph(" ", contentFont));
        }

        // ===== 의료진 정보 및 서명 =====
        PdfPTable doctorTable = new PdfPTable(2);
        doctorTable.setWidthPercentage(100);
        doctorTable.setWidths(new float[]{5f, 5f});
        doctorTable.setSpacingBefore(20);

        // 의료진 정보
        PdfPCell doctorInfoCell = new PdfPCell();
        doctorInfoCell.setBorder(Rectangle.NO_BORDER);
        doctorInfoCell.setPadding(10);
        
        Paragraph doctorInfoTitle = new Paragraph("담당 의료진", labelFont);
        doctorInfoTitle.setSpacingAfter(10);
        doctorInfoCell.addElement(doctorInfoTitle);
        
        Paragraph doctorName = new Paragraph("진 료 의: 김○○", valueFont);
        doctorInfoCell.addElement(doctorName);
        Paragraph doctorLicense = new Paragraph("의사면허: 제12345호", valueFont);
        doctorInfoCell.addElement(doctorLicense);
        Paragraph doctorDept = new Paragraph("진 료 과: 내과", valueFont);
        doctorInfoCell.addElement(doctorDept);
        
        doctorTable.addCell(doctorInfoCell);

        // 서명란
        PdfPCell signCell = new PdfPCell();
        signCell.setBorder(Rectangle.BOX);
        signCell.setPadding(15);
        signCell.setFixedHeight(100);
        signCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        Paragraph signTitle = new Paragraph("담당의 서명", labelFont);
        signTitle.setAlignment(Element.ALIGN_CENTER);
        signCell.addElement(signTitle);
        
        Paragraph signLine = new Paragraph(" ", contentFont);
        signLine.setSpacingBefore(20);
        signCell.addElement(signLine);
        
        Paragraph doctorSign = new Paragraph("김○○  (인)", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
        doctorSign.setAlignment(Element.ALIGN_CENTER);
        doctorSign.setSpacingBefore(10);
        signCell.addElement(doctorSign);
        
        doctorTable.addCell(signCell);
        
        document.add(doctorTable);

        // ===== 하단 안내사항 =====
        document.add(new Paragraph(" ", contentFont));
        
        LineSeparator line = new LineSeparator();
        line.setLineWidth(1f);
        line.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(line));
        
        Paragraph notice = new Paragraph("※ 본 소견서는 의료법에 따라 작성되었으며, 진료 당시의 환자 상태를 바탕으로 한 의학적 소견입니다.", 
                                        new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.GRAY));
        notice.setAlignment(Element.ALIGN_CENTER);
        notice.setSpacingBefore(10);
        document.add(notice);
        
        Paragraph hospitalStamp = new Paragraph("서울대학교병원장  (직인)", 
                                              new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD));
        hospitalStamp.setAlignment(Element.ALIGN_RIGHT);
        hospitalStamp.setSpacingBefore(15);
        document.add(hospitalStamp);

        document.close();

        // 파일 존재 및 크기 체크
        if (!file.exists() || file.length() == 0) {
            System.out.println("소견서 PDF 파일 생성 실패");
            return;
        }

        System.out.println("소견서 PDF 생성 완료: " + file.getAbsolutePath() + ", 크기: " + file.length());

        try {
            String[] cmd = {"cmd", "/c", "start", "\"\"", "\"" + file.getAbsolutePath() + "\""};
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 깔끔한 테이블 셀 생성 헬퍼 메소드
    private static void addCleanCell(PdfPTable table, String labelText, Font labelFont, 
                                    String valueText, Font valueFont) {
        // 라벨 셀
        PdfPCell labelCell = new PdfPCell(new Phrase(labelText, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(new BaseColor(248, 248, 248));
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(labelCell);

        // 값 셀
        PdfPCell valueCell = new PdfPCell(new Phrase(valueText, valueFont));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(BaseColor.LIGHT_GRAY);
        valueCell.setPadding(8);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(valueCell);
    }
}