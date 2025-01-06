package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.domain.user.dto.UpdateUserRequestDto;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto;
import com.example.ormi5finalteam1.service.EmailService;
import com.example.ormi5finalteam1.service.UserService;
import com.example.ormi5finalteam1.service.VocabularyListService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final VocabularyListService vocabularyListService;
    private final EmailService emailService;

    @GetMapping("/email-duplication")
    public boolean checkEmail(@Email @RequestParam String email) {
        return userService.existByEmail(email);
    }

    @GetMapping("/nickname-duplication")
    public boolean checkNickname(@RequestParam String nickname) {
        return userService.existByNickname(nickname);
    }

    @PostMapping("/signup")
    public void signup(@Valid @RequestBody CreateUserRequestDto requestDto) {
        userService.createUser(requestDto);
    }

    @GetMapping("/me")
    public Provider getMe(@AuthenticationPrincipal Provider provider) {
        return userService.getUser(provider.id()).toProvider();
    }

    @GetMapping("/me/vocabulary-list")
    public Page<MyVocabularyListResponseDto> getUserVocabularies(
        @AuthenticationPrincipal Provider provider,
        Pageable pageable) {
        return vocabularyListService.getMyVocabulariesDtoProjection(provider.id(),pageable);
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<Void> withdrawal(@AuthenticationPrincipal Provider provider) {
        userService.delete(provider);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/request-verification")
    public void requestEmailVerification(@RequestParam @Email String email)
        throws MessagingException {
        userService.requestEmailVerification(email);
    }

    @PostMapping("/verify-email")
    public void verifyEmail(@RequestParam @Email String email, @RequestParam String code) {
        emailService.verifyCode(email, code);
    }

    @GetMapping("/auth/password")
    public void resetPassword(@RequestParam @Email String email) throws MessagingException {
        userService.sendTemporaryPassword(email);
    }

    @PutMapping("/me")
    public void update(@AuthenticationPrincipal Provider provider, @RequestBody @Valid
        UpdateUserRequestDto requestDto) {
        userService.updateUser(provider.id(), requestDto);
    }

}
