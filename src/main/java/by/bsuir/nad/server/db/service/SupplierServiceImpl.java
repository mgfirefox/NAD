package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.dao.SupplierDao;
import by.bsuir.nad.server.db.dao.SupplierDaoImpl;
import by.bsuir.nad.server.db.entity.Supplier;
import lombok.NonNull;

public class SupplierServiceImpl extends ServiceImpl<Supplier, Long> implements SupplierService {
    @NonNull
    private final SupplierDao dao;

    public SupplierServiceImpl() {
        this(new SupplierDaoImpl());
    }

    public SupplierServiceImpl(@NonNull SupplierDao dao) {
        super(dao);
        this.dao = dao;
    }
}
