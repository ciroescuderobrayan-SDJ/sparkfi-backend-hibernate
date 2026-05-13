package org.sparkfi.repository;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.Course;
import org.sparkfi.repository.impl.GenericRepositoryImpl;

public class CourseRepository extends GenericRepositoryImpl<Course, Long> {

    public CourseRepository(EntityManager entityManager) {
        super(entityManager, Course.class);
    }
}
