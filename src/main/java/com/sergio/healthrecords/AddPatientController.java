package com.sergio.healthrecords;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

// 1. Implement Initializable
public class AddPatientController implements Initializable {
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private ChoiceBox<String> gender;
    @FXML
    private Label age;
    @FXML
    private DatePicker birthDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gender.getItems().addAll("Male", "Female");

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
    }

    public void set_BirthDate() {
        LocalDate currentDate = LocalDate.now();
        age.setText(String.valueOf(Period.between((birthDate.getValue()), currentDate).getYears()));
    }

    @FXML
    private void handleSaveButton() {
        String sql = "INSERT INTO patient_info(last_name, first_name) VALUES(?,?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lastName.getText());
            pstmt.setString(2, firstName.getText());
            pstmt.executeUpdate();

            System.out.println("Patient saved successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void getLastPatient() {
        String sql = "SELECT * FROM patient_info WHERE id = 1";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Check if a result was found
            if (rs.next()) {
                // Retrieve values by column name (or index)
                String lastName = rs.getString("last_name");
                String firstName = rs.getString("first_name");

                // Print to console
                System.out.println("--- FETCHED PATIENT ---");
                System.out.println("Last Name: " + lastName);
                System.out.println("First Name: " + firstName);
            } else {
                System.out.println("No patient found with ID 1.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

}