package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.db.entity.SupplyProduct;

import java.util.List;

public interface SupplyProductDao extends Dao<SupplyProduct, SupplyProduct.Id> {
    List<SupplyProduct> findBySupplyId(Long supplyId);
}
