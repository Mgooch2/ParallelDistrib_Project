import java.net.*;
import java.io.*;
import java.util.*;

/**
 * PeerRouterClientDemo
 */

public class PeerRouterClientDemo {

    public static void main(String[] args) throws IOException {
        // blocking
        try (Socket s = new Socket("localhost", 6666)) {
            System.out.println("Connected!");
            var out = new PrintWriter(s.getOutputStream());
            var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            var scanner = new Scanner(System.in);
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