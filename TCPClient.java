import java.io.*;
import java.net.*;

public class TCPClient {
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err.println("Usage: $ java TCPServer [router_hostname] [destination_ip]");
      System.exit(1);
    }
    String ARG_ROUTER_HOSTNAME = args[0];
    String ARG_DESTINATION_IP = args[1];
    // Variables for setting up connection and communication
    Socket socket = null; // socket to connect with ServerRouter
    PrintWriter out = null; // for writing to ServerRouter
    BufferedReader in = null; // for reading form ServerRouter
    InetAddress addr = InetAddress.getLocalHost();
    String host = addr.getHostAddress(); // Client machine's IP
    String routerName = ARG_ROUTER_HOSTNAME; // ServerRouter host name
    int socketPortNum = 5555; // port number

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
    Reader reader = new FileReader("file.txt");
    BufferedReader fromFile = new BufferedReader(reader); // reader for the string file
    String fromServer; // messages received from ServerRouter
    String fromUser; // messages sent to ServerRouter
    String address = ARG_DESTINATION_IP; // destination IP (Server)
    long t0, t1, t;

    // Communication process (initial sends/receives
    out.println(address);// initial send (IP of the destination Server)
    fromServer = in.readLine();// initial receive from router (verification of connection)
    System.out.println("ServerRouter: " + fromServer);
    out.println(host); // Client sends the IP of its machine as initial send
    t0 = System.currentTimeMillis();

    // Communication while loop
    while ((fromServer = in.readLine()) != null) {
      System.out.println("Server: " + fromServer);
      t1 = System.currentTimeMillis();
      if (fromServer.equals("Bye.")) // exit statement
        break;
      t = t1 - t0;
      System.out.println("Cycle time: " + t);

      fromUser = fromFile.readLine(); // reading strings from a file
      if (fromUser != null) {
        System.out.println("Client: " + fromUser);
        out.println(fromUser); // sending the strings to the Server via ServerRouter
        t0 = System.currentTimeMillis();
      }
    }

    // closing connections
    out.close();
    in.close();
    socket.close();
  }
}
