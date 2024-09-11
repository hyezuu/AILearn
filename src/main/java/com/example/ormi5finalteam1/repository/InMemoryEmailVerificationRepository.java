package com.example.ormi5finalteam1.repository;

import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryEmailVerificationRepository implements EmailVerificationRepository {
    private final Map<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();

    @Override
    public void save(String email) {
        verifiedEmails.put(email, true);
    }

    @Override
    public boolean existByEmail(String email) {
        return verifiedEmails.getOrDefault(email, false);
    }

    @Override
    public void remove(String email) {
        verifiedEmails.remove(email);
    }
}
