import java.io.*;
import java.net.*;

public class TCPServer {
  public static void main(String[] args) throws IOException {
    final int socketPortNum = 5555; // port number
    // Initialize variables for communication.
    String routerName = DotEnv.getenv("ROUTER_HOSTNAME"); // ServerRouter host name
    String address = DotEnv.getenv("DESTINATION_IP"); // destination IP (Client)

    // Variables for setting up connection and communication
    Socket socket = null; // socket to connect with ServerRouter
    PrintWriter out = null; // for writing to ServerRouter
    BufferedReader in = null; // for reading form ServerRouter
    InetAddress addr = InetAddress.getLocalHost();
    String host = addr.getHostAddress(); // Server machine's IP

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
    String fromServer; // messages sent to ServerRouter
    String fromClient; // messages received from ServerRouter

    // Communication process (initial sends/receives)
    out.println(address);// initial send (IP of the destination Client)
    fromClient = in.readLine();// initial receive from router (verification of connection)
    
    // Communication while loop
    while ((fromClient = in.readLine()) != null) {
      System.out.println("Client said: " + fromClient);
      fromServer = fromClient.toUpperCase(); // converting received message to upper case
      if (fromClient.equals("Done.")) {
        fromServer = "Done.";
      }
      System.out.println("Server said: " + fromServer);
      out.println(fromServer); // sending the converted message back to the Client via ServerRouter
      if (fromClient.equals("Bye."))
        break;
    }
    // closing connections
    out.close();
    in.close();
    socket.close();
  }
}
