package com.example.final_project;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AddFlightController {

    @FXML private TextField flightNumberField;
    @FXML private TextField originField;
    @FXML private TextField destinationField;
    @FXML private TextField departureDateField;
    @FXML private TextField priceField;
    @FXML private TextField totalSeatsField;

    @FXML
    private void handleSave() {
        String flightNumber = flightNumberField.getText();
        String origin = originField.getText();
        String destination = destinationField.getText();
        String departureDate = departureDateField.getText();
        double price = Double.parseDouble(priceField.getText());
        int totalSeats = Integer.parseInt(totalSeatsField.getText());

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test", "root", "root123")) {

            String sql = "INSERT INTO flights (flight_number, origin, destination, departure_date, price, booked_seats, total_seats) " +
                    "VALUES (?, ?, ?, ?, ?, 0, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, flightNumber);
            stmt.setString(2, origin);
            stmt.setString(3, destination);
            stmt.setString(4, departureDate);
            stmt.setDouble(5, price);
            stmt.setInt(6, totalSeats);
            stmt.executeUpdate();

            // Закрываем окно после добавления
            Stage stage = (Stage) flightNumberField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
