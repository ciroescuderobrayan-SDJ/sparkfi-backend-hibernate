package org.sparkfi.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JpaUtil {

    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();

    private JpaUtil() {
    }

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            Map<String, String> properties = new HashMap<>();

            properties.put("jakarta.persistence.jdbc.url", System.getenv("SPARKFI_DB_URL"));
            properties.put("jakarta.persistence.jdbc.user", System.getenv("SPARKFI_DB_USER"));
            properties.put("jakarta.persistence.jdbc.password", System.getenv("SPARKFI_DB_PASSWORD"));

            return Persistence.createEntityManagerFactory("sparkfiPU", properties);
        } catch (Throwable exception) {
            System.err.println("Error al crear EntityManagerFactory: " + exception);
            throw new ExceptionInInitializerError(exception);
        }
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
