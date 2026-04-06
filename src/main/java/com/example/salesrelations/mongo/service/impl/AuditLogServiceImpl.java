package com.example.salesrelations.mongo.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.salesrelations.kafka.event.AuditLogEvent;
import com.example.salesrelations.kafka.producer.AuditLogProducer;
import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;
import com.example.salesrelations.mongo.service.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogProducer auditLogProducer;

    public AuditLogServiceImpl(AuditLogProducer auditLogProducer) {
        this.auditLogProducer = auditLogProducer;
    }

    @Override
    public void saveLog(AuditAction action,
                        String username,
                        String entityName,
                        String entityId,
                        AuditStatus status,
                        String message) {

        AuditLogEvent event = new AuditLogEvent(
                action,
                username,
                entityName,
                entityId,
                status,
                message,
                LocalDateTime.now()
        );

        auditLogProducer.sendAuditEvent(event);
    }
}