package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.dao.Dao;
import by.bsuir.nad.server.db.exception.EntityPersistenceException;
import jakarta.persistence.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.NoResultException;

import java.util.List;

@RequiredArgsConstructor
public abstract class ServiceImpl<T, K> implements Service<T, K> {
    @NonNull
    private final Dao<T, K> dao;

    @Override
    public void add(T entity) throws EntityPersistenceException {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = dao.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            try {
                dao.findByNonPrimaryKey(entity);
                transaction.rollback();
                throw new EntityPersistenceException("Entity already exists");
            } catch (NoResultException e) {
                dao.insert(entity);

                transaction.commit();
            }
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            dao.closeEntityManager();
        }
    }

    @Override
    public List<T> get() throws EntityPersistenceException {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = dao.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            List<T> entities = dao.select();

            transaction.commit();
            if (entities.isEmpty()) {
                throw new EntityPersistenceException("Entity table is empty");
            }
            return entities;
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            dao.closeEntityManager();
        }
    }

    @Override
    public void edit(T entity) {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = dao.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            dao.update(entity);

            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            dao.closeEntityManager();
        }
    }

    @Override
    public void remove(T entity) {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = dao.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            dao.delete(entity);

            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            dao.closeEntityManager();
        }
    }

    @Override
    public void removeByPrimaryKey(K primaryKey) throws EntityPersistenceException {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = dao.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            try {
                T entity = dao.findByPrimaryKey(primaryKey);
                dao.delete(entity);

                transaction.commit();
            } catch (NoResultException e) {
                throw new EntityPersistenceException("Entity with this primary key is not found", e);
            }
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            dao.closeEntityManager();
        }
    }

    @Override
    public T findByPrimaryKey(K primaryKey) {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = dao.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            T entity = dao.findByPrimaryKey(primaryKey);
            transaction.commit();
            return entity;
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            dao.closeEntityManager();
        }
    }

    @Override
    public K findByNonPrimaryKey(T nonPrimaryKey) {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = dao.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            K primaryKey = dao.findByNonPrimaryKey(nonPrimaryKey);
            transaction.commit();
            return primaryKey;
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            dao.closeEntityManager();
        }
    }
}
