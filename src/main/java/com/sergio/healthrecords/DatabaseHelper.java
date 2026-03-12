package com.sergio.healthrecords;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

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

    public static Patient getPatientById(String id) {
        String sql = "SELECT * FROM patient_info WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Patient(
                        rs.getString("id"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("gender"),
                        rs.getString("birth_date"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("height"),
                        rs.getString("weight"),
                        rs.getString("address_one"),
                        rs.getString("address_two"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("zip"),
                        rs.getString("country"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void saveFullPatient(
            String fName, String mName, String lName, String gen, String bDay,
            String mail, String ph, String h, String w, String add1, String add2,
            String cty, String st, String zp, String cntry, String stat,
            Insurance ins,
            MedicalHistory medicalHist,
            List<Surgery> surgeries, List<Medication> realMeds) {

        String patientSql = "INSERT INTO patient_info (first_name, middle_name, last_name, gender, birth_date, email, phone, height, weight, address_one, address_two, city, state, zip, country, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String insuranceSql = "INSERT INTO insurance_info (patient_id, payor_relationship, primary_payor, subscriber_birth_date, policy_number, group_number, subscriber_first_name, subscriber_last_name, plan_effective_date, plan_expiry_date) VALUES (?,?,?,?,?,?,?,?,?,?)";
        String medicalHistorySql = "INSERT INTO medical_history (patient_id, chronic_conditions, allergies, family_history) VALUES (?,?,?,?)";
        String surgerySql = "INSERT INTO patient_surgeries (patient_id, procedure_name, surgery_date) VALUES (?, ?, ?)";
        String medSql = "INSERT INTO patient_medications (patient_id, name, dosage, frequency, pharmacy, start_date, end_date) VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = getConnection(URL)) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(patientSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, fName); pstmt.setString(2, mName); pstmt.setString(3, lName);
                pstmt.setString(4, gen); pstmt.setString(5, bDay); pstmt.setString(6, mail);
                pstmt.setString(7, ph); pstmt.setString(8, h); pstmt.setString(9, w);
                pstmt.setString(10, add1); pstmt.setString(11, add2); pstmt.setString(12, cty);
                pstmt.setString(13, st); pstmt.setString(14, zp); pstmt.setString(15, cntry);
                pstmt.setString(16, stat);
                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    long newId = rs.getLong(1);

                    // 1. Save Insurance
                    try (PreparedStatement insPstmt = conn.prepareStatement(insuranceSql)) {
                        insPstmt.setLong(1, newId);
                        insPstmt.setString(2, ins.getPayorRelationship());
                        insPstmt.setString(3, ins.getPrimaryPayor());
                        insPstmt.setString(4, ins.getSubscriberBirthDateAsLocalDate() != null ? ins.getSubscriberBirthDateAsLocalDate().toString() : "N/A");
                        insPstmt.setString(5, ins.getPolicyNumber());
                        insPstmt.setString(6, ins.getGroupNumber());
                        insPstmt.setString(7, ins.getSubscriberFirstName());
                        insPstmt.setString(8, ins.getSubscriberLastName());
                        insPstmt.setString(9, ins.getPlanEffectiveDateAsLocalDate() != null ? ins.getPlanEffectiveDateAsLocalDate().toString() : "N/A");
                        insPstmt.setString(10, ins.getPlanExpiryDateAsLocalDate() != null ? ins.getPlanExpiryDateAsLocalDate().toString() : "N/A");
                        insPstmt.executeUpdate();
                    }

                    // 2. Save Medical History
                    try (PreparedStatement medPstmt = conn.prepareStatement(medicalHistorySql)) {
                        medPstmt.setLong(1, newId);
                        medPstmt.setString(2, medicalHist.getChronicConditions());
                        medPstmt.setString(3, medicalHist.getAllergies());
                        medPstmt.setString(4, medicalHist.getFamilyHistory());
                        medPstmt.executeUpdate();
                    }

                    try (PreparedStatement sPstmt = conn.prepareStatement(surgerySql)) {
                        for (Surgery s : surgeries) {
                            sPstmt.setLong(1, newId);
                            sPstmt.setString(2, s.getProcedure());
                            sPstmt.setString(3, s.getDate() != null ? s.getDate().toString() : "");
                            sPstmt.addBatch(); // Efficiently queue all rows
                        }
                        sPstmt.executeBatch(); // Send all to database at once
                    }

                    try (PreparedStatement mPstmt = conn.prepareStatement(medSql)) {
                        for (Medication m : realMeds) {
                            mPstmt.setLong(1, newId);
                            mPstmt.setString(2, m.getMedicationName());
                            mPstmt.setString(3, m.getDosage());
                            mPstmt.setString(4, m.getFrequency());
                            mPstmt.setString(5, m.getPharmacy());
                            mPstmt.setString(6, m.getStartDate() != null ? m.getStartDate().toString() : "");
                            mPstmt.setString(7, m.getEndDate() != null ? m.getEndDate().toString() : "");
                            mPstmt.addBatch();
                        }
                        mPstmt.executeBatch();
                    }
                }
                conn.commit();
                System.out.println("Full record saved successfully.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFullPatient(
            String id, // This is the ID we are updating
            String fName, String mName, String lName, String gen, String bDay,
            String mail, String ph, String h, String w, String add1, String add2,
            String cty, String st, String zp, String cntry, String stat,
            Insurance ins,
            MedicalHistory medicalHist,
            List<Surgery> surgeries, List<Medication> realMeds) {

        String patientSql = "UPDATE patient_info SET first_name=?, middle_name=?, last_name=?, gender=?, birth_date=?, email=?, phone=?, height=?, weight=?, address_one=?, address_two=?, city=?, state=?, zip=?, country=?, status=? WHERE id=?";
        String insuranceSql = "UPDATE insurance_info SET payor_relationship=?, primary_payor=?, subscriber_birth_date=?, policy_number=?, group_number=?, subscriber_first_name=?, subscriber_last_name=?, plan_effective_date=?, plan_expiry_date=? WHERE patient_id=?";
        String medicalHistorySql = "UPDATE medical_history SET chronic_conditions=?, allergies=?, family_history=? WHERE patient_id=?";

        // Clean-up queries
        String deleteSurgeriesSql = "DELETE FROM patient_surgeries WHERE patient_id = ?";
        String deleteMedicationsSql = "DELETE FROM patient_medications WHERE patient_id = ?";

        // Insert queries for re-adding dependencies
        String surgerySql = "INSERT INTO patient_surgeries (patient_id, procedure_name, surgery_date) VALUES (?, ?, ?)";
        String medSql = "INSERT INTO patient_medications (patient_id, name, dosage, frequency, pharmacy, start_date, end_date) VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = getConnection(URL)) {
            conn.setAutoCommit(false); // Start transaction

            try {
                // 1. Update Main Info
                try (PreparedStatement pstmt = conn.prepareStatement(patientSql)) {
                    pstmt.setString(1, fName); pstmt.setString(2, mName); pstmt.setString(3, lName);
                    pstmt.setString(4, gen); pstmt.setString(5, bDay); pstmt.setString(6, mail);
                    pstmt.setString(7, ph); pstmt.setString(8, h); pstmt.setString(9, w);
                    pstmt.setString(10, add1); pstmt.setString(11, add2); pstmt.setString(12, cty);
                    pstmt.setString(13, st); pstmt.setString(14, zp); pstmt.setString(15, cntry);
                    pstmt.setString(16, stat);
                    pstmt.setString(17, id); // WHERE clause
                    pstmt.executeUpdate();
                }

                // 2. Update Insurance
                try (PreparedStatement insPstmt = conn.prepareStatement(insuranceSql)) {
                    insPstmt.setString(1, ins.getPayorRelationship());
                    insPstmt.setString(2, ins.getPrimaryPayor());
                    insPstmt.setString(3, ins.getSubscriberBirthDateAsLocalDate() != null ? ins.getSubscriberBirthDateAsLocalDate().toString() : "N/A");
                    insPstmt.setString(4, ins.getPolicyNumber());
                    insPstmt.setString(5, ins.getGroupNumber());
                    insPstmt.setString(6, ins.getSubscriberFirstName());
                    insPstmt.setString(7, ins.getSubscriberLastName());
                    insPstmt.setString(8, ins.getPlanEffectiveDateAsLocalDate() != null ? ins.getPlanEffectiveDateAsLocalDate().toString() : "N/A");
                    insPstmt.setString(9, ins.getPlanExpiryDateAsLocalDate() != null ? ins.getPlanExpiryDateAsLocalDate().toString() : "N/A");
                    insPstmt.setString(10, id);
                    insPstmt.executeUpdate();
                }

                // 3. Update Medical History
                try (PreparedStatement medPstmt = conn.prepareStatement(medicalHistorySql)) {
                    medPstmt.setString(1, medicalHist.getChronicConditions());
                    medPstmt.setString(2, medicalHist.getAllergies());
                    medPstmt.setString(3, medicalHist.getFamilyHistory());
                    medPstmt.setString(4, id);
                    medPstmt.executeUpdate();
                }

                try (PreparedStatement delPstmt = conn.prepareStatement(deleteSurgeriesSql)) {
                    delPstmt.setString(1, id);
                    delPstmt.executeUpdate();
                }
                try (PreparedStatement sPstmt = conn.prepareStatement(surgerySql)) {
                    for (Surgery s : surgeries) {
                        sPstmt.setString(1, id);
                        sPstmt.setString(2, s.getProcedure());
                        sPstmt.setString(3, s.getDate() != null ? s.getDate().toString() : "");
                        sPstmt.addBatch();
                    }
                    sPstmt.executeBatch();
                }

// 5. Reset & Re-insert Medications
                try (PreparedStatement delPstmt = conn.prepareStatement(deleteMedicationsSql)) {
                    delPstmt.setString(1, id);
                    delPstmt.executeUpdate();
                }
                try (PreparedStatement mPstmt = conn.prepareStatement(medSql)) {
                    for (Medication m : realMeds) {
                        mPstmt.setString(1, id);
                        mPstmt.setString(2, m.getMedicationName());
                        mPstmt.setString(3, m.getDosage());
                        mPstmt.setString(4, m.getFrequency());
                        mPstmt.setString(5, m.getPharmacy());
                        mPstmt.setString(6, m.getStartDate() != null ? m.getStartDate().toString() : "");
                        mPstmt.setString(7, m.getEndDate() != null ? m.getEndDate().toString() : "");
                        mPstmt.addBatch();
                    }
                    mPstmt.executeBatch();
                }

                conn.commit();
                System.out.println("Full record updated successfully.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Insurance getInsuranceByPatientId(String patientId) {
        String sql = "SELECT * FROM insurance_info WHERE patient_id = ?";

        try (Connection conn = getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Insurance(
                        rs.getString("patient_id"),
                        rs.getString("payor_relationship"),
                        rs.getString("primary_payor"),
                        rs.getString("subscriber_birth_date"),
                        rs.getString("policy_number"),
                        rs.getString("group_number"),
                        rs.getString("subscriber_first_name"),
                        rs.getString("subscriber_last_name"),
                        rs.getString("plan_effective_date"),
                        rs.getString("plan_expiry_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static MedicalHistory getMedicalHistoryByPatientId(String patientId) {
        String sql = "SELECT * FROM medical_history WHERE patient_id = ?";
        try (Connection conn = getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new MedicalHistory(
                        rs.getString("patient_id"),
                        rs.getString("chronic_conditions"),
                        rs.getString("allergies"),
                        rs.getString("family_history")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static ObservableList<Surgery> getSurgeriesByPatientId(String patientId) {
        ObservableList<Surgery> list = FXCollections.observableArrayList();
        String sql = "SELECT procedure_name, surgery_date FROM patient_surgeries WHERE patient_id = ?";

        try (Connection conn = getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String dateStr = rs.getString("surgery_date");
                LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : null;
                list.add(new Surgery(rs.getString("procedure_name"), date));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static ObservableList<Medication> getMedicationsByPatientId(String patientId) {
        ObservableList<Medication> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM patient_medications WHERE patient_id = ?";

        try (Connection conn = getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Parse dates
                String startStr = rs.getString("start_date");
                String endStr = rs.getString("end_date");

                LocalDate startDate = (startStr != null && !startStr.isEmpty()) ? LocalDate.parse(startStr) : null;
                LocalDate endDate = (endStr != null && !endStr.isEmpty()) ? LocalDate.parse(endStr) : null;

                list.add(new Medication(
                        rs.getString("name"),
                        rs.getString("dosage"),
                        rs.getString("frequency"),
                        rs.getString("pharmacy"),
                        startDate,
                        endDate
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Pad the list until it reaches 15 rows
        while (list.size() < 15) {
            list.add(new Medication()); // Uses your empty constructor
        }

        return list;
    }


}