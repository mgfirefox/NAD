package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.dao.ManufacturerDao;
import by.bsuir.nad.server.db.dao.ManufacturerDaoImpl;
import by.bsuir.nad.server.db.entity.Manufacturer;
import lombok.NonNull;

public class ManufacturerServiceImpl extends ServiceImpl<Manufacturer, Long> implements ManufacturerService {
    @NonNull
    private final ManufacturerDao dao;

    public ManufacturerServiceImpl() {
        this(new ManufacturerDaoImpl());
    }

    public ManufacturerServiceImpl(@NonNull ManufacturerDao dao) {
        super(dao);
        this.dao = dao;
    }
}
