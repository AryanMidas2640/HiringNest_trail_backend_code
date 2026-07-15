package com.Address.demo.ServiceImp;

import com.Address.demo.Service.ResumeService;
import com.Address.demo.dto.ResumeResponse;
import com.Address.demo.model.Resume;
import com.Address.demo.repositry.JobRepository;
import com.Address.demo.repositry.ResumeRepository;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;
import java.util.regex.*;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final MongoTemplate mongoTemplate;

    public ResumeServiceImpl(
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            MongoTemplate mongoTemplate) {

        this.resumeRepository = resumeRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ResumeResponse parseResume(
            MultipartFile file,
            String skills,
            String education,
            String username) {

        ResumeResponse response = new ResumeResponse();

        try {

            PDDocument document =
                    PDDocument.load(file.getInputStream());

            String text =
                    new PDFTextStripper()
                            .getText(document);

            document.close();

            response.setName(findName(text));
            response.setEmail(findEmail(text)); // 🔥 ADD THIS BACK
            response.setPhone(findPhone(text));

            response.setMatchedSkills(findSkills(text, skills));
            response.setEducation(findEducation(text, education));
            response.setExperience(findExperience(text));
            response.setMatchingCount(countMatches(response));

            Resume resume = new Resume();

            resume.setUsername(username);

            resume.setName(response.getName());
            resume.setEmail(response.getEmail()); // 🔥 IMPORTANT FIX
            resume.setPhone(response.getPhone());
            resume.setMatchedSkills(response.getMatchedSkills());
            resume.setEducation(response.getEducation());
            resume.setExperience(response.getExperience());
            resume.setMatchingCount(response.getMatchingCount());

            mongoTemplate.save(resume, "resume");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String findName(String text) {
        return text.isBlank() ? null : text.split("\n")[0];
    }

    private String findEmail(String text) {

        Matcher matcher =
                Pattern.compile("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+")
                        .matcher(text);

        return matcher.find() ? matcher.group() : null;
    }

    private String findPhone(String text) {

        Matcher matcher =
                Pattern.compile("\\d{10}")
                        .matcher(text);

        return matcher.find() ? matcher.group() : null;
    }

    private List<String> findSkills(
            String text,
            String skillsInput) {

        List<String> requiredSkills =
                Arrays.asList(skillsInput.split(","));

        List<String> matched =
                requiredSkills.stream()
                        .filter(skill ->
                                text.toLowerCase()
                                        .contains(skill.trim().toLowerCase()))
                        .toList();

        return matched.isEmpty() ? null : matched;
    }

    private String findEducation(
            String text,
            String educationInput) {

        List<String> educationList =
                Arrays.asList(educationInput.split(","));

        for (String edu : educationList) {

            if (text.toLowerCase()
                    .contains(edu.trim().toLowerCase())) {

                return edu.trim();
            }
        }

        return null;
    }

    private String findExperience(String text) {

        Matcher matcher =
                Pattern.compile("\\d+\\s+years")
                        .matcher(text.toLowerCase());

        return matcher.find() ? matcher.group() : null;
    }

    private Integer countMatches(
            ResumeResponse r) {

        int count = 0;

        if (r.getName() != null) count++;
        if (r.getPhone() != null) count++;
        if (r.getMatchedSkills() != null) count++;
        if (r.getEducation() != null) count++;
        if (r.getExperience() != null) count++;

        return count;
    }
}