package com.example.demo.service;

import com.example.demo.model.Packet;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.pcap4j.core.BpfProgram.BpfCompileMode.OPTIMIZE;

public class PacketCaptureService {

    private final AtomicInteger incomingCount = new AtomicInteger(0);
    private final AtomicInteger outgoingCount = new AtomicInteger(0);
    private final AtomicInteger connectionCount = new AtomicInteger(0);
    
    private PcapHandle pcapHandle;
    private ExecutorService executor;
    private volatile boolean running = false;
    
    private Consumer<Packet> packetConsumer;

    public PacketCaptureService() {
    }

    public void setPacketConsumer(Consumer<Packet> consumer) {
        this.packetConsumer = consumer;
    }

    public int getIncomingCount() {
        return incomingCount.get();
    }

    public int getOutgoingCount() {
        return outgoingCount.get();
    }

    public int getConnectionCount() {
        return connectionCount.get();
    }

    public void startCapture(String deviceName, RuleEngineDetector ruleEngine) throws PcapNativeException, NotOpenException {
        if (running) return;

        PcapNetworkInterface nif;
        if (deviceName == null || deviceName.trim().isEmpty()) {
            // If no device name provided, get the first available interface
            List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
            if (allDevs.isEmpty()) {
                throw new PcapNativeException("No network interfaces found");
            }
            nif = allDevs.get(0);
            System.out.println("Using default network interface: " + nif.getName() + " (" + nif.getDescription() + ")");
        } else {
            try {
                nif = Pcaps.getDevByName(deviceName);
            } catch (PcapNativeException e) {
                // If specified device not found, fall back to first available
                List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
                if (allDevs.isEmpty()) {
                    throw new PcapNativeException("No network interfaces found");
                }
                nif = allDevs.get(0);
                System.out.println("Device '" + deviceName + "' not found, using: " + nif.getName());
            }
        }

        String bpfFilter = "ip";
        PcapHandle.Builder handleBuilder = new PcapHandle.Builder(nif.getName())
                .snaplen(65536)
                .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                .bufferSize(1 * 1024 * 1024)
                .timeoutMillis(10);

        pcapHandle = handleBuilder.build();
        pcapHandle.setFilter(bpfFilter, OPTIMIZE);
        running = true;
        
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                while (running) {
                    org.pcap4j.packet.Packet pcapPacket = pcapHandle.getNextPacketEx();
                    if (pcapPacket != null) {
                        Packet packet = convertPacket(pcapPacket);
                        if (packet != null) {
                            updateStats(packet);
                            if (packetConsumer != null) {
                                packetConsumer.accept(packet);
                            }
                            if (ruleEngine != null) {
                                ruleEngine.analyze(packet);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (running) {
                    System.err.println("Capture error: " + e.getMessage());
                }
            }
        });
    }

    public void stopCapture() {
        running = false;
        if (pcapHandle != null) {
            try {
                pcapHandle.breakLoop();
            } catch (NotOpenException e) {
                // Ignore if already closed
            }
            pcapHandle.close();
        }
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    private void updateStats(Packet packet) {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            String localHostAddress = localAddress.getHostAddress();
            
            if (packet.getSourceIp().equals(localHostAddress)) {
                outgoingCount.incrementAndGet();
            } else {
                incomingCount.incrementAndGet();
            }
            connectionCount.incrementAndGet();
        } catch (UnknownHostException e) {
            incomingCount.incrementAndGet();
            connectionCount.incrementAndGet();
        }
    }

    private Packet convertPacket(org.pcap4j.packet.Packet pcapPacket) {
        IpV4Packet ipPacket = pcapPacket.get(IpV4Packet.class);
        if (ipPacket == null) return null;

        String srcIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipPacket.getHeader().getDstAddr().getHostAddress();
        String protocol = ipPacket.getHeader().getProtocol().name();

        String payload = "";
        if (pcapPacket.getPayload() != null) {
            payload = pcapPacket.getPayload().toString();
        }

        int srcPort = 0;
        int dstPort = 0;

        TcpPacket tcp = pcapPacket.get(TcpPacket.class);
        UdpPacket udp = pcapPacket.get(UdpPacket.class);

        if (tcp != null) {
            srcPort = tcp.getHeader().getSrcPort().valueAsInt();
            dstPort = tcp.getHeader().getDstPort().valueAsInt();
        }

        if (udp != null) {
            srcPort = udp.getHeader().getSrcPort().valueAsInt();
            dstPort = udp.getHeader().getDstPort().valueAsInt();
        }

        return new Packet(
                srcIp,
                dstIp,
                srcPort,
                dstPort,
                protocol,
                payload,
                System.currentTimeMillis()
        );
    }
}
