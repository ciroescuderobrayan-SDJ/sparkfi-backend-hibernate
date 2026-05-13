package org.sparkfi;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.Course;
import org.sparkfi.model.User;
import org.sparkfi.repository.CourseRepository;
import org.sparkfi.repository.UserRepository;
import org.sparkfi.util.JpaUtil;

public class Main {

    public static void main(String[] args) {
        EntityManager entityManager = JpaUtil.getEntityManager();

        UserRepository userRepository = new UserRepository(entityManager);
        CourseRepository courseRepository = new CourseRepository(entityManager);

        try {
            User user = new User(
                    "student_" + System.currentTimeMillis(),
                    "student@sparkfi.com",
                    "hashed_password_example"
            );

            Course course = new Course(
                    "Finanzas personales basicas",
                    "Curso introductorio para aprender a manejar el dinero.",
                    45,
                    "Principiante"
            );

            userRepository.save(user);
            courseRepository.save(course);

            System.out.println("Usuarios registrados:");
            userRepository.findAll()
                    .forEach(savedUser -> System.out.println(savedUser.getId() + " - " + savedUser.getUsername()));

            System.out.println("Cursos registrados:");
            courseRepository.findAll()
                    .forEach(savedCourse -> System.out.println(savedCourse.getId() + " - " + savedCourse.getTitle()));

        } finally {
            entityManager.close();
            JpaUtil.close();
        }
    }
}
