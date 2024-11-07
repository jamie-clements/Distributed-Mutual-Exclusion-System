import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 	Logger class is a singleton, providing logging capabilities (advanced features).
 *  Allows for logging messages/structured event logs to a filed named "system_log.txt".
 *  This class also clears the log file content at the beginning of the application run.
 *  
 */
public class Logger {
    private static final String LOG_FILE = "system_log.txt"; // name of log file
    private static Logger instance = null;	// singleton instance of Logger
    
    /**
     * Clears content of the log file. This method is called within the main method
     * of the 'Coordinator' class at the beginning of systems lifecycle.
     * This ensures only the most recent run is logged
     */
    public synchronized void clearLogFile() {
        try {
            new PrintWriter(LOG_FILE).close();
        } catch (IOException e) {
            System.err.println("Error clearing the log file: " + e.getMessage());
        }
    }

    /**
     * Constructor made private to prevent instantiation of the Logger class.
     * Logger follows the singleton pattern, ensuring a single instance throughout the application.
     */
    private Logger() {}

    
    /**
     * Returns the singleton instance of the Logger class.
     * If the instance doesn't exist, it initializes a new instance.
     * 
     * @return The single instance of Logger.
     */
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    
    /**
     * Logs messages to the 'system_log.txt' file with timestamps.
     * 
     * @param message The message to be logged.
     */
    public synchronized void log(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'BST'").format(new Date()) + " - " + message);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
    

    /**
     * Logs events with a structured format to "system_log.txt". This includes a timestamp,
     * description, node information, and the current queue length if applicable.
     * 
     * @param action The action being logged (e.g., "Token Issued", "Token Request").
     * @param nodeInfo The node information involved in the action.
     * @param queueLength The current length of the queue, or a negative value if not applicable.
     */
    public void logEvent(String action, String nodeInfo, int queueLength) {
        String formattedMessage = String.format("[%-19s] | %-30s | Node: %-20s", 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'BST'").format(new Date()), action, nodeInfo);
        if (queueLength >= 0) { // If queue length is part of the log
            formattedMessage += String.format("| Queue Length: %d", queueLength);
        }
        log(formattedMessage);
    }
}
