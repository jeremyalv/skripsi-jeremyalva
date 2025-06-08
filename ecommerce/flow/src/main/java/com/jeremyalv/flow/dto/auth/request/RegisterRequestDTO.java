package com.jeremyalv.flow.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "User email is required")
    @Size(min = 1, max = 255, message = "Email must be at most 255 characters")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Password is required")
    private String password;
}
