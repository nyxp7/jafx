package com.example.demo.service;

import com.example.demo.model.Alerte;
import com.example.demo.model.Packet;
import com.example.demo.regles.Regle;

import java.util.ArrayList;
import java.util.List;

public class RuleEngineDetector {

    private final List<Regle> regles =
            new ArrayList<>();

    private final AlertManagerDetector alertManager;

    public RuleEngineDetector(
            AlertManagerDetector alertManager
    ) {
        this.alertManager = alertManager;
    }

    public void addRule(Regle regle) {
        regles.add(regle);
    }

    public void analyze(Packet packet) {

        for (Regle rule : regle) {

            if (regle.matches(packet)) {

                Alerte alerte =
                        new Alerte(
                                rule.getAlertMessage(),
                                rule.getSeverity()
                        );

                alertManager.raiseAlert(alerte);
            }
        }
    }
}