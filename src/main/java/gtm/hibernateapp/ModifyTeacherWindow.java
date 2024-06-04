package gtm.hibernateapp;

import gtm.hibernateapp.entities.Teacher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gtm.hibernateapp.TeacherCondition.*;

public class ModifyTeacherWindow {
    static String message = "Are you sure you want to continue?";
    public static void display(Teacher selectedTeacher, Consumer<Teacher> callback) {
        Stage window = new Stage();
        window.setTitle("Modify teacher's data");
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

        Label name_label = new Label();
        name_label.setText("Name:");
        GridPane.setConstraints(name_label, 0, 0);

        TextField name_field = new TextField(selectedTeacher.getName());
        name_field.setMinWidth(200);
        GridPane.setConstraints(name_field, 1, 0);

        Label surname_label = new Label();
        surname_label.setText("Surname:");
        GridPane.setConstraints(surname_label, 0, 1);

        TextField surname_field = new TextField(selectedTeacher.getSurname());
        GridPane.setConstraints(surname_field, 1, 1);

        Label condition_label = new Label();
        condition_label.setText("Condition:");
        GridPane.setConstraints(condition_label, 0, 2);

        ComboBox<String> condition_field = new ComboBox<>();
        condition_field.getItems().addAll(PRESENT.toString(), ABSENT.toString(), ILL.toString(), DELEGATION.toString());
        condition_field.setValue(selectedTeacher.getCondition().toString());
        GridPane.setConstraints(condition_field, 1, 2);

        Label birth_year_label = new Label();
        birth_year_label.setText("Birth year:");
        GridPane.setConstraints(birth_year_label, 0, 3);

        TextField birth_year_field = new TextField(Integer.toString(selectedTeacher.getBirth_year()));
        GridPane.setConstraints(birth_year_field, 1, 3);

        Label salary_label = new Label();
        salary_label.setText("Salary:");
        GridPane.setConstraints(salary_label, 0, 4);

        TextField salary_field = new TextField(Double.toString(selectedTeacher.getSalary()));
        GridPane.setConstraints(salary_field, 1, 4);

        Label phone_number_label = new Label();
        phone_number_label.setText("Phone number:");
        GridPane.setConstraints(phone_number_label, 0, 5);

        TextField phone_number_field = new TextField(selectedTeacher.getPhone_number());
        GridPane.setConstraints(phone_number_field, 1, 5);

        Label email_label = new Label();
        email_label.setText("Email:");
        GridPane.setConstraints(email_label, 0, 6);

        TextField email_field = new TextField(selectedTeacher.getEmail());
        GridPane.setConstraints(email_field, 1, 6);

        Button add_button = new Button();
        add_button.setText("Modify teacher's data");
        add_button.setOnAction(actionEvent -> {

            if (name_field.getText().isEmpty() || !name_field.getText().matches("^[A-Z][a-z]*$")) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered name is invalid!");
                return;
            }

            if (surname_field.getText().isEmpty() || !surname_field.getText().matches("^[A-Z][a-z]*$")) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered surname is invalid!");
                return;
            }

            if (birth_year_field.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered birth year is invalid!");
                return;
            }
            else {
                try {
                    int birth_year = Integer.parseInt(birth_year_field.getText());
                    if (birth_year < 1900 || birth_year > 2024) {
                        Toolkit.getDefaultToolkit().beep();
                        AlertWindow.display("Entered birth year is invalid!");
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    AlertWindow.display("Entered birth year is invalid!");
                    return;
                }
            }

            if (salary_field.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered salary is invalid!");
                return;
            }
            else {
                try {
                    double salary = Double.parseDouble(salary_field.getText());
                    if (salary < 0) {
                        Toolkit.getDefaultToolkit().beep();
                        AlertWindow.display("Entered salary is invalid!");
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    AlertWindow.display("Entered salary is invalid!");
                    return;
                }
            }

            if (phone_number_field.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered phone number is invalid!");
                return;
            }
            else {
                String pattern = "^(\\+\\d{1,3}( )?)?((\\d{3} ?){2}\\d{3}|\\d{9,11})$";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(phone_number_field.getText());

                if (!m.matches()) {
                    Toolkit.getDefaultToolkit().beep();
                    AlertWindow.display("Entered phone number is invalid!");
                    return;
                }
            }

            if (email_field.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Entered email is invalid!");
                return;
            }
            else {
                String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(email_field.getText());

                if (!m.matches()) {
                    Toolkit.getDefaultToolkit().beep();
                    AlertWindow.display("Entered email is invalid!");
                    return;
                }
            }

            String name = name_field.getText();
            String surname = surname_field.getText();
            TeacherCondition condition = switch (condition_field.getValue()) {
                case "Present" -> PRESENT;
                case "Absent" -> ABSENT;
                case "Ill" -> ILL;
                case "Delegation" -> DELEGATION;
                default -> null;
            };

            int birth_year = Integer.parseInt(birth_year_field.getText());
            double salary = Double.parseDouble(salary_field.getText());
            String phone_number = phone_number_field.getText();
            String email = email_field.getText();

            Teacher teacher = new Teacher();
            teacher.setName(name);
            teacher.setSurname(surname);
            teacher.setCondition(condition);
            teacher.setBirth_year(birth_year);
            teacher.setPhone_number(phone_number);
            teacher.setEmail(email);
            teacher.setSalary(salary);

            callback.accept(teacher);
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

        gridPane.getChildren().addAll(name_label, name_field, surname_label, surname_field, condition_label,
                condition_field, birth_year_label, birth_year_field, salary_label, salary_field, phone_number_label,
                phone_number_field, email_label, email_field);

        vBox.getChildren().addAll(gridPane, h_layout);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Objects.requireNonNull(ModifyTeacherWindow.class.getResource("style.css")).toExternalForm());
        window.setScene(scene);
        window.showAndWait();
    }
}
