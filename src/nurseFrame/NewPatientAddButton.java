//간호사 메인 프레임

package nurseFrame;

import application.*;

import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class NewPatientAddButton extends HBox {
	
    private Button newPatientAddButton;
    private SearchPatient searchPatient;
    
    public void setSearchPatient(SearchPatient searchPatient) {
    	this.searchPatient = searchPatient;
    }

    public NewPatientAddButton() {
        // 버튼 생성
        newPatientAddButton = new Button("신규 환자 등록");
        newPatientAddButton.setPadding(new Insets(15, 10, 15, 10));
        
        newPatientAddButton.setMaxWidth(Double.MAX_VALUE);
        newPatientAddButton.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(newPatientAddButton, Priority.ALWAYS);
        
        //버튼 디자인 변경
        newPatientAddButton.setStyle(
        	"-fx-font-bold: bold; "	 +
        	"-fx-background-color: transparent;" + 
        	"-fx-border-color: transparent;" + 
        	"-fx-focus-color: transparent;" + 
        	"-fx-faint-focus-color: transparent;"
        );

        // 버튼 이벤트
        newPatientAddButton.setOnAction(e -> {
            // 신규 등록 창(Stage)
            Stage dialog = new Stage();
            dialog.setTitle("신규 환자 등록");

            // 입력 필드
            TextField nameField = new TextField();
            TextField weightField = new TextField();
            TextField heightField = new TextField();
            ComboBox<String> bloodTypeBox = new ComboBox<>();
            bloodTypeBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
            DatePicker birthDatePicker = new DatePicker(); //YYYY. MM. DD. 형태로 입력하면 텍스트 입력도 가능.
            TextField phoneField = new TextField();
            ComboBox<String> genderBox = new ComboBox<>();
            genderBox.getItems().addAll("M", "F");
            TextField addressField = new TextField();

            Button saveBtn = new Button("저장");
            saveBtn.setPrefSize(85, 30);
            Button cancelBtn = new Button("취소");
            cancelBtn.setPrefSize(85, 30);

            // 저장 버튼 이벤트
            saveBtn.setOnAction(ev -> {
                try {
                    String name = nameField.getText();
                    double weight = Double.parseDouble(weightField.getText());
                    double height = Double.parseDouble(heightField.getText());
                    String bloodType = bloodTypeBox.getValue();
                    LocalDate birthDate = birthDatePicker.getValue();
                    String phone = phoneField.getText();
                    String gender = genderBox.getValue();
                    String address = addressField.getText();

                    Patient newPatient = new Patient(name, weight, height, bloodType, birthDate, phone, gender, address);

                    // DB 저장
                    PatientDAO.addPatient(newPatient);
                    
                    if (searchPatient != null) {
                    	searchPatient.setNameField(name);
                    	searchPatient.searchPatients();
                    }

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("성공");
                    success.setHeaderText("신규 환자 등록 완료");
                    success.setContentText(name + " 환자가 정상적으로 등록되었습니다.");
                    success.showAndWait();

                    dialog.close();
                } catch (Exception ex) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("에러");
                    error.setHeaderText("등록 실패");
                    error.setContentText("입력 값 확인 또는 DB 오류: " + ex.getMessage());
                    error.showAndWait();
                }
            });

            cancelBtn.setOnAction(ev -> dialog.close());

            // 👉 GridPane 레이아웃 구성
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            grid.add(new Label("이름"), 0, 0); grid.add(nameField, 1, 0);
            grid.add(new Label("몸무게"), 0, 1); grid.add(weightField, 1, 1);
            grid.add(new Label("키"), 0, 2); grid.add(heightField, 1, 2);
            grid.add(new Label("혈액형"), 0, 3); grid.add(bloodTypeBox, 1, 3);
            grid.add(new Label("생년월일"), 0, 4); grid.add(birthDatePicker, 1, 4);
            grid.add(new Label("전화번호"), 0, 5); grid.add(phoneField, 1, 5);
            grid.add(new Label("성별"), 0, 6); grid.add(genderBox, 1, 6);
            grid.add(new Label("주소"), 0, 7); grid.add(addressField, 1, 7);

            HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
            grid.add(buttonBox, 1, 8);

            // 👉 Scene 세팅
            Scene scene = new Scene(grid, 300, 320);
            dialog.setScene(scene);

            // 창 띄우기
            dialog.show();
        });
        
        //이 클래스의 영역 스타일 지정.
    	this.setStyle(
    		"-fx-background-color: #ffffff;"+ //배경색 지정
    		"-fx-background-radius: 15;" + //배경 둥글게
    		"-fx-border-radius: 15;" + //테두리도 같이 둥글게
    		"-fx-border-color: lightgray;" //테두리 색
    	); 
    	
        this.getChildren().add(newPatientAddButton); //HBox 레이아웃에 버튼 추가
        this.setAlignment(Pos.CENTER); //내부 아이템을 가운데 정렬
        this.setPadding(Insets.EMPTY);
        this.setSpacing(0);
    }

    public Button getAddButton() {
        return newPatientAddButton;
    }
}
