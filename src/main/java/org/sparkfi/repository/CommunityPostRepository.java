package org.sparkfi.repository;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.CommunityPost;
import org.sparkfi.repository.impl.GenericRepositoryImpl;

public class CommunityPostRepository extends GenericRepositoryImpl<CommunityPost, Long> {

    public CommunityPostRepository(EntityManager entityManager) {
        super(entityManager, CommunityPost.class);
    }
}
