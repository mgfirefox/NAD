package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.db.entity.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;

public class PersonDaoImpl extends DaoImpl<Person, Long> implements PersonDao {
    private EntityManager entityManager;

    public PersonDaoImpl() {
        super(Person.class);
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
    public Long findByNonPrimaryKey(@NonNull Person nonPrimaryKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> root = cq.from(Person.class);

        Predicate[] wherePredicates = {
                cb.equal(root.get("lastName"), nonPrimaryKey.getLastName()),
                cb.equal(root.get("firstName"), nonPrimaryKey.getFirstName()),
                cb.equal(root.get("middleName"), nonPrimaryKey.getMiddleName()),
                cb.equal(root.get("gender"), nonPrimaryKey.getGender()),
                cb.equal(root.get("phoneNumber"), nonPrimaryKey.getPhoneNumber()),
                cb.equal(root.get("email"), nonPrimaryKey.getEmail()),
        };
        cq.select(root).where(wherePredicates);

        Person person = entityManager.createQuery(cq).getSingleResult();
        return person.getId();
    }
}
