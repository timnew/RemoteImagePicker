package me.timnew.remoteimagepicker.servers;

import java.net.InetAddress;

import static java.lang.String.format;

public class ServerInfo {
    public static final int DEFAULT_PORT = 4000;

    public final InetAddress address;
    public final int port;

    public ServerInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public ServerInfo(InetAddress address) {
        this(address, DEFAULT_PORT);
    }

    @Override
    public String toString() {
        return format("%s:%d", address.getHostAddress(), port);
    }

    public String buildUrl(String path) {
        return format("http://%s:%d%s", address.getHostAddress(), port, path);
    }

    public String buildUrl(String template, Object... args) {
        return buildUrl(format(template, args));
    }
}
