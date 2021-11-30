/**
 * PeerClient
 * 
 * A class primarily composed of a main method which immediately attempts to ...
 * ... register the local InetAddress with the serverSocket
 * 
 * REGISTER: Pass the string "register" to add this local host's IP to
 * GET: Pass the string "request" to get an IP address from the router
 */

// get host name if it can otherwise IP address
// socket.Connect has InetSocketAddress (host name, port num)

import java.io.*;
import java.net.*;

public class PeerClient {
    public static void main(String[] args)
    {
    	// Networking declarations
    	Socket serverSocket = null;
    	PrintWriter writer = null;
    	Reader reader = null;
    	InetAddress myAddress = null;
    	
    	// Server connection declarations
    	String routerHostname = "ROUTER_HOSTNAME";
    	int routerPortNumber = 6666;
    	
    	// Local host declarations
    	String localHostname = "";
    	int localPortNumber = 6667;
    	InetSocketAddress mySocketAddress = null;
    	
    	// Information to send
    	String command = args[0]; // "REGISTER" to enlist this IP, "GET" to retrieve host name / IP address and port number
    	String textFromFile = getStringFromFile("file.txt");
    	
    	try
    	{
    		// Connecting to the server
    		myAddress = InetAddress.getLocalHost();
    		serverSocket = new Socket(routerHostname, routerPortNumber);
    		
    		// Establishing local socket address
    		mySocketAddress = new InetSocketAddress(localHostname, localPortNumber);
    		
    		// Sending to the server
    		writer = new PrintWriter(serverSocket.getOutputStream(), true);
    		
    		// Initial communication sends a command to the router
    		writer.println(command); // Initial command to determine what is to be done
    		
    		// Second communication sends InetSocketAddress with toString()
    		writer.println(mySocketAddress.toString());
    	}
    	catch (Exception e)
    	{
    		
    	}
    }
    // Helper functions
    private static String getStringFromFile(String fileName)
    {
    	String textFromFile = "";
    	try
    	{
    		Reader reader = new FileReader(fileName);
    		while (reader.ready())
    		{
    			textFromFile += (char)reader.read();
    		}
    	}
    	catch (Exception e)
    	{
    		
    	}
    	return textFromFile;
    }
}