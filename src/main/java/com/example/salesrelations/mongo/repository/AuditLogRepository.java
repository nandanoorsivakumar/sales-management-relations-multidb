package com.example.salesrelations.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.salesrelations.mongo.document.AuditLog;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}