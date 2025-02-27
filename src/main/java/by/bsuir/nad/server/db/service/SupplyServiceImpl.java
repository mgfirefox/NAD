package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.dao.*;
import by.bsuir.nad.server.db.entity.Product;
import by.bsuir.nad.server.db.entity.Supply;
import by.bsuir.nad.server.db.entity.SupplyProduct;
import by.bsuir.nad.server.db.exception.EntityPersistenceException;
import by.bsuir.nad.server.db.exception.SupplyPersistenceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SupplyServiceImpl extends ServiceImpl<Supply, Long> implements SupplyService {
    @NonNull
    private final SupplyDao supplyDao;
    @NonNull
    private final SupplyProductDao supplyProductDao;
    @NonNull
    private final ProductDao productDao;

    public SupplyServiceImpl() {
        this(new SupplyDaoImpl(), new SupplyProductDaoImpl(), new ProductDaoImpl());
    }

    public SupplyServiceImpl(@NonNull SupplyDao supplyDao, @NonNull SupplyProductDao supplyProductDao, @NonNull ProductDao productDao) {
        super(supplyDao);
        this.supplyDao = supplyDao;
        this.supplyProductDao = supplyProductDao;
        this.productDao = productDao;
    }

    @Override
    public void add(Supply supply) throws SupplyPersistenceException {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = supplyDao.createEntityManager();
            supplyProductDao.setEntityManager(entityManager);
            transaction = entityManager.getTransaction();
            transaction.begin();

            try {
                supplyDao.findByNonPrimaryKey(supply);
                transaction.rollback();
                throw new SupplyPersistenceException("Supply already exists");
            } catch (NoResultException e) {
                supplyDao.insert(supply);

                Long supplyId = supplyDao.findByNonPrimaryKey(supply);
                for (SupplyProduct supplyProduct : supply.getSupplyProducts()) {
                    SupplyProduct.Id supplyProductId = supplyProduct.getId();
                    supplyProductId.setSupplyId(supplyId);
                    supplyProductId.setProductId(supplyProductId.getProduct().getId());
                    supplyProductDao.insert(supplyProduct);
                }
                transaction.commit();
            }
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            supplyDao.closeEntityManager();
            supplyProductDao.closeEntityManager();
        }
    }

    @Override
    public List<Supply> get() throws SupplyPersistenceException {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = supplyDao.createEntityManager();
            supplyProductDao.setEntityManager(entityManager);
            productDao.setEntityManager(entityManager);
            transaction = entityManager.getTransaction();
            transaction.begin();

            List<Supply> supplies = supplyDao.select();
            for (Supply supply : supplies) {
                supply.setSupplyProducts(supplyProductDao.findBySupplyId(supply.getId()));
                for (SupplyProduct supplyProduct : supply.getSupplyProducts()) {
                    SupplyProduct.Id supplyProductId = supplyProduct.getId();
                    Product product = productDao.findByPrimaryKey(supplyProductId.getProductId());
                    supplyProductId.setProduct(product);
                }
            }

            transaction.commit();
            if (supplies.isEmpty()) {
                throw new SupplyPersistenceException("Supply table is empty");
            }
            return supplies;
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            supplyDao.closeEntityManager();
            supplyProductDao.closeEntityManager();
            productDao.closeEntityManager();
        }
    }

    @Override
    public void edit(Supply supply) {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = supplyDao.createEntityManager();
            supplyProductDao.setEntityManager(entityManager);
            transaction = entityManager.getTransaction();
            transaction.begin();

            supplyDao.update(supply);
            for (SupplyProduct supplyProduct : supply.getSupplyProducts()) {
                supplyProductDao.update(supplyProduct);
            }

            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            supplyDao.closeEntityManager();
            supplyProductDao.closeEntityManager();
        }
    }
}
