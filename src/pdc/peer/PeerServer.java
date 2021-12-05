package pdc.peer;
/*
 * PARAMETERS FOR THE CONSOLE:
 * register - to enable
 * get - to have a certain IP address
 * 
 */

import java.io.*;
import java.net.*;

import pdc.util.*;

public abstract class PeerServer implements AutoCloseable {
    InetSocketAddress routerAddress; // the address of the router which owns me
    ServerSocket serverSocket;

    String myNodeIP;

    public PeerServer(String routerHostname, int routerPort) throws IOException {
        this.routerAddress = new InetSocketAddress(routerHostname, routerPort);
        this.serverSocket = new ServerSocket(0);
    }

    public void registerMe() throws Exception {
        String myID = RoutingCommon.sendSocketCommand(routerAddress, "REGISTER " + serverSocket.getLocalPort());
        System.out.println("Registered with " + routerAddress + " as node " + myID);
    }

    /**
     * Establish socket connections.
     * Blocks forever. On accept, spawns a new thread and runs
     * handleAccept(clientSocket).
     * 
     * @throws IOException
     */
    public void listen() throws IOException {
        // block until someone connects
        System.out.println(
                "Router accepting connections at " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort()
                        + "...");
        while (true) {
            Socket clientSocket = serverSocket.accept(); // blocking
            System.out.println("Accepted connection from " + clientSocket.getInetAddress() + "!");
            Thread t = new Thread(() -> {
                handleAccept(clientSocket);
            });
            t.start();
        }
    }

    /** What to do when a client connects to me */
    public abstract void handleAccept(Socket clientSocket);

    


    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (Exception e) {
        }
    }
}
