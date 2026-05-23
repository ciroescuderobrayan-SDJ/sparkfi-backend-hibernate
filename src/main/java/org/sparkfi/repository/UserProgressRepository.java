package org.sparkfi.repository;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.UserProgress;
import org.sparkfi.repository.impl.GenericRepositoryImpl;

public class UserProgressRepository extends GenericRepositoryImpl<UserProgress, Long> {

    public UserProgressRepository(EntityManager entityManager) {
        super(entityManager, UserProgress.class);
    }
}
