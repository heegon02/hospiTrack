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
		Label titleLabel = new Label("HospiTrack\n\n\n");
		titleLabel.setStyle(
			"-fx-font-family: '돋움';" + 
			"-fx-font-size: 30px;"	
		);
		HBox titleLine = new HBox();
		titleLine.setAlignment(Pos.CENTER);
		titleLine.getChildren().add(titleLabel);
		
		//모드 변경 버튼
		HBox modeLine = new HBox(20);
		IOSToggleSwitch modeButton = new IOSToggleSwitch();
		modeLine.setAlignment(Pos.CENTER);
		modeLine.getChildren().add(modeButton);
		
		//아이디라인
		HBox idLine = new HBox(20);
		idLine.setAlignment(Pos.CENTER);
		Label idLabel = new Label(" I D");
		idLabel.setStyle(
			"-fx-font-family: 'Arial';" + 
			"-fx-font-size: 27;"
		);
		TextField idTextField = new TextField();
		idTextField.setStyle(
			"-fx-min-height: 30;" + 
			"-fx-min-width: 100;"
		);
		idLine.getChildren().addAll(idLabel, idTextField);
		
		//패스워드라인
		HBox passwordLine = new HBox(20);
		passwordLine.setAlignment(Pos.CENTER);
		Label passwordLabel = new Label("PW");
		passwordLabel.setStyle(
				"-fx-font-family: 'Arial';" + 
				"-fx-font-size: 27;"
			);
		TextField passwordTextField = new TextField();
		passwordTextField.setStyle(
				"-fx-min-height: 30;" + 
				"-fx-min-width: 100;"
			);
		passwordLine.getChildren().addAll(passwordLabel, passwordTextField);
		
		//로그인버튼
		Button loginButton = new Button("Login");
		loginButton.setStyle(
			    "-fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);" +
			    "-fx-text-fill: white;" +                
			    "-fx-font-size: 14px;" +                
			    "-fx-font-weight: bold;" +               
			    "-fx-background-radius: 20;" +           
			    "-fx-padding: 10 30 10 30;" +            
			    "-fx-cursor: hand;"                     
			);

			// Hover 시 효과
		loginButton.setOnMouseEntered(e -> loginButton.setStyle(
			    "-fx-background-color: linear-gradient(to right, #00f2fe, #4facfe);" + 
			    "-fx-text-fill: white;" +
			    "-fx-font-size: 14px;" +
			    "-fx-font-weight: bold;" +
			    "-fx-background-radius: 20;" +
			    "-fx-padding: 10 30 10 30;" +
			    "-fx-cursor: hand;" +
			    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 2);" 
			));

			// Hover 벗어났을 때 원래대로
		loginButton.setOnMouseExited(e -> loginButton.setStyle(
			    "-fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);" +
			    "-fx-text-fill: white;" +
			    "-fx-font-size: 14px;" +
			    "-fx-font-weight: bold;" +
			    "-fx-background-radius: 20;" +
			    "-fx-padding: 10 30 10 30;" +
			    "-fx-cursor: hand;"
			));
		
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
                        new nurseFrame.NurseFrame(user).start(new Stage());
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
		
		HBox loginLine = new HBox();
		loginLine.setAlignment(Pos.CENTER);
		loginLine.getChildren().add(loginButton);
		
		//직원정보수정 전화번호 라벨
	    Label underLabel = new Label("\n\n직원 정보 수정은 00-000-0000으로 전화주세요");
	    HBox underLine = new HBox();
	    underLine.setAlignment(Pos.CENTER);
	    underLine.getChildren().add(underLabel);
	    
	    
	    // 전체 레이아웃
        VBox root = new VBox(15);
        root.getChildren().addAll(titleLine, modeLine, idLine, passwordLine, loginLine, underLine);
        
        loginUI.setScene(new Scene(root, 300, 400));
        loginUI.show();

	}
	
	
	//간호사, 의사 모드 변경 버튼.
	public class IOSToggleSwitch extends HBox {

	    private final BooleanProperty isDoctorMode = new SimpleBooleanProperty(true);

	    private final Label doctorLabel = new Label("의사");
	    private final Label nurseLabel = new Label("간호사");
	    private final Circle knob = new Circle(18);

	    public IOSToggleSwitch() {
	    	// Hover 시 그림자 효과
	    	this.setOnMouseEntered(e -> {
	    	    this.setStyle(this.getStyle() + 
	    	        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 8, 0, 0, 3);");
	    	});
	    	this.setOnMouseExited(e -> {
	    	    this.setStyle(this.getStyle().replaceAll("-fx-effect:.*?;", ""));
	    	});
	    	
	    	
	        this.setMinSize(90, 40);
	        this.setMaxSize(90, 40);
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
	            this.setStyle(
	            		"-fx-background-color: #87CEEB;" +
	            		"-fx-background-radius: 20;" + 
	            		"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 8, 0, 0, 3);"
	            );
	            

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
	            this.setStyle(
	            		"-fx-background-color: #4cd964;"+ 
	            		"-fx-background-radius: 20;" + 
	            		"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 8, 0, 0, 3);"
	            );

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
