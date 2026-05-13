package org.sparkfi.repository.impl;

import jakarta.persistence.EntityManager;
import org.sparkfi.repository.Repository;

import java.util.List;
import java.util.Optional;

public class GenericRepositoryImpl<T, ID> implements Repository<T, ID> {

    protected final EntityManager entityManager;
    private final Class<T> entityClass;

    public GenericRepositoryImpl(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) {
        executeInTransaction(() -> entityManager.persist(entity));
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return entityManager
                .createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                .getResultList();
    }

    @Override
    public void update(T entity) {
        executeInTransaction(() -> entityManager.merge(entity));
    }

    @Override
    public void delete(T entity) {
        executeInTransaction(() -> {
            T managedEntity = entityManager.contains(entity) ? entity : entityManager.merge(entity);
            entityManager.remove(managedEntity);
        });
    }

    protected void executeInTransaction(Runnable action) {
        try {
            entityManager.getTransaction().begin();
            action.run();
            entityManager.getTransaction().commit();
        } catch (RuntimeException exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw exception;
        }
    }
}
