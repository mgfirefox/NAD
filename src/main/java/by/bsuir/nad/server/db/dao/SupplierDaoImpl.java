package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.db.entity.Person;
import by.bsuir.nad.server.db.entity.Supplier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;

public class SupplierDaoImpl extends DaoImpl<Supplier, Long> implements SupplierDao {
    private EntityManager entityManager;

    public SupplierDaoImpl() {
        super(Supplier.class);
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
    public Long findByNonPrimaryKey(@NonNull Supplier nonPrimaryKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Supplier> cq = cb.createQuery(Supplier.class);
        Root<Supplier> root = cq.from(Supplier.class);

        Predicate[] wherePredicates = {
                cb.equal(root.get("name"), nonPrimaryKey.getName()),
        };
        cq.select(root).where(wherePredicates);

        Supplier supplier = entityManager.createQuery(cq).getSingleResult();
        return supplier.getId();
    }
}
