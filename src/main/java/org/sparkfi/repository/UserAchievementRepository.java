package org.sparkfi.repository;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.UserAchievement;
import org.sparkfi.repository.impl.GenericRepositoryImpl;

public class UserAchievementRepository extends GenericRepositoryImpl<UserAchievement, Long> {

    public UserAchievementRepository(EntityManager entityManager) {
        super(entityManager, UserAchievement.class);
    }
}
