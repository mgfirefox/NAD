package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.entity.Manufacturer;
import by.bsuir.nad.server.db.entity.Product;
import by.bsuir.nad.server.db.exception.EntityPersistenceException;
import by.bsuir.nad.server.main.ServerApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceImplTest {
    @BeforeAll
    static void configure() {
        if (ServerApplication.getContext() == null) {
            ServerApplication.setContext(new ClassPathXmlApplicationContext("server-context.xml"));
        }
    }

    @Test
    void addTwoSameProducts() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("test");

        ManufacturerService manufacturerService = new ManufacturerServiceImpl();
        Long manufacturerId = manufacturerService.findByNonPrimaryKey(manufacturer);
        manufacturer.setId(manufacturerId);

        Product product = new Product();
        product.setName("test");
        product.setManufacturer(manufacturer);
        product.setUnit(Product.Unit.PIECE);

        ProductService productService = new ProductServiceImpl();
        assertThrows(EntityPersistenceException.class, () -> {
            productService.add(product);
            productService.add(product);
        });
    }
}