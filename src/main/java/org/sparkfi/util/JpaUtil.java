package org.sparkfi.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.Map;

public class JpaUtil {

    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();

    private JpaUtil() {
    }

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            Map<String, String> properties = new HashMap<>();

            properties.put("jakarta.persistence.jdbc.url", getRequiredConfig(dotenv, "SPARKFI_DB_URL"));
            properties.put("jakarta.persistence.jdbc.user", getRequiredConfig(dotenv, "SPARKFI_DB_USER"));
            properties.put("jakarta.persistence.jdbc.password", getRequiredConfig(dotenv, "SPARKFI_DB_PASSWORD"));

            return Persistence.createEntityManagerFactory("sparkfiPU", properties);
        } catch (Throwable exception) {
            System.err.println("Error al crear EntityManagerFactory: " + exception);
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static String getRequiredConfig(Dotenv dotenv, String key) {
        String value = System.getenv(key);

        if (value == null || value.isBlank()) {
            value = dotenv.get(key);
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable " + key + " en el sistema o en el archivo .env");
        }

        return value;
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
