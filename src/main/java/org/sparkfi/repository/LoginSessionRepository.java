package org.sparkfi.repository;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.LoginSession;
import org.sparkfi.repository.impl.GenericRepositoryImpl;

public class LoginSessionRepository extends GenericRepositoryImpl<LoginSession, Long> {

    public LoginSessionRepository(EntityManager entityManager) {
        super(entityManager, LoginSession.class);
    }
}
