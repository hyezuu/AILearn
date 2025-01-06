package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.verificationcode.VerificationCode;
import com.example.ormi5finalteam1.repository.EmailVerificationRepository;
import com.example.ormi5finalteam1.repository.VerificationCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
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
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String serviceEmail;
    private static final Integer EXPIRATION_TIME_IN_MINUTES = 5;
    private final EmailVerificationRepository emailVerificationRepository;

    public void sendVerificationEmail(String to) throws MessagingException {
        String code = generateVerificationCode();
        VerificationCode verificationCode = new VerificationCode(code, to,
            EXPIRATION_TIME_IN_MINUTES);
        verificationCodeRepository.save(verificationCode);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(serviceEmail);
        helper.setTo(to);
        helper.setSubject("AILEARN 회원가입 : 이메일 인증");

        Context context = new Context();
        context.setVariable("verificationCode", code);
        String htmlContent = templateEngine.process("email/verificationEmailTemplate", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public void verifyCode(String email, String code) {
        String value  = verificationCodeRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_CODE_NOT_FOUND));

        if (!value.equals(code)) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EMAIL_MISMATCH);
        }

        verificationCodeRepository.remove(email);
        emailVerificationRepository.save(email);
    }

    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.existByEmail(email);
    }

    public void clearVerificationStatus(String email) {
        emailVerificationRepository.remove(email);
    }

    public void sendTemporaryPasswordEmail(String to, String tempPassword) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(serviceEmail);
        helper.setTo(to);
        helper.setSubject("AILEARN : 임시 비밀번호 발급");

        Context context = new Context();
        context.setVariable("temporaryPassword", tempPassword);
        String htmlContent = templateEngine.process("email/temporaryPasswordEmailTemplate", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

}
