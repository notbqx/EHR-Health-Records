package com.sergio.healthrecords;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.ResourceBundle;


// 1. Implement Initializable
public class AddPatientController implements Initializable {
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private ChoiceBox<String> gender;
    @FXML private Label age;
    @FXML private DatePicker birthDate;
    @FXML private ChoiceBox<String> payorRelationship;
    @FXML private ComboBox<String> primaryPayor;
    @FXML private DatePicker subscriberBirthDate;
    @FXML private TextField policyNumber;
    @FXML private TextField groupNumber;
    @FXML private TextField subscriberFirstName;
    @FXML private TextField subscriberLastName;
    @FXML private DatePicker planEffectiveDate;
    @FXML private DatePicker planExpiryDate;
    @FXML private Pane chronicConditionsPane;
    private CheckComboBox<String> chronicConditions;

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gender.getItems().addAll("Male", "Female");
        primaryPayor.getItems().addAll(
                "Self",
                "Triple-S (SSS)",
                "MCS",
                "Humana",
                "Plan Vital (ASES)",
                "Medicare",
                "MMM / PMC",
                "First Medical",
                "Plan de Salud Menonita",
                "Other");
        payorRelationship.getItems().addAll("Self", "Spouse", "Child", "Legal Guardian", "Other");

        chronicConditions = new CheckComboBox<>();
        chronicConditions.getItems().addAll("Hypertension", "Diabetes", "Asthma", "Heart Disease");
        chronicConditionsPane.getChildren().add(chronicConditions);

        primaryPayor.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {


            if (newVal.equals("Self")) {

                planEffectiveDate.setDisable(true);
                planEffectiveDate.setEditable(false);
                planExpiryDate.setDisable(true);
                planExpiryDate.setEditable(false);
                subscriberBirthDate.setValue(birthDate.getValue());
                subscriberFirstName.setText(firstName.getText());
                subscriberLastName.setText(lastName.getText());
                payorRelationship.setValue(primaryPayor.getValue());
                payorRelationship.setDisable(true);

            }
            else {
                planEffectiveDate.setDisable(false);
                planEffectiveDate.setEditable(true);
                planExpiryDate.setDisable(false);
                planExpiryDate.setEditable(true);
                subscriberBirthDate.setValue(null);
                subscriberFirstName.setText(null);
                subscriberLastName.setText(null);
                payorRelationship.setValue(null);
                payorRelationship.setDisable(false);
            }

            if ("Medicare".equals(newVal) || "Plan Vital (ASES)".equals(newVal) || newVal.equals("Self") || newVal.equals("Other")) {
                groupNumber.setText("N/A");
                groupNumber.setEditable(false);
                groupNumber.setDisable(true);
                policyNumber.setText("N/A");
                policyNumber.setEditable(false);
                policyNumber.setDisable(true);
            } else {
               // groupNumber.clear();
                groupNumber.setEditable(true);
                groupNumber.setDisable(false);
                policyNumber.setEditable(true);
                policyNumber.setDisable(false);
                groupNumber.setText(null);
                policyNumber.setText(null);
            }
        });



        Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {

                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item.isAfter(LocalDate.now())) {
                            this.setDisable(true);
                            this.setStyle("-fx-background-color: #ffc0cb;"); // Optional: color disabled dates
                        }
                    }
                };
            }
        };
        birthDate.setDayCellFactory(dayCellFactory);
        subscriberBirthDate.setDayCellFactory(dayCellFactory);
    }

    public void set_BirthDate() {
        LocalDate currentDate = LocalDate.now();
        age.setText(String.valueOf(Period.between((birthDate.getValue()), currentDate).getYears()));
    }
}