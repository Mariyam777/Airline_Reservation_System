package com.example.final_project;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private StackPane mainPane;
    @FXML private TableView<Flight> flightTable;
    @FXML private TableColumn<Flight, String> flightNumberColumn;
    @FXML private TableColumn<Flight, String> originColumn;
    @FXML private TableColumn<Flight, String> destinationColumn;
    @FXML private TableColumn<Flight, String> departureColumn;
    @FXML private TableColumn<Flight, Double> priceColumn;
    @FXML private TableColumn<Flight, Integer> availableSeatsColumn;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML private Button addFlightButton;
    @FXML private Button deleteFlightButton;
    @FXML private Button bookFlightButton;

    private String currentUser;

    @FXML
    private void initialize() {
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availableSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
    }

    @FXML
    private void handleLogin() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            String role = authenticateUser(username, password);
            if (role != null) {
                currentUser = username;
                statusLabel.setText("Welcome, " + username + " (" + role + ")");
                loadFlights();
                configureUIForRole(role);
            } else {
                Main.showError("Login Failed", "Invalid credentials");
            }
        } catch (SQLException e) {
            Main.showError("Database Error", e.getMessage());
        }
    }
    @FXML
    private void openRegisterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("User Registration");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void configureUIForRole(String role) {
        if (role.equals("admin")) {
            addFlightButton.setVisible(true);
            deleteFlightButton.setVisible(true);
            bookFlightButton.setVisible(false);
        } else {
            addFlightButton.setVisible(false);
            deleteFlightButton.setVisible(false);
            bookFlightButton.setVisible(true);
        }
    }

    private String authenticateUser(String username, String password) throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test", "root", "root123")) {

            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT role FROM users WHERE username = ? AND password = ?"
            );
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }
            return null;
        }
    }

    private void loadFlights() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test", "root", "root123")) {

            List<Flight> flights = new ArrayList<>();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM flights");

            while (rs.next()) {
                flights.add(new Flight(
                        rs.getString("flight_number"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("departure_date"),
                        rs.getDouble("price"),
                        rs.getInt("total_seats"),
                        rs.getInt("booked_seats")
                ));
            }

            flightTable.setItems(FXCollections.observableArrayList(flights));
        }
    }


    // 💡 Открытие отдельного окна для добавления рейса
    @FXML
    private void addFlight() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_flight.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Flight");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadFlights();
        } catch (Exception e) {
            Main.showError("Error", "Unable to open Add Flight window:\n" + e.getMessage());
        }
    }

    @FXML
    private void deleteFlight() {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/test", "root", "root123")) {

                String delete = "DELETE FROM flights WHERE flight_number = ?";
                PreparedStatement stmt = conn.prepareStatement(delete);
                stmt.setString(1, selectedFlight.getFlightNumber());
                stmt.executeUpdate();

                loadFlights();
            } catch (SQLException e) {
                Main.showError("Database Error", e.getMessage());
            }
        } else {
            Main.showError("Delete Error", "Select a flight to delete.");
        }
    }

    @FXML
    private void bookFlight() {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null && selectedFlight.getAvailableSeats() > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Booking");
            confirm.setContentText("Book flight " + selectedFlight.getFlightNumber() + "?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try (Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/test", "root", "root123")) {

                        String updateQuery = "UPDATE flights SET booked_seats = booked_seats + 1 WHERE flight_number = ?";
                        PreparedStatement stmt = conn.prepareStatement(updateQuery);
                        stmt.setString(1, selectedFlight.getFlightNumber());
                        stmt.executeUpdate();

                        loadFlights();
                    } catch (SQLException e) {
                        Main.showError("Database Error", e.getMessage());
                    }
                }
            });
        } else {
            Main.showError("Booking Error", "Select a flight with available seats.");
        }
    }

    public static class Flight {
        private final String flightNumber;
        private final String origin;
        private final String destination;
        private final String departureDate;
        private final double price;
        private final int totalSeats;
        private final int bookedSeats;

        public Flight(String flightNumber, String origin, String destination,
                      String departureDate, double price, int totalSeats, int bookedSeats) {
            this.flightNumber = flightNumber;
            this.origin = origin;
            this.destination = destination;
            this.departureDate = departureDate;
            this.price = price;
            this.totalSeats = totalSeats;
            this.bookedSeats = bookedSeats;
        }

        public String getFlightNumber() { return flightNumber; }
        public String getOrigin() { return origin; }
        public String getDestination() { return destination; }
        public String getDepartureDate() { return departureDate; }
        public double getPrice() { return price; }
        public int getAvailableSeats() { return totalSeats - bookedSeats; }
    }
}

