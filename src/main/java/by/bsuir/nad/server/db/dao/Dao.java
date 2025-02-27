package by.bsuir.nad.server.db.dao;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.EntityManager;
import lombok.NonNull;

import java.util.List;

public interface Dao<T, K> {
    void setEntityManager(EntityManager entityManager);
    EntityManager createEntityManager() throws PersistenceException;
    void closeEntityManager() throws PersistenceException;

    void insert(@NonNull T entity);
    List<T> select();
    T update(@NonNull T entity);
    void delete(@NonNull T entity);
    T findByPrimaryKey(@NonNull K primaryKey);
    K findByNonPrimaryKey(@NonNull T nonPrimaryKey);
}
