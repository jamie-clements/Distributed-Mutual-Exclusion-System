import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Represents a Node in the DME system.
 * Each Node sends token requests to the Coordinator, waits for the token, enters critical section,
 * and returns the token after exiting critical section and execution. Handles coordinator downtime gracefully.
 * 
 * 
 */
public class Node{

	// Instance Variables
    private Random ra; // Sleep intervals
    private Socket	s; // Socket for communicating with coordinator
    private PrintWriter pout = null; // PrintWriter for sending data to coordinator
    private ServerSocket n_ss; // Server socket for reciveing token
    private Socket	n_token; // Socket connection for token
    private String	c_host = "127.0.0.1"; // Coordinator host address
    private int 	c_request_port = 7003; // Port for sending request to coordinator
    private int 	c_return_port = 7004; // Port for sending token return messsages to coordinator
    private String	n_host = "127.0.0.1"; // Node's host address
    private String 	n_host_name; // Node's host name
    private int     n_port; // Node's port
    private int priority; // store the node's priority
    
    
    // Constants for handling coordinator downtime
    private static final int MAX_RETRIES = 3; // Maximum number of retry attempts
    private static final long RETRY_INTERVAL = 5000; // Wait time between retries in milliseconds (5000ms = 5 seconds)
    private static final long MAX_RETRY_INTERVAL = 60000; // Maximum retry interval to avoid excessive waiting
    
    
    /**
     * Constructs a Node object with the specified parameters.
     * 
     * @param nam The name of the node.
     * @param port The port number of the node.
     * @param sec Average waiting time for token request.
     * @param priority Priority of the node.
     */
    public Node(String nam, int port, int sec, int priority){	
		ra = new Random();
		n_host_name = nam;
		n_port = port;
		this.priority = priority; // Store the priority
		System.out.println("Node " + n_host_name + ":" + n_port + " with priority " + priority + " is active ....");

        
		try {
            n_ss = new ServerSocket(n_port); // Initalising server socket to listen for token only once
            while (true) {
                // Sleep a random number of seconds linked to the initialization sec value
                try {
                    Thread.sleep((ra.nextInt(sec) + 1) * 1000);
                } catch (InterruptedException ie) {
                    System.out.println(nam + " ---> Error Sleeping.");
                    ie.printStackTrace();
                }

                try {
                    /*
                     *  Request the token, send to the coordinator a token request.
                     */
                	try {
                        s = new Socket(c_host, c_request_port);
                        pout = new PrintWriter(s.getOutputStream(), true);
                        pout.println(n_host + " " + n_port + " " + priority); // Include the priority in the request
                        pout.close();
                        System.out.println("\n[---------- Token Request Made ----------]");
                        System.out.println("Token request made to port: " + c_request_port + ".");
                    } catch (IOException e) {
                    	/*
                    	 *  Dealing with Coorinatior being closed down/crashing
                    	 *  Node continues to loop, atempting to request token again,
                    	 *  Node remains active + ready to connect to Coordinator when back online.
                    	 */
                        handleCoordinatorDown();
                        continue; // Continue trying in the next iteration
                    }
                    /*
                     *  Wait for the token
                     */
                    System.out.println("\n[---------- Waiting for Token ----------]");
                    n_token = n_ss.accept();
                    System.out.println("Token received successfully!");

                    /*
                     *  Execute Critical Section
                     *  Randomize the critical section execution time between 3 and 5 seconds
 				     */ 
                    Random random = new Random();
                    int sleepTime = random.nextInt(3) + 3;
                    System.out.println("\n[---------- Entering Critical Section ----------]");
                    Logger.getInstance().logEvent("Node Start Critical Section", n_host_name + ":" + n_port, -1);
                    System.out.println("Node " + n_host_name + " has entered the critical section.");
                    Thread.sleep(sleepTime * 1000); // Simulating critical section execution
                    System.out.println("Node " + n_host_name + " has exited the critical section.");
                    Logger.getInstance().logEvent("Node End Critical Section", n_host_name + ":" + n_port, -1);
                    System.out.println("[---------- Critical Section Exited ------------]");

                    /*  
                     * Return the token
                     */
                    try (Socket returnSocket = new Socket(c_host, c_return_port);
                         PrintWriter returnPout = new PrintWriter(returnSocket.getOutputStream(), true)) {
                        // Send a message when returning the token
                    	returnPout.println("TOKEN_RETURNED");
                    	System.out.println("\n[---------- Returning token ----------]");
                    	System.out.println("Token returned by " + n_host_name + ":" + n_port);
                        System.out.println("Token returned to coordinator. \n \n");

                    }
                    
                } catch (IOException | InterruptedException e) {
                    System.out.println("Exception: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        if (s != null) s.close();
                        if (n_token != null) n_token.close();
                        // Note: Do not close n_ss here as reusing it
                    } catch (IOException ex) {
                        System.out.println("Error closing resources: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("ServerSocket Initialization Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    } 	
    
    /**
     * Method to deal with the coordinator being closed down (or crashing).
     * Retries connecting to coordinator multiple times with exponential backoff strategy.
     * If reconnection attempts fail after specified number of retries, logs failure,
     * but doesn't terminate the node. Allows node to continue and attempt to request token again in the next cycle.
     */
    private void handleCoordinatorDown() {
        long currentRetryInterval = RETRY_INTERVAL;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("Attempt " + attempt + " to reconnect...");
                Thread.sleep(currentRetryInterval);
                s = new Socket(c_host, c_request_port); // Attempt to reconnect
                // If connection is successful, exit the loop
                System.out.println("Reconnected to coordinator.");
                return;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                return;
            } catch (IOException e) {
                System.out.println("Reconnect attempt " + attempt + " failed.");
                // Exponential backoff logic
                currentRetryInterval = Math.min(currentRetryInterval * 2, MAX_RETRY_INTERVAL);
            }
        }
        System.out.println("Unable to reconnect to coordinator after " + MAX_RETRIES + " attempts.");
    }
    
    private static void sendShutdownSignal() {
        try (Socket socket = new Socket("127.0.0.1", 7003);
             PrintWriter pout = new PrintWriter(socket.getOutputStream(), true)) {
            // Send a special shutdown message to the coordinator
            pout.println("SHUTDOWN_REQUEST");
            System.out.println("Shutdown signal sent to coordinator.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Main method to start a Node.
     * 
     * @param args Command line arguments: <port number> <average waiting time> <priority>.
     */
    public static void main (String args[]){
    	if (args.length > 0 && "-shutdown".equals(args[0])) {
            sendShutdownSignal();
            return;
        }
    	if (args.length != 3) {
            System.out.print("Usage: Node <port number> <average waiting time> <priority>");
            System.exit(1);
        }
        
        int n_port = Integer.parseInt(args[0]);
        int priority = Integer.parseInt(args[1]);
        int sec = Integer.parseInt(args[2]);
        try {
            InetAddress n_inet_address = InetAddress.getLocalHost();
            String n_host_name = n_inet_address.getHostName();
            new Node(n_host_name, n_port, priority, sec);
        } catch (java.net.UnknownHostException e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
