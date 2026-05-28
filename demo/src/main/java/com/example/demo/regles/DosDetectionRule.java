package com.example.demo.regles;

import com.example.demo.model.Packet;
import com.example.demo.model.Severite;

public class DosDetectionRule implements Regle {
    
    private static final int PORT_SCAN_THRESHOLD = 100;
    private int connectionCount = 0;
    private long windowStart = System.currentTimeMillis();
    private static final long WINDOW_MS = 1000;

    @Override
    public boolean matches(Packet packet) {
        long now = System.currentTimeMillis();
        if (now - windowStart > WINDOW_MS) {
            connectionCount = 0;
            windowStart = now;
        }
        connectionCount++;
        
        if (connectionCount > PORT_SCAN_THRESHOLD) {
            return true;
        }
        
        if (packet.getDestinationPort() < 1024 && packet.getPayload().isEmpty()) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getAlertMessage() {
        return "Possible DoS attack or port scan detected";
    }

    @Override
    public Severite getSeverity() {
        return Severite.HIGH;
    }
}
