import java.net.*;
import java.io.*;

/**
 * Represents a connection handler thread that reacts to a node request
 * Recieves and records the node request in the buffer
 * 
 */
public class C_Connection_r extends Thread{
	
    // Instance variables
	private C_buffer buffer;
    private Socket s;
    private InputStream in;
    private BufferedReader bin;
    
    /**
     * Constructor for creating a new connection handler
     * @param s Socket associated with connection
     * @param b	Buffer where requests are recorded
     */
    public C_Connection_r(Socket s, C_buffer b){
    	this.s = s;
    	this.buffer = b;
    }
    
    // Method to initiate the shutdown process
    private void initiateShutdown() {
        // Signal all components to stop accepting requests and shut down gracefully
        System.out.println("Shutting down...");
        System.exit(0); // This is a simplistic approach for demonstration
    }
    
    /**
     * Exectutes thread's task of processing the node request
     */
    public void run() {
        try {
            // Initialize input stream and buffered reader at the start
            in = s.getInputStream();
            bin = new BufferedReader(new InputStreamReader(in));
            
            // Read the first line for either a shutdown request or regular request
            String requestLine = bin.readLine();
            if (requestLine == null) {
                throw new IOException("Connection closed by client before sending data.");
            }

            // Check for shutdown request
            if ("SHUTDOWN_REQUEST".equals(requestLine.trim())) {
                System.out.println("Shutdown request received. Initiating shutdown...");
                initiateShutdown();
                return; // Stop processing further as we're shutting down
            }

            System.out.println("\n[------------- Processing Connection Request -------------]");
            System.out.println("C:connection IN - dealing with request from socket " + s);

            // Proceed to handle the request since it's not a shutdown request
            String[] out_request = requestLine.trim().split(" ");
            int priority = Integer.parseInt(out_request[2]); // Extract priority
            Request request = new Request(out_request[0], Integer.parseInt(out_request[1]), priority);
            buffer.saveRequest(request);

            // Log token request with the provided Logger instance
            Logger.getInstance().logEvent("Token Request", request.getNode() + ":" + request.getPort(), -1);

            System.out.println("C:connection OUT - received and recorded request from " + request.getNode() + ":" + request.getPort() + " (socket closed)");

        } catch (IOException e) {
            System.out.println("Error handling connection request: " + e.getMessage());
            // Handle exception appropriately. Consider logging or other mechanisms over System.exit(1).
        } finally {
            // Ensure resources are closed in the finally block to avoid resource leaks
            try {
                if (bin != null) bin.close();
                if (in != null) in.close();
                if (s != null && !s.isClosed()) s.close();
            } catch (IOException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }

        buffer.show(); // Display current requests in buffer
    }
}
