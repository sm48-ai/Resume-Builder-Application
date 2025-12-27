package com.satish.controller;

import com.satish.dto.AuthResponse;
import com.satish.dto.LoginRequest;
import com.satish.dto.RegisterRequest;
import com.satish.service.AuthService;
import com.satish.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.satish.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {
    private final AuthService authService;

    private final FileUploadService fileUploadService;
    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        log.info("Inside AuthController- register() : {}", request);
            AuthResponse response = authService.register(request);
            log.info("Response From service : {}", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log.info("Inside AuthController- verifyEmail(): {}",token);
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Email Verification Successfully"));
    }

    @PostMapping(UPLOAD_IMAGE)
    public ResponseEntity<?> uploadImage(@RequestPart("image")MultipartFile file) throws IOException {
        log.info("Inside AuthController- uploadImage() : {}", file);
       Map<String,String> response=fileUploadService.uploadSingleImage(file);
       return ResponseEntity.ok(response);
    }
    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping(AUT_VALIDATION)
    public String testValidationToken(){
        return "Token Validation is Working";
    }

}
