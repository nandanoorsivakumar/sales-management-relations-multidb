package com.example.salesrelations.mongo.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;

@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    private AuditAction action;
    private String username;
    private String entityName;
    private String entityId;
    private AuditStatus status;
    private String message;
    private LocalDateTime timestamp;

    public AuditLog() {
    }

    public AuditLog(String id, AuditAction action, String username, String entityName,
                    String entityId, AuditStatus status, String message, LocalDateTime timestamp) {
        this.id = id;
        this.action = action;
        this.username = username;
        this.entityName = entityName;
        this.entityId = entityId;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public AuditStatus getStatus() {
        return status;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}