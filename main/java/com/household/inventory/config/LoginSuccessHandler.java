package com.household.inventory.config;

import com.household.inventory.repository.UserRepository;
import com.household.inventory.service.EmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public LoginSuccessHandler(UserRepository userRepository,
                               EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        String username = authentication.getName();

        userRepository.findByUsername(username)
                .ifPresent(user ->
                        emailService.sendLoginAlert(
                                user.getEmail(),
                                user.getUsername()
                        )
                );

        response.sendRedirect("/inventory");
    }
}
