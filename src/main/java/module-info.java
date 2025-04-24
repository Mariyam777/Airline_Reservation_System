module com.example.finalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;
    requires java.desktop;


    opens com.example.final_project to javafx.fxml;
    exports com.example.final_project;
}
