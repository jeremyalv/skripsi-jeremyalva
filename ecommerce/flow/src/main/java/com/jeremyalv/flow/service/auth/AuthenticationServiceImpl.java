package com.jeremyalv.flow.service.auth;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jeremyalv.flow.constants.Constants;
import com.jeremyalv.flow.dto.analytics.MessageEnvelopeDTO;
import com.jeremyalv.flow.dto.auth.request.RegisterRequestDTO;
import com.jeremyalv.flow.dto.auth.request.SignInRequestDTO;
import com.jeremyalv.flow.dto.auth.response.AuthenticationResponseDTO;
import com.jeremyalv.flow.dto.auth.response.RegisterResponseDTO;
import com.jeremyalv.flow.exceptions.auth.UserAlreadyExistsException;
import com.jeremyalv.flow.exceptions.auth.UserNotFoundException;
import com.jeremyalv.flow.model.User;
import com.jeremyalv.flow.repository.UserRepository;
import com.jeremyalv.flow.strategy.MessagingStrategy;
import com.jeremyalv.flow.utils.Utils;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final MessagingStrategy<String, User> userMessagingStrategy;

    private final MeterRegistry meterRegistry;

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        checkIfUserExists(request); 
        
        User user = buildAndRegisterUser(request);
        
        publishRegisterEvent(Constants.PUBLISH_USERS_EVENT_NAME, user);

        return RegisterResponseDTO.builder().user(user).build();
    }

    @Override
    public AuthenticationResponseDTO signIn(SignInRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(), 
                    request.getPassword()
                )
        );

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(String.format("User %s not found", request.getEmail())));

        return AuthenticationResponseDTO.builder().user(user).build();
    }

    private void publishRegisterEvent(String eventName, User user) {
        String topic = Utils.DetermineTopic(userMessagingStrategy, Constants.USERS_TOPIC_NAME);
        log.debug("Topic is {}", topic);
        
        MessageEnvelopeDTO<String, User> registerEventDto = new MessageEnvelopeDTO<>(
                topic,
                user.getId() != null ? user.getId().toString() : "unknown_id",
                user,
                Collections.emptyMap()
        );

        String strategyName = userMessagingStrategy.getClass().getSimpleName();
        Timer.Sample sample = Timer.start(meterRegistry);
        String outcome = "unknown";
        try {
            userMessagingStrategy.publish(registerEventDto)
                    .whenComplete((result, exception) -> {
                        String finalOutcome = "unknown";
                        if (exception != null) {
                            log.error(String.format("Async publish failed for %s event: %s", eventName, exception.getMessage()));
                            finalOutcome = "failure";
                        } else if (result != null && result.isSuccess()) {
                            finalOutcome = "success";
                        } else {
                            finalOutcome = "completed_unknown";
                        }

                        sample.stop(meterRegistry.timer("messaging.publish.latency", 
                                "eventName", eventName,
                                        "strategy", strategyName,
                                        "topic", topic,
                                        "outcome", finalOutcome));
                    });
        } catch (Exception e) {
            log.error("Synchronous error during publish()");
            outcome = "error_sync";
            sample.stop(meterRegistry.timer("messaging.publish.latency",
                            "eventName", eventName,
                            "strategy", strategyName,
                            "topic", topic,
                            "outcome", outcome));
        }
    }
    
    private boolean checkIfUserExists(RegisterRequestDTO request) {
        boolean userExistsInRepository = userRepository.findByEmail(request.getEmail()).isPresent();
        
        if (userExistsInRepository) {
            log.warn("Registration failed: User with email {%s} already exists", request.getEmail());
            throw new UserAlreadyExistsException(request.getEmail());
        }

        return false;
    }

    private User buildAndRegisterUser(RegisterRequestDTO request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        return userRepository.save(user);
    }
}
