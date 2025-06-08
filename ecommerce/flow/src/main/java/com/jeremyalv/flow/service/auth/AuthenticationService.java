package com.jeremyalv.flow.service.auth;

import com.jeremyalv.flow.dto.auth.request.RegisterRequestDTO;
import com.jeremyalv.flow.dto.auth.request.SignInRequestDTO;
import com.jeremyalv.flow.dto.auth.response.RegisterResponseDTO;
import com.jeremyalv.flow.dto.auth.response.AuthenticationResponseDTO;

public interface AuthenticationService {
    RegisterResponseDTO register(RegisterRequestDTO request);
    AuthenticationResponseDTO signIn(SignInRequestDTO request);
}
