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

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private static final Color SPARKFI_GREEN = new Color(18, 132, 102);
    private static final Color SPARKFI_LIGHT_GREEN = new Color(229, 247, 241);
    private static final Color SPARKFI_DARK = new Color(31, 41, 55);
    private static final Color SPARKFI_GRAY = new Color(75, 85, 99);
    private static final Color SPARKFI_ORANGE = new Color(217, 119, 6);
    private static final Color WHITE = Color.WHITE;

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

        User jhonFredy = new User(
                "Jhon Fredy",
                "jhon.fredy." + timestamp + "@sparkfi.com",
                "hashed_password_example"
        );

        User santiagoYosa = new User(
                "Santiago Yosa",
                "santiago.yosa." + timestamp + "@sparkfi.com",
                "hashed_password_example"
        );

        User gustavoMontoya = new User(
                "Gustavo Montoya",
                "gustavo.montoya." + timestamp + "@sparkfi.com",
                "hashed_password_example"
        );

        List<Course> courses = createSampleCourses();
        Course jhonCompletedCourse = courses.get(0);
        Course jhonInProgressCourse = courses.get(1);
        Course santiagoCompletedCourse = courses.get(2);
        Course santiagoInProgressCourse = courses.get(3);
        Course gustavoCompletedCourse = courses.get(4);
        Course gustavoInProgressCourse = courses.get(5);

        Challenge challenge = new Challenge(
                "Reto de ahorro semanal",
                "Ahorrar una cantidad fija durante 7 dias.",
                new BigDecimal("50000.00"),
                7,
                "Principiante"
        );

        userRepository.save(jhonFredy);
        userRepository.save(santiagoYosa);
        userRepository.save(gustavoMontoya);
        courses.forEach(courseRepository::save);
        challengeRepository.save(challenge);

        loginSessionRepository.save(new LoginSession(
                jhonFredy,
                "session_token_" + timestamp,
                now,
                true
        ));

        communityPostRepository.save(new CommunityPost(
                santiagoYosa,
                "Mi primer avance en SparkFi",
                "Hoy empece a organizar mis gastos y crear mi primer reto de ahorro.",
                now
        ));

        userAchievementRepository.save(new UserAchievement(
                gustavoMontoya,
                "Primer paso financiero",
                "Logro obtenido por crear el primer avance dentro de SparkFi.",
                now
        ));

        userProgressRepository.save(new UserProgress(jhonFredy, jhonCompletedCourse, 100, true));
        userProgressRepository.save(new UserProgress(jhonFredy, jhonInProgressCourse, 45, false));
        userProgressRepository.save(new UserProgress(santiagoYosa, santiagoCompletedCourse, 100, true));
        userProgressRepository.save(new UserProgress(santiagoYosa, santiagoInProgressCourse, 60, false));
        userProgressRepository.save(new UserProgress(gustavoMontoya, gustavoCompletedCourse, 100, true));
        userProgressRepository.save(new UserProgress(gustavoMontoya, gustavoInProgressCourse, 25, false));

        userSettingsRepository.save(new UserSettings(
                jhonFredy,
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

    private static List<Course> createSampleCourses() {
        List<Course> courses = new ArrayList<>();

        courses.add(new Course(
                "Presupuesto Facil",
                "Aprende a crear un presupuesto sencillo para organizar tu dinero.",
                35,
                "Principiante"
        ));
        courses.add(new Course(
                "Domina tus Gastos",
                "Identifica en que se va tu dinero y toma mejores decisiones.",
                40,
                "Principiante"
        ));
        courses.add(new Course(
                "Evita Gastar de Mas",
                "Reconoce gastos innecesarios y crea habitos de compra mas conscientes.",
                30,
                "Principiante"
        ));
        courses.add(new Course(
                "Gastos Hormiga: Aprende a Detectarlos",
                "Descubre pequenos gastos diarios que afectan tu presupuesto.",
                25,
                "Principiante"
        ));
        courses.add(new Course(
                "Organiza tu Mes Financiero",
                "Planea ingresos, gastos y metas para cada mes.",
                45,
                "Intermedio"
        ));
        courses.add(new Course(
                "Como Hacer un Presupuesto Personal",
                "Construye un presupuesto personal paso a paso.",
                50,
                "Principiante"
        ));
        courses.add(new Course(
                "Control Financiero Basico",
                "Aprende las bases para controlar tus finanzas personales.",
                40,
                "Principiante"
        ));
        courses.add(new Course(
                "Tu Dinero bajo Control",
                "Organiza tus gastos y define prioridades para usar mejor tu dinero.",
                38,
                "Intermedio"
        ));
        courses.add(new Course(
                "Aprende a Priorizar tus Gastos",
                "Diferencia necesidades, gustos y gastos que pueden esperar.",
                32,
                "Principiante"
        ));
        courses.add(new Course(
                "Menos Impulso, Mas Control",
                "Mejora tus decisiones de compra y reduce gastos impulsivos.",
                30,
                "Principiante"
        ));

        return courses;
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
                drawHeader(content);

                float y = 675;
                y = writeSubtitle(content, "Resumen general", y);
                y = writeSummaryRow(
                        content,
                        "Usuarios", userRepository.findAll().size(),
                        "Cursos", courseRepository.findAll().size(),
                        "Retos", challengeRepository.findAll().size(),
                        y
                );
                y = writeSummaryRow(
                        content,
                        "Progresos", userProgressRepository.findAll().size(),
                        "Logros", userAchievementRepository.findAll().size(),
                        "Sesiones", loginSessionRepository.findAll().size(),
                        y
                );

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

                y -= 10;
                y = writeSubtitle(content, "Progreso por usuario", y);
                for (UserProgress progress : userProgressRepository.findAll()) {
                    String status = Boolean.TRUE.equals(progress.getCompleted()) ? "Completado" : "En progreso";
                    Color statusColor = Boolean.TRUE.equals(progress.getCompleted()) ? SPARKFI_GREEN : SPARKFI_ORANGE;
                    y = writeLine(
                            content,
                            "- " + progress.getUser().getUsername()
                                    + " | " + progress.getCourse().getTitle()
                                    + " | " + progress.getProgressPercentage()
                                    + "% | " + status,
                            statusColor,
                            y
                    );
                }
            }

            document.save(reportPath.toFile());
            System.out.println("Reporte PDF generado en: " + reportPath.toAbsolutePath());
        } catch (IOException exception) {
            System.out.println("No se pudo generar el PDF: " + exception.getMessage());
        }
    }

    private static void drawHeader(PDPageContentStream content) throws IOException {
        content.setNonStrokingColor(SPARKFI_GREEN);
        content.addRect(0, 705, 612, 90);
        content.fill();

        writeText(content, "Reporte SparkFi", PDType1Font.HELVETICA_BOLD, 24, 60, 755, 0, WHITE);
        writeText(content, "Resumen de usuarios, cursos y progreso financiero", PDType1Font.HELVETICA, 12, 60, 733, 0, WHITE);
    }

    private static float writeSummaryRow(
            PDPageContentStream content,
            String firstLabel,
            int firstValue,
            String secondLabel,
            int secondValue,
            String thirdLabel,
            int thirdValue,
            float y
    ) throws IOException {
        drawSummaryCard(content, 60, y - 42, firstLabel, firstValue);
        drawSummaryCard(content, 230, y - 42, secondLabel, secondValue);
        drawSummaryCard(content, 400, y - 42, thirdLabel, thirdValue);
        return y - 62;
    }

    private static void drawSummaryCard(PDPageContentStream content, float x, float y, String label, int value) throws IOException {
        content.setNonStrokingColor(SPARKFI_LIGHT_GREEN);
        content.addRect(x, y, 145, 42);
        content.fill();

        writeText(content, label, PDType1Font.HELVETICA, 10, x + 12, y + 25, 0, SPARKFI_GRAY);
        writeText(content, String.valueOf(value), PDType1Font.HELVETICA_BOLD, 15, x + 12, y + 9, 0, SPARKFI_GREEN);
    }

    private static float writeTitle(PDPageContentStream content, String text, float y) throws IOException {
        return writeText(content, text, PDType1Font.HELVETICA_BOLD, 18, 60, y, 28, SPARKFI_DARK);
    }

    private static float writeSubtitle(PDPageContentStream content, String text, float y) throws IOException {
        return writeText(content, text, PDType1Font.HELVETICA_BOLD, 13, 60, y, 22, SPARKFI_GREEN);
    }

    private static float writeLine(PDPageContentStream content, String text, float y) throws IOException {
        return writeLine(content, text, SPARKFI_DARK, y);
    }

    private static float writeLine(PDPageContentStream content, String text, Color color, float y) throws IOException {
        return writeText(content, text, PDType1Font.HELVETICA, 11, 60, y, 17, color);
    }

    private static float writeText(PDPageContentStream content, String text, PDType1Font font, int fontSize, float x, float y, float spacing, Color color) throws IOException {
        content.beginText();
        content.setNonStrokingColor(color);
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
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
