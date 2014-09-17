package me.timnew.remoteimagepicker.servers;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import me.timnew.remoteimagepicker.events.DiscoveryStatusChangedEvent;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.*;
import org.androidannotations.api.BackgroundExecutor;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static me.timnew.remoteimagepicker.events.DiscoveryStatusChangedEvent.DiscoveryChangeTypes;
import static me.timnew.remoteimagepicker.events.DiscoveryStatusChangedEvent.DiscoveryChangeTypes.SERVER_ONLINE;

@EBean
public class DiscoveryService {
    private static final String TAG = "ImageServerDiscovery";

    private static final int DISCOVERY_PORT = 4000;
    private static final int TIMEOUT_MS = 500;

    public static final String BROADCAST_DISCOVERY_REQUEST = "broadcastDiscoveryRequest";
    public static final String LISTEN_FOR_DISCOVERY_RESPONSE = "listenForDiscoveryResponse";

    @RootContext
    protected Context context;

    @SystemService
    protected WifiManager wifiManager;

    @Bean
    protected Bus bus;

    private DatagramSocket socket;

    private Set<InetAddress> servers = new HashSet<InetAddress>();

    @AfterInject
    protected void afterInject() {
        bus.register(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stop();
        bus.unregister(this);
    }

    public void start() {
        try {
            socket = createSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        listenForResponses();
        broadcastRequest();
    }

    public void stop() {
        BackgroundExecutor.cancelAll(BROADCAST_DISCOVERY_REQUEST, true);
        BackgroundExecutor.cancelAll(LISTEN_FOR_DISCOVERY_RESPONSE, true);
        socket.close();
    }

    private DatagramSocket createSocket() throws SocketException {
        DatagramSocket socket = new DatagramSocket();

        socket.setBroadcast(true);
        socket.setSoTimeout(TIMEOUT_MS);

        return socket;
    }

    @Background(id = BROADCAST_DISCOVERY_REQUEST)
    protected void broadcastRequest() {
        String data = "ImageServer";

        Log.d(TAG, "Looking for server");

        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), getBroadcastAddress(), DISCOVERY_PORT);

        while (true) {
            try {
                socket.send(packet);
                Thread.sleep(30000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private InetAddress getBroadcastAddress() {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();

        if (dhcp == null) {
            Log.d(TAG, "Could not get dhcp info");
            return null;
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);

        try {
            return InetAddress.getByAddress(quads);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Background(id = LISTEN_FOR_DISCOVERY_RESPONSE)
    protected void listenForResponses() {
        byte[] buf = new byte[1024];

        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength());
                Log.d(TAG, "Received response " + msg);

                if (msg.startsWith("ImageServer:"))
                    onServerResponded(packet);

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onServerResponded(DatagramPacket packet) {
        InetAddress address = packet.getAddress();

        if (servers.add(address)) {
            notifyServerStatusChanged(address, SERVER_ONLINE);
        }
    }

    private void notifyServerStatusChanged(InetAddress address, DiscoveryChangeTypes type) {
        Log.d(TAG, format("%s %s", type, address));

        DiscoveryStatusChangedEvent event = new DiscoveryStatusChangedEvent(address, type);

        bus.post(event);
    }

    public void onEvent(DiscoveryServiceCommands command) {
        command.applyTo(this);
    }

    public static enum DiscoveryServiceCommands {
        START_DISCOVERY {
            @Override
            public void applyTo(DiscoveryService service) {
                service.start();
            }
        },
        STOP_DISCOVERY {
            @Override
            public void applyTo(DiscoveryService service) {
                service.stop();
            }
        };

        public abstract void applyTo(DiscoveryService service);
    }
}
