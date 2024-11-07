import java.time.Instant;

/**
 * Request class represnts a request within the system, including its priority.
 * Higher priority numbers indicate that they are a higher priority, critical processes recieve highest priority numbers.
 * 
 * Starvation of low priority nodes is addressed by implementing aging,
 * ensuring that fairness mechanism for low-priority requests.
 * (Increasing priority of waiting requests overtime, reducing chance of starvation).
 * 
 */
public class Request implements Comparable<Request> {
    private String node; // Node making the request
    private int port; // Port associated with request
    private int priority; // Priority of request (higher = higher priority)
    private long timestamp; // The creation time of request
    
    /**
     * Constructs a new Request object with the given node, port an priority.
     * @param node Node making request
     * @param port Priority of request
     * @param priority Creation time of request
     */
    public Request(String node, int port, int priority) {
        this.node = node;
        this.port = port;
        this.priority = priority;
        this.timestamp = Instant.now().getEpochSecond(); // Timestamp set to the current time
    } 
    

    /**
     * Gets node associated with this request
     * @return node assoicated with this request
     */
    public String getNode() {
        return node;
    }

    /**
     * Gets port associated with this request
     * @return port assoicated with this request
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets priority of this request
     * @return priority of this request
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Gets timestamp of request
     * @return timestamp of request
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Compares this request with another request based on priority and timestamp
     * Requests with a higher priority take precedence.
     * If priorities are equal, older requests get precedence.
     * 
     * @param other The request to compare to
     * @return A negative integer, zero, or positive integer if this request is less than, equal to,
     * 			or greater than the other request.
     */
    @Override
    public int compareTo(Request other) {
    	// First compare based on priority
        int priorityComparison = Integer.compare(other.priority, this.priority); // Higher priority
        if (priorityComparison == 0) {
            // If priorities are equal, older requests get precedence
            return Long.compare(this.timestamp, other.timestamp);
        }
        return priorityComparison;
    }
}
