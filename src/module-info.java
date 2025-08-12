/**
 * 
 */
/**
 * 
 */
module hospiTrack {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.web;
	
	requires java.sql;
	requires java.desktop;
	
	requires itextpdf;
	
	exports application;
	exports nurseFrame;
	exports login;
	
	opens doctorFrame to javafx.fxml;   // FXML에서 Controller 접근 가능
    exports doctorFrame;               // 다른 모듈에서 사용 가능
}