package com.satish.controller;

import com.satish.document.Resume;
import com.satish.dto.CreateResumeRequest;
import com.satish.service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.satish.util.AppConstants.*;

@RestController
@Slf4j
@RequestMapping(RESUME)
@RequiredArgsConstructor

public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping()
    public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request,
                                          Authentication authentication){
       Resume newResume= resumeService.createResume(request, authentication);
       return ResponseEntity.status(HttpStatus.CREATED).body(newResume);
    }

    @GetMapping()
    public ResponseEntity<?> getUserResumes(){
        return null;
    }

    @GetMapping(ID)
    public ResponseEntity<?> getResumeById(@PathVariable String id){
        return null;

    }

    @PutMapping(ID)
    public ResponseEntity<?> updateResume(@PathVariable String id,
                                          @RequestBody Resume updatedData){
        return null;
    }

    @PutMapping(UPLOAD_RESUME_IMAGES)
    public ResponseEntity<?> uploadResumeImages(@PathVariable String id,
                                                @RequestPart(value = "thumbnail", required = true)MultipartFile thumbnail,
                                                @RequestPart(value = "profileImage",required = false)MultipartFile profileImage,
                                                HttpServletRequest request){
        return null;
    }


    @DeleteMapping(ID)
    public ResponseEntity<?> deleteResume(@PathVariable String id){
        return null;
    }
}
