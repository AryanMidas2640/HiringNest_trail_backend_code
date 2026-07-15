package com.Address.demo.Controller;
import com.Address.demo.Service.JobService;
import com.Address.demo.dto.ApiResponse;
import com.Address.demo.model.Model;
import com.Address.demo.model.Notification;
import com.Address.demo.repositry.ApplicationRepository;
import com.Address.demo.repositry.JobRepository;
import com.Address.demo.repositry.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import com.Address.demo.repositry.UserRepositry;
import com.Address.demo.security.JwtHelper;
import com.Address.demo.model.User;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final UserRepositry userRepositry;
    private final JwtHelper jwtHelper;
    private final NotificationRepository notificationRepository;

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    // ✅ ONLY ONE CONSTRUCTOR (IMPORTANT)
    public JobController(JobService jobService,
                         UserRepositry userRepositry,
                         JwtHelper jwtHelper,
                         JobRepository jobRepository,
                         ApplicationRepository applicationRepository , NotificationRepository notificationRepository) {

        this.jobService = jobService;
        this.notificationRepository = notificationRepository;
        this.userRepositry = userRepositry;
        this.jwtHelper = jwtHelper;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    // ===============================
    // ADD JOB
    // ===============================
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Model>> addJob(
            @Valid @RequestBody Model model,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // 🔥 TOKEN → EMAIL (NOT USERNAME)
            String token = authHeader.replace("Bearer ", "");
            String email = jwtHelper.getEmailFromToken(token);

            // 🔥 FIND USER BY EMAIL (FIXED)
            User user = userRepositry.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 🔥 SET RECRUITER EMAIL (MAIN FIX)
            model.setEmail(user.getEmail());

            // SAVE JOB
            Model savedJob = jobService.saveJob(model);

            Notification n = new Notification();

            n.setType("JOB_POSTED");

            n.setMessage(
                    user.getUsername()
                            + " posted job : "
                            + model.getJobTitle()
            );

            n.setCreatedAt(
                    java.time.LocalDateTime.now().toString()
            );

            notificationRepository.save(n);

            ApiResponse<Model> response =
                    new ApiResponse<>("Success", "Job added successfully", savedJob);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            ApiResponse<Model> error =
                    new ApiResponse<>("Error", e.getMessage(), null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/count/{city}")
    public String getJobCountByCity(@PathVariable String city) {
        long count = jobService.getJobCountByCity(city);  // Uses default DB
        return "Total jobs in " + city + " : " + count;
    }


    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<Model>>> getJobsByCity(@PathVariable String city) {

        List<Model> jobs = jobService.getJobsByCity(city);

        ApiResponse<List<Model>> response =
                new ApiResponse<>("success", "Jobs fetched successfully", jobs);

        return ResponseEntity.ok(response);
    }
    /*public List<Model> getJobsByCity(@PathVariable String city) {
        return jobService.getJobsByCity(city);  // Uses default DB
    }

     */
    @GetMapping("/all")
    public List<Model> getAllJobs() {

        return jobService.getAllJobs();
    }
    @GetMapping("/tenant/{tenantId}")
    public List<Model> getJobsByTenantId(@PathVariable String tenantId) {
        return jobService.getJobsByTenantId(tenantId);
    }

    @GetMapping("/{jobId}")
    public Model getJobById(
            @PathVariable String jobId) {

        return jobService.getJobById(jobId);
    }

    @GetMapping("/test")
    public String test() {


        return "API WORKING";

    }
    @GetMapping("/admin/students/count")
    public long totalStudents() {
        return userRepositry.countByRole("STUDENT");
    }

    @GetMapping("/admin/recruiters/count")
    public long totalRecruiters() {
        return userRepositry.countByRole("RECRUITER");
    }

    @GetMapping("/admin/jobs/count")
    public long totalJobs() {
        return jobRepository.count();
    }

    @GetMapping("/admin/applications/count")
    public long totalApplications() {
        return applicationRepository.count();
    }

    @GetMapping("/admin/online-users/count")
    public long onlineUsers() {
        return userRepositry.countByOnline(true);
    }

    @GetMapping("/admin/students")
    public List<User> students() {
        return userRepositry.findByRole("STUDENT");
    }

    @GetMapping("/admin/recruiters")
    public List<User> recruiters() {
        return userRepositry.findByRole("RECRUITER");
    }
}
