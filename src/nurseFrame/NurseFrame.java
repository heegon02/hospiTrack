//간호사 메인 프레임
package nurseFrame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NurseFrame extends Application {

	private VBox superMainContainer;
	private HBox mainContainer0;
	private HBox mainContainer;
	private VBox container0;
    private VBox container1;
    private VBox container2;
    private VBox container3;
    private VBox container4;

    @Override
    public void start(Stage stage) {
    	
    	//슈퍼메인컨테이너 (세로로 배치)
    	superMainContainer = new VBox(0);
    	
    	
    	//메인컨테이너0 (가로로 배치) 상단 프로그램 이름
    	mainContainer0 = new HBox(0);
    	Label programTitle = new Label("      hospiTrack");
    	programTitle.setStyle(
    		"-fx-font-family: '고딕';"+
    		"-fx-font-size: 19px;" + 
    		"-fx-min-height: 50px;" + 
    		"-fx-min-width: 1600px;" 
    	);
    	
    	mainContainer0.getChildren().addAll(programTitle);
    	
    	//메인컨테이너 (가로로 배치)
    	mainContainer = new HBox(10);
    	
    	container0 = new VBox(2);
    	container0.setPadding(new Insets(15));
    	
    	Sidebar sidebar =new Sidebar();
    	
    	container0.getChildren().addAll(sidebar);
    
    	
    	//컨테이너1
        container1 = new VBox(10); // 10px 간격
        container1.setPadding(new Insets(15));

        NewPatientAddButton newPatientPanel = new NewPatientAddButton();
        SearchPatient searchPanel = new SearchPatient();
        PatientInfoPanel infoPanel = new PatientInfoPanel();

        searchPanel.setOnPatientSelected(patientId -> {
            infoPanel.loadPatientInfo(patientId);
        });

        container1.getChildren().addAll(newPatientPanel, searchPanel, infoPanel);
        
        
        //컨테이너2
        container2 = new VBox(10);
        container2.setPadding(new Insets(15));
        
        Memo memoPanel = new Memo();
        PrescriptionDetail DetailPanel = new PrescriptionDetail();
        
        container2.getChildren().addAll(memoPanel, DetailPanel);
        
        
        
        //컨테이너3
        container3 = new VBox(10);
        container3.setPadding(new Insets(15));
        container3.setMinSize(300, 850);
        
        PatientList pl = new PatientList();
        
        infoPanel.setPatientList(pl);
        container3.getChildren().addAll(pl);
        
        
        
        //컨테이너4
        container4 = new VBox(10);
        container4.setPadding(new Insets(15));
        container4.setMinSize(400, 850);
        
        MedicalRecords mr = new MedicalRecords();
        
        container4.getChildren().addAll(mr);
        
        
        //메인컨테이너에 컨테이너 싹 다 넣기. 
        mainContainer.getChildren().addAll(container0, container1, container2, container3, container4);
        
        superMainContainer.getChildren().addAll(mainContainer0, mainContainer);
        

        //전체 프레임에 넣기.
        Scene scene = new Scene(superMainContainer, 1600, 900);
        stage.setScene(scene);
        stage.setTitle("간호사 전용 사용화면입니다.");
        stage.show();
    }

    public void addItem(javafx.scene.Node item) {
        container1.getChildren().add(item);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
