import java.net.*;
import java.io.*;
import java.util.*;

import util.DotEnv;

/**
 * PeerRouterClientDemo
 */

public class PeerRouterClientDemo {

    public static void main(String[] args) throws IOException {
        DotEnv.load(".env");
        final String routerHostname = DotEnv.getEnvOrDefault("ROUTER_HOSTNAME", "localhost");
        final int routerPort = Integer.parseInt(DotEnv.getEnvOrDefault("ROUTER_PORT", "6666"));
        InetSocketAddress addr = new InetSocketAddress(routerHostname, routerPort);
        // blocking
        System.out.println("Connecting to " + routerHostname + ":" + routerPort + "...");
        try (Socket s = new Socket(); var scanner = new Scanner(System.in);) {
            s.connect(addr);
            System.out.println("Connected!");
            var out = new PrintWriter(s.getOutputStream());
            var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            String send;
            while ((send = scanner.nextLine()) != null) {
                out.println(send);
                out.flush();
                System.out.println(in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Error: Could not resolve host.");
        } catch (SocketTimeoutException e) {
            System.err.println("Timed out.");
        }
    }
}