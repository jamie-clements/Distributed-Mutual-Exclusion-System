import java.util.PriorityQueue;

/**
 * PriorityQueueTest class is to test the usafe of the PriorityQueue with custom objects.
 * This is achieved by adding Request instances to the queue with varying priorities and confirms their order.
 * 
 * 
 * Demonstrating that the Priority Queue mechanism is effective within the DME.
 */
public class PriorityQueueTest {
	
	/**
	 * Main method to execute PriorityQueue test
	 * @param args Command line arguments (not used, creating Request instances)
	 */
    public static void main(String[] args) {
        // priority queue to store Request objects
        PriorityQueue<Request> queue = new PriorityQueue<>();

        // Adding Request instances to queue with varying priorities
        queue.add(new Request("Node1", 1000, 1)); // Lowest priority
        queue.add(new Request("Node2", 1001, 5)); // Highest priority
        queue.add(new Request("Node3", 1002, 3)); // Medium priority

        // Remove elements from the priority queue and assert their order
        Request highestPriority = queue.poll();
        Request mediumPriority = queue.poll();
        Request lowestPriority = queue.poll();

        // Assertions verifying the priority order
        System.out.println("Asserting priorities...");
        if (highestPriority.getPriority() == 5 &&
            mediumPriority.getPriority() == 3 &&
            lowestPriority.getPriority() == 1) {
            System.out.println("Test passed successfully. \n");
        } else {
            System.out.println("Test failed. \n");
        }

        // Print out to inspect order, ensure correctness.
        System.out.println("Highest Priority: " + highestPriority.getPriority());
        System.out.println("Medium Priority: " + mediumPriority.getPriority());
        System.out.println("Lowest Priority: " + lowestPriority.getPriority());
    }
}
