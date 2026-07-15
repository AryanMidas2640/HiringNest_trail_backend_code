package com.Address.demo.repositry;

import com.Address.demo.model.Model;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface JobRepository extends MongoRepository<Model, String> {

    Model findByJobId(String jobId);

    List<Model> findByTenantId(String tenantId);

    long count();
}