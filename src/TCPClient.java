import java.io.*;
import java.net.*;

public class TCPClient {
  public static void main(String[] args) throws IOException {
    // Initialize variables for communication.
    final int socketPortNum = 5555; // port number
    String routerName = DotEnv.getenv("ROUTER_HOSTNAME"); // ServerRouter host name
    String address = DotEnv.getenv("DESTINATION_IP"); // destination IP (Server)
    if (args.length < 1) {
      System.err.println("Usage: $ java TCPClient [file_to_send]");
      System.exit(1);
    }
    String fileName = args[0];

    // Variables for setting up connection and communication
    Socket socket = null; // socket to connect with ServerRouter
    PrintWriter out = null; // for writing to ServerRouter
    BufferedReader in = null; // for reading form ServerRouter
    InetAddress addr = InetAddress.getLocalHost();
    String host = addr.getHostAddress(); // Client machine's IP

    // Tries to connect to the ServerRouter
    try {
      socket = new Socket(routerName, socketPortNum);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (UnknownHostException e) {
      System.err.println("Don't know about router: " + routerName);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to: " + routerName);
      System.exit(1);
    }

    // Variables for message passing
    Reader reader = new FileReader(fileName);
    BufferedReader fromFile = new BufferedReader(reader); // reader for the string file
    String fromServer; // messages received from ServerRouter
    String fromUser; // messages sent to ServerRouter

    // Communication process (initial sends/receives
    out.println(address);// initial send (IP of the destination Server)
    fromServer = in.readLine();// initial receive from router (verification of connection)
    System.out.println("ServerRouter: " + fromServer);

    // Receive a ready message from the server.
    fromServer = in.readLine(); // initial receive from router (verification of connection)
    System.out.println("Server: " + fromServer);

    // Write the whole file to the server.
    long t0 = System.currentTimeMillis();
    while ((fromUser = fromFile.readLine()) != null) {
      System.out.println("Client: " + fromUser);
      out.println(fromUser);
    }
    long t1 = System.currentTimeMillis();
    long delta1 = t1 - t0;

    out.println("Done.");
    // Wait for a response.
    while ((fromServer = in.readLine()) != null) {
      System.out.println("Server: " + fromServer);
      if (fromServer.equals("Done.")) { // exit statement
        out.println("Bye.");
        break;
      }
    }
    long t2 = System.currentTimeMillis();
    long delta2 = t2 - t1;
    System.out.println("Time to write: " + delta1 + "ms");
    System.out.println("Time to read: " + delta2 + "ms");

    // closing connections
    in.close();
    fromFile.close();
    socket.close();
  }
}
