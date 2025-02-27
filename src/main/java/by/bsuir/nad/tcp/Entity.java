package by.bsuir.nad.tcp;

import by.bsuir.nad.server.db.entity.*;
import by.bsuir.nad.gson.GsonHelper;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Entity implements Serializable {
    public enum EntityType {
        UNAUTHORIZED_USER, USER, PERSON, SUPPLY, SUPPLIER, PRODUCT, MANUFACTURER,
    }

    public static class UnauthorizedUserConverter {
        public static UnauthorizedUser get(Entity entity) {
            return UnauthorizedUser.fromJson(entity.data);
        }

        public static void set(Entity entity, UnauthorizedUser user) {
            entity.type = EntityType.UNAUTHORIZED_USER;
            entity.data = UnauthorizedUser.toJson(user);
        }
    }

    public static class UserConverter {
        public static User get(Entity entity) {
            return User.fromJson(entity.data);
        }

        public static void set(Entity entity, User user) {
            entity.type = EntityType.USER;
            entity.data = User.toJson(user);
        }

        public static List<User> getList(Entity entity) {
            return User.fromJsonList(entity.data);
        }

        public static void setList(Entity entity, List<User> users) {
            entity.type = EntityType.USER;
            entity.data = User.toJsonList(users);
        }

        public static Long getPrimaryKey(Entity entity) {
            return Long.parseLong(entity.data);
        }

        public static void setPrimaryKey(Entity entity, Long primaryKey) {
            entity.type = EntityType.USER;
            entity.data = String.valueOf(primaryKey);
        }
    }

    public static class PersonConverter {
        public static Person get(Entity entity) {
            return Person.fromJson(entity.data);
        }

        public static void set(Entity entity, Person person) {
            entity.type = EntityType.PERSON;
            entity.data = Person.toJson(person);
        }

        public static List<Person> getList(Entity entity) {
            return Person.fromJsonList(entity.data);
        }

        public static void setList(Entity entity, List<Person> persons) {
            entity.type = EntityType.PERSON;
            entity.data = Person.toJsonList(persons);
        }

        public static Long getPrimaryKey(Entity entity) {
            return Long.parseLong(entity.data);
        }

        public static void setPrimaryKey(Entity entity, Long primaryKey) {
            entity.type = EntityType.PERSON;
            entity.data = String.valueOf(primaryKey);
        }
    }

    public static class SupplyConverter {
        public static Supply get(Entity entity) {
            return Supply.fromJson(entity.data);
        }

        public static void set(Entity entity, Supply person) {
            entity.type = EntityType.SUPPLY;
            entity.data = Supply.toJson(person);
        }

        public static List<Supply> getList(Entity entity) {
            return Supply.fromJsonList(entity.data);
        }

        public static void setList(Entity entity, List<Supply> persons) {
            entity.type = EntityType.SUPPLY;
            entity.data = Supply.toJsonList(persons);
        }

        public static Long getPrimaryKey(Entity entity) {
            return Long.parseLong(entity.data);
        }

        public static void setPrimaryKey(Entity entity, Long primaryKey) {
            entity.type = EntityType.SUPPLY;
            entity.data = String.valueOf(primaryKey);
        }
    }

    public static class SupplierConverter {
        public static Supplier get(Entity entity) {
            return Supplier.fromJson(entity.data);
        }

        public static void set(Entity entity, Supplier person) {
            entity.type = EntityType.SUPPLIER;
            entity.data = Supplier.toJson(person);
        }

        public static List<Supplier> getList(Entity entity) {
            return Supplier.fromJsonList(entity.data);
        }

        public static void setList(Entity entity, List<Supplier> persons) {
            entity.type = EntityType.SUPPLIER;
            entity.data = Supplier.toJsonList(persons);
        }

        public static Long getPrimaryKey(Entity entity) {
            return Long.parseLong(entity.data);
        }

        public static void setPrimaryKey(Entity entity, Long primaryKey) {
            entity.type = EntityType.SUPPLIER;
            entity.data = String.valueOf(primaryKey);
        }
    }

    public static class ProductConverter {
        public static Product get(Entity entity) {
            return Product.fromJson(entity.data);
        }

        public static void set(Entity entity, Product product) {
            entity.type = EntityType.PRODUCT;
            entity.data = Product.toJson(product);
        }

        public static List<Product> getList(Entity entity) {
            return Product.fromJsonList(entity.data);
        }

        public static void setList(Entity entity, List<Product> products) {
            entity.type = EntityType.PRODUCT;
            entity.data = Product.toJsonList(products);
        }

        public static Long getPrimaryKey(Entity entity) {
            return Long.parseLong(entity.data);
        }

        public static void setPrimaryKey(Entity entity, Long primaryKey) {
            entity.type = EntityType.PRODUCT;
            entity.data = String.valueOf(primaryKey);
        }
    }

    public static class ManufacturerConverter {
        public static Manufacturer get(Entity entity) {
            return Manufacturer.fromJson(entity.data);
        }

        public static void set(Entity entity, Manufacturer manufacturer) {
            entity.type = EntityType.MANUFACTURER;
            entity.data = Manufacturer.toJson(manufacturer);
        }

        public static List<Manufacturer> getList(Entity entity) {
            return Manufacturer.fromJsonList(entity.data);
        }

        public static void setList(Entity entity, List<Manufacturer> manufacturers) {
            entity.type = EntityType.MANUFACTURER;
            entity.data = Manufacturer.toJsonList(manufacturers);
        }

        public static Long getPrimaryKey(Entity entity) {
            return Long.parseLong(entity.data);
        }

        public static void setPrimaryKey(Entity entity, Long primaryKey) {
            entity.type = EntityType.MANUFACTURER;
            entity.data = String.valueOf(primaryKey);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;

    private EntityType type;
    private String data;

    public static Entity fromJson(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, Entity.class);
    }

    public static String toJson(Entity entity) {
        return new GsonHelper(GSON_VERSION).toJson(entity);
    }
}
