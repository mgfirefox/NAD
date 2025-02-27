package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.db.entity.Supply;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;

public class SupplyDaoImpl extends DaoImpl<Supply, Long> implements SupplyDao {
    private EntityManager entityManager;

    public SupplyDaoImpl() {
        super(Supply.class);
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
    public Long findByNonPrimaryKey(@NonNull Supply nonPrimaryKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Supply> cq = cb.createQuery(Supply.class);
        Root<Supply> root = cq.from(Supply.class);

        Predicate[] wherePredicates = {
                cb.equal(root.get("supplier"), nonPrimaryKey.getSupplier()),
                cb.equal(root.get("deliveryDate"), nonPrimaryKey.getDeliveryDate()),
                cb.equal(root.get("paymentDate"), nonPrimaryKey.getPaymentDate()),
                cb.equal(root.get("cost"), nonPrimaryKey.getCost()),
        };
        cq.select(root).where(wherePredicates);

        Supply supply = entityManager.createQuery(cq).getSingleResult();
        return supply.getId();
    }
}
