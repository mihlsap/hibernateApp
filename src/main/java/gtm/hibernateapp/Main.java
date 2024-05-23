package gtm.hibernateapp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Objects;

public class Main extends Application {

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistence");

    EntityManager entityManager = entityManagerFactory.createEntityManager();

    Button add_group_button, add_teacher_button, delete_group_button, delete_teacher_button, modify_group_data_button,
            modify_teachers_data_button;
    Stage window;
    Scene scene;
    ObservableList<Group> groups = FXCollections.observableArrayList();
    TableView<Group> groupTableView = new TableView<>(groups);
    TableView<Teacher> teacherTableView;

    public static void main(String[] args){ launch(); }

    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("Teacher Management App");
        window.setResizable(false);
        window.setFullScreen(false);



        entityManager.getTransaction().begin();

        entityManager.getTransaction().commit();

        entityManager.close();


        String message = "Are you sure you want to continue?";

        window.setOnCloseRequest(windowEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message))
                windowEvent.consume();
        });

        BorderPane borderPane = new BorderPane();

        groupTableView = new TableView<>(groups);

        TableColumn<Group, String> group_name_column = new TableColumn<>("Name");
        group_name_column.setMinWidth(120);
        group_name_column.setCellValueFactory(new PropertyValueFactory<>("name_of_group"));

        TableColumn<Group, String> occupancy_column = new TableColumn<>("Occupancy");
        occupancy_column.setMinWidth(120);
        occupancy_column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOccupancy()));

        TableColumn<Group, Integer> maximum_occupancy_column = new TableColumn<>("Maximum occupancy");
        maximum_occupancy_column.setMinWidth(150);
        maximum_occupancy_column.setCellValueFactory(new PropertyValueFactory<>("max_occupancy"));
        groupTableView.getColumns().addAll(group_name_column, occupancy_column, maximum_occupancy_column);

        groupTableView.setItems(groups);

        teacherTableView = new TableView<>();

        TableColumn<Teacher, String> name_column = new TableColumn<>("Name");
        name_column.setMinWidth(120);
        name_column.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Teacher, String> surname_column = new TableColumn<>("Surname");
        surname_column.setMinWidth(120);
        surname_column.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Teacher, TeacherCondition> condition_column = new TableColumn<>("Condition");
        condition_column.setMinWidth(120);
        condition_column.setCellValueFactory(new PropertyValueFactory<>("condition"));

        TableColumn<Teacher, Integer> birth_year_column = new TableColumn<>("Birth year");
        birth_year_column.setMinWidth(120);
        birth_year_column.setCellValueFactory(new PropertyValueFactory<>("birth_year"));

        TableColumn<Teacher, Double> salary_column = new TableColumn<>("Salary");
        salary_column.setMinWidth(120);
        salary_column.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<Teacher, String> phone_number_column = new TableColumn<>("Phone number");
        phone_number_column.setMinWidth(120);
        phone_number_column.setCellValueFactory(new PropertyValueFactory<>("phone_number"));

        TableColumn<Teacher, String> email_column = new TableColumn<>("Email");
        email_column.setMinWidth(120);
        email_column.setCellValueFactory(new PropertyValueFactory<>("email"));

        teacherTableView.getColumns().addAll(name_column, surname_column, condition_column, birth_year_column,
                salary_column, phone_number_column, email_column);

        add_group_button = new Button();
        add_group_button.setText("Add group");
        add_group_button.getStyleClass().add("groups_buttons");
        add_group_button.setOnAction(actionEvent -> AddGroupWindow.display(group -> groups.add(group)));

        add_teacher_button = new Button();
        add_teacher_button.setText("Add teacher");
        add_teacher_button.getStyleClass().add("teachers_buttons");
        add_teacher_button.setDisable(true);
        add_teacher_button.fire();

        delete_teacher_button = new Button();
        delete_teacher_button.setText("Delete teacher");
        delete_teacher_button.getStyleClass().add("teachers_buttons");
        delete_teacher_button.setDisable(true);

        delete_group_button = new Button();
        delete_group_button.setText("Delete group");
        delete_group_button.getStyleClass().add("groups_buttons");
        delete_group_button.setDisable(true);

        modify_group_data_button = new Button();
        modify_group_data_button.setText("Modify group's data");
        modify_group_data_button.getStyleClass().add("groups_buttons");
        modify_group_data_button.setDisable(true);

        modify_teachers_data_button = new Button();
        modify_teachers_data_button.setText("Modify teacher's data");
        modify_teachers_data_button.getStyleClass().add("teachers_buttons");
        modify_teachers_data_button.setDisable(true);

        groupTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                add_teacher_button.setDisable(false);
                delete_group_button.setDisable(false);
                modify_group_data_button.setDisable(false);
                delete_teacher_button.setDisable(true);
                modify_teachers_data_button.setDisable(true);
                teacherTableView.setItems(newValue.getTeachersList());
            } else {
                add_teacher_button.setDisable(true);
                delete_group_button.setDisable(true);
                modify_group_data_button.setDisable(true);
            }
        });

        teacherTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            delete_teacher_button.setDisable(newValue == null);
            modify_teachers_data_button.setDisable(newValue == null);
        });

        add_teacher_button.setOnAction(actionEvent -> {
            Group selectedGroup = groupTableView.getSelectionModel().getSelectedItem();
            if (selectedGroup.getTeachersList().size() == selectedGroup.getMax_occupancy()) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Maximum occupancy of a group has been reached!");
                actionEvent.consume();
                return;
            }
            AddTeacherWindow.display(teacher -> {
                selectedGroup.addTeacher(teacher);
                groupTableView.refresh();
            });
        });

        delete_teacher_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message)) {
                actionEvent.consume();
                return;
            }

            Group selectedGroup = groupTableView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                Teacher selectedTeacher = teacherTableView.getSelectionModel().getSelectedItem();
                selectedGroup.removeTeacher(selectedTeacher);
                groupTableView.refresh();
            }
        });

        delete_group_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message)) {
                actionEvent.consume();
                return;
            }

            Group selectedGroup = groupTableView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                groups.remove(selectedGroup);
                groupTableView.refresh();
            }
        });

        modify_group_data_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message)) {
                actionEvent.consume();
                return;
            }
            Group selectedGroup = groupTableView.getSelectionModel().getSelectedItem();
            ModifyGroupWindow.display(selectedGroup,group -> {
                groups.remove(selectedGroup);
                group.setTeachersList(selectedGroup.getTeachersList());
                group.calculateOccupancy();
                groups.add(group);
                groupTableView.refresh();
            });
        });

        modify_teachers_data_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message)) {
                actionEvent.consume();
                return;
            }

            Group selectedGroup = groupTableView.getSelectionModel().getSelectedItem();
            Teacher selectedTeacher = teacherTableView.getSelectionModel().getSelectedItem();

            ModifyTeacherWindow.display(selectedTeacher,teacher -> {
                selectedGroup.getTeachersList().remove(selectedTeacher);
                selectedGroup.getTeachersList().add(teacher);
                teacherTableView.refresh();
            });
        });

        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setLeft(groupTableView);

