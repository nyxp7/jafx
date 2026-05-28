package com.example.demo.regles;

import com.example.demo.model.Packet;
import com.example.demo.model.severité;

public interface Regle {
    boolean matches(Packet packet);
    String getAlertMessage();
    severité getSeverity();
}
