package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.service.EmailVerificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-verification")
    public void sendVerificationEmail(@RequestParam String email) throws MessagingException {
        emailVerificationService.sendVerificationEmail(email);
    }

    @PostMapping("/verify-email")
    public void verifyEmail(@RequestParam String email, @RequestParam String code) {
        emailVerificationService.verifyCode(email, code);
    }
}
