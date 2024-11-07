import java.net.*;

/**
 * Represents a receiver thread responsible for listening to token requests from nodes
 * 
 *
 */
public class C_receiver extends Thread{
    
	// Instance variables
    private C_buffer buffer; 
    private int port;
    private ServerSocket s_socket; 
    private Socket socketFromNode;
    
    /**
     * Constructor for creating a new receiver thread.
     * @param b Buffer where requests are stored.
     * @param p Port number the server listens to for token requests
     */
    public C_receiver (C_buffer b, int p){
		buffer = b;
		port = p;
    }
    
    
    /**
     * Executes reciever thread's task of:
     * listening to token requests and creating connections.
     */
    public void run () {
    	// Create socket the server (coordinator) will listen to
	    try {
	    	s_socket = new ServerSocket(port); 
	    	System.out.println("[----- C_Receiver Waiting for Token Requests -----]\n");
	    } catch (java.io.IOException e) {
	    	System.out.println("Exception whilst creating connection " + e);
	    	return;
	    }
	
		while (true) {
		    try{
		       // Get a new connection
		    	// Create a separate thread to service the request, a C_Connection_r thread
		    	socketFromNode = s_socket.accept();
		    	System.out.println("\n\n---------------------------------------------------------------------");
		    	System.out.println("                    NEW CONNECTION");
		    	System.out.println("----------------------------------------------------------------------");
		    	System.out.println("C:Receiver - Connection established in node with port number: " + port + ".");
		    	System.out.println ("C:receiver - Coordinator has received a request ...");
		    	
		    	// Starting new connection
		    	// Create a new thread for each connection to handle the request
		    	new C_Connection_r(socketFromNode, buffer).start();
		    	
		    } catch (java.io.IOException e) {
		    	System.out.println("Exception when creating a connection "+ e);
		    }
		    
		}
    }//end run
}
