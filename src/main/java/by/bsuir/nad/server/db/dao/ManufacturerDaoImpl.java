package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.db.entity.Manufacturer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;

public class ManufacturerDaoImpl extends DaoImpl<Manufacturer, Long> implements ManufacturerDao {
    private EntityManager entityManager;

    public ManufacturerDaoImpl() {
        super(Manufacturer.class);
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
    public Long findByNonPrimaryKey(@NonNull Manufacturer nonPrimaryKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Manufacturer> cq = cb.createQuery(Manufacturer.class);
        Root<Manufacturer> root = cq.from(Manufacturer.class);

        Predicate[] wherePredicates = {
                cb.equal(root.get("name"), nonPrimaryKey.getName()),
        };
        cq.select(root).where(wherePredicates);

        Manufacturer manufacturer = entityManager.createQuery(cq).getSingleResult();
        return manufacturer.getId();
    }
}
