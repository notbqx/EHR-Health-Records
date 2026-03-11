package com.sergio.healthrecords;

import javafx.beans.property.SimpleStringProperty;

public class Patient {
    private final SimpleStringProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;

    public Patient(String id, String first, String last) {
        this.id = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(first);
        this.lastName = new SimpleStringProperty(last);
    }

    public String getId() { return id.get(); }
    public String getFirstName() { return firstName.get(); }
    public String getLastName() { return lastName.get(); }

    public SimpleStringProperty idProperty() { return id; }
    public SimpleStringProperty firstNameProperty() { return firstName; }
    public SimpleStringProperty lastNameProperty() { return lastName; }


}
