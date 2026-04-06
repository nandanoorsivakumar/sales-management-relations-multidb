package com.example.salesrelations.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.salesrelations.kafka.event.AuditLogEvent;
import com.example.salesrelations.mongo.document.AuditLog;
import com.example.salesrelations.mongo.repository.AuditLogRepository;

@Service
public class AuditLogConsumer {

    private final AuditLogRepository auditLogRepository;

    public AuditLogConsumer(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @KafkaListener(topics = "audit-log-topic", groupId = "audit-log-group")
    public void consumeAuditEvent(AuditLogEvent event) {

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(event.getAction());
        auditLog.setUsername(event.getUsername());
        auditLog.setEntityName(event.getEntityName());
        auditLog.setEntityId(event.getEntityId());
        auditLog.setStatus(event.getStatus());
        auditLog.setMessage(event.getMessage());
        auditLog.setTimestamp(event.getTimestamp());

        AuditLog savedLog = auditLogRepository.save(auditLog);

        System.out.println("Audit log saved to MongoDB with id: " + savedLog.getId());
    }
}