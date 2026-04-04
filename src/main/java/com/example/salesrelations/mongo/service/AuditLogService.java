package com.example.salesrelations.mongo.service;

import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;

public interface AuditLogService {

    void saveLog(AuditAction action,
                 String username,
                 String entityName,
                 String entityId,
                 AuditStatus status,
                 String message);
}