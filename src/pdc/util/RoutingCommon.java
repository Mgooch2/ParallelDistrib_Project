package pdc.util;

import java.io.*;
import java.net.*;

public class RoutingCommon {
    final static int SOCKET_TIMEOUT_DURATION_MS = 5000;

    /**
     * Sends a single command to a ServerSocket, returns the response.
     * 
     * @param endpoint The ServerSocket to connect to
     * @param command  A single line which will be printed to the router.
     * @return The router's response.
     */
    public static String sendSocketCommand(InetSocketAddress endpoint, String command)
            throws IOException, SocketTimeoutException {
        if (endpoint == null) {
            return null;
        }
        try (Socket s = new Socket();) {
            s.connect(endpoint, SOCKET_TIMEOUT_DURATION_MS);
            var out = new PrintWriter(s.getOutputStream());
            var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out.println(command);
            out.flush();
            // Await input
            String response = in.readLine();
            return response;
        }
    }

    /**
     * Connect to the router with the given socketaddress, and request the node with
     * the given name using a GET command. Parse and return the response.
     * @throws Exception If the requested node cannot be resolved.
     */
    public static InetSocketAddress requestNodeSocketAddress(String nodeName, InetSocketAddress routerAddr)
            throws Exception {
        String command = "GET " + nodeName; // -> GET "M4"
        try {
            // Send a request.
            String response = RoutingCommon.sendSocketCommand(routerAddr, command);
            if (response.startsWith("FAIL")) {
                throw new RuntimeException("Router " + routerAddr.toString() + " responded: " + response);
            }
            String[] parts = response.split(":"); // hostname:PORT => [hostname, PORT];
            return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
        } finally {
        }
    }
}