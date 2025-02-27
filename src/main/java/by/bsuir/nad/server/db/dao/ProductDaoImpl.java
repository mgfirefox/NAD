package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.db.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;

public class ProductDaoImpl extends DaoImpl<Product, Long> implements ProductDao {
    private EntityManager entityManager;

    public ProductDaoImpl() {
        super(Product.class);
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
    public Long findByNonPrimaryKey(@NonNull Product nonPrimaryKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        Predicate[] wherePredicates = {
                cb.equal(root.get("name"), nonPrimaryKey.getName()),
                cb.equal(root.get("manufacturer"), nonPrimaryKey.getManufacturer()),
                cb.equal(root.get("unit"), nonPrimaryKey.getUnit()),
        };
        cq.select(root).where(wherePredicates);

        Product product = entityManager.createQuery(cq).getSingleResult();
        return product.getId();
    }
}
