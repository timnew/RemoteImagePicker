package me.timnew.remoteimagepicker.events;

import me.timnew.remoteimagepicker.servers.DiscoveryService;
import me.timnew.remoteimagepicker.servers.ServerInfo;

import java.net.InetAddress;

public class DiscoveryStatusChangedEvent {
    public final ServerInfo server;
    public final DiscoveryChangeTypes type;

    public DiscoveryStatusChangedEvent(InetAddress address, DiscoveryChangeTypes type) {
        this.server = new ServerInfo(address);
        this.type = type;
    }

    public static enum DiscoveryChangeTypes {
        SERVER_ONLINE,
        SERVER_OFFLINE
    }
}
