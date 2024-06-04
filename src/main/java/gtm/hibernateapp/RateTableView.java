package gtm.hibernateapp;

import gtm.hibernateapp.entities.*;
import gtm.hibernateapp.persistence.CustomPersistenceUnitInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;

public class RateTableView {

    EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new
            CustomPersistenceUnitInfo(), new HashMap<>());

    Button add_rate_button, delete_rate_button, modify_rate_button;
    ObservableList<Rate> rates = FXCollections.observableArrayList();
    TableView<Rate> rateTableView = new TableView<>(rates);

    public void getRateList (Group selectedGroup) {
        rates.clear();
        rates.addAll(selectedGroup.getRates());
    }

    public void display(Group selectedGroup) {
        Stage window = new Stage();
        window.setTitle("Rates of " + selectedGroup.getName_of_group());
        window.setResizable(false);
        window.setFullScreen(false);

        String message = "Are you sure you want to continue?";

        window.setOnCloseRequest(windowEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message))
                windowEvent.consume();
            entityManagerFactory.close();
        });

        BorderPane borderPane = new BorderPane();

        getRateList(selectedGroup);

        TableColumn<Rate, Double> value_column = new TableColumn<>("Value");
        value_column.setMinWidth(120);
        value_column.setCellValueFactory(new PropertyValueFactory<>("value"));

        TableColumn<Rate, LocalDate> date_column = new TableColumn<>("Date");
        date_column.setMinWidth(120);
        date_column.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Rate, String> comment_column = new TableColumn<>("Comment");
        comment_column.setMinWidth(120);
        comment_column.setCellValueFactory(new PropertyValueFactory<>("comment"));

        rateTableView.getColumns().addAll(value_column, date_column, comment_column);


        add_rate_button = new Button();
        add_rate_button.setText("Add rate");
        add_rate_button.getStyleClass().add("rate_buttons");
//        add_teacher_button.setDisable(true);
        add_rate_button.fire();

        delete_rate_button = new Button();
        delete_rate_button.setText("Delete rate");
        delete_rate_button.getStyleClass().add("rate_buttons");
        delete_rate_button.setDisable(true);

        modify_rate_button = new Button();
        modify_rate_button.setText("Modify rate's data");
        modify_rate_button.getStyleClass().add("rate_buttons");
        modify_rate_button.setDisable(true);

        rateTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            delete_rate_button.setDisable(newValue == null);
            modify_rate_button.setDisable(newValue == null);
        });

        add_rate_button.setOnAction(actionEvent -> {
            AddRateWindow.display(selectedGroup, rate -> {
                EntityManager entityManager = entityManagerFactory.createEntityManager();

                entityManager.getTransaction().begin();
                entityManager.persist(rate);
                entityManager.getTransaction().commit();
                entityManager.close();

                getRateList(selectedGroup);
                rateTableView.refresh();
            });
        });

        delete_rate_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message)) {
                actionEvent.consume();
                return;
            }

            Rate selectedRate = rateTableView.getSelectionModel().getSelectedItem();
            if (selectedRate != null) {
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                entityManager.getTransaction().begin();
                entityManager.remove(entityManager.find(Rate.class, selectedRate.getId()));
                entityManager.getTransaction().commit();
                entityManager.close();

                getRateList(selectedGroup);
                rateTableView.refresh();
            }
        });

        modify_rate_button.setOnAction(actionEvent -> {
            Toolkit.getDefaultToolkit().beep();
            if (!ConfirmationWindow.display(message)) {
                actionEvent.consume();
                return;
            }

            Rate selectedRate = rateTableView.getSelectionModel().getSelectedItem();

            ModifyRateWindow.display(selectedRate,rate -> {
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                entityManager.getTransaction().begin();

                Rate managedRate = entityManager.find(Rate.class, selectedRate.getId());

                rate.setId(managedRate.getId());
                rate.setGroup(managedRate.getGroup());
                entityManager.merge(rate);

                entityManager.getTransaction().commit();
                entityManager.close();

                getRateList(selectedGroup);
                rateTableView.refresh();
            });
        });

        borderPane.setPadding(new Insets(10, 10, 10, 10));

        VBox vBox = new VBox(10);

        HBox buttonsHBox = new HBox(20);
        buttonsHBox.getChildren().addAll(add_rate_button, delete_rate_button, modify_rate_button);

        HBox searchHBox = new HBox(20);
        Label searchLabel = new Label("Filter teacher's table contents:");

        vBox.getChildren().addAll(buttonsHBox, searchHBox);

        borderPane.setBottom(vBox);

        borderPane.setCenter(rateTableView);

        Scene scene = new Scene(borderPane);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        scene.getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (!rateTableView.getBoundsInParent().contains(mouseEvent.getX(), mouseEvent.getY())) {
                rateTableView.getSelectionModel().clearSelection();
                delete_rate_button.setDisable(true);
            }
        });

        window.setScene(scene);
        window.show();
    }
}
