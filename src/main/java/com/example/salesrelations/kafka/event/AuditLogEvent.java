package com.example.salesrelations.kafka.event;

import java.time.LocalDateTime;

import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;

public class AuditLogEvent {

    private AuditAction action;
    private String username;
    private String entityName;
    private String entityId;
    private AuditStatus status;
    private String message;
    private LocalDateTime timestamp;

    public AuditLogEvent() {
    }

    public AuditLogEvent(AuditAction action, String username, String entityName,
                         String entityId, AuditStatus status, String message,
                         LocalDateTime timestamp) {
        this.action = action;
        this.username = username;
        this.entityName = entityName;
        this.entityId = entityId;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
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