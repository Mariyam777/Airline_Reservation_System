package com.example.final_project;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Initialize database
            initializeDatabase();

            // 2. Load FXML
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/final_project/main.fxml"));

            // 3. Setup stage
            primaryStage.setTitle("Airline Reservation System");
            primaryStage.setScene(new Scene(root, 900, 600));
            primaryStage.show();

        } catch (Exception e) {
            showError("Fatal Error", "Application failed to start:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test", "root", "root123")) {

            // Create database and tables
            conn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS test");
            conn.createStatement().executeUpdate("USE test");

            // Create flights table
            conn.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS flights (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "flight_number VARCHAR(20) NOT NULL, " +
                            "origin VARCHAR(50) NOT NULL, " +
                            "destination VARCHAR(50) NOT NULL, " +
                            "departure_date VARCHAR(20) NOT NULL, " +
                            "price DECIMAL(10,2) NOT NULL, " +
                            "booked_seats INT DEFAULT 0, " +
                            "total_seats INT NOT NULL)"
            );

// Create users table
            conn.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (\n" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                            "    username VARCHAR(50) UNIQUE NOT NULL,\n" +
                            "    password VARCHAR(50) NOT NULL,\n" +
                            "    role VARCHAR(10) NOT NULL DEFAULT 'user'\n" +
                            ");\n"
            );

// Insert default user
            conn.createStatement().executeUpdate(
                    "INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin123')"
            );

        } catch (SQLException e) {
            throw new Exception("Database initialization failed: " + e.getMessage());
        }
    }


    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}