package com.sergio.healthrecords;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.converter.LocalDateStringConverter;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


// 1. Implement Initializable
public class AddPatientController implements Initializable {

    private String currentPatientId = null;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab addPatientTab;

    // Patient
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField middleName;
    @FXML
    private ChoiceBox<String> gender;
    @FXML
    private Label age;
    @FXML
    private DatePicker birthDate;
    @FXML
    private TextField addressOne;
    @FXML
    private TextField addressTwo;
    @FXML
    private TextField city;
    @FXML
    private TextField state;
    @FXML
    private TextField zip;
    @FXML
    private TextField country;
    @FXML
    private TextField email;
    @FXML
    private TextField phone;
    @FXML
    private TextField height;
    @FXML
    private TextField weight;
    @FXML
    private RadioButton alive;
    @FXML
    private RadioButton deceased;

    // Insurance
    @FXML
    private ChoiceBox<String> payorRelationship;
    @FXML
    private ComboBox<String> primaryPayor;
    @FXML
    private DatePicker subscriberBirthDate;
    @FXML
    private TextField policyNumber;
    @FXML
    private TextField groupNumber;
    @FXML
    private TextField subscriberFirstName;
    @FXML
    private TextField subscriberLastName;
    @FXML
    private DatePicker planEffectiveDate;
    @FXML
    private DatePicker planExpiryDate;

    //Medical history
    @FXML
    private TextArea allergies;
    @FXML
    private TextArea familyHistory;
    @FXML
    private Pane chronicConditionsPane;
    private CheckComboBox<String> chronicConditions;

    // Surgery
    @FXML
    private TableView<Surgery> surgeryTable;
    @FXML
    private TableColumn<Surgery, String> procCol;
    @FXML
    private TableColumn<Surgery, LocalDate> dateCol;
    @FXML
    private TextField procedureInputText;
    @FXML
    private DatePicker procedureDatePick;

    // Medications
    @FXML
    private TableView<Medication> medicationTable;
    @FXML private TableColumn<Medication, String> colMedName;
    @FXML private TableColumn<Medication, String> colDosage;
    @FXML private TableColumn<Medication, String> colFreq;
    @FXML private TableColumn<Medication, String> colPharm;


    @FXML private TableColumn<Medication, LocalDate> colStart;
    @FXML private TableColumn<Medication, LocalDate> colEnd;

    //Search
    @FXML
    private TableView<Patient> patientTable;
    @FXML
    private TextField searchField;
    private ObservableList<Patient> masterData = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Patient, String> colId;
    @FXML
    private TableColumn<Patient, String> colFirstName;
    @FXML
    private TableColumn<Patient, String> colLastName;

    @FXML private Button saveButton;



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

            if(newVal == null) {
                return;
            }

