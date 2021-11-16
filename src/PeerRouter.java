import java.util.*;
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
    final private Map<Character, InetAddress> routerMap;
    
    private List<InetAddress> nodeIPAddresses = new Vector<InetAddress>(); /* getNodeAddress("M1") -> nodeIPAddresses[0] */
    private List<Socket> clients = new Vector<Socket>();
    private InetAddress myIPAddress = InetAddress.getLocalHost();


    public PeerRouter(final char routerPrefixChar, final Map<Character, InetAddress> routerMap) {
        this.routerPrefixChar = routerPrefixChar;
        this.routerMap = routerMap;
        routerMap.put(routerPrefixChar, myIPAddress);
    }

    public static void main(String[] args) {
        // parse config file for my router prefix, and list of other routers
        var routerMap = new HashMap<Character,InetAddress>();
        PeerRouter p = new PeerRouter("M", routerMap); // construct
    }
    /**
     * @param nodeID the ID of a node, like "M4"
     * @returns the IP address of that node
     */
    private InetAddress resolveNodeIP(String nodeID) {
        throw new UnsupportedOperationException("not implemented");
    }
    /**
     * @returns the IP address of the node
     */
    private InetAddress requestNodeIP(char routerPrefix, String nodeID) {
       throw new UnsupportedOperationException("not implemented");
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