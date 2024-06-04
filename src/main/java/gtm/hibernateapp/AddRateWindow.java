package gtm.hibernateapp;

import gtm.hibernateapp.entities.Group;
import gtm.hibernateapp.entities.Rate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddRateWindow {
    static String message = "Are you sure you want to continue?";
    public static void display(Group selectedGroup, Consumer<Rate> callback) {
        Stage window = new Stage();
        window.setTitle("Add rate");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(330);
        window.setHeight(320);
        window.setResizable(false);

        window.setOnCloseRequest(windowEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message))
                windowEvent.consume();
        });

        VBox vBox = new VBox();

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);

        Label mark_label = new Label();
        mark_label.setText("Mark:");
        GridPane.setConstraints(mark_label, 0, 0);

        TextField mark_field = new TextField();
        mark_field.setPromptText("eg. 5.95");
        mark_field.setMinWidth(200);
        GridPane.setConstraints(mark_field, 1, 0);

        Label date_label = new Label();
        date_label.setText("Date (dd-mm-yyyy):");
        GridPane.setConstraints(date_label, 0, 1);

        TextField date_field = new TextField();
        date_field.setPromptText("eg. 12.04.2024");
        GridPane.setConstraints(date_field, 1, 1);

        Label comment_label = new Label();
        comment_label.setText("Comment:");
        GridPane.setConstraints(comment_label, 0, 2);

        TextField comment_field = new TextField();
        comment_field.setPromptText("eg. Excellent");
        GridPane.setConstraints(comment_field, 1, 2);

        Button add_button = new Button();
        add_button.setText("Add rate");
        add_button.setOnAction(actionEvent -> {

            if (mark_field.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered mark is invalid!");
                return;
            }
            else {
                try {
                    double mark = Double.parseDouble(mark_field.getText());
                    if (mark < 0 || mark > 6) {
                        Toolkit.getDefaultToolkit().beep();
                        AlertWindow.display("Entered mark is invalid!");
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    AlertWindow.display("Entered mark is invalid!");
                    return;
                }
            }

            if (date_field.getText().isEmpty() || LocalDate.parse(date_field.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy")).isAfter(LocalDate.now())) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered date is invalid!");
                return;
            }

            if (comment_field.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered comment is invalid!");
                return;
            }


            Double mark = Double.parseDouble(mark_field.getText());
            LocalDate date = LocalDate.parse(date_field.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String comment = comment_field.getText();

            Rate rate = new Rate();
            rate.setValue(mark);
            rate.setDate(date);
            rate.setComment(comment);
            rate.setGroup(selectedGroup);
            callback.accept(rate);
            window.close();
        });

        Button cancel_button = new Button();
        cancel_button.setText("Cancel");
        cancel_button.getStyleClass().add("no_button");
        cancel_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (ConfirmationWindow.display(message))
                window.close();
        });

        HBox h_layout = new HBox(30);
        h_layout.getChildren().addAll(add_button, cancel_button);
        h_layout.setAlignment(Pos.CENTER);

        gridPane.getChildren().addAll(mark_label, mark_field, date_label, date_field, comment_label, comment_field);

        vBox.getChildren().addAll(gridPane, h_layout);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Objects.requireNonNull(AddTeacherWindow.class.getResource("style.css")).toExternalForm());
        window.setScene(scene);
        window.showAndWait();
    }
}
