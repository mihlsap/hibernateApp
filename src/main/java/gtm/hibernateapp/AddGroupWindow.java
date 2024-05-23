package gtm.hibernateapp;

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
import java.util.Objects;
import java.util.function.Consumer;

public class AddGroupWindow {
    static String message = "Are you sure you want to continue?";
    public static void display(Consumer<Group> callback) {
        Stage window = new Stage();
        window.setTitle("Add group");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(360);
        window.setHeight(160);
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

        Label name_label = new Label();
        name_label.setText("Group's name:");
        GridPane.setConstraints(name_label, 0, 0);

        TextField name_field = new TextField();
        name_field.setPromptText("eg. Group 1");
        GridPane.setConstraints(name_field, 1, 0);

        Label occupancy_label = new Label();
        occupancy_label.setText("Maximum group's occupancy:");
        GridPane.setConstraints(occupancy_label, 0, 2);

        TextField occupancy_field = new TextField();
        occupancy_field.setPromptText("eg. 5");
        GridPane.setConstraints(occupancy_field, 1, 2);

        Button add_button = new Button();
        add_button.setText("Add group");
        add_button.setOnAction(actionEvent -> {
            String name_of_group = name_field.getText();
            int max_occupancy;
            try {
                max_occupancy = Integer.parseInt(occupancy_field.getText());
            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered maximum occupancy is invalid!");
                return;
            }

            if (name_of_group.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered name is invalid!");
                return;
            }

            Group group = new Group(name_of_group, max_occupancy);

            callback.accept(group);

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

        gridPane.getChildren().addAll(name_label, name_field, occupancy_label, occupancy_field);

        vBox.getChildren().addAll(gridPane, h_layout);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Objects.requireNonNull(AddGroupWindow.class.getResource("style.css")).toExternalForm());
        window.setScene(scene);
        window.showAndWait();
    }
}