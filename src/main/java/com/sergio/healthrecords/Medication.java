package com.sergio.healthrecords;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;

public class Medication {
    private final SimpleStringProperty medicationName;
    private final SimpleStringProperty dosage;
    private final SimpleStringProperty frequency;
    private final SimpleStringProperty pharmacy;
    private final SimpleObjectProperty<LocalDate> startDate;
    private final SimpleObjectProperty<LocalDate> endDate;

    // Full constructor for actual data
    public Medication(String medicationName, String dosage, String frequency, String pharmacy, LocalDate startDate, LocalDate endDate) {
        this.medicationName = new SimpleStringProperty(medicationName);
        this.dosage = new SimpleStringProperty(dosage);
        this.frequency = new SimpleStringProperty(frequency);
        this.pharmacy = new SimpleStringProperty(pharmacy);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
    }

    // Empty constructor for placeholder rows
    public Medication() {
        this("", "", "", "", null, null);
    }

    // Property getters (Required for CellValueFactories)
    public SimpleStringProperty medicationNameProperty() { return medicationName; }
    public SimpleStringProperty dosageProperty() { return dosage; }
    public SimpleStringProperty frequencyProperty() { return frequency; }
    public SimpleStringProperty pharmacyProperty() { return pharmacy; }
    public SimpleObjectProperty<LocalDate> startDateProperty() { return startDate; }
    public SimpleObjectProperty<LocalDate> endDateProperty() { return endDate; }

    // Standard getters
    public String getMedicationName() { return medicationName.get(); }
    public String getDosage() { return dosage.get(); }
    public String getFrequency() { return frequency.get(); }
    public String getPharmacy() { return pharmacy.get(); }
    public LocalDate getStartDate() { return startDate.get(); }
    public LocalDate getEndDate() { return endDate.get(); }

    public void setMedicationName(String name) { this.medicationName.set(name); }
    public void setDosage(String dosage) { this.dosage.set(dosage); }
    public void setFrequency(String frequency) { this.frequency.set(frequency); }
    public void setPharmacy(String pharmacy) { this.pharmacy.set(pharmacy); }
    public void setStartDate(LocalDate date) { this.startDate.set(date); }
    public void setEndDate(LocalDate date) { this.endDate.set(date); }
}