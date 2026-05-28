package com.example.demo.model;

import java.time.LocalDateTime;
import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Alerte {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty severity = new SimpleStringProperty();
    private final StringProperty protocol = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> timestamp = new SimpleObjectProperty<>();
    private final StringProperty sourceIp = new SimpleStringProperty();
    private final StringProperty destinationIp = new SimpleStringProperty();
    private final IntegerProperty port = new SimpleIntegerProperty();

    public Alerte(int id, String title, String description, String severity, String protocol,
                 LocalDateTime timestamp, String sourceIp, String destinationIp, int port) {
        this.id.set(id);
        this.title.set(title);
        this.description.set(description);
        this.severity.set(severity);
        this.protocol.set(protocol);
        this.timestamp.set(timestamp);
        this.sourceIp.set(sourceIp);
        this.destinationIp.set(destinationIp);
        this.port.set(port);
    }


    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty severityProperty() { return severity; }
    public StringProperty protocolProperty() { return protocol; }
    public ObjectProperty<LocalDateTime> timestampProperty() { return timestamp; }
    public StringProperty sourceIpProperty() { return sourceIp; }
    public StringProperty destinationIpProperty() { return destinationIp; }
    public IntegerProperty portProperty() { return port; }


    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public String getSeverity() { return severity.get(); }
    public String getProtocol() { return protocol.get(); }
    public LocalDateTime getTimestamp() { return timestamp.get(); }
    public String getSourceIp() { return sourceIp.get(); }
    public String getDestinationIp() { return destinationIp.get(); }
    public int getPort() { return port.get(); }


    public String getMessage() {
        return message;
    }

    public Severité getSeverity() {
        return severité;
    }

    public ObjectProperty<LocalDateTime> getTimestamp() {
        return timestamp;
    }
}

