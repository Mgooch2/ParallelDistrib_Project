package util;

import java.io.*;
import java.net.*;

/**
 * A utility class for making connections to a PeerRouter
 */
public class PeerRouterConnection implements AutoCloseable {
    private final InetAddress address; // the address of the socket to connect to
    private final int port; // the port of the socket to connect to.

    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;

    public static PeerRouterConnection connect(InetAddress router, int port) {
        try {
            var r = new PeerRouterConnection(router, port);
            r.doConnect();
            return r;
        } catch (IOException e) {
            System.out.println("IO Error connecting with " + router + ": " + e.getMessage());
            return null;
        }
    }
    
    private PeerRouterConnection(InetAddress router, int port) throws IOException {
        this.address = router;
        this.port = port;
        this.socket = new Socket();

    }
    private void doConnect() throws IOException {
        SocketAddress serverAddress = new InetSocketAddress(address, port);
        this.socket.connect(serverAddress);
        socket.setKeepAlive(true);
        socket.isConnected();
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(String message) {
        try {
            if (!socket.isConnected()) doConnect();
            out.write(message);
        } catch (Exception e) {
            System.err.println("Error sending message to " + address + ": " + e.getMessage());
        }
    }
    public String recieveMessage() {
        try {
            if (!socket.isConnected()) doConnect();
            return in.readLine();
        } catch (Exception e) {
            System.err.println("Error receiving message from " + address + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {}
    }
}
