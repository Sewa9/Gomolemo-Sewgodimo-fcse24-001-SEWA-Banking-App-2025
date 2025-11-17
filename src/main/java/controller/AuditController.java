package controller;

import java.time.LocalDateTime;

public class AuditController {

    public void logAction(String actionDescription) {
        // In a real system, write to DB or log file
        System.out.println(LocalDateTime.now() + " - ACTION: " + actionDescription);
    }
}

