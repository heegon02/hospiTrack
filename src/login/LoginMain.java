package login;

import application.LoginDAO;


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;



public class LoginMain {

	private String mode;
	private String id;
	private String pw;
	
	LoginMain() {
		
		Stage loginUI = new Stage();
		loginUI.setTitle("로그인");
		
		//프로그램 이름
		Label titleLabel = new Label("1인 의사를 위한 병원 내부 환자 데이터 관리 시스템");
		
		//모드 변경 버튼
		IOSToggleSwitch modeButton = new IOSToggleSwitch();
		
		
		//아이디라인
		HBox idLine = new HBox(20);
		Label idLabel = new Label("ID");
		TextField idTextField = new TextField();
		idLine.getChildren().addAll(idLabel, idTextField);
		
		//패스워드라인
		HBox passwordLine = new HBox(20);
		Label passwordLabel = new Label("PW");
		TextField passwordTextField = new TextField();
		passwordLine.getChildren().addAll(passwordLabel, passwordTextField);
		
		//로그인버튼
		Button loginButton = new Button("Login");
		//로그인버튼 이벤트처리 -> id, pw 변수에 저장.
		loginButton.setOnAction(e -> {
            id = idTextField.getText();   
            pw = passwordTextField.getText();
            mode = modeButton.doctorModeProperty().get() ? "DOCTOR" : "NURSE";
            LoginDAO loginDAO = new LoginDAO();  
            LoginDAO.User user = loginDAO.login(id, pw, mode);
            
            if (user != null) {
                System.out.println("로그인 성공: " + user.getName() + " (" + user.getRole() + ")");
                
             // 로그인 창 닫기
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();

                if ("NURSE".equals(user.getRole())) {
                    try {
                        new nurseFrame.NurseFrame().start(new Stage());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if ("DOCTOR".equals(user.getRole())) {
                	try {
                        new doctorFrame.HospitalEMR().start(new Stage());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                
                
            } else {
                System.out.println("로그인 실패: 아이디/비밀번호/역할 확인 필요");
            }
            
            
            
        });
		
		//직원정보수정 전화번호 라벨
	    Label underLabel = new Label("직원 정보 수정은 00-000-0000으로 전화주세요");
	    
	    
	    // 전체 레이아웃
        VBox root = new VBox(15);
        root.getChildren().addAll(titleLabel, modeButton, idLine, passwordLine, loginButton, underLabel);

        loginUI.setScene(new Scene(root, 400, 300));
        loginUI.show();

	}
	
	
	
	


	public class IOSToggleSwitch extends HBox {

	    private final BooleanProperty isDoctorMode = new SimpleBooleanProperty(true);

	    private final Label doctorLabel = new Label("의사");
	    private final Label nurseLabel = new Label("간호사");
	    private final Circle knob = new Circle(18);

	    public IOSToggleSwitch() {
	        this.setMinSize(100, 40);
	        this.setMaxSize(100, 40);
	        this.setStyle("-fx-background-color: #4cd964; -fx-background-radius: 20;");
	        this.setAlignment(Pos.CENTER);

	        knob.setFill(Color.WHITE);

	        // 텍스트 라벨 스타일
	        doctorLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
	        nurseLabel.setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");

	        // 가운데 정렬용 spacer
	        Region spacerLeft = new Region();
	        Region spacerRight = new Region();

	        HBox row = new HBox();
	        row.setPrefSize(100, 40);
	        row.setAlignment(Pos.CENTER);
	        row.setSpacing(10);

	        // 처음에는 왼쪽 knob, 오른쪽 중앙에 "의사"
	        row.getChildren().addAll(spacerLeft, doctorLabel, spacerRight, knob);
	        HBox.setHgrow(spacerLeft, javafx.scene.layout.Priority.ALWAYS);
	        HBox.setHgrow(spacerRight, javafx.scene.layout.Priority.ALWAYS);

	        this.getChildren().add(row);

	        // 클릭 이벤트
	        this.setOnMouseClicked(e -> toggle());
	    }

	    private void toggle() {
	        if (isDoctorMode.get()) {
	            // 간호사 모드
	            isDoctorMode.set(false);
	            this.setStyle("-fx-background-color: #dddddd; -fx-background-radius: 20;");

	            doctorLabel.setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");
	            nurseLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

	            // 텍스트 위치 변경 → 왼쪽 중앙에 "간호사"
	            ((HBox) this.getChildren().get(0)).getChildren().setAll(knob, nurseLabel);

	            TranslateTransition moveRight = new TranslateTransition(Duration.millis(200), knob);
	            moveRight.setToX(0); // HBox 재배치되므로 이동 초기화
	            moveRight.play();
	        } else {
	            // 의사 모드
	            isDoctorMode.set(true);
	            this.setStyle("-fx-background-color: #4cd964; -fx-background-radius: 20;");

	            doctorLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
	            nurseLabel.setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");

	            // 텍스트 위치 변경 → 오른쪽 중앙에 "의사"
	            ((HBox) this.getChildren().get(0)).getChildren().setAll(doctorLabel, knob);

	            TranslateTransition moveLeft = new TranslateTransition(Duration.millis(200), knob);
	            moveLeft.setToX(0); 
	            moveLeft.play();
	        }
	    }

	    public BooleanProperty doctorModeProperty() {
	        return isDoctorMode;
	    }
	}

	
	
	
	
	
	
	
	
}
