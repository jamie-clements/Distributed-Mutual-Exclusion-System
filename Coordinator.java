import java.net.*;

/**
 * Represents the Coordinator of the DME system
 * Initalises and runs the components of the system, including receiver and mutex.
 * 
 */
public class Coordinator {
	
    /**
     * Main method to start Coordinator.
     * @param args Command line arguments (if provided). port number specified.
     */
    public static void main (String args[]){
    	
    	// Shared buffer
    	C_buffer buffer = new C_buffer(); 
		int port = 7003; // Default port number
		
		// Clear the log file at the start of the application
        Logger.getInstance().clearLogFile();
		
		try {    
		    InetAddress c_addr = InetAddress.getLocalHost();
		    String c_name = c_addr.getHostName();
		    System.out.println ("Coordinator address is "+c_addr);
		    System.out.println ("Coordinator host name is "+c_name+"\n\n");    
		} catch (Exception e) {
		    System.err.println(e);
		    System.err.println("Error in coordinator");
		}
				
		// allows defining port at launch time
		if (args.length == 1) 
			port = Integer.parseInt(args[0]);
	
		// Create and run a C_receiver and a C_mutex object sharing a C_buffer object
		C_receiver receiver_c = new C_receiver(buffer, 7003);
		Thread receiverThread = new Thread(receiver_c);
		
		C_mutex mutex_c = new C_mutex(buffer, 7004);
		Thread mutex_thread = new Thread(mutex_c);
		
		/*
		 * Mutex manages access to shared resources.
		 * Listens for incoming requests, queues them up, ensures execution in orderly manner.
		 */
		receiverThread.start();
		mutex_thread.start();
    }
    
}
