package com.sergio.healthrecords;

import javafx.beans.property.SimpleStringProperty;

public class MedicalHistory {
    private final SimpleStringProperty patientId;
    private final SimpleStringProperty chronicConditions;
    private final SimpleStringProperty allergies;      // New
    private final SimpleStringProperty familyHistory;  // New

    public MedicalHistory(String patientId, String chronicConditions, String allergies, String familyHistory) {
        this.patientId = new SimpleStringProperty(patientId);
        this.chronicConditions = new SimpleStringProperty(chronicConditions);
        this.allergies = new SimpleStringProperty(allergies);
        this.familyHistory = new SimpleStringProperty(familyHistory);
    }

    // Getters
    public String getPatientId() { return patientId.get(); }
    public String getChronicConditions() { return chronicConditions.get(); }
    public String getAllergies() { return allergies.get(); }
    public String getFamilyHistory() { return familyHistory.get(); }
}