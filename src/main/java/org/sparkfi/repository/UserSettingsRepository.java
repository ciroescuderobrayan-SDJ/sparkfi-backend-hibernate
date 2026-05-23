package org.sparkfi.repository;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.UserSettings;
import org.sparkfi.repository.impl.GenericRepositoryImpl;

public class UserSettingsRepository extends GenericRepositoryImpl<UserSettings, Long> {

    public UserSettingsRepository(EntityManager entityManager) {
        super(entityManager, UserSettings.class);
    }
}
