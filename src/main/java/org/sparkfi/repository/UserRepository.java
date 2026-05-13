package org.sparkfi.repository;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.User;
import org.sparkfi.repository.impl.GenericRepositoryImpl;

public class UserRepository extends GenericRepositoryImpl<User, Long> {

    public UserRepository(EntityManager entityManager) {
        super(entityManager, User.class);
    }
}
