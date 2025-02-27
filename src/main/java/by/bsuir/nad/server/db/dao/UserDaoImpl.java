package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.db.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;

public class UserDaoImpl extends DaoImpl<User, Long> implements UserDao {
    private EntityManager entityManager;

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public EntityManager createEntityManager() throws PersistenceException {
        entityManager = super.createEntityManager();
        return entityManager;
    }

    @Override
    public void closeEntityManager() throws PersistenceException {
        super.closeEntityManager();
        entityManager = null;
    }

    @Override
    public Long findByNonPrimaryKey(@NonNull User nonPrimaryKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        Predicate[] wherePredicates = {
                cb.equal(root.get("name"), nonPrimaryKey.getName()),
                cb.equal(root.get("passwordHash"), nonPrimaryKey.getPasswordHash()),
                cb.equal(root.get("passwordSalt"), nonPrimaryKey.getPasswordSalt()),
                cb.equal(root.get("role"), nonPrimaryKey.getRole()),
                cb.equal(root.get("person"), nonPrimaryKey.getPerson()),
        };
        cq.select(root).where(wherePredicates);

        User user = entityManager.createQuery(cq).getSingleResult();
        return user.getId();
    }

    @Override
    public User findByName(@NonNull String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        Predicate[] wherePredicates = {
                cb.equal(root.get("name"), name),
        };
        cq.select(root).where(wherePredicates);

        return entityManager.createQuery(cq).getSingleResult();
    }
}
