package com.sergio.healthrecords;

import java.time.LocalDate;

public class Surgery {
    private String procedure;
    private LocalDate date;

    public Surgery(String procedure, LocalDate date) {
        this.procedure = procedure;
        this.date = date;
    }

    // Getters AND Setters required for Table Editing
    public String getProcedure() { return procedure; }
    public void setProcedure(String procedure) { this.procedure = procedure; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}