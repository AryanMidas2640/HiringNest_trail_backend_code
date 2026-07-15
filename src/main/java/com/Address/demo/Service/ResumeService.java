package com.Address.demo.Service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeService<ResumeResponse> {

    ResumeResponse parseResume(
            MultipartFile file,
            String skills,
            String education,
            String username
    );
}