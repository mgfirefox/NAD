package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.exception.EntityPersistenceException;

import java.util.List;

public interface Service<T, K> {
    void add(T entity) throws EntityPersistenceException;
    List<T> get() throws EntityPersistenceException;
    void edit(T entity);
    void remove(T entity);
    void removeByPrimaryKey(K primaryKey) throws EntityPersistenceException;
    T findByPrimaryKey(K primaryKey);
    K findByNonPrimaryKey(T nonPrimaryKey);
}
