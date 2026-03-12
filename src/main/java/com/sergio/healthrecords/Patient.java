package com.sergio.healthrecords;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Patient {
    private final SimpleStringProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty middleName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty gender;
    private final SimpleStringProperty birthDate;
    private final SimpleStringProperty email;
    private final SimpleStringProperty phone;
    private final SimpleStringProperty height;
    private final SimpleStringProperty weight;
    private final SimpleStringProperty addressOne;
    private final SimpleStringProperty addressTwo;
    private final SimpleStringProperty city;
    private final SimpleStringProperty state;
    private final SimpleStringProperty zip;
    private final SimpleStringProperty country;
    private final SimpleStringProperty status;

    public Patient(String id, String first, String last) {
        this.id = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(first);
        this.lastName = new SimpleStringProperty(last);
        this.middleName = new SimpleStringProperty("");
        this.gender = new SimpleStringProperty("");
        this.birthDate = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.phone = new SimpleStringProperty("");
        this.height = new SimpleStringProperty("");
        this.weight = new SimpleStringProperty("");
        this.addressOne = new SimpleStringProperty("");
        this.addressTwo = new SimpleStringProperty("");
        this.city = new SimpleStringProperty("");
        this.state = new SimpleStringProperty("");
        this.zip = new SimpleStringProperty("");
        this.country = new SimpleStringProperty("");
        this.status = new SimpleStringProperty("");
    }

    public Patient(
            String id,
            String first,
            String middleName,
            String last,
            String gender,
            String birthDate,
            String email,
            String phone,
            String height,
            String weight,
            String addressOne,
            String addressTwo,
            String city,
            String state,
            String zip,
            String country,
            String status) {

        this.id = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(first);
        this.middleName = new SimpleStringProperty(middleName);
        this.lastName = new SimpleStringProperty(last);
        this.gender = new SimpleStringProperty(gender);
        this.birthDate = new SimpleStringProperty(birthDate);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.height = new SimpleStringProperty(height);
        this.weight = new SimpleStringProperty(weight);
        this.addressOne = new SimpleStringProperty(addressOne);
        this.addressTwo = new SimpleStringProperty(addressTwo);
        this.city = new SimpleStringProperty(city);
        this.state = new SimpleStringProperty(state);
        this.zip = new SimpleStringProperty(zip);
        this.country = new SimpleStringProperty(country);
        this.status = new SimpleStringProperty(status);
    }

    public String getId() { return id.get(); }
    public String getFirstName() { return firstName.get(); }
    public String getMiddleName() { return middleName.get(); }
    public String getLastName() { return lastName.get(); }
    public String getGender() { return gender.get(); }
    public LocalDate getBirthDateAsLocalDate() {
        String dateStr = birthDate.get();
        if (dateStr == null || dateStr.isEmpty()|| dateStr.equalsIgnoreCase("N/A")) {
            return null;
        }
        // Assumes you stored it as YYYY-MM-DD
        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
    }
    public String getEmail() { return email.get(); }
    public String getPhone() { return phone.get(); }
    public String getHeight() { return height.get(); }
    public String getWeight() { return weight.get(); }
    public String getAddressOne() { return addressOne.get(); }
    public String getAddressTwo() { return addressTwo.get(); }
    public String getCity() { return city.get(); }
    public String getState() { return state.get(); }
    public String getZip() { return zip.get(); }
    public String getCountry() { return country.get(); }
    public String getStatus() { return status.get(); }

    public SimpleStringProperty idProperty() { return id; }
    public SimpleStringProperty firstNameProperty() { return firstName; }
    public SimpleStringProperty middleNameProperty() { return middleName; }
    public SimpleStringProperty lastNameProperty() { return lastName; }
    public SimpleStringProperty genderProperty() { return gender; }
    public SimpleStringProperty birthDateProperty() { return birthDate; }
    public SimpleStringProperty emailProperty() { return email; }
    public SimpleStringProperty phoneProperty() { return phone; }
    public SimpleStringProperty heightProperty() { return height; }
    public SimpleStringProperty weightProperty() { return weight; }
    public SimpleStringProperty addressOneProperty() { return addressOne; }
    public SimpleStringProperty addressTwoProperty() { return addressTwo; }
    public SimpleStringProperty statusProperty() { return status; }




}
