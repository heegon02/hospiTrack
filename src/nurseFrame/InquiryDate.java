package nurseFrame;

import application.AppointmentDAO;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class InquiryDate extends VBox {

    private DatePicker datePicker;
    private Button searchBtn;
    private TableView<AppointmentRow> tableView;
    
    public interface AppointmentSelectedListener {
    	void onAppointmentSelected(int appointmentId);
    }
    private AppointmentSelectedListener listener;
    public void setOnAppointmentSelected(AppointmentSelectedListener listener) {
    	this.listener = listener;
    }

    public InquiryDate() {
        this.setSpacing(10);
        this.setPadding(new Insets(20));

        // 제목
        Label titleLabel = new Label("날짜별 환자 조회");
        titleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;"
        );

        // 날짜 선택 + 버튼
        datePicker = new DatePicker();
        datePicker.setPromptText("조회할 날짜 선택");

        searchBtn = new Button("조회");

        HBox searchBox = new HBox(10, datePicker, searchBtn);
        searchBox.setSpacing(10);

        // 👉 검색 결과 표시용 TableView
        tableView = new TableView<>();
        tableView.setMinHeight(690);
        tableView.setPrefHeight(690);
        tableView.setMaxHeight(690);

        // 컬럼1: 이름
        TableColumn<AppointmentRow, String> nameCol = new TableColumn<>("이름");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        // 컬럼2: 진료 시간
        TableColumn<AppointmentRow, String> timeCol = new TableColumn<>("진료 시간");
        timeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTime()));

        tableView.getColumns().addAll(nameCol, timeCol);
        
        tableView.setRowFactory(tv -> {
            TableRow<AppointmentRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    AppointmentRow selected = row.getItem();
                    
                    // 👉 AppointmentRow 에 patientId 도 저장하도록 바꿔야 함
                    if (listener != null) {
                        listener.onAppointmentSelected(selected.getAppointmentId());
                    }
                }
            });
            return row;
        });

        // 조회 버튼 이벤트
        searchBtn.setOnAction(e -> searchAppointments());

        // 이 클래스의 스타일 (SearchPatient와 동일)
        this.setStyle(
            "-fx-background-color: #ffffff;" +   // 배경색
            "-fx-background-radius: 15;" +      // 배경 둥글게
            "-fx-border-radius: 15;" +          // 테두리 둥글게
            "-fx-border-color: lightgray;"      // 테두리 색
        );

        this.getChildren().addAll(titleLabel, searchBox, tableView);
    }

    // DB 조회
    private void searchAppointments() {
        if (datePicker.getValue() == null) return;

        String date = datePicker.getValue().toString();
        List<AppointmentRow> results = AppointmentDAO.getPatientNamesAndTimesByDate2(date);

        tableView.getItems().clear();
        tableView.getItems().addAll(results);
    }

    // 내부 데이터 클래스 (TableView에 표시할 Row)
    public static class AppointmentRow {
    	private final int appointmentId;
    	private final int patientId;
        private final String name;
        private final String time;

        public AppointmentRow(int appointmentId, int patientId, String name, String time) {
        	this.appointmentId = appointmentId;
        	this.patientId = patientId;
            this.name = name;
            this.time = time;
        }

        public int getAppointmentId() {return appointmentId;}
        public int getPatientId() { return patientId; }
        public String getName() { return name; }
        public String getTime() { return time; }
    }
}
