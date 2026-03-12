package com.sergio.healthrecords;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;

public class Surgery {
    private final SimpleStringProperty procedure;
    private final SimpleObjectProperty<LocalDate> date;

    public Surgery(String procedure, LocalDate date) {
        this.procedure = new SimpleStringProperty(procedure);
        this.date = new SimpleObjectProperty<>(date);
    }

    public String getProcedure() { return procedure.get(); }
    public LocalDate getDate() { return date.get(); }

    // Property getters are required for TableColumn CellValueFactories
    public SimpleStringProperty procedureProperty() { return procedure; }
    public SimpleObjectProperty<LocalDate> dateProperty() { return date; }

    public void setProcedure(String newValue) {
        this.procedure.set(newValue);
    }
}