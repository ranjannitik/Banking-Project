package com.smartbank.user.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_entry")
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String action;
    private String userEmail;
    private String status;

    public LogEntry() {}

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getAction() { return action; }
    public String getUserEmail() { return userEmail; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setAction(String action) { this.action = action; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setStatus(String status) { this.status = status; }
}
