package org.sparkfi;

import jakarta.persistence.EntityManager;
import org.sparkfi.model.Challenge;
import org.sparkfi.model.CommunityPost;
import org.sparkfi.model.Course;
import org.sparkfi.model.LoginSession;
import org.sparkfi.model.User;
import org.sparkfi.model.UserAchievement;
import org.sparkfi.model.UserProgress;
import org.sparkfi.model.UserSettings;
import org.sparkfi.repository.ChallengeRepository;
import org.sparkfi.repository.CommunityPostRepository;
import org.sparkfi.repository.CourseRepository;
import org.sparkfi.repository.LoginSessionRepository;
import org.sparkfi.repository.UserAchievementRepository;
import org.sparkfi.repository.UserProgressRepository;
import org.sparkfi.repository.UserRepository;
import org.sparkfi.repository.UserSettingsRepository;
import org.sparkfi.util.JpaUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        EntityManager entityManager = JpaUtil.getEntityManager();

        UserRepository userRepository = new UserRepository(entityManager);
        CourseRepository courseRepository = new CourseRepository(entityManager);
        ChallengeRepository challengeRepository = new ChallengeRepository(entityManager);
        CommunityPostRepository communityPostRepository = new CommunityPostRepository(entityManager);
        LoginSessionRepository loginSessionRepository = new LoginSessionRepository(entityManager);
        UserAchievementRepository userAchievementRepository = new UserAchievementRepository(entityManager);
        UserProgressRepository userProgressRepository = new UserProgressRepository(entityManager);
        UserSettingsRepository userSettingsRepository = new UserSettingsRepository(entityManager);

        try {
            long timestamp = System.currentTimeMillis();
            LocalDateTime now = LocalDateTime.now();

            User user = new User(
                    "student_" + timestamp,
                    "student@sparkfi.com",
                    "hashed_password_example"
            );

            Course course = new Course(
                    "Finanzas personales basicas",
                    "Curso introductorio para aprender a manejar el dinero.",
                    45,
                    "Principiante"
            );

            Challenge challenge = new Challenge(
                    "Reto de ahorro semanal",
                    "Ahorrar una cantidad fija durante 7 dias.",
                    new BigDecimal("50000.00"),
                    7,
                    "Principiante"
            );

            userRepository.save(user);
            courseRepository.save(course);
            challengeRepository.save(challenge);

            LoginSession loginSession = new LoginSession(
                    user,
                    "session_token_" + timestamp,
                    now,
                    true
            );

            CommunityPost communityPost = new CommunityPost(
                    user,
                    "Mi primer avance en SparkFi",
                    "Hoy empece a organizar mis gastos y crear mi primer reto de ahorro.",
                    now
            );

            UserAchievement userAchievement = new UserAchievement(
                    user,
                    "Primer paso financiero",
                    "Logro obtenido por crear el primer avance dentro de SparkFi.",
                    now
            );

            UserProgress userProgress = new UserProgress(
                    user,
                    course,
                    35,
                    false
            );

            UserSettings userSettings = new UserSettings(
                    user,
                    "COP",
                    true,
                    true,
                    "es"
            );

            loginSessionRepository.save(loginSession);
            communityPostRepository.save(communityPost);
            userAchievementRepository.save(userAchievement);
            userProgressRepository.save(userProgress);
            userSettingsRepository.save(userSettings);

            System.out.println("Usuarios registrados:");
            userRepository.findAll()
                    .forEach(savedUser -> System.out.println(savedUser.getId() + " - " + savedUser.getUsername()));

            System.out.println("Cursos registrados:");
            courseRepository.findAll()
                    .forEach(savedCourse -> System.out.println(savedCourse.getId() + " - " + savedCourse.getTitle()));

            System.out.println("Retos registrados:");
            challengeRepository.findAll()
                    .forEach(savedChallenge -> System.out.println(savedChallenge.getId() + " - " + savedChallenge.getTitle()));

            System.out.println("Sesiones registradas:");
            loginSessionRepository.findAll()
                    .forEach(savedSession -> System.out.println(savedSession.getId() + " - " + savedSession.getSessionToken()));

            System.out.println("Publicaciones registradas:");
            communityPostRepository.findAll()
                    .forEach(savedPost -> System.out.println(savedPost.getId() + " - " + savedPost.getTitle()));

            System.out.println("Logros registrados:");
            userAchievementRepository.findAll()
                    .forEach(savedAchievement -> System.out.println(savedAchievement.getId() + " - " + savedAchievement.getTitle()));

            System.out.println("Progresos registrados:");
            userProgressRepository.findAll()
                    .forEach(savedProgress -> System.out.println(savedProgress.getId() + " - " + savedProgress.getProgressPercentage() + "%"));

            System.out.println("Configuraciones registradas:");
            userSettingsRepository.findAll()
                    .forEach(savedSettings -> System.out.println(savedSettings.getId() + " - " + savedSettings.getPreferredCurrency()));

        } finally {
            entityManager.close();
            JpaUtil.close();
        }
    }
}
