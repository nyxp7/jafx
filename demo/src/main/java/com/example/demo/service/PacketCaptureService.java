package com.example.demo.service;

import org.pcap4j.packet.*;

public class PacketCaptureService {



    public PacketCaptureService() {

    }

    private com.example.demo.model.Packet convertPacket(org.pcap4j.packet.Packet pcapPacket) {
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

        return new com.example.demo.model.Packet(
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
