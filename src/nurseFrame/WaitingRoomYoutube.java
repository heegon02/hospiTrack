package nurseFrame;

import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WaitingRoomYoutube extends StackPane {

    public WaitingRoomYoutube() {

        // 테두리+둥근모서리 스타일 적용할 VBox (패널 역할)
        VBox borderPane = new VBox();
        borderPane.setPadding(new Insets(10)); // 내부 여백 (WebView와 테두리 사이 공간)
        borderPane.setStyle(
            "-fx-background-color: white;" +    // 배경색
            "-fx-border-color: lightgray;" +      // 테두리 색 (파란계열)
            "-fx-border-radius: 15;" +          // 둥근 테두리 radius
            "-fx-background-radius: 15;"        // 배경색도 테두리에 맞춰 둥글게
        );

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://www.youtube.com");

        webView.setMaxWidth(Double.MAX_VALUE);
        webView.setMaxHeight(Double.MAX_VALUE);

        borderPane.getChildren().add(webView);
        
        borderPane.setMaxWidth(Double.MAX_VALUE);
        borderPane.setMaxHeight(Double.MAX_VALUE);

        // 최종적으로 이 StackPane에 둥근 테두리 패널을 추가
        this.getChildren().add(borderPane);
    }
}
