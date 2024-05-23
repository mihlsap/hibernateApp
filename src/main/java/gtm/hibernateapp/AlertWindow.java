package gtm.hibernateapp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Objects;

public class AlertWindow {
    public static void display(String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Warning!");
        window.setWidth(300);
        window.setHeight(100);
        window.setResizable(false);

        window.setOnCloseRequest(windowEvent -> {
            Toolkit.getDefaultToolkit().beep();
            windowEvent.consume();
        });

        Image icon = new Image("C:\\Users\\Dell\\Desktop\\stuff\\studia\\semestr 4\\Programowanie aplikacji użytkowych\\fxapp\\src\\main\\resources\\org\\fxapp\\warning_sign.png");
        window.getIcons().add(icon);

        Label label = new Label();
        label.setText(message);

        Button close_button = new Button();
        close_button.setText("OK");
        close_button.setOnAction(actionEvent -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, close_button);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(Objects.requireNonNull(AlertWindow.class.getResource("style.css")).toExternalForm());
        window.setScene(scene);
        window.showAndWait();
    }
}
