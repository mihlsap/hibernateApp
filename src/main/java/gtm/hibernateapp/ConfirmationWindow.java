package gtm.hibernateapp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Objects;

public class ConfirmationWindow {

    static Boolean choice;
    public static Boolean display(String message) {
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

        Button yes_button = new Button();
        yes_button.setText("Yes");
        yes_button.setOnAction(actionEvent -> {
            choice = true;
            window.close();
        });

        Button no_button = new Button();
        no_button.setText("No");
        no_button.getStyleClass().add("no_button");
        no_button.setOnAction(actionEvent -> {
            choice = false;
            window.close();
        });

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(yes_button, no_button);
        hBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(label, hBox);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);

        scene.getStylesheets().add(Objects.requireNonNull(ConfirmationWindow.class.getResource("style.css")).toExternalForm());

        window.setScene(scene);
        window.showAndWait();
        return choice;
    }
}
