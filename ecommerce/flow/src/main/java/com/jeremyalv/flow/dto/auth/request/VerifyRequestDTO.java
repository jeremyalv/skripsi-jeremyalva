package com.jeremyalv.flow.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRequestDTO {
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Token cannot be blank")
    private String token;
}
