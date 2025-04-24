package com.example.final_project;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        roleChoiceBox.setValue("user");
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleChoiceBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("All fields are required.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test", "root", "root123")) {

            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                statusLabel.setText("Username already exists.");
            } else {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role);
                stmt.executeUpdate();
                statusLabel.setText("Registration successful.");
            }

        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }
}
