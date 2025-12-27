package com.satish.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @Email(message = "Email Should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Name is Required")
    @Size(min = 2, max = 15,message = "Name must be between 2 and 15 character")
    private String name;
    @NotBlank(message = "password is Required")
    @Size(min = 5, max = 15,message = "password must be between 5 and 15 character")
    private String password;
    private String profileImageUrl;
}
