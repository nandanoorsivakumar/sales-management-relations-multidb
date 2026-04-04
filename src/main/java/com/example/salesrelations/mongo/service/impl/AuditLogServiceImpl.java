package com.example.salesrelations.mongo.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.salesrelations.mongo.document.AuditLog;
import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;
import com.example.salesrelations.mongo.repository.AuditLogRepository;
import com.example.salesrelations.mongo.service.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void saveLog(AuditAction action,
                        String username,
                        String entityName,
                        String entityId,
                        AuditStatus status,
                        String message) {

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setUsername(username);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setStatus(status);
        auditLog.setMessage(message);
        auditLog.setTimestamp(LocalDateTime.now());

        AuditLog saved = auditLogRepository.save(auditLog);  // here in mongo db will actual saved logic, so entity itself will be saved here

        System.out.println("Audit log saved in MongoDB with id: " + saved.getId());
    }
}