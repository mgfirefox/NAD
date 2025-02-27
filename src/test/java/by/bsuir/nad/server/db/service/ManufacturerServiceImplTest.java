package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.entity.Manufacturer;
import by.bsuir.nad.server.db.exception.EntityPersistenceException;
import by.bsuir.nad.server.main.ServerApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class ManufacturerServiceImplTest {
    @BeforeAll
    static void configure() {
        if (ServerApplication.getContext() == null) {
            ServerApplication.setContext(new ClassPathXmlApplicationContext("server-context.xml"));
        }
    }

    @Test
    void addTwoSameManufacturers() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("test");

        ManufacturerServiceImpl manufacturerService = new ManufacturerServiceImpl();
        assertThrows(EntityPersistenceException.class, () -> {
            manufacturerService.add(manufacturer);
            manufacturerService.add(manufacturer);
        });
    }

}