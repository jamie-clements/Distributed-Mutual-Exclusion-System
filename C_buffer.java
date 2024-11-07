import java.util.*;

/**
 * This class represents a buffer for storing requests within the DME
 * Requests are stored in a priority queue based on their priority.
 * 
 */
public class C_buffer {
	
	// PriorityQueue to store requests based on their priority
	private PriorityQueue<Request> data;
    
	
	/**
	 * Constructor for creating a new C_buffer object
	 * Initalises priority queue for storing requests.
	 */
    public C_buffer (){
    	data = new PriorityQueue<>();
    }    
    
    /**
     * Saves a request to the buffer
     * Ages existing requests to adjust their priorities based on waiting time.
     * @param r Request to be saved
     */
    public synchronized void saveRequest(Request r) {
    	ageRequests(); // Age existing requests to adjust their priorities based on waiting time
        data.add(r);
        notifyAll(); // Notify any waiting threads that an item has been added
    }

    
    /**
     * Retrieves and removes highest priority request from buffer
     * Waits if the buffer is empty until an item is available
     * @return The highest priority request, or null (if interrupted)
     */
    public synchronized Request get() {
        while (data.isEmpty()) {
            try {
                wait(); // Wait until the buffer is not empty
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                return null;
            }
        }
        return data.poll(); // Retrieve and remove the highest priority request
    }

    
    /**
     * Returns the number of requests currently stored in buffer
     * @return The size of the buffer
     */
    public synchronized int size() {
        return data.size();
    }
    
    /**
     * Ages requests by increasing their priority based on how long they've been waiting.
     * (Increases priority by 1)
     */
    private void ageRequests() {
        // Temporarily store aged requests
        List<Request> agedRequests = new ArrayList<>();
        while (!data.isEmpty()) {
            Request r = data.poll();
            // Adjust priority based on waiting time here, increase priority by one
            Request agedRequest = new Request(r.getNode(), r.getPort(), r.getPriority() + 1);
            agedRequests.add(agedRequest);
        }
        // Re-add aged requests to the queue
        for (Request r : agedRequests) {
            data.add(r);
        }
    }
    
    
    /**
     * Displays current requeusts in buffer along with their details
     * Used for debugging
     */
    public synchronized void show() {
            System.out.println("Current Request Queue: ");
            for (Request r : data) {
                System.out.println("Node: " + r.getNode() + ", Port: " + r.getPort() + ", Priority: " + r.getPriority());
            }
        }
    

}
