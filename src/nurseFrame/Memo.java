//간호사 메모 작성 공간

package nurseFrame;

import application.NursingRecordDAO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Memo extends VBox {
	
	private Integer patientId; //환자 id 저장.
	
	public Memo() {
		
		Label titleLabel = new Label("간호사 메모 작성");
		titleLabel.setStyle(
			"-fx-font-size: 12px;" + 
		    "-fx-font-weight: bold;"
		);
		
		Button submitButton = new Button("전송");
		
		HBox upperHBox = new HBox(10, titleLabel, submitButton);
		
		TextField memoField = new TextField();
		memoField.setPromptText("환자의 특이사항을 입력하세요.");
		memoField.setPrefSize(280, 150);
		
		VBox mainVBox = new VBox (10,upperHBox, memoField);

		//이 클래스의 영역 스타일 지정.
		this.setStyle(
			"-fx-background-color: #ffffff;"+ //배경색 지정
			"-fx-background-radius: 15;" + //배경 둥글게
			"-fx-border-radius: 15;" + //테두리도 같이 둥글게
			"-fx-border-color: lightgray;" //테두리 색
		); 
		this.setAlignment(Pos.TOP_LEFT);
		this.setPadding(new Insets(10));
		
		this.getChildren().add(mainVBox);
		
		// 전송 버튼 이벤트 처리
		submitButton.setOnAction(e -> {
		    if (patientId == null) {
		        System.out.println("⚠ 환자가 선택되지 않았습니다.");
		        return;
		    }

		    String note = memoField.getText().trim();
		    if (note.isEmpty()) {
		        System.out.println("⚠ 메모를 입력하세요.");
		        return;
		    }

		    try {
		        // DB 저장
		        NursingRecordDAO.addNursingRecord(patientId, note);

		        // 확인 메시지
		        Alert success = new Alert(Alert.AlertType.INFORMATION);
		        success.setTitle("성공");
		        success.setHeaderText("간호 메모 등록 완료");
		        success.setContentText("환자 ID " + patientId + " 메모가 정상적으로 저장되었습니다.");
		        success.showAndWait();

		        memoField.clear(); // 입력창 초기화
		    } catch (Exception ex) {
		        Alert error = new Alert(Alert.AlertType.ERROR);
		        error.setTitle("DB 오류");
		        error.setHeaderText("메모 저장 실패");
		        error.setContentText(ex.getMessage());
		        error.showAndWait();
		    }
		});
		
		
	}
	
	
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	
	
}
