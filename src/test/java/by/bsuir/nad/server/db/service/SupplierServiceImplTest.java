package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.entity.Supplier;
import by.bsuir.nad.server.db.exception.EntityPersistenceException;
import by.bsuir.nad.server.main.ServerApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class SupplierServiceImplTest {
    @BeforeAll
    static void configure() {
        if (ServerApplication.getContext() == null) {
            ServerApplication.setContext(new ClassPathXmlApplicationContext("server-context.xml"));
        }
    }

    @Test
    void addTwoSameSuppliers() {
        Supplier supplier = new Supplier();
        supplier.setName("test");

        SupplierServiceImpl supplierService = new SupplierServiceImpl();
        assertThrows(EntityPersistenceException.class, () -> {
            supplierService.add(supplier);
            supplierService.add(supplier);
        });
    }
}