import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import util.*;

import java.io.*;
import java.net.*;

/**
 * PeerRouter Should accept connections via java.net.Socket Should respond to
 * the command "REGISTER" by putting the client's IP into a table. Respond with
 * the node's ID ("M4") Should respond to the command "GET M4" by retrieving the
 * IP address of the 4th client Should respond to "UNREGISTER" by removing the
 * client IP if it exists. If "M" is not the router's own routerPrefixChar,
 * request "GET M4" from the router "M"
 */
public class PeerRouter implements AutoCloseable {
    /** Uniquely identifies this router */
    final private char routerPrefix; 
    /** A map of known routers */
    final private Map<Character, PeerRouterConnection> routerConnections = new ConcurrentHashMap<Character, PeerRouterConnection>();
    /** A list of nodes registered with this router. */
    private Map<Integer, InetAddress> nodes = new ConcurrentHashMap<Integer, InetAddress>();
    private AtomicInteger nextNodeID = new AtomicInteger(1);

    private InetAddress localhost;
    final int routerPort;

    private ServerSocket serverSocket;

    /**
     * Creates a PeerRouter instance, with the given ID and port number.
     * 
     * @throws IOException if an IO exception occurs while creating the ServerSocket
     */
    public PeerRouter(final char routerPrefix, final int routerPort) throws IOException {
        this.routerPrefix = routerPrefix;
        this.routerPort = routerPort;
        this.serverSocket = new ServerSocket(routerPort);
        this.localhost = InetAddress.getLocalHost();
    }

    /**
     * Establish socket connections.
     * 
     * @throws IOException
     */
    public void listen() throws IOException {
        // block until someone connects
        System.out.println("Router accepting connections at " + localhost + ":" + routerPort + "...");
        while (true) {
            Socket clientSocket = serverSocket.accept(); // blocking
            System.out.println("Accepted connection from " + clientSocket.getInetAddress() + "!");
            Thread t = new Thread(() -> {
                handleAccept(clientSocket);
            });
            t.start();
        }
    }

    public void handleAccept(Socket clientSocket) {
        try (clientSocket;
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            out.println("Your IP address is: " + clientSocket.getInetAddress());
            String command;
            while ((command = in.readLine()) != null) {
                System.out.println(clientSocket.getInetAddress() + ": " + command);
                out.println("You said: " + command.toUpperCase());
                out.flush();
            }
            System.out.println("Connection with " + clientSocket.getInetAddress() + " ended.");
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error communicating with " + clientSocket.getInetAddress() + ": " + e.getMessage());
        }
    }

    public void handleCommand(String command) {
        
    }

    /**
     * 
     */
    public void connectRouter(char routerPrefix, InetAddress addr, int port) {
        System.out.println("Establishing connection with router " + routerPrefix );
        var connection = PeerRouterConnection.connect(addr, routerPort);
        if (connection != null) {
            System.out.println("Establishing connection with router " + routerPrefix );
        }
        routerConnections.put(routerPrefix, connection);
    }

    public int registerNodeIP(InetAddress nodeAddr) {
        int id = nextNodeID.getAndAdd(1);
        nodes.put(id, nodeAddr);
        System.out.println("Registered " + nodeAddr + " as node " + routerPrefix + id);
        return id;
    }

    public void unregisterNodeIP(InetAddress nodeAddr) {
        nodes.keySet().removeIf(k -> nodes.get(k).equals(nodeAddr));
    }

    public static void main(String[] args) throws IOException {
        // Load configuration
        DotEnv.load(".env");
        char prefix = DotEnv.getEnvOrDefault("ROUTER_PREFIX", "M").charAt(0);
        int port = Integer.parseInt(DotEnv.getEnvOrDefault("ROUTER_PORT", "6666"));
        // Construct PeerRouter
        try (PeerRouter p = new PeerRouter(prefix, 6666);) {
            p.listen();
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }

    /**
     * @param routerPrefix the prefix of the router, e.g. 'M'
     * @param nodeNum      the number representing which node to get, e.g. '4'
     * @returns the IP address of that node, or null if it does not exist
     */
    public InetAddress resolveNodeIP(char routerPrefix, int nodeNum) {
        if (routerPrefix == this.routerPrefix) {
            // That's me! Let me check my table...
            return nodes.get(nodeNum);
        } else if (routerConnections.containsKey(routerPrefix)) {

        }
        System.out.println("Could not find router " + routerPrefix);
        return null;
    }

    // public static void temp(String par1) {
    //     String[] var1 = par1.split(" ");

    //     switch (var1[0]) {
    //     case "GET":
    //         getNodeAddress(var1[1]);
    //         break;
    //     case "REGISTER":
    //         // register(var1[1]);
    //         break;
    //     }

    // }

    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }
}