package org.sparkfi.repository;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.Challenge;
import org.sparkfi.repository.impl.GenericRepositoryImpl;

public class ChallengeRepository extends GenericRepositoryImpl<Challenge, Long> {

    public ChallengeRepository(EntityManager entityManager) {
        super(entityManager, Challenge.class);
    }
}
