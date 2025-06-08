package com.jeremyalv.flow.controller;

import com.jeremyalv.flow.dto.auth.request.RegisterRequestDTO;
import com.jeremyalv.flow.dto.auth.request.SignInRequestDTO;
import com.jeremyalv.flow.dto.auth.response.RegisterResponseDTO;
import com.jeremyalv.flow.dto.auth.response.SignInResponseDTO;
import com.jeremyalv.flow.dto.auth.response.AuthenticationResponseDTO;
import com.jeremyalv.flow.service.auth.AuthenticationService;
import com.jeremyalv.flow.service.auth.JwtService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;

    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO registerResponse = authService.register(request);

        return new ResponseEntity<>(registerResponse, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponseDTO> signIn(@RequestBody SignInRequestDTO request) {
        AuthenticationResponseDTO authResponse = authService.signIn(request);

        String jwtToken = jwtService.generateToken(authResponse.getUser());

        SignInResponseDTO signInResponse = SignInResponseDTO
                .builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return new ResponseEntity<>(signInResponse, HttpStatus.OK);
    }
}
