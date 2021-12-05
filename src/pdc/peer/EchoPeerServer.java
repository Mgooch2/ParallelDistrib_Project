package pdc.peer;

import java.io.*;
import java.net.*;

import pdc.util.*;

/**
 * EchoPeerClient
 */
public class EchoPeerServer extends PeerServer {

    public EchoPeerServer(String routerHostname, int routerPort) throws IOException {
        super(routerHostname, routerPort);
    }

    @Override
    public void handleAccept(Socket clientSocket) {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            // out.println("Your IP address is: " + clientSocket.getInetAddress());
            String input;
            while ((input = in.readLine()) != null) {
                // DEBUG: Print received command
                System.out.println(clientSocket.getInetAddress() + ": " + input);
                String response = input.toUpperCase();
                System.out.println("My response: " + response);
                out.println(response);
                out.flush();
            }
            System.out.println("Connection with " + clientSocket.getInetAddress() + " ended.");
        } catch (IOException e) {
            System.out.println("Error communicating with " + clientSocket.getInetAddress() + ": " + e.getMessage());
        }
    }

    public void sendFile(String filename) {
    }

    public static void main(String[] args) throws IOException {
        // Load variables
        DotEnv.load(".env");
        final String ROUTER_HOSTNAME = DotEnv.getEnv("ROUTER_HOSTNAME");
        final int ROUTER_PORT = Integer.parseInt(DotEnv.getEnv("ROUTER_PORT"));
        // Main logic
        try (PeerServer peerClient = new EchoPeerServer(ROUTER_HOSTNAME, ROUTER_PORT)) {
            peerClient.registerMe();
            peerClient.listen();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}