package com.sergio.healthrecords;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Insurance {

    private final SimpleStringProperty patientId;
    private final SimpleStringProperty payorRelationship;
    private final SimpleStringProperty primaryPayor;
    private final SimpleStringProperty subscriberBirthDate;
    private final SimpleStringProperty policyNumber;
    private final SimpleStringProperty groupNumber;
    private final SimpleStringProperty subscriberFirstName;
    private final SimpleStringProperty subscriberLastName;
    private final SimpleStringProperty planEffectiveDate;
    private final SimpleStringProperty planExpiryDate;




    public Insurance(
            String patientId,
            String payorRelationship,
            String primaryPayor,
            String subscriberBirthDate,
            String policyNumber,
            String groupNumber,
            String subscriberFirstName,
            String subscriberLastName,
            String planEffectiveDate,
            String planExpiryDate
    ){
       this.patientId = new SimpleStringProperty(patientId);
       this.payorRelationship = new SimpleStringProperty(payorRelationship);
       this.primaryPayor = new SimpleStringProperty(primaryPayor);
       this.subscriberBirthDate = new SimpleStringProperty(subscriberBirthDate);
       this.policyNumber = new SimpleStringProperty(policyNumber);
       this.groupNumber = new SimpleStringProperty(groupNumber);
       this.subscriberFirstName = new SimpleStringProperty(subscriberFirstName);
       this.subscriberLastName = new SimpleStringProperty(subscriberLastName);
       this.planEffectiveDate = new SimpleStringProperty(planEffectiveDate);
       this.planExpiryDate = new SimpleStringProperty(planExpiryDate);
    }

    public String getPatientId() {return patientId.get();}
    public String getPayorRelationship() {return payorRelationship.get();}
    public String getPrimaryPayor() {return primaryPayor.get();}
    public String getPolicyNumber() {return policyNumber.get();}
    public String getGroupNumber() {return groupNumber.get();}
    public String getSubscriberFirstName() {return subscriberFirstName.get();}
    public String getSubscriberLastName() {return subscriberLastName.get();}

    public LocalDate getSubscriberBirthDateAsLocalDate() {
        String dateStr = subscriberBirthDate.get();
        if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("N/A")) {
            return null;
        }
        // Assumes you stored it as YYYY-MM-DD
        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalDate getPlanEffectiveDateAsLocalDate() {
        String dateStr = planEffectiveDate.get();
        if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("N/A")) {
            return null;
        }
        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalDate getPlanExpiryDateAsLocalDate() {
        String dateStr = planExpiryDate.get();
        if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("N/A")) {
            return null;
        }
        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
