package gtm.hibernateapp;

import gtm.hibernateapp.entities.*;
import gtm.hibernateapp.persistence.CustomPersistenceUnitInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Main extends Application {

    EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new
            CustomPersistenceUnitInfo(), new HashMap<>());

    Button add_group_button, delete_group_button, modify_group_data_button, export_data_button;
    Stage window;
    Scene scene;
    ObservableList<Group> groups = FXCollections.observableArrayList();
    TableView<Group> groupTableView = new TableView<>(groups);

    public void getGroups () {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        TypedQuery<Group> groupTypedQuery = entityManager.createQuery("select g from Group g", Group.class);
        List<Group> resultList = groupTypedQuery.getResultList();

        for (Group group : resultList) {
            Group managedGroup = entityManager.find(Group.class, group.getId());
            group.setId(managedGroup.getId());
            group.setName_of_group(managedGroup.getName_of_group());
            group.setMax_occupancy(managedGroup.getMax_occupancy());

            TypedQuery<Teacher> teacherTypedQuery = entityManager.createQuery("select t from Teacher t where t.group.id = :number", Teacher.class);
            teacherTypedQuery.setParameter("number", group.getId());
            List<Teacher> teachers = teacherTypedQuery.getResultList();
            group.setNumber_of_teachers(teachers.size());
            group.setOccupancy((double) teachers.size() / (double) group.getMax_occupancy() * 100 + " %");

            TypedQuery<Rate> rateTypedQuery = entityManager.createQuery("select r from Rate r where r.group.id = :number", Rate.class);
            rateTypedQuery.setParameter("number", group.getId());
            List<Rate> rates = rateTypedQuery.getResultList();
            double average = 0;
            for (Rate rate : rates)
                average += rate.getValue();
            average /= rates.size();
            group.setAverage_rate(average);

            group.setNumber_of_rates(rates.size());

            entityManager.merge(group);
        }

        groups.clear();
        groups.addAll(resultList);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public static void main(String[] args){ launch(); }

    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("Teacher Management App");
        window.setResizable(false);
        window.setFullScreen(false);

        getGroups();

        String message = "Are you sure you want to continue?";

        window.setOnCloseRequest(windowEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message))
                windowEvent.consume();
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            TypedQuery<Group> groupTypedQuery = entityManager.createQuery("select g from Group g", Group.class);
            List<Group> groupResultList = groupTypedQuery.getResultList();
            TypedQuery<Teacher> teachersTypedQuery = entityManager.createQuery("select t from Teacher t", Teacher.class);
            List<Teacher> teachersResultList = teachersTypedQuery.getResultList();
            TypedQuery<Rate> rateTypedQuery = entityManager.createQuery("select r from Rate r", Rate.class);
            List<Rate> rateResultList = rateTypedQuery.getResultList();
            entityManager.close();

            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh-mm-ssa");

            String formattedDate = date.format(dateFormatter);
            String formattedTime = date.format(timeFormatter);

            String filename = "groups_" + formattedDate + "_" + formattedTime + ".csv";
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Dell\\Desktop\\stuff\\studia\\semestr 4\\Programowanie aplikacji użytkowych\\hibernateApp\\src\\main\\resources\\logs\\groups\\" + filename)))) {
                for (Group group : groupResultList) {
                    writer.println(group.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            filename = "teachers_" + formattedDate + "_" + formattedTime + ".csv";
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Dell\\Desktop\\stuff\\studia\\semestr 4\\Programowanie aplikacji użytkowych\\hibernateApp\\src\\main\\resources\\logs\\teachers\\" + filename)))) {
                for (Teacher teacher : teachersResultList) {
                    writer.println(teacher.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            filename = "rates_" + formattedDate + "_" + formattedTime + ".csv";
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Dell\\Desktop\\stuff\\studia\\semestr 4\\Programowanie aplikacji użytkowych\\hibernateApp\\src\\main\\resources\\logs\\rates\\" + filename)))) {
                for (Rate rate : rateResultList) {
                    writer.println(rate.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            entityManagerFactory.close();
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
        maximum_occupancy_column.setMinWidth(120);
        maximum_occupancy_column.setCellValueFactory(new PropertyValueFactory<>("max_occupancy"));

        TableColumn<Group, Double> average_rate_column = new TableColumn<>("Average Rate");
        average_rate_column.setMinWidth(120);
        average_rate_column.setCellValueFactory(new PropertyValueFactory<>("average_rate"));

        groupTableView.getColumns().addAll(group_name_column, occupancy_column, maximum_occupancy_column, average_rate_column);

        groupTableView.setItems(groups);

        add_group_button = new Button();
        add_group_button.setText("Add group");
        add_group_button.getStyleClass().add("groups_buttons");
        add_group_button.setOnAction(actionEvent -> AddGroupWindow.display(group -> {
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();
            entityManager.persist(group);
            entityManager.getTransaction().commit();
            entityManager.close();

            getGroups();
            groupTableView.refresh();
        }));

        delete_group_button = new Button();
        delete_group_button.setText("Delete group");
        delete_group_button.getStyleClass().add("groups_buttons");
        delete_group_button.setDisable(true);

        modify_group_data_button = new Button();
        modify_group_data_button.setText("Modify group's data");
        modify_group_data_button.getStyleClass().add("groups_buttons");
        modify_group_data_button.setDisable(true);

        export_data_button = new Button();
        export_data_button.setText("Export data");
        export_data_button.getStyleClass().add("export_button");

        groupTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                delete_group_button.setDisable(false);
                modify_group_data_button.setDisable(false);
            } else {
                delete_group_button.setDisable(true);
                modify_group_data_button.setDisable(true);
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
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                entityManager.getTransaction().begin();
                entityManager.remove(entityManager.find(Group.class, selectedGroup.getId()));
                entityManager.getTransaction().commit();
                entityManager.close();

                getGroups();
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
            if(selectedGroup != null) {
                ModifyGroupWindow.display(selectedGroup, group -> {
                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    entityManager.getTransaction().begin();

                    Group managedGroup = entityManager.find(Group.class, selectedGroup.getId());

                    group.setId(managedGroup.getId());
                    entityManager.merge(group);

                    entityManager.getTransaction().commit();
                    entityManager.close();

                    getGroups();
                    groupTableView.refresh();
                });
            }

        });

        groupTableView.setRowFactory(tableView -> {
            TableRow<Group> selectedGroup = new TableRow<>();

            MenuItem teachersItem = new MenuItem("Manage teachers");
            teachersItem.setOnAction(actionEvent -> {
                TeacherTableView teacherTableView = new TeacherTableView();
                Stage window = new Stage();
                teacherTableView.display(entityManagerFactory, selectedGroup.getItem(), window);
                window.setOnCloseRequest(windowEvent -> {
                    getGroups();
                    groupTableView.refresh();
                });
            });

            MenuItem ratesItem = new MenuItem("Manage rates");
            ratesItem.setOnAction(actionEvent -> {
                RateTableView rateTableView = new RateTableView();
                Stage window = new Stage();
                rateTableView.display(entityManagerFactory, selectedGroup.getItem(), window);
                window.setOnCloseRequest(windowEvent -> {
                    getGroups();
                    groupTableView.refresh();
                });
            });

            ContextMenu contextMenu = new ContextMenu(teachersItem, ratesItem);

            selectedGroup.contextMenuProperty().bind(Bindings.when(selectedGroup.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return selectedGroup;
        });

        export_data_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message)) {
                actionEvent.consume();
                return;
            }
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            TypedQuery<Group> groupTypedQuery = entityManager.createQuery("select g from Group g", Group.class);
            List<Group> groupResultList = groupTypedQuery.getResultList();
            TypedQuery<Teacher> teachersTypedQuery = entityManager.createQuery("select t from Teacher t", Teacher.class);
            List<Teacher> teachersResultList = teachersTypedQuery.getResultList();
            TypedQuery<Rate> rateTypedQuery = entityManager.createQuery("select r from Rate r", Rate.class);
            List<Rate> rateResultList = rateTypedQuery.getResultList();
            entityManager.close();

            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh-mm-ssa");

            String formattedDate = date.format(dateFormatter);
            String formattedTime = date.format(timeFormatter);

            String filename = "groups_" + formattedDate + "_" + formattedTime + ".csv";
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Dell\\Desktop\\stuff\\studia\\semestr 4\\Programowanie aplikacji użytkowych\\hibernateApp\\src\\main\\resources\\logs\\groups\\" + filename)))) {
                for (Group group : groupResultList) {
                    writer.println(group.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            filename = "teachers_" + formattedDate + "_" + formattedTime + ".csv";
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Dell\\Desktop\\stuff\\studia\\semestr 4\\Programowanie aplikacji użytkowych\\hibernateApp\\src\\main\\resources\\logs\\teachers\\" + filename)))) {
                for (Teacher teacher : teachersResultList) {
                    writer.println(teacher.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            filename = "rates_" + formattedDate + "_" + formattedTime + ".csv";
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Dell\\Desktop\\stuff\\studia\\semestr 4\\Programowanie aplikacji użytkowych\\hibernateApp\\src\\main\\resources\\logs\\rates\\" + filename)))) {
                for (Rate rate : rateResultList) {
                    writer.println(rate.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setLeft(groupTableView);

        VBox vBox = new VBox(10);

        HBox buttonsHBox = new HBox(20);
        buttonsHBox.getChildren().addAll(add_group_button, delete_group_button, modify_group_data_button, export_data_button);

        vBox.getChildren().addAll(buttonsHBox);

        borderPane.setBottom(vBox);

        scene = new Scene(borderPane);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        window.setScene(scene);
        window.show();
    }
}
