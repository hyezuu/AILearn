package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.verificationcode.VerificationCode;
import com.example.ormi5finalteam1.repository.VerificationCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String serviceEmail;
    private static final Integer EXPIRATION_TIME_IN_MINUTES = 5;
    private final Map<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();

    public void sendVerificationEmail(String to) throws MessagingException {
        String code = generateVerificationCode();
        VerificationCode verificationCode = new VerificationCode(code, to,
            EXPIRATION_TIME_IN_MINUTES);
        verificationCodeRepository.save(verificationCode);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(serviceEmail);
        helper.setTo(to);
        helper.setSubject("이메일 인증");

        Context context = new Context();
        context.setVariable("verificationCode", code);
        String htmlContent = templateEngine.process("verificationEmailTemplate", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public void verifyCode(String email, String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByCode(code)
            .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_CODE_NOT_FOUND));

        if (!verificationCode.getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EMAIL_MISMATCH);
        }

        if (verificationCode.isExpired(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }
        verificationCodeRepository.remove(code);
        verifiedEmails.put(email, true);
    }

    public boolean isEmailVerified(String email) {
        return verifiedEmails.getOrDefault(email, false);
    }

    public void clearVerificationStatus(String email) {
        verifiedEmails.remove(email);
    }
}