            if ("Self".equals(newVal)) {

                planEffectiveDate.setDisable(true);
                planEffectiveDate.setEditable(false);
                planExpiryDate.setDisable(true);
                planExpiryDate.setEditable(false);
                subscriberBirthDate.setValue(birthDate.getValue());
                subscriberFirstName.setText(firstName.getText());
                subscriberLastName.setText(lastName.getText());
                payorRelationship.setValue(primaryPayor.getValue());
                payorRelationship.setDisable(true);

            } else {
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

            if ("Medicare".equals(newVal) || "Plan Vital (ASES)".equals(newVal) || "Self".equals(newVal) || "Other".equals(newVal)) {
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

        procCol.setCellValueFactory(new PropertyValueFactory<>("procedure"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        surgeryTable.setEditable(true);
        procCol.setCellFactory(TextFieldTableCell.forTableColumn());
        procCol.setOnEditCommit(event -> {
            Surgery surgery = event.getRowValue();
            surgery.setProcedure(event.getNewValue());
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


        //Search
        // 1. Link Columns to Model Properties
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colFirstName.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        colLastName.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        // 2. Load from DB
        DatabaseHelper db = new DatabaseHelper();
        masterData.addAll(db.getAllPatients());

        // 3. Setup FilteredList
        FilteredList<Patient> filteredData = new FilteredList<>(masterData, p -> true);

        // 4. Connect listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(patient -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return patient.getFirstName().toLowerCase().contains(lower) ||
                        patient.getLastName().toLowerCase().contains(lower);
            });
        });

        patientTable.setItems(filteredData);

        ObservableList<Medication> emptyList = FXCollections.observableArrayList();
        padMedicationList(emptyList);
        medicationTable.setItems(emptyList);

        medicationTable.setEditable(true);

// Name Column
        colMedName.setCellValueFactory(cellData -> cellData.getValue().medicationNameProperty());
        colMedName.setCellFactory(TextFieldTableCell.forTableColumn());
        colMedName.setOnEditCommit(e -> e.getRowValue().setMedicationName(e.getNewValue()));

// Dosage Column
        // Dosage Column
        colDosage.setCellValueFactory(cellData -> cellData.getValue().dosageProperty());
        colDosage.setCellFactory(TextFieldTableCell.forTableColumn());
        colDosage.setOnEditCommit(e -> e.getRowValue().setDosage(e.getNewValue()));

// Frequency Column
        colFreq.setCellValueFactory(cellData -> cellData.getValue().frequencyProperty());
        colFreq.setCellFactory(TextFieldTableCell.forTableColumn());
        colFreq.setOnEditCommit(e -> e.getRowValue().setFrequency(e.getNewValue()));

// Pharmacy Column
        colPharm.setCellValueFactory(cellData -> cellData.getValue().pharmacyProperty());
        colPharm.setCellFactory(TextFieldTableCell.forTableColumn());
        colPharm.setOnEditCommit(e -> e.getRowValue().setPharmacy(e.getNewValue()));

// Start Date Column
        colStart.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        colStart.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        colStart.setOnEditCommit(e -> e.getRowValue().setStartDate(e.getNewValue()));

// End Date Column
        colEnd.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        colEnd.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        colEnd.setOnEditCommit(e -> e.getRowValue().setEndDate(e.getNewValue()));
    }


    public void set_BirthDate() {
        if (birthDate.getValue() != null) {
            LocalDate currentDate = LocalDate.now();
            age.setText(String.valueOf(Period.between((birthDate.getValue()), currentDate).getYears()));
        }
        else {
            age.setText("0");
        }
    }

    @FXML
    private void addProcedure() {
        Surgery newSurgery = new Surgery(procedureInputText.getText(), procedureDatePick.getValue());
        surgeryTable.getItems().add(newSurgery);
        procedureInputText.clear();
        procedureDatePick.setValue(null);
    }

    @FXML
    private void handleSaveButton() {
        DatabaseHelper db = new DatabaseHelper();
        String status = alive.isSelected() ? "Alive" : "Deceased";

        // 1. Create the Insurance object from UI fields
        Insurance ins = new Insurance(
                "0", // ID not known yet
                payorRelationship.getValue(),
                primaryPayor.getValue(),
                subscriberBirthDate.getValue() != null ? subscriberBirthDate.getValue().toString() : "",
                policyNumber.getText(),
                groupNumber.getText(),
                subscriberFirstName.getText(),
                subscriberLastName.getText(),
                planEffectiveDate.getValue() != null ? planEffectiveDate.getValue().toString() : "",
                planExpiryDate.getValue() != null ? planExpiryDate.getValue().toString() : ""
        );

        MedicalHistory medicalHist = new MedicalHistory("0", String.join(", ", chronicConditions.getCheckModel().getCheckedItems()), allergies.getText(), familyHistory.getText());

        List<Medication> realMeds = medicationTable.getItems().stream()
                .filter(m -> m.getMedicationName() != null && !m.getMedicationName().isEmpty())
                .toList();

        if (currentPatientId == null) {
            db.saveFullPatient(
                    firstName.getText(), middleName.getText(), lastName.getText(),
                    gender.getValue(),
                    birthDate.getValue() != null ? birthDate.getValue().toString() : "",
                    email.getText(), phone.getText(), height.getText(), weight.getText(),
                    addressOne.getText(), addressTwo.getText(), city.getText(),
                    state.getText(), zip.getText(), country.getText(), status,
                    ins, medicalHist, new ArrayList<>(surgeryTable.getItems()), realMeds);


        }
        else {
            db.updateFullPatient(
                    currentPatientId,
                    firstName.getText(), middleName.getText(), lastName.getText(),
                    gender.getValue(),
                    birthDate.getValue() != null ? birthDate.getValue().toString() : "",
                    email.getText(), phone.getText(), height.getText(), weight.getText(),
                    addressOne.getText(), addressTwo.getText(), city.getText(),
                    state.getText(), zip.getText(), country.getText(), status,
                    ins, medicalHist, new ArrayList<>(surgeryTable.getItems()), realMeds);
        }

        this.currentPatientId = null;

        // 2. Clear Personal Info
        firstName.clear();
        lastName.clear();
        middleName.clear();
        gender.setValue(null);
        birthDate.setValue(null);
        age.setText("0");
        addressOne.clear();
        addressTwo.clear();
        city.clear();
        state.clear();
        zip.clear();
        country.clear();
        email.clear();
        phone.clear();
        height.clear();
        weight.clear();
        alive.setSelected(true); // Default to alive

        // 3. Clear Insurance & History
        primaryPayor.setValue(null);
        payorRelationship.setValue(null);
        subscriberBirthDate.setValue(null);
        policyNumber.clear();
        groupNumber.clear();
        subscriberFirstName.clear();
        subscriberLastName.clear();
        planEffectiveDate.setValue(null);
        planExpiryDate.setValue(null);
        allergies.clear();
        familyHistory.clear();
        chronicConditions.getCheckModel().clearChecks();

        // 4. Reset Tables
        surgeryTable.getItems().clear();
        medicationTable.getItems().clear();
        padMedicationList(medicationTable.getItems());

        saveButton.setText("Save new patient");
        System.out.println("Form cleared for new patient entry.");
    }



    @FXML
    private void handleViewSelectedPatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            this.currentPatientId = selected.getId();
            saveButton.setText("Save Changes");
            // 1. Fetch Core Patient Details
            Patient fullData = DatabaseHelper.getPatientById(selected.getId());

            // 2. Fetch Insurance Details (New Step)
            Insurance insData = DatabaseHelper.getInsuranceByPatientId(selected.getId());

            MedicalHistory medicalHistoryData = DatabaseHelper.getMedicalHistoryByPatientId(selected.getId());

            ObservableList<Surgery> surgeryList = DatabaseHelper.getSurgeriesByPatientId(selected.getId());
            ObservableList<Medication> medList = DatabaseHelper.getMedicationsByPatientId(selected.getId());


            // Populate Patient UI components
            firstName.setText(fullData.getFirstName());
            middleName.setText(fullData.getMiddleName());
            lastName.setText(fullData.getLastName());
            gender.setValue(fullData.getGender());
            birthDate.setValue(fullData.getBirthDateAsLocalDate());
            email.setText(fullData.getEmail());
            phone.setText(fullData.getPhone());
            height.setText(fullData.getHeight());
            weight.setText(fullData.getWeight());
            addressOne.setText(fullData.getAddressOne());
            addressTwo.setText(fullData.getAddressTwo());
            city.setText(fullData.getCity());
            state.setText(fullData.getState());
            zip.setText(fullData.getZip());
            country.setText(fullData.getCountry());

            if ("Alive".equalsIgnoreCase(fullData.getStatus())) {
                alive.setSelected(true);
            } else {
                deceased.setSelected(true);
            }

            // 3. Populate Insurance UI components (New Step)
            if (insData != null) {
                primaryPayor.setValue(insData.getPrimaryPayor());
                payorRelationship.setValue(insData.getPayorRelationship());
                policyNumber.setText(insData.getPolicyNumber());
                groupNumber.setText(insData.getGroupNumber());
                subscriberFirstName.setText(insData.getSubscriberFirstName());
                subscriberLastName.setText(insData.getSubscriberLastName());
                subscriberBirthDate.setValue(insData.getSubscriberBirthDateAsLocalDate());
                planEffectiveDate.setValue(insData.getPlanEffectiveDateAsLocalDate());
                planExpiryDate.setValue(insData.getPlanExpiryDateAsLocalDate());
            }

            if (medicalHistoryData != null) {
                allergies.setText(medicalHistoryData.getAllergies());
                familyHistory.setText(medicalHistoryData.getFamilyHistory());

                chronicConditions.getCheckModel().clearChecks();

                // 2. Get the string (e.g., "Diabetes, Hypertension, Asthma")
                String savedConditions = medicalHistoryData.getChronicConditions();

                if (savedConditions != null && !savedConditions.isEmpty()) {
                    // 3. Split by comma and trim whitespace
                    String[] conditionArray = savedConditions.split(",\\s*");

                    // 4. Loop through and check each matching item in the list
                    for (String condition : conditionArray) {
                        chronicConditions.getCheckModel().check(condition);
                    }
                }
            }

            // 1. Clear the table first so old data doesn't persist
            surgeryTable.getItems().clear();


            ObservableList<Surgery> surgeries = DatabaseHelper.getSurgeriesByPatientId(selected.getId());


            if (surgeries != null && !surgeries.isEmpty()) {
                surgeryTable.setItems(surgeries);
            }

            if (medList != null) {
                medicationTable.setItems(medList);
            }


            tabPane.getSelectionModel().select(addPatientTab);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a patient from the list.");
            alert.show();
        }
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void padMedicationList(ObservableList<Medication> list) {
        while (list.size() < 15) {
            list.add(new Medication()); // Uses your empty constructor
        }
    }

    public void stopEditing() {
        if (!addPatientTab.isSelected()) {
            System.out.println("hola");
            this.currentPatientId = null;
            saveButton.setText("Save new patient");
        }
    }
}


