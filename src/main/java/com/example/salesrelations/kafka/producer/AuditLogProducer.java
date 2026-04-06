package com.example.salesrelations.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.salesrelations.kafka.event.AuditLogEvent;

@Service
public class AuditLogProducer {

    private static final String TOPIC_NAME = "audit-log-topic";

    private final KafkaTemplate<String, AuditLogEvent> kafkaTemplate;

    public AuditLogProducer(KafkaTemplate<String, AuditLogEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuditEvent(AuditLogEvent event) {
        kafkaTemplate.send(TOPIC_NAME, event.getEntityName(), event);
        System.out.println("Audit event sent to Kafka topic: " + TOPIC_NAME);
    }
}