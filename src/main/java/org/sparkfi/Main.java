package org.sparkfi;

import jakarta.persistence.EntityManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
import org.sparkfi.repository.Repository;
import org.sparkfi.repository.UserAchievementRepository;
import org.sparkfi.repository.UserProgressRepository;
import org.sparkfi.repository.UserRepository;
import org.sparkfi.repository.UserSettingsRepository;
import org.sparkfi.util.JpaUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static UserRepository userRepository;
    private static CourseRepository courseRepository;
    private static ChallengeRepository challengeRepository;
    private static CommunityPostRepository communityPostRepository;
    private static LoginSessionRepository loginSessionRepository;
    private static UserAchievementRepository userAchievementRepository;
    private static UserProgressRepository userProgressRepository;
    private static UserSettingsRepository userSettingsRepository;

    public static void main(String[] args) {
        EntityManager entityManager = JpaUtil.getEntityManager();

        userRepository = new UserRepository(entityManager);
        courseRepository = new CourseRepository(entityManager);
        challengeRepository = new ChallengeRepository(entityManager);
        communityPostRepository = new CommunityPostRepository(entityManager);
        loginSessionRepository = new LoginSessionRepository(entityManager);
        userAchievementRepository = new UserAchievementRepository(entityManager);
        userProgressRepository = new UserProgressRepository(entityManager);
        userSettingsRepository = new UserSettingsRepository(entityManager);

        try (Scanner scanner = new Scanner(System.in)) {
            runMenu(scanner);
        } finally {
            entityManager.close();
            JpaUtil.close();
        }
    }

    private static void runMenu(Scanner scanner) {
        String option;

        do {
            printMenu();
            option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> createSampleData();
                case "2" -> listUsers();
                case "3" -> listCourses();
                case "4" -> listChallenges();
                case "5" -> generatePdfReport();
                case "6" -> clearData(scanner);
                case "0" -> System.out.println("Saliendo de SparkFi Backend...");
                default -> System.out.println("Opcion no valida. Intenta nuevamente.");
            }
        } while (!option.equals("0"));
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== SPARKFI BACKEND =====");
        System.out.println("1. Crear datos de prueba");
        System.out.println("2. Listar usuarios");
        System.out.println("3. Listar cursos");
        System.out.println("4. Listar retos");
        System.out.println("5. Generar reporte PDF");
        System.out.println("6. Limpiar datos de prueba");
        System.out.println("0. Salir");
        System.out.print("Elige una opcion: ");
    }

    private static void createSampleData() {
        long timestamp = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();

        User user = new User(
                "student_" + timestamp,
                "student_" + timestamp + "@sparkfi.com",
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

        loginSessionRepository.save(new LoginSession(
                user,
                "session_token_" + timestamp,
                now,
                true
        ));

        communityPostRepository.save(new CommunityPost(
                user,
                "Mi primer avance en SparkFi",
                "Hoy empece a organizar mis gastos y crear mi primer reto de ahorro.",
                now
        ));

        userAchievementRepository.save(new UserAchievement(
                user,
                "Primer paso financiero",
                "Logro obtenido por crear el primer avance dentro de SparkFi.",
                now
        ));

        userProgressRepository.save(new UserProgress(
                user,
                course,
                35,
                false
        ));

        userSettingsRepository.save(new UserSettings(
                user,
                "COP",
                true,
                true,
                "es"
        ));

        System.out.println("Datos de prueba creados correctamente.");
    }

    private static void listUsers() {
        List<User> users = userRepository.findAll();

        System.out.println();
        System.out.println("Usuarios registrados:");
        if (users.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        users.forEach(user -> System.out.println(user.getId() + " - " + user.getUsername() + " - " + user.getEmail()));
    }

    private static void listCourses() {
        List<Course> courses = courseRepository.findAll();

        System.out.println();
        System.out.println("Cursos registrados:");
        if (courses.isEmpty()) {
            System.out.println("No hay cursos registrados.");
            return;
        }

        courses.forEach(course -> System.out.println(course.getId() + " - " + course.getTitle() + " - " + course.getLevel()));
    }

    private static void listChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();

        System.out.println();
        System.out.println("Retos registrados:");
        if (challenges.isEmpty()) {
            System.out.println("No hay retos registrados.");
            return;
        }

        challenges.forEach(challenge -> System.out.println(challenge.getId() + " - " + challenge.getTitle() + " - " + challenge.getTargetAmount()));
    }

    private static void generatePdfReport() {
        Path reportPath = Path.of("sparkfi-report.pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = 740;
                y = writeTitle(content, "Reporte SparkFi", y);
                y = writeLine(content, "Usuarios registrados: " + userRepository.findAll().size(), y);
                y = writeLine(content, "Cursos registrados: " + courseRepository.findAll().size(), y);
                y = writeLine(content, "Retos registrados: " + challengeRepository.findAll().size(), y);
                y = writeLine(content, "Publicaciones registradas: " + communityPostRepository.findAll().size(), y);
                y = writeLine(content, "Logros registrados: " + userAchievementRepository.findAll().size(), y);
                y = writeLine(content, "Progresos registrados: " + userProgressRepository.findAll().size(), y);
                y = writeLine(content, "Configuraciones registradas: " + userSettingsRepository.findAll().size(), y);
                y = writeLine(content, "Sesiones registradas: " + loginSessionRepository.findAll().size(), y);

                y -= 15;
                y = writeSubtitle(content, "Cursos disponibles", y);
                for (Course course : courseRepository.findAll()) {
                    y = writeLine(content, "- " + course.getTitle() + " (" + course.getLevel() + ")", y);
                }

                y -= 10;
                y = writeSubtitle(content, "Retos disponibles", y);
                for (Challenge challenge : challengeRepository.findAll()) {
                    y = writeLine(content, "- " + challenge.getTitle() + " - Meta: " + challenge.getTargetAmount(), y);
                }
            }

            document.save(reportPath.toFile());
            System.out.println("Reporte PDF generado en: " + reportPath.toAbsolutePath());
        } catch (IOException exception) {
            System.out.println("No se pudo generar el PDF: " + exception.getMessage());
        }
    }

    private static float writeTitle(PDPageContentStream content, String text, float y) throws IOException {
        return writeText(content, text, PDType1Font.HELVETICA_BOLD, 18, y, 28);
    }

    private static float writeSubtitle(PDPageContentStream content, String text, float y) throws IOException {
        return writeText(content, text, PDType1Font.HELVETICA_BOLD, 13, y, 22);
    }

    private static float writeLine(PDPageContentStream content, String text, float y) throws IOException {
        return writeText(content, text, PDType1Font.HELVETICA, 11, y, 17);
    }

    private static float writeText(PDPageContentStream content, String text, PDType1Font font, int fontSize, float y, float spacing) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(60, y);
        content.showText(text);
        content.endText();
        return y - spacing;
    }

    private static void clearData(Scanner scanner) {
        System.out.println();
        System.out.println("Esta opcion elimina todos los datos de las tablas de SparkFi.");
        System.out.print("Escribe SI para confirmar: ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("SI")) {
            System.out.println("Limpieza cancelada.");
            return;
        }

        int deletedSettings = deleteAll(userSettingsRepository);
        int deletedProgress = deleteAll(userProgressRepository);
        int deletedAchievements = deleteAll(userAchievementRepository);
        int deletedPosts = deleteAll(communityPostRepository);
        int deletedSessions = deleteAll(loginSessionRepository);
        int deletedChallenges = deleteAll(challengeRepository);
        int deletedCourses = deleteAll(courseRepository);
        int deletedUsers = deleteAll(userRepository);

        System.out.println("Datos eliminados correctamente:");
        System.out.println("- Configuraciones: " + deletedSettings);
        System.out.println("- Progresos: " + deletedProgress);
        System.out.println("- Logros: " + deletedAchievements);
        System.out.println("- Publicaciones: " + deletedPosts);
        System.out.println("- Sesiones: " + deletedSessions);
        System.out.println("- Retos: " + deletedChallenges);
        System.out.println("- Cursos: " + deletedCourses);
        System.out.println("- Usuarios: " + deletedUsers);
    }

    private static <T> int deleteAll(Repository<T, Long> repository) {
        List<T> entities = repository.findAll();
        entities.forEach(repository::delete);
        return entities.size();
    }
}
