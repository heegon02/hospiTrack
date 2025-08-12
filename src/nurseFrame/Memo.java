//간호사 메모 작성 공간

package nurseFrame;

import application.NursingRecordDAO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Memo extends VBox {
    private Integer patientId;
    private TextArea memoField;
    private Button submitButton;
    
    public Button getSubmitButton() {
    	return submitButton;
    }

    public Memo() {
        Label titleLabel = new Label("간호사 메모 작성");
        submitButton = new Button("입력");

        HBox upperHBox = new HBox(10, titleLabel, submitButton);

        memoField = new TextArea();
        memoField.setPromptText("환자의 특이사항을 입력하세요.");
        memoField.setPrefSize(280, 150);

        VBox mainVBox = new VBox(10, upperHBox, memoField);

        this.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: lightgray;"
        ); 
        this.setAlignment(Pos.TOP_LEFT);
        this.setPadding(new Insets(10));
        this.getChildren().add(mainVBox);

        // 입력 버튼 이벤트
        submitButton.setOnAction(e -> {
            if (patientId == null) {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setHeaderText("환자가 선택되지 않았습니다.");
                warn.showAndWait();
                return;
            }

            String note = memoField.getText().trim();
            if (note.isEmpty()) {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setHeaderText("메모를 입력하세요.");
                warn.showAndWait();
                return;
            }

            try {
                NursingRecordDAO.upsertNursingRecord(patientId, note);
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setHeaderText("간호 메모 등록 완료");
                success.setContentText("환자 ID " + patientId + " 오늘 메모가 저장되었습니다.");
                success.showAndWait();
            } catch (Exception ex) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText("메모 저장 실패");
                error.setContentText(ex.getMessage());
                error.showAndWait();
            }
        });
    }

    // 환자 선택 시 오늘 메모 자동 불러오기
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
        try {
            String todayNote = NursingRecordDAO.getTodayNursingNote(patientId);
            if (todayNote != null) {
                memoField.setText(todayNote);
            } else {
                memoField.clear();
            }
        } catch (Exception e) {
            memoField.clear();
            System.err.println("오늘 메모 불러오기 오류: " + e.getMessage());
        }
    }
}

