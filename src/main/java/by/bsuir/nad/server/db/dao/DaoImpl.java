package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.main.ServerApplication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
public abstract class DaoImpl<T, K> implements Dao<T, K> {
    @Setter
    private EntityManager entityManager;
    @NonNull
    private final Class<T> classOfT;

    @Override
    public EntityManager createEntityManager() throws PersistenceException {
        EntityManagerFactory entityManagerFactory = ServerApplication.getContext().getBean("entityManagerFactory", EntityManagerFactory.class);
        entityManager = entityManagerFactory.createEntityManager();
        return entityManager;
    }

    public void closeEntityManager() throws PersistenceException {
        if (entityManager != null) {
            entityManager.close();
            entityManager = null;
        }
    }

    @Override
    public void insert(@NonNull T entity) {
        entityManager.persist(entity);
    }

    @Override
    public List<T> select() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(classOfT);
        Root<T> root = cq.from(classOfT);
        cq.select(root);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public T update(@NonNull T entity) {
        return entityManager.merge(entity);
    }

    @Override
    public void delete(@NonNull T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    @Override
    public T findByPrimaryKey(@NonNull K primaryKey) {
        return entityManager.find(classOfT, primaryKey);
    }
}
