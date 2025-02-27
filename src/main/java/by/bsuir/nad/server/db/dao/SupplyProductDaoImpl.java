package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.db.entity.SupplyProduct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;

import java.util.List;

public class SupplyProductDaoImpl extends DaoImpl<SupplyProduct, SupplyProduct.Id> implements SupplyProductDao {
    private EntityManager entityManager;

    public SupplyProductDaoImpl() {
        super(SupplyProduct.class);
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
    public SupplyProduct.Id findByNonPrimaryKey(@NonNull SupplyProduct nonPrimaryKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SupplyProduct> cq = cb.createQuery(SupplyProduct.class);
        Root<SupplyProduct> root = cq.from(SupplyProduct.class);

        Predicate[] wherePredicates = {
                cb.equal(root.get("amount"), nonPrimaryKey.getAmount()),
                cb.equal(root.get("pricePerUnit"), nonPrimaryKey.getPricePerUnit()),
        };
        cq.select(root).where(wherePredicates);

        SupplyProduct supplyProduct = entityManager.createQuery(cq).getSingleResult();
        return supplyProduct.getId();
    }

    @Override
    public List<SupplyProduct> findBySupplyId(Long supplyId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SupplyProduct> cq = cb.createQuery(SupplyProduct.class);
        Root<SupplyProduct> root = cq.from(SupplyProduct.class);

        Predicate[] wherePredicates = {
                cb.equal(root.get("id").get("supplyId"), supplyId),
        };
        cq.select(root).where(wherePredicates);

        return entityManager.createQuery(cq).getResultList();
    }
}
