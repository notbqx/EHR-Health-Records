module com.sergio.healthrecords {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;

    opens com.sergio.healthrecords to javafx.fxml;
    exports com.sergio.healthrecords;
}