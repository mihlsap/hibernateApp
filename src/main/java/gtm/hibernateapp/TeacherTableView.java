package gtm.hibernateapp;

import gtm.hibernateapp.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class TeacherTableView {

    Button add_teacher_button, delete_teacher_button, modify_teachers_data_button;
    ObservableList<Teacher> teachers = FXCollections.observableArrayList();
    TableView<Teacher> teacherTableView = new TableView<>(teachers);

    public void getTeachersList (EntityManagerFactory entityManagerFactory, Group selectedGroup) {
        teachers.clear();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        TypedQuery<Teacher> teacherTypedQuery = entityManager.createQuery("select t from Teacher t where t.group.id = :number", Teacher.class);
        teacherTypedQuery.setParameter("number", selectedGroup.getId());
        List<Teacher> teachersQueryResult = teacherTypedQuery.getResultList();
        entityManager.close();

        teachers.addAll(teachersQueryResult);
    }

    public void display(EntityManagerFactory entityManagerFactory, Group selectedGroup, Stage window) {
//        Stage window = new Stage();
        window.setTitle("Teachers in " + selectedGroup.getName_of_group());
        window.setResizable(false);
        window.setFullScreen(false);

        String message = "Are you sure you want to continue?";

        window.setOnCloseRequest(windowEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message))
                windowEvent.consume();
        });

        BorderPane borderPane = new BorderPane();

        getTeachersList(entityManagerFactory, selectedGroup);

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


        add_teacher_button = new Button();
        add_teacher_button.setText("Add teacher");
        add_teacher_button.getStyleClass().add("teachers_buttons");
//        add_teacher_button.setDisable(true);
        add_teacher_button.fire();

        delete_teacher_button = new Button();
        delete_teacher_button.setText("Delete teacher");
        delete_teacher_button.getStyleClass().add("teachers_buttons");
        delete_teacher_button.setDisable(true);

        modify_teachers_data_button = new Button();
        modify_teachers_data_button.setText("Modify teacher's data");
        modify_teachers_data_button.getStyleClass().add("teachers_buttons");
        modify_teachers_data_button.setDisable(true);

        teacherTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            delete_teacher_button.setDisable(newValue == null);
            modify_teachers_data_button.setDisable(newValue == null);
        });

        add_teacher_button.setOnAction(actionEvent -> {
            if (teachers.size() == selectedGroup.getMax_occupancy()) {
                Toolkit.getDefaultToolkit().beep();
                AlertWindow.display("Maximum occupancy of a group has been reached!");
                actionEvent.consume();
                return;
            }
            AddTeacherWindow.display(selectedGroup, teacher -> {
                EntityManager entityManager = entityManagerFactory.createEntityManager();

                entityManager.getTransaction().begin();
                entityManager.persist(teacher);
                entityManager.getTransaction().commit();
                entityManager.close();

                getTeachersList(entityManagerFactory, selectedGroup);
                teacherTableView.refresh();
            });
        });

        delete_teacher_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message)) {
                actionEvent.consume();
                return;
            }

            Teacher selectedTeacher = teacherTableView.getSelectionModel().getSelectedItem();
            if (selectedTeacher != null) {
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                entityManager.getTransaction().begin();
                entityManager.remove(entityManager.find(Teacher.class, selectedTeacher.getId()));
                entityManager.getTransaction().commit();
                entityManager.close();

                getTeachersList(entityManagerFactory, selectedGroup);
                teacherTableView.refresh();
            }
        });

        modify_teachers_data_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message)) {
                actionEvent.consume();
                return;
            }

            Teacher selectedTeacher = teacherTableView.getSelectionModel().getSelectedItem();

            ModifyTeacherWindow.display(selectedTeacher,teacher -> {
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                entityManager.getTransaction().begin();

                Teacher managedTeacher = entityManager.find(Teacher.class, selectedTeacher.getId());

                teacher.setId(managedTeacher.getId());
                teacher.setGroup(managedTeacher.getGroup());
                entityManager.merge(teacher);

                entityManager.getTransaction().commit();
                entityManager.close();

                getTeachersList(entityManagerFactory, selectedGroup);
                teacherTableView.refresh();
            });
        });

        borderPane.setPadding(new Insets(10, 10, 10, 10));

        VBox vBox = new VBox(10);

        HBox buttonsHBox = new HBox(20);
        buttonsHBox.getChildren().addAll(add_teacher_button, delete_teacher_button, modify_teachers_data_button);

        HBox searchHBox = new HBox(20);
        Label searchLabel = new Label("Filter teacher's table contents:");

        TextField filterField = new TextField();
        filterField.setPromptText("eg. John");
//        filterField.setDisable(true);

//TODO
// fix filtering conditions in teacherTableView

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (teachers.isEmpty())
                filterField.setDisable(true);
            else
                filterField.setDisable(false);

            FilteredList<Teacher> filteredData = new FilteredList<>(teachers, p -> true);
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

        Scene scene = new Scene(borderPane);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        scene.getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (!teacherTableView.getBoundsInParent().contains(mouseEvent.getX(), mouseEvent.getY())) {
                teacherTableView.getSelectionModel().clearSelection();
                delete_teacher_button.setDisable(true);
            }
        });

        window.setScene(scene);
        window.show();
    }
}
