package com.example.demo.service;



import com.example.demo.model.Alerte;

import java.util.ArrayList;
import java.util.List;

public class AlertManagerDetector {

    private final List<Alerte> alerts =
            new ArrayList<>();

    public void raiseAlert(Alerte alert) {

        alerts.add(alert);

        System.out.println(
                "[ALERT] "
                        + alerte.getSeverity()
                        + " - "
                        + alerte.getMessage()
        );
    }

    public List<Alerte> getAllAlerts() {
        return alerts;
    }
}