package com.Address.demo.Controller;

import com.Address.demo.model.Application;
import com.Address.demo.model.Model;
import com.Address.demo.model.Notification;
import com.Address.demo.model.Resume;
import com.Address.demo.repositry.ApplicationRepository;
import com.Address.demo.repositry.NotificationRepository;
import com.Address.demo.security.JwtHelper;
import com.Address.demo.Service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class ApplyController {

    private final ApplicationRepository applicationRepository;
    private final JwtHelper jwtHelper;
    private final MongoTemplate mongoTemplate;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    public ApplyController(
            ApplicationRepository applicationRepository,
            JwtHelper jwtHelper,
            MongoTemplate mongoTemplate,
            EmailService emailService,NotificationRepository notificationRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.jwtHelper = jwtHelper;
        this.mongoTemplate = mongoTemplate;
        this.emailService = emailService;
        this.notificationRepository = notificationRepository;
    }

    // ===============================
    // APPLY JOB
    // ===============================
    @PostMapping("/apply/{jobId}/{status}")
    public String applyJob(
            @PathVariable String jobId,
            @PathVariable String status,
            HttpServletRequest request) {

        try {

            // 🔥 SAFE TOKEN HANDLING
            String auth = request.getHeader("Authorization");

            if (auth == null || !auth.startsWith("Bearer ")) {
                return "Unauthorized - No Token";
            }

            String token = auth.substring(7).trim();

            // 🔥 EMAIL FROM JWT (ONLY SOURCE OF TRUTH)
            String studentEmail = jwtHelper.getEmailFromToken(token);

            System.out.println("Student Email: " + studentEmail);



            // ===============================
            // FETCH JOB
            // ===============================
            Query jobQuery = new Query();
            jobQuery.addCriteria(Criteria.where("jobId").is(jobId));

            Model job = mongoTemplate.findOne(jobQuery, Model.class, "jobs");

            if (job == null) {
                return "Job Not Found";
            }

            String tenantId = job.getTenantId();

            // ===============================
            // FETCH RESUME (BY EMAIL)
            // ===============================
            Query resumeQuery = new Query();
            resumeQuery.addCriteria(Criteria.where("email").is(studentEmail));

            Resume resume = mongoTemplate.findOne(resumeQuery, Resume.class, "resume");

            if (resume == null) {
                return "Resume not found";
            }

            String username = resume.getUsername();

            // ===============================
            // CHECK OLD APPLICATION
            // ===============================
            Application oldApp =
                    applicationRepository.findByStudentUsernameAndJobId(username, jobId);

            if (oldApp != null) {

                if (oldApp.getStatus().equalsIgnoreCase("Applied")) {
                    return "Already Applied";
                }

                if (oldApp.getStatus().equalsIgnoreCase("Hold")) {
                    oldApp.setStatus("Applied");
                    applicationRepository.save(oldApp);

                    sendEmails(resume, job, studentEmail);
                    return "Re-Applied Successfully";
                }
            }

            // ===============================
            // SAVE NEW APPLICATION
            // ===============================
            Application app = new Application();

            app.setTenantId(tenantId);
            app.setStudentUsername(username);
            app.setStudentName(resume.getName());
            app.setResumeName(resume.getName());

            app.setJobId(job.getJobId());
            app.setJobTitle(job.getJobTitle());
            app.setCompanyName(job.getCompanyName());

            app.setStatus(status.trim());

            applicationRepository.save(app);

            Notification n = new Notification();

            n.setTenantId(job.getTenantId());

            n.setRecruiterEmail(job.getEmail());

            n.setStudentName(resume.getName());

            n.setStudentUsername(username);

            n.setJobId(job.getJobId());

            n.setJobTitle(job.getJobTitle());

            n.setCompanyName(job.getCompanyName());

            n.setType("APPLICATION");

            n.setMessage(
                    resume.getName() +
                            " applied for " +
                            job.getJobTitle()
            );

            n.setRead(false);

            n.setCreatedAt(java.time.LocalDateTime.now().toString());

            notificationRepository.save(n);

            // ===============================
            // SEND EMAILS
            // ===============================
            sendEmails(resume, job, studentEmail);

            return "Applied Successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

    }

    // ===============================
    // EMAIL FUNCTION
    // ===============================
    private void sendEmails(Resume resume, Model job, String studentEmail) {

        try {

            // 📧 STUDENT MAIL (ONLY APPLIED CONFIRMATION)
            emailService.sendMail(
                    studentEmail,
                    "Application Received - " + job.getJobTitle(),
                    "Hi " + resume.getName() + ",\n\n" +
                            "Your application for " + job.getJobTitle() +
                            " at " + job.getCompanyName() +
                            " has been successfully submitted.\n\n" +
                            "We will notify you soon.\n\nThanks 🚀"
            );

            // 📧 RECRUITER MAIL (ONLY NEW APPLICANT)
            emailService.sendMail(
                    job.getEmail(),
                    "New Applicant - " + job.getJobTitle(),
                    "Hello,\n\n" +
                            "New candidate applied.\n\n" +
                            "Name: " + resume.getName() +
                            "\nEmail: " + studentEmail +
                            "\nJob: " + job.getJobTitle() +
                            "\n\nPlease check dashboard.\n\nThanks."
            );

            System.out.println("Student Email = " + studentEmail);
            System.out.println("Recruiter Email = " + job.getEmail());

            System.out.println("✅ Emails sent successfully");

        } catch (Exception e) {
            System.out.println("❌ Email failed: " + e.getMessage());
        }
    }

    // ===============================
    // MY APPLIED JOBS
    // ===============================
    @GetMapping("/my-applied")
    public List<Application> myApplied(HttpServletRequest request) {

        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return List.of();
        }

        String token = auth.substring(7);
        String studentEmail = jwtHelper.getEmailFromToken(token);

        Query resumeQuery = new Query();
        resumeQuery.addCriteria(Criteria.where("email").is(studentEmail));

        Resume resume = mongoTemplate.findOne(resumeQuery, Resume.class, "resume");

        if (resume == null) {
            return List.of();
        }

        return applicationRepository.findByStudentUsername(resume.getUsername());
    }

    // ===============================
    // MY APPLICANTS
    // ===============================
    @GetMapping("/my-applicants")
    public List<Application> myApplicants(HttpServletRequest request) {

        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return List.of();
        }

        String token = auth.substring(7);
        String tenant = jwtHelper.getTenantIdFromToken(token);

       // return applicationRepository.findByTenantId(tenant);
        return applicationRepository.findByTenantId(tenant);
    }

    @GetMapping("/notifications")
    public List<Notification> getNotifications(HttpServletRequest request) {

        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return List.of();
        }

        String token = auth.substring(7);

        String tenant = jwtHelper.getTenantIdFromToken(token);

        return notificationRepository.findByTenantIdOrderByCreatedAtDesc(tenant);
    }

    @GetMapping("/notifications/count")
    public long notificationCount(HttpServletRequest request) {

        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return 0;
        }

        String token = auth.substring(7);

        String tenant = jwtHelper.getTenantIdFromToken(token);

        return notificationRepository.countByTenantIdAndReadFalse(tenant);
    }
}