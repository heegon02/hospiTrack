package login;

import javafx.application.Application;
import javafx.stage.Stage;

public class Start extends Application {
    @Override
    public void start(Stage primaryStage) {
        new LoginMain(); // 여기서 LoginMain() 호출
    }

    public static void main(String[] args) {
        launch(args);
    }
}