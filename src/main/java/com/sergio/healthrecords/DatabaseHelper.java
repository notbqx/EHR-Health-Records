package com.sergio.healthrecords;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

import static java.sql.DriverManager.getConnection;

public class DatabaseHelper {
    // The URL for your SQLite file (it will be created in your project folder)
    private static final String URL = "jdbc:sqlite:healthrecords.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = getConnection(URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static ObservableList<Patient> getAllPatients() {
        ObservableList<Patient> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM patient_info";

        try (Connection conn = getConnection(URL);
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Patient(
                        rs.getString("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void savePatient(String first, String middle, String last, String gender,
                            String dob, String age, String email, String phone,
                            String height, String weight, String addr1, String addr2, String status) {

        String sql = "INSERT INTO patient_info (first_name, middle_name, last_name, gender, birth_date, age, email, phone, height, weight, address_one, address_two, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, first);
            pstmt.setString(2, middle);
            pstmt.setString(3, last);
            pstmt.setString(4, gender);
            pstmt.setString(5, dob);
            pstmt.setString(6, age);
            pstmt.setString(7, email);
            pstmt.setString(8, phone);
            pstmt.setString(9, height);
            pstmt.setString(10, weight);
            pstmt.setString(11, addr1);
            pstmt.setString(12, addr2);
            pstmt.setString(13, status);

            pstmt.executeUpdate();
            System.out.println("Patient saved successfully!");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}