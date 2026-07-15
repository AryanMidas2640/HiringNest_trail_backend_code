package com.Address.demo.Service;

import com.Address.demo.model.Model;
import java.util.List;

public interface JobService {

    Model saveJob(Model model);

    long getJobCountByCity(String city);

    List<Model> getJobsByCity(String city);

    List<Model> getAllJobs();

    Model getJobById(String jobId);

    List<Model> getJobsByTenantId(String tenantId);
}