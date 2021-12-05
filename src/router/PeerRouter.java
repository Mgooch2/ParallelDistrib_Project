package router;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import util.*;
import util.RoutingCommon;

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
    final private Map<Character, InetSocketAddress> routers = new ConcurrentHashMap<Character, InetSocketAddress>();
    /** A list of nodes registered with this router. */
    private Map<Integer, InetSocketAddress> nodes = new ConcurrentHashMap<Integer, InetSocketAddress>();
    private AtomicInteger nextNodeID = new AtomicInteger(1);

    private InetAddress localhost;
    final int routerPort;

    private ServerSocket serverSocket;

    /**
     * Creates a PeerRouter instance, with the given ID and port number.
     * 
     * @param routerPrefix A character which will identify this router
     * @param routerPort   The port this router will accept connections from
     * @param routersList  A comma-separated list of other router specifications;
     *                     see parseRoutersList
     * @throws IOException if an IO exception occurs while creating the ServerSocket
     */
    public PeerRouter(final char routerPrefix, final int routerPort, final Map<Character, InetSocketAddress> routers)
            throws IOException {
        this.routerPrefix = routerPrefix;
        this.routerPort = routerPort;
        this.serverSocket = new ServerSocket(routerPort);
        this.localhost = InetAddress.getLocalHost();
        if (routers != null) {
            System.out.println(routers.toString());
            this.routers.putAll(routers);
        }
    }

    public static void main(String[] args) throws IOException {
        // Load configuration
        DotEnv.load(".env");
        final char ROUTER_PREFIX = DotEnv.getEnv("ROUTER_PREFIX").charAt(0);
        final int ROUTER_PORT = Integer.parseInt(DotEnv.getEnv("ROUTER_PORT"));
        final String FRIEND_ROUTERS = DotEnv.getEnv("FRIEND_ROUTERS");

        // Construct PeerRouter
        try (PeerRouter p = new PeerRouter(ROUTER_PREFIX, ROUTER_PORT, parseRoutersList(FRIEND_ROUTERS));) {
            p.listen();
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }

    /**
     * Establish socket connections.
     * Blocks forever. On accept, spawns a new thread and runs handleAccept(clientSocket).
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
            // out.println("Your IP address is: " + clientSocket.getInetAddress());
            String command;
            while ((command = in.readLine()) != null) {
                // DEBUG: Print received command
                System.out.println(clientSocket.getInetAddress() + ": " + command);
                String response = handleCommand(command, clientSocket);
                System.out.println("My response: " + response);
                out.println(response);
                out.flush();
            }
            System.out.println("Connection with " + clientSocket.getInetAddress() + " ended.");
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error communicating with " + clientSocket.getInetAddress() + ": " + e.getMessage());
        }
    }

    /**
     * 
     * @param command      An input command
     * @param clientSocket The socket from which the command was received.
     * @return The string to reply with. If the request could not be handled,
     *         responds "FAIL " followed by a descriptive message.
     */
    public String handleCommand(String command, Socket clientSocket) {
        // Split command on first space
        String[] comps = command.split(" ", 2);
        String cmd = comps[0].toUpperCase();
        String args = "";
        if (comps.length > 1) {
            args = comps[1];
        }
        switch (cmd) {
            case "ECHO":
                return args;
            case "UPPER":
                return args.toUpperCase();
            case "GET": // Must be followed by an 
                if (args.isEmpty()) {
                    return "FAIL Get command must contain a node identifier";
                }
                // 'm' and 'M' resolve the same router.
                try {
                    char prefix = args.toUpperCase().charAt(0);
                    int num = Integer.parseInt(args.substring(1));
                    InetSocketAddress addr = resolveNodeIP(prefix, num);
                    return addr.getHostName() + ":" + addr.getPort();
                } catch (NumberFormatException e) {
                    return "FAIL Parsing " + args.substring(1);
                } catch (Exception e) {
                    System.err.println();
                    String msg = "Error resolving node " + args + ": " + e.getMessage();
                    System.err.println(msg);
                    return "FAIL " + msg;
                }
            case "REGISTER": // REGISTER [port num]
                try {
                    int port = Integer.parseInt(args);
                    return registerNodeIP(new InetSocketAddress(clientSocket.getInetAddress(), port));
                } catch (NumberFormatException e) {
                    return "FAIL Register command must specify a port number.";
                }
            default:
                return "FAIL Unknown command";
        }
    }

    /**
     * Parses a raw String containing a specification A router specification is a
     * string in the format (char)Prefix:(String)Hostname:(int)Port
     * 
     * @param routersList A comma-separated string of router specifications,
     *                    example: "N:hostname:6667,O:hostname:6668"
     */
    public static Map<Character, InetSocketAddress> parseRoutersList(String routersList) {
        var routers = new HashMap<Character, InetSocketAddress>();
        for (String r : routersList.split(",")) {
            if (r != null && !r.isBlank()) {
                String[] parts = r.split(":");
                char prefix = parts[0].charAt(0);
                String host = parts[1];
                int port = Integer.parseInt(parts[2]);
                var addr = new InetSocketAddress(host, port);
                routers.put(prefix, addr);
            }
        }
        return routers;
    }

    /**
     * Places a new IP address into the node lookup table, and assign it an
     * identifier.
     * If it already exists, return the existing identifier.
     * 
     * @param nodeAddr
     * @return The identifier (e.g. "M1") assigned to this node.
     */
    public String registerNodeIP(InetSocketAddress nodeAddr) {
        if (nodes.containsValue(nodeAddr)) { // node is already registered...
            int id = nodes.entrySet().stream()
                    .filter(entry -> Objects.equals(entry.getValue(), nodeAddr))
                    .map(Map.Entry::getKey)
                    .findAny().orElseThrow();
            return "" + routerPrefix + id;
        } else { // otherwise, register a new node.
            int id = nextNodeID.getAndAdd(1);
            nodes.put(id, nodeAddr);
            System.out.println("Registered " + nodeAddr + " as node " + routerPrefix + id);
            return "" + routerPrefix + id;
        }
    }

    public void unregisterNodeIP(InetSocketAddress nodeAddr) {
        nodes.keySet().removeIf(k -> nodes.get(k).equals(nodeAddr));
    }

    /**
     * @param prefix the prefix of the router, e.g. 'M'
     * @param num    the number representing which node to get, e.g. '4'
     * @returns the socket address of that node
     * @throws Exception If the node could not be resolved.
     */
    public InetSocketAddress resolveNodeIP(char prefix, int num) throws Exception {
        if (prefix == this.routerPrefix) { // That's me! Let me check my table...
            if (!nodes.containsKey(num)) { // That node is not registered.
                throw new RuntimeException("Node " + prefix + num + " is not registered.");
            } // else...
            return nodes.get(num);
        } else if (!routers.containsKey(prefix)) {
            throw new RuntimeException("A router with prefix " + prefix + " is not configured.");
        }
        // Attempt to resolve the node IP from a remote router.
        InetSocketAddress r = routers.get(prefix);
        return RoutingCommon.requestNodeSocketAddress("" + prefix + num, r);
    }

    // Implement the close() method so we can use the PeerRouter in a try-with
    // block.
    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}