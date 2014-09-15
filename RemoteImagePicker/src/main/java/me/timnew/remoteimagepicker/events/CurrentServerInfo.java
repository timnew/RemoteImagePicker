package me.timnew.remoteimagepicker.events;

import me.timnew.remoteimagepicker.servers.ServerInfo;

public class CurrentServerInfo {
    public final ServerInfo currentServer;

    public CurrentServerInfo(ServerInfo currentServer) {
        this.currentServer = currentServer;
    }
}
