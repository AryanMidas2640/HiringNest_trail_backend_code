package com.Address.demo.repositry;

import com.Address.demo.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    // Recruiter ke tenant ki saari notifications
    List<Notification> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    // Unread notification count
    long countByTenantIdAndReadFalse(String tenantId);
}