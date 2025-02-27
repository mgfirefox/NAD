package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.dao.ProductDao;
import by.bsuir.nad.server.db.dao.ProductDaoImpl;
import by.bsuir.nad.server.db.entity.Product;
import lombok.NonNull;

public class ProductServiceImpl extends ServiceImpl<Product, Long> implements ProductService {
    @NonNull
    private final ProductDao dao;

    public ProductServiceImpl() {
        this(new ProductDaoImpl());
    }

    public ProductServiceImpl(@NonNull ProductDao dao) {
        super(dao);
        this.dao = dao;
    }
}
