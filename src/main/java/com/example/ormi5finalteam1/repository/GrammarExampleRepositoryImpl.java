package com.example.ormi5finalteam1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class GrammarExampleRepositoryImpl implements GrammarExampleCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void insertGrammarExampleWithRawQuery(String rawQuery) {
        em.createNativeQuery(rawQuery).executeUpdate();
    }
}
