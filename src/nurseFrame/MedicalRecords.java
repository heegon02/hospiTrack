package nurseFrame;

import application.MedicalRecordDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class MedicalRecords extends VBox {

    private Integer patientId;
    private Consumer<Integer> appointmentDateSelectedListener;

    public MedicalRecords() {
        Label titleLabel = new Label("환자 진료 기록");
        titleLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;"
        );

        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setAlignment(Pos.TOP_LEFT);

        this.getChildren().add(titleLabel);

        this.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: lightgray;"
        );

        this.setMinSize(350, 815);
        this.setMaxSize(350, 815);
    }

    public void setOnAppointmentDateSelected(Consumer<Integer> listener) {
        this.appointmentDateSelectedListener = listener;
    }

    private void onDateSelected(int appointmentId) {
        if (appointmentDateSelectedListener != null) {
            appointmentDateSelectedListener.accept(appointmentId);
        }
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;

        // 기존 버튼 등 클리어
        this.getChildren().remove(1, this.getChildren().size());

        // List<String> => List<Appointment>로 변경
        List<Appointment> appointments = MedicalRecordDAO.getAppointmentsByPatientId(patientId);

        for (Appointment appt : appointments) {
            VBox recordContainer = new VBox(5);

            // 세부 버튼 영역 (처음엔 숨김)
            VBox detailBox = new VBox(5);
            detailBox.setPadding(new Insets(5, 0, 5, 20));
            detailBox.setVisible(false);
            detailBox.setManaged(false);
            detailBox.setAlignment(Pos.CENTER);

            // 버튼 5개 생성 및 스타일링 (이 부분 기존과 동일)
            Button receiptBtn = new Button("영수증 출력");
            Button paymentBtn = new Button("수납완료처리");
            Button prescriptionBtn = new Button("처방전 출력");
            Button noteRequestBtn = new Button("소견서 요청");
            Button notePrintBtn = new Button("소견서 출력");
            
            //처방전 출력 버튼
            prescriptionBtn.setOnAction(e -> {
                PrescriptionPrint.printPrescription(appt.getAppointmentId());
            });
            
            //소견서 출력 버튼
            notePrintBtn.setOnAction(e -> {
            	MedicalOpinionPrint.printMedicalOpinion(appt.getAppointmentId());
            });

            for (Button btn : new Button[]{receiptBtn, paymentBtn, prescriptionBtn, noteRequestBtn, notePrintBtn}) {
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setStyle(
                    "-fx-background-color: gray;" +
                    "-fx-border-color: gray;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-max-width: 120;" +
                    "-fx-min-width: 120;" +
                    "-fx-border-radius: 90;" +
                    "-fx-background-radius: 90;"
                );
                btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: gray;" +
                    "-fx-border-color: gray;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-max-width: 120;" +
                    "-fx-min-width: 120;" +
                    "-fx-border-radius: 90;" +
                    "-fx-background-radius: 90;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 2);"
                ));
                btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-background-color: gray;" +
                    "-fx-border-color: gray;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-max-width: 120;" +
                    "-fx-min-width: 120;" +
                    "-fx-border-radius: 90;" +
                    "-fx-background-radius: 90;"
                ));
            }

            detailBox.getChildren().addAll(receiptBtn, paymentBtn, prescriptionBtn, noteRequestBtn, notePrintBtn);

            // 버튼 텍스트를 Appointment의 visitDatetime으로 설정
            Button recordBtn = new Button(appt.getVisitDatetime());
            recordBtn.setMaxWidth(Double.MAX_VALUE);
            recordBtn.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: lightgray;" +
                "-fx-font-size: 16px;"
            );
            recordBtn.setOnMouseEntered(e -> recordBtn.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: lightgray;" +
                "-fx-font-size: 16px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 2);"
            ));
            recordBtn.setOnMouseExited(e -> recordBtn.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: lightgray;" +
                "-fx-font-size: 16px;"
            ));

            recordBtn.setOnAction(e -> {
                boolean currentlyVisible = detailBox.isVisible();

                // 모든 날짜의 detailBox 닫기
                for (var node : this.getChildren()) {
                    if (node instanceof VBox) {
                        VBox container = (VBox) node;
                        if (container.getChildren().size() > 1) {
                            var dbox = container.getChildren().get(1);
                            dbox.setVisible(false);
                            dbox.setManaged(false);
                        }
                    }
                }

                // 선택한 날짜 detailBox 토글
                detailBox.setVisible(!currentlyVisible);
                detailBox.setManaged(!currentlyVisible);

                // 필요 시 선택한 appointmentId 전달
                onDateSelected(appt.getAppointmentId());
            });

            recordContainer.getChildren().addAll(recordBtn, detailBox);
            this.getChildren().add(recordContainer);
        }
    }


    private void styleRecordButton(Button btn) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: lightgray;" +
            "-fx-font-size: 16px;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: lightgray;" +
            "-fx-font-size: 16px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 2);"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: lightgray;" +
            "-fx-font-size: 16px;"
        ));
    }

    private void toggleDetailBox(int appointmentId) {
        // 여기선 간단히 로그 출력만 함.
        // 필요하면 별도 UI 확장 가능
        System.out.println("선택한 appointmentId: " + appointmentId);
    }

    public Integer getPatientId() {
        return patientId;
    }
}
