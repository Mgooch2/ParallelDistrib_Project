/*
 * PARAMETERS FOR THE CONSOLE:
 * register - to enable
 * get - to have a certain IP address
 * 
 */

import java.io.*;
import java.net.*;

public class PeerClient {
  public static void main(String[] args) throws IOException {
    // Initialize variables for communication.
    final int socketPortNum = 5555; // port number
    String routerName = DotEnv.getenv("ROUTER_HOSTNAME"); // ServerRouter host name
    String address = DotEnv.getenv("DESTINATION_IP"); // destination IP (Server)
	InetSocketAddress inetSA = new InetSocketAddress(address, socketPortNum);
    
    // Variables for setting up connection and communication
    Socket socket = null; // socket to connect with ServerRouter
    PrintWriter out = null; // for writing to ServerRouter
    BufferedReader in = null; // for reading form ServerRouter
    InetAddress addr = InetAddress.getLocalHost();
    String host = addr.getHostAddress(); // Client machine's IP
    
    // Console parameters
    final String registerCommand = "REGISTER";
    final String getCommand = "GET";
    String command = args[0].toUpperCase();

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
    
    // Based on the command, the initial communications are different
    switch(command)
    {
    case registerCommand:
    	out.println(registerCommand); // Initial send is the string "REGISTER"
		//out.println(inetSA.ToString()); ??? Could somehow send comm info via InetSocketAddress??
    	out.println(address); // Second send is the desired IP to connect to with P2P
		out.println(socketPortNum); // Third send is the desired port number
    	System.exit(0); // The server router should be able to register using these two sends only
    	break;
    case getCommand:
    	//
    	out.println(getCommand); // Initial send is the string "GET"
    	break; // Break the switch statement, resuming to regular TCP communications logic.
    }
    
    
    // Variables for message passing
    Reader reader = new FileReader("file.txt");
    BufferedReader fromFile = new BufferedReader(reader); // reader for the string file
    String fromServer; // messages received from ServerRouter
    String fromUser; // messages sent to ServerRouter

    // Communication process (initial sends/receives
    out.println(address);// initial send (IP of the destination Server)
    fromServer = in.readLine();// initial receive from router (verification of connection)
    System.out.println("ServerRouter: " + fromServer);
    out.println(host); // Client sends the IP of its machine as initial send

    // Communication while loop
    while ((fromServer = in.readLine()) != null) {
      System.out.println("Server: " + fromServer);
      if (fromServer.equals("Bye.")) // exit statement
        break;

      fromUser = fromFile.readLine(); // reading strings from a file
      if (fromUser != null) {
        System.out.println("Client: " + fromUser);
        out.println(fromUser); // sending the strings to the Server via ServerRouter
      }
    }

    // closing connections
    out.close();
    in.close();
    socket.close();
  }
}
