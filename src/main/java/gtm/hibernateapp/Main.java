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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Main extends Application {

    EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new
            CustomPersistenceUnitInfo(), new HashMap<>());

    Button add_group_button, delete_group_button, modify_group_data_button;
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
            group.calculate_occupancy();
            group.calculate_average_rate();
            Group managedGroup = entityManager.find(Group.class, group.getId());
            group.setId(managedGroup.getId());
            entityManager.merge(group);
        }

        groups.clear();
        groups.addAll(resultList);

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

//            // persist -> add data to the context
//            // find -> get data from the database to the context
//            // getReference -> get data from the database to the context ONLY when it is used afterwards
//            // remove -> mark data for removal from the database
//            // refresh -> reverts changes made to the object in the context by getting the data from the database and assigning it to the object
//            // merge -> update object in the table after altering it in the context
//            // detach -> remove data from the context, but not from database if it already exists there
//            // flush -> mirror the context in the database, not waiting for commit


        String message = "Are you sure you want to continue?";

        window.setOnCloseRequest(windowEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message))
                windowEvent.consume();
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

        groupTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                delete_group_button.setDisable(false);
                modify_group_data_button.setDisable(false);
            } else {
                delete_group_button.setDisable(true);
                modify_group_data_button.setDisable(true);
            }
        });

        groupTableView.setRowFactory(tableView -> {
            TableRow<Group> selectedGroup = new TableRow<>();

            MenuItem teachersItem = new MenuItem("Manage teachers");
            teachersItem.setOnAction(actionEvent -> {
                if(selectedGroup != null) {
                    TeacherTableView teacherTableView = new TeacherTableView();

                    teacherTableView.display(selectedGroup.getItem());

                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    entityManager.getTransaction().begin();

                    Group managedGroup = entityManager.find(Group.class, selectedGroup.getItem().getId());

                    managedGroup.calculate_occupancy();
                    System.out.println(managedGroup);

                    entityManager.getTransaction().commit();
                    entityManager.close();

                    getGroups();
                    groupTableView.refresh();
                }
            });

            MenuItem ratesItem = new MenuItem("Manage rates");
            ratesItem.setOnAction(actionEvent -> {
                if(selectedGroup != null) {
                    RateTableView rateTableView = new RateTableView();
                    rateTableView.display(selectedGroup.getItem());

                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    entityManager.getTransaction().begin();

                    Group managedGroup = entityManager.find(Group.class, selectedGroup.getItem().getId());

                    managedGroup.calculate_occupancy();
                    System.out.println(managedGroup);

                    entityManager.getTransaction().commit();
                    entityManager.close();

                    getGroups();
                    groupTableView.refresh();
                }
            });

            ContextMenu contextMenu = new ContextMenu(teachersItem, ratesItem);

            selectedGroup.contextMenuProperty().bind(Bindings.when(selectedGroup.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return selectedGroup;
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

        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setLeft(groupTableView);

        VBox vBox = new VBox(10);

        HBox buttonsHBox = new HBox(20);
        buttonsHBox.getChildren().addAll(add_group_button, delete_group_button, modify_group_data_button);

        vBox.getChildren().addAll(buttonsHBox);

        borderPane.setBottom(vBox);

        scene = new Scene(borderPane);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        window.setScene(scene);
        window.show();
    }
}
