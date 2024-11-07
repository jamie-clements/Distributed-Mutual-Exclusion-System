import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Represents a mutex thread responsible for granting and receiving tokens.
 * 
 *
 */
public class C_mutex extends Thread{
   
	// Instance variables
    private C_buffer buffer;
    private Socket s;
    private int port;
    private String n_host;
    private int n_port;
    private ServerSocket ss_back = null;
  
	/**
	 * Constructor for creating a new mutex thread
	 * @param b buffer from which to fetch node requests
	 * @param p port number for listening for token return
	 */
    public C_mutex (C_buffer b, int p){
		buffer = b;
		port = p;
    }

    /**
     * Exectures mutex threa'ds task for granting and receiving tokens
     */
    public void run(){
	try{ 
		// Listening from server socket on specified port
		ss_back = new ServerSocket(port);
		
	    while (true){
		// Printing some info on current buffer content, debugging purposes
		// Check if buffer is not empty
		if (buffer.size() != 0) {		    
			Request request = buffer.get();
            String n_host = request.getNode();
            int n_port = request.getPort();
            
		    // Log before issuing token
		    Logger.getInstance().logEvent("Token Issued", n_host + ":" + n_port, buffer.size());
		    System.out.println("\n[----------- Token Granting Process -----------]");
            System.out.println("C:mutex - Giving token to " + n_host + ":"+ n_port + ".");
		    
		    // Granting the token
		    //
		    try{
		    	s = new Socket(n_host, n_port);
		    	System.out.println("C:mutex - TOKEN given to " + n_host + ":" + n_port + ".");
		    	s.close();
		    } catch (java.io.IOException e) {
		    	System.out.println("ERROR: Mutex connecting to the node for granting the TOKEN - " + e);
		    }
			    
			    
		    // Getting the token back
            try {
                Socket tokenReturnSocket = ss_back.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(tokenReturnSocket.getInputStream()));
                String message = br.readLine();
                if ("TOKEN_RETURNED".equals(message)) {
                	System.out.println("\n--------------------------------------------------------------------");
                    System.out.println("C:mutex - TOKEN returned to coordinator from " + n_host + ":" + n_port);
                    System.out.println("--------------------------------------------------------------------\n");
                    // Log token return
                    Logger.getInstance().logEvent("Token Returned", n_host + ":" + n_port, buffer.size());
                }
                tokenReturnSocket.close();
            } catch (java.io.IOException e) {
                System.out.println("ERROR: Mutex waiting for the TOKEN back - " + e);
	            }
	        }
	    } // endwhile
	} catch (Exception e) {
	    System.out.print(e);
	} finally {
	    // Close the ServerSocket in finally block to ensure it is closed
	    if (ss_back != null && !ss_back.isClosed()) {
	        try {
	            ss_back.close();
	        } catch (IOException e) {
	            System.out.println("Error closing ServerSocket: " + e);
	        }
	    }
	}
}
}
 