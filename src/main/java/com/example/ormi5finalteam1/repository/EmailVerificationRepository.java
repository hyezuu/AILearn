package com.example.ormi5finalteam1.repository;

public interface EmailVerificationRepository {
    void save(String email);
    boolean existByEmail(String email);
    void remove(String email);
}
