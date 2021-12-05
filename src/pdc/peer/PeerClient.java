package pdc.peer;

import java.io.*;
import java.net.*;

import pdc.util.*;

public abstract class PeerClient implements AutoCloseable {
    private InetSocketAddress routerAddress; // the address of the router which owns me

    public PeerClient(String routerHostname, int routerPort) throws IOException {
        this.routerAddress = new InetSocketAddress(routerHostname, routerPort);
    }
    /**
     * Opens a new socket, connected to the given node name. 
     * @param nodeName e.g. M1
     * @return A socket. The caller is responsible for closing this resource.
     * @throws Exception If the nodename fails to resolve, or the socket fails to connect.
     */
    public void connectTo(String nodeName) throws Exception {
        System.out.println("Attempting to resolve node " + nodeName + "...");
        InetSocketAddress nodeAddr = RoutingCommon.requestNodeSocketAddress(nodeName, routerAddress);
        System.out.println("Resolved " + nodeName + " as " + nodeAddr + "! Connecting...");
        try (Socket s = new Socket(nodeAddr.getAddress(), nodeAddr.getPort())) {
            System.out.println("Connected!");
            handleConnect(s);
            System.out.println("Connection with " + nodeName + "ended.");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public abstract void handleConnect(Socket s);

    @Override
    public void close() {}
}