//        Label groupsTableLabel = new Label("Groups table");
//        groupsTableLabel.setFont(Font.font(18));
//        Label teachersTableLabel = new Label("Teachers table");
//        teachersTableLabel.setFont(Font.font(18));
//
//        HBox tablesLabels = new HBox(150);
//        tablesLabels.getChildren().addAll(groupsTableLabel, teachersTableLabel);
//        borderPane.setTop(tablesLabels);

        VBox vBox = new VBox(10);

        HBox buttonsHBox = new HBox(20);
        buttonsHBox.getChildren().addAll(add_group_button, delete_group_button, modify_group_data_button, add_teacher_button,
                delete_teacher_button, modify_teachers_data_button);

        HBox searchHBox = new HBox(20);
        Label searchLabel = new Label("Filter teacher's table contents:");

        javafx.scene.control.TextField filterField = new TextField();
        filterField.setPromptText("eg. John");
//        filterField.setDisable(true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            Group selectedGroup = groupTableView.getSelectionModel().getSelectedItem();
//            if (!selectedGroup.getTeachersList().isEmpty())
//                filterField.setDisable(false);
            ObservableList<Teacher> teacherObservableList = selectedGroup.getTeachersList();
            FilteredList<Teacher> filteredData = new FilteredList<>(teacherObservableList, p -> true);
            filteredData.setPredicate(teacher -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (teacher.getName().toLowerCase().contains(lowerCaseFilter))
                    return true;
                else if (teacher.getSurname().toLowerCase().contains(lowerCaseFilter))
                    return true;
                else if (teacher.getCondition().toString().contains(lowerCaseFilter))
                    return true;
                else if (teacher.getEmail().contains(lowerCaseFilter))
                    return true;
                else if (Double.toString(teacher.getSalary()).contains(lowerCaseFilter))
                    return true;
                else if (Integer.toString(teacher.getBirth_year()).contains(lowerCaseFilter))
                    return true;
                else if (teacher.getEmail().contains(lowerCaseFilter))
                    return true;
                else return teacher.getPhone_number().contains(lowerCaseFilter);
            });
            SortedList<Teacher> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(teacherTableView.comparatorProperty());

            teacherTableView.setItems(sortedData);
        });

        searchHBox.getChildren().addAll(searchLabel, filterField);

        vBox.getChildren().addAll(buttonsHBox, searchHBox);

        borderPane.setBottom(vBox);

        borderPane.setCenter(teacherTableView);

        scene = new Scene(borderPane);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        scene.getRoot().addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (!teacherTableView.getBoundsInParent().contains(mouseEvent.getX(), mouseEvent.getY())) {
                teacherTableView.getSelectionModel().clearSelection();
                delete_teacher_button.setDisable(true);
            }
        });

        window.setScene(scene);
        window.show();
    }
}
