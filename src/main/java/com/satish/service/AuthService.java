package com.satish.service;

import com.satish.document.User;
import com.satish.dto.LoginRequest;
import com.satish.repository.UserRepository;
import com.satish.dto.AuthResponse;
import com.satish.dto.RegisterRequest;
import com.satish.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    @Value("${app.base.url:http://localhost:8080}")
    private String appBaseUrl;

    public AuthResponse register(RegisterRequest request){
        log.info("Inside AuthService: register() {}", request);
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("User already Exists with this email User right Email");
        }
        User newUser=toDocument(request);
        userRepository.save(newUser);
        sendVerificationEmail(newUser);
        return toResponse(newUser);

    }

    public void verifyEmail(String token){
        log.info("Inside AuthService verifyEmail(): {}",token);
        User user=userRepository.findByVerificationToken(token)
                .orElseThrow(()->new RuntimeException("Invalid or Expired Token"));
        if(user.getVerificationExpires()!=null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Verification Token Has Expired . please request new one");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationExpires(null);
        userRepository.save(user);
    }

    private void sendVerificationEmail(User newUser){
        log.info("Inside Auth Service Send VerificationEmail() {}", newUser);
        try{
            String link=appBaseUrl+"/api/auth/verify-email?token="+newUser.getVerificationToken();
            String html="<div style='font-family:sans-serif'>"+
                    "<h2> Verify your Email</h2>"+
                    "<p> Hi "+newUser.getName()+", Please Confirm Your Email to Activate Your Account. </p>"+
                    "<p><a href='"+link +
                    "' style='display:inline-block;padding:10px 16px; background:#6366f1;color:#fff;border-radius:6px;text-decoration:none'>Verify Email</a></p>"
                    +"<p> Or Copy this Link: "+ link +"</p>"+
                    "<p>This link will Expires in 24 hours. </p>"+
                    "</div>";
            emailService.sendHtmlEmail(newUser.getEmail(), "Verify Your Email", html);
        } catch (Exception e) {
            log.error("Exception Occured at sendVerificationEmail() : {}", e.getMessage());
            throw new RuntimeException("Failed to send verification"+e.getMessage());
        }
    }

    private AuthResponse toResponse(User newUser){
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .emailVerified(newUser.isEmailVerified())
                .subscriptionPlan(newUser.getSubscriptionPlan())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }
    private User toDocument(RegisterRequest request){
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(request.getProfileImageUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }
    public AuthResponse login(LoginRequest request){
       User existingUser= userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("Invalid Email or Password"));
       if(!passwordEncoder.matches(request.getPassword(),existingUser.getPassword())){
           throw new UsernameNotFoundException("Invalid Email or Password Hello How are you");
       }
       if(!existingUser.isEmailVerified()){
           throw new RuntimeException("Please Verify Your Email before Logging iun..");
       }
       String token=jwtUtils.generateToken(existingUser.getId());
       AuthResponse returnValue=toResponse(existingUser);
       returnValue.setToken(token);

       return returnValue;
    }

    public void resendVerification(String email) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));
        if(user.isEmailVerified()){
            throw new RuntimeException("Email is already verified");
        }
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationExpires(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        sendVerificationEmail(user);
    }

    public AuthResponse getProfile(Object principleObject) {
        User existingUser=(User) principleObject;
        return toResponse(existingUser);
    }
}
