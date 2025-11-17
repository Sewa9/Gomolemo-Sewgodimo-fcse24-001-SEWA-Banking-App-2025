package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {

    private static final String LOG_FILE = "audit.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Prevent instantiation
    private AuditLogger() { }

    /**
     * Log INFO level message
     */
    public static void info(String message) {
        log("INFO", message);
    }

    /**
     * Log WARN level message
     */
    public static void warn(String message) {
        log("WARN", message);
    }

    /**
     * Log ERROR level message
     */
    public static void error(String message) {
        log("ERROR", message);
    }

    /**
     * Core logging method
     */
    private static synchronized void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = timestamp + " [" + level + "] " + message;

        // Print to console
        System.out.println(logMessage);

        // Append to log file
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(logMessage);
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }
}

