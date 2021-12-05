package pdc.peer;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import pdc.util.*;

public class EchoPeerClient extends PeerClient {

    private Scanner scanner;

    public EchoPeerClient(String routerHostname, int routerPort, String filename) throws IOException {
        super(routerHostname, routerPort);
        if (filename.equals("-")) {
            scanner = new Scanner(System.in);
        } else {
            scanner = new Scanner(new File(filename));
        }
    }

    @Override
    public void handleConnect(Socket s) {
        try (PrintWriter out = new PrintWriter(s.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            while (scanner.hasNextLine()) {
                out.println(scanner.nextLine());
                out.flush();
                System.out.println("Server said: " + in.readLine());
            }
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Server said: " + response);
            }
        } catch (IOException e) {
            System.out.println(
                    "Error communicating with " + s.getInetAddress() + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Load variables
        DotEnv.load(".env");
        final String ROUTER_HOSTNAME = DotEnv.getEnv("ROUTER_HOSTNAME");
        final int ROUTER_PORT = Integer.parseInt(DotEnv.getEnv("ROUTER_PORT"));
        // Main logic
        if (args.length != 2) {
            System.err.println("Usage: java [classname] [DestinationNode] [Input.txt]");
            System.exit(1);
        }
        final String destinationName = args[0];
        final String filename = args[1];
        try (PeerClient peerClient = new EchoPeerClient(ROUTER_HOSTNAME, ROUTER_PORT, filename)) {
            
            peerClient.connectTo(destinationName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void close() {
        super.close();
        scanner.close();
    }
}
