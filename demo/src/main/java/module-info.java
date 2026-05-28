module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.pcap4j.core;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.service;
    opens com.example.demo.service to javafx.fxml;
    exports com.example.demo.controller;
    opens com.example.demo.controller to javafx.fxml;
    exports com.example.demo.model;
    opens com.example.demo.model to javafx.fxml;
    exports com.example.demo.regles;
    opens com.example.demo.regles to javafx.fxml;
}