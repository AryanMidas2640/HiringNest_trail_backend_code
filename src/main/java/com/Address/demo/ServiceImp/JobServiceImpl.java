package com.Address.demo.ServiceImp;

import com.Address.demo.Service.JobService;
import com.Address.demo.model.Model;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final MongoTemplate mongoTemplate;

    public JobServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // ===============================
    // SAVE JOB (Recruiter adds job)
    // ===============================
    @Override
    public Model saveJob(Model model) {

        // token se tenant aayega
        model.setTenantId(
                com.Address.demo.security.TenantContext.getTenant()
        );

        mongoTemplate.save(model, "jobs");

        return model;
    }

    // ===============================
    // GET ALL JOBS (Student + Recruiter)
    // ===============================
    @Override
    public List<Model> getAllJobs() {

        return mongoTemplate.findAll(
                Model.class,
                "jobs"
        );
    }

    // ===============================
    // GET SINGLE JOB
    // ===============================
    @Override
    public Model getJobById(String jobId) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where("jobId").is(jobId)
        );

        return mongoTemplate.findOne(
                query,
                Model.class,
                "jobs"
        );
    }

    // ===============================
    // GET Recruiter Own Jobs
    // ===============================
    @Override
    public List<Model> getJobsByTenantId(String tenantId) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where("tenantId").is(tenantId)
        );

        return mongoTemplate.find(
                query,
                Model.class,
                "jobs"
        );
    }

    // ===============================
    // CITY WISE JOBS
    // ===============================
    @Override
    public List<Model> getJobsByCity(String city) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where("city").is(city)
        );

        return mongoTemplate.find(
                query,
                Model.class,
                "jobs"
        );
    }

    // ===============================
    // CITY COUNT
    // ===============================
    @Override
    public long getJobCountByCity(String city) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where("city").is(city)
        );

        return mongoTemplate.count(
                query,
                Model.class,
                "jobs"
        );
    }
}