// ===============================
// ApplicationRepository.java
// ===============================
package com.Address.demo.repositry;

import com.Address.demo.model.Application;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApplicationRepository
        extends MongoRepository<Application,String> {

    List<Application> findByTenantId(String tenantId);

    List<Application> findByStudentUsername(String studentUsername);

    Application findByStudentUsernameAndJobId(String studentUsername, String jobId);

    long count();
}