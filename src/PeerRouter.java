import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.lang.*;
import java.net.*;


/**
 * PeerRouter
 * Should accept connections via java.net.Socket
 * Should respond to the command "REGISTER" by putting the client's IP into a table.
    * Respond with the node's ID ("M4")
 * Should respond to the command "GET M4" by retrieving the IP address of the 4th client
 * Should respond to "UNREGISTER" by removing the client IP if it exists.
 * If "M" is not the router's own routerPrefixChar, request "GET M4" from the router "M"
 */
public class PeerRouter {
    final private char routerPrefixChar; /* Uniquely identifies this router */
    final private Map<Character, InetAddress> routerMap; // List of routers

    final int routerPort = 6666;
    
    private SequentialRegistry<InetAddress> nodes = new SequentialRegistry<InetAddress>(1);
    private List<Socket> clients = new Vector<Socket>();
    private InetAddress myIPAddress;

    private ServerSocket serverSocket;

    /**
     * 
     */
    public PeerRouter(final char routerPrefixChar, final Map<Character, InetAddress> routerMap) {
        this.routerPrefixChar = routerPrefixChar;
        this.routerMap = routerMap;
        try {
            this.myIPAddress = InetAddress.getLocalHost();
            this.serverSocket = new ServerSocket(routerPort);
        } catch (IOException e) {
            System.err.println("Error creating ServerSocket on port " + routerPort);
            System.err.println(e.getMessage());
            System.exit(1);
        }
        routerMap.put(routerPrefixChar, myIPAddress);
    }

    public void accept() throws IOException{
        // block until someone connects
        System.out.println("Router accepting connections at " + myIPAddress + ":" + routerPort + "...");
        while (true) {
            Socket sock = serverSocket.accept(); // blocking
            System.out.println("Accepted connection from " + sock.getInetAddress() + "!");
            Thread t = new Thread(() -> {
                try (
                    sock;
                    PrintWriter out = new PrintWriter(sock.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                ) {
                    out.println("Your IP address is: " + sock.getInetAddress());
                    String command;
                    while ((command = in.readLine()) != null) {
                        System.out.println(sock.getInetAddress() + ": " + command);
                        out.println("You said: " + command);
                        out.flush();
                    }
                    System.out.println("Connection closed.");
                    sock.close();
                    clients.remove(sock);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }

    public static void main(String[] args) throws IOException {
        // TODO: parse config file for my router prefix, and list of other routers
        var routerMap = new ConcurrentHashMap<Character,InetAddress>();
        PeerRouter p = new PeerRouter('M', routerMap); // construct
        p.accept();
    }
    /**
     * @param routerPrefix the prefix of the router, e.g. 'M'
     * @param nodeNum the number representing which node to get, e.g. '4'
     * @returns the IP address of that node, or null if it does not exist
     */
    private InetAddress resolveNodeIP(char routerPrefix, int nodeNum) {
        if (routerPrefix == this.routerPrefixChar) {
            // That's me! Let me check my table...
            return nodes.get(nodeNum);
        } else if (routerMap.containsKey(routerPrefix)) {

        }
        System.out.println("Could not find router " + routerMap);
        return null;
    }


    public static void temp(String par1) {
        String[] var1 = par1.split(" ");

        switch(var1[0]) {
            case "GET":
                getNodeAddress(var1[1]);
                break;
            case "REGISTER":
                //register(var1[1]);
                break;
        }

    }

    public static InetAddress getNodeAddress(String var1) {
        char par1 = 'g';
        int par2 = 7;

        //access address table using split request char and int and return address

        return null;
    }

    private class SThread extends Thread {
            @Override
            public void run() {

            }
    }
}