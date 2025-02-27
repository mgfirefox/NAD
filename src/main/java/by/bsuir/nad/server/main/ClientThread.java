package by.bsuir.nad.server.main;

import by.bsuir.nad.server.db.entity.*;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import by.bsuir.nad.server.db.exception.EntityPersistenceException;
import by.bsuir.nad.server.db.service.*;
import by.bsuir.nad.tcp.*;
import by.bsuir.nad.tcp.Entity.*;
import by.bsuir.nad.tcp.Error;
import by.bsuir.nad.tcp.ServerResponse.ServerResponseStatus;
import jakarta.persistence.PersistenceException;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ClientThread implements Runnable {
    @NonNull
    private final Socket socket;

    @NonNull
    private final BufferedReader in;
    private final PrintWriter out;

    @NonNull
    private final UserService userService;
    @NonNull
    private final PersonService personService;
    @NonNull
    private final SupplierService supplierService;
    @NonNull
    private final ProductService productService;
    @NonNull
    private final ManufacturerService manufacturerService;
    @NonNull
    private final SupplyService supplyService;

    public ClientThread(@NonNull Socket socket) throws IOException {
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //out = new PrintWriter(socket.getOutputStream(), true);
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        userService = new UserServiceImpl();
        personService = new PersonServiceImpl();
        supplierService = new SupplierServiceImpl();
        manufacturerService = new ManufacturerServiceImpl();
        productService = new ProductServiceImpl();
        supplyService = new SupplyServiceImpl();

        Thread thread = new Thread(this, "ClientThread-" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        thread.start();
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                ServerRequest request = ServerRequest.readJson(in);
                Entity entity = Entity.fromJson(request.getData());

                System.out.println(request);

                switch (request.getType()) {
                    case SIGN_UP -> signUpUser(UnauthorizedUserConverter.get(entity));
                    case SIGN_IN -> signInUser(UnauthorizedUserConverter.get(entity));
                    case GET -> getEntities(entity);
                    case ADD -> addEntity(entity);
                    case EDIT -> editEntity(entity);
                    case REMOVE -> removeEntity(entity);
                }
            } catch (SocketException e) {
                e.printStackTrace();
                System.out.println("Соединение с Клиентом " + socket.getInetAddress().getHostAddress() + ':' + socket.getPort() + " потеряно: " + e.getMessage());
                break;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Произошла ошибка ввода/вывода: " + e.getMessage());
                break;
            } catch (PersistenceException e) {
                e.printStackTrace();
                System.out.println("Произошла ошибка при обращении к базе данных: " + e.getMessage());
                break;
            } catch (RuntimeException e) {
                e.printStackTrace();
                break;
            }
        }
        closeSocket();
    }

    private void closeSocket() {
        try {
            socket.close();
            System.out.println("Клиент " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " успешно отключился");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось закрыть соединение Клиента " + socket.getInetAddress().getHostAddress() + ':' + socket.getPort() + ": " + e.getMessage());
        }
    }

    private void signUpUser(UnauthorizedUser unauthorizedUser) throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            User user = userService.add(unauthorizedUser);

            Entity entity = new Entity();
            entity.setType(EntityType.USER);
            UserConverter.set(entity, user);

            response.setStatus(ServerResponseStatus.OK);
            response.setEntity(entity);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Не удалось зарегистрироваться"));
        }

        ServerResponse.writeJson(response, out);
    }

    private void signInUser(UnauthorizedUser unauthorizedUser) throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            User user = userService.find(unauthorizedUser);

            Entity entity = new Entity();
            entity.setType(EntityType.USER);
            UserConverter.set(entity, user);

            response.setStatus(ServerResponseStatus.OK);
            response.setEntity(entity);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Неверное имя или пароль"));
        }

        ServerResponse.writeJson(response, out);
    }

    private void getEntities(Entity entity) throws IOException {
        switch (entity.getType()) {
            case USER -> getUsers();
            case PERSON -> getPersons();
            case SUPPLY -> getSupplies();
            case SUPPLIER -> getSuppliers();
            case PRODUCT -> getProducts();
            case MANUFACTURER -> getManufacturers();
        }
    }

    private void getUsers() throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            Entity entity = new Entity();
            entity.setType(EntityType.USER);
            UserConverter.setList(entity, userService.get());

            response.setStatus(ServerResponseStatus.OK);
            response.setEntity(entity);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Нет личных данных"));
        }
        ServerResponse.writeJson(response, out);
    }

    private void getPersons() throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            Entity entity = new Entity();
            entity.setType(EntityType.PERSON);
            PersonConverter.setList(entity, personService.get());

            response.setStatus(ServerResponseStatus.OK);
            response.setEntity(entity);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Нет личных данных"));
        }
        ServerResponse.writeJson(response, out);
    }

    private void getSupplies() throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            Entity entity = new Entity();
            entity.setType(EntityType.SUPPLY);
            SupplyConverter.setList(entity, supplyService.get());

            response.setStatus(ServerResponseStatus.OK);
            response.setEntity(entity);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Нет данных поставок"));
        }
        ServerResponse.writeJson(response, out);
    }

    private void getSuppliers() throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            Entity entity = new Entity();
            entity.setType(EntityType.SUPPLIER);
            SupplierConverter.setList(entity, supplierService.get());

            response.setStatus(ServerResponseStatus.OK);
            response.setEntity(entity);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Нет данных поставщиков"));
        }
        ServerResponse.writeJson(response, out);
    }

    private void getProducts() throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            Entity entity = new Entity();
            entity.setType(EntityType.PRODUCT);
            ProductConverter.setList(entity, productService.get());

            response.setStatus(ServerResponseStatus.OK);
            response.setEntity(entity);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Нет данных продуктов"));
        }
        ServerResponse.writeJson(response, out);
    }

    private void getManufacturers() throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            Entity entity = new Entity();
            entity.setType(EntityType.MANUFACTURER);
            ManufacturerConverter.setList(entity, manufacturerService.get());

            response.setStatus(ServerResponseStatus.OK);
            response.setEntity(entity);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Нет данных производителей"));
        }
        ServerResponse.writeJson(response, out);
    }

    private void addEntity(Entity entity) throws IOException {
        switch (entity.getType()) {
            case UNAUTHORIZED_USER -> addUser(UnauthorizedUserConverter.get(entity));
            case SUPPLY -> addSupply(SupplyConverter.get(entity));
            case SUPPLIER -> addSupplier(SupplierConverter.get(entity));
            case PRODUCT -> addProduct(ProductConverter.get(entity));
            case MANUFACTURER -> addManufacturer(ManufacturerConverter.get(entity));
        }
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void addUser(UnauthorizedUser unauthorizedUser) throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            userService.add(unauthorizedUser);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Не удалось добавить пользователя"));
            ServerResponse.writeJson(response, out);
            return;
        }

        Entity entity = new Entity();
        entity.setType(EntityType.USER);
        UserConverter.setList(entity, userService.get());

        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);

        ServerResponse.writeJson(response, out);
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void addSupply(Supply supply) throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            supplyService.add(supply);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Не удалось добавить поставку"));
            ServerResponse.writeJson(response, out);
            return;
        }

        Entity entity = new Entity();
        entity.setType(EntityType.SUPPLY);
        SupplierConverter.setList(entity, supplierService.get());

        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);

        ServerResponse.writeJson(response, out);
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void addSupplier(Supplier supplier) throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            supplierService.add(supplier);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Не удалось добавить поставщика"));
            ServerResponse.writeJson(response, out);
            return;
        }

        Entity entity = new Entity();
        entity.setType(EntityType.SUPPLIER);
        SupplierConverter.setList(entity, supplierService.get());

        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);

        ServerResponse.writeJson(response, out);
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void addProduct(Product product) throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            productService.add(product);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Не удалось добавить продукт"));
            ServerResponse.writeJson(response, out);
            return;
        }

        Entity entity = new Entity();
        entity.setType(EntityType.PRODUCT);
        ProductConverter.setList(entity, productService.get());

        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);

        ServerResponse.writeJson(response, out);
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void addManufacturer(Manufacturer manufacturer) throws IOException {
        ServerResponse response = new ServerResponse();

        try {
            manufacturerService.add(manufacturer);
        } catch (EntityPersistenceException e) {
            response.setStatus(ServerResponseStatus.ERROR);
            response.setError(new Error("Не удалось добавить производителя"));
            ServerResponse.writeJson(response, out);
            return;
        }

        Entity entity = new Entity();
        entity.setType(EntityType.MANUFACTURER);
        ManufacturerConverter.setList(entity, manufacturerService.get());

        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);

        ServerResponse.writeJson(response, out);
    }


    private void editEntity(Entity entity) throws IOException {
        switch (entity.getType()) {
            case PERSON -> editPerson(PersonConverter.get(entity));
            case SUPPLY -> editSupply(SupplyConverter.get(entity));
            case SUPPLIER -> editSupplier(SupplierConverter.get(entity));
            case PRODUCT -> editProduct(ProductConverter.get(entity));
            case MANUFACTURER -> editManufacturer(ManufacturerConverter.get(entity));
        }
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void editPerson(Person person) throws IOException {
        personService.edit(person);

        Entity entity = new Entity();
        entity.setType(EntityType.PERSON);
        PersonConverter.setList(entity, personService.get());

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void editSupply(Supply supply) throws IOException {
        supplyService.edit(supply);

        Entity entity = new Entity();
        entity.setType(EntityType.SUPPLIER);
        SupplyConverter.setList(entity, supplyService.get());

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void editSupplier(Supplier supplier) throws IOException {
        supplierService.edit(supplier);

        Entity entity = new Entity();
        entity.setType(EntityType.SUPPLY);
        SupplierConverter.setList(entity, supplierService.get());

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void editProduct(Product product) throws IOException {
        productService.edit(product);

        Entity entity = new Entity();
        entity.setType(EntityType.PRODUCT);
        ProductConverter.setList(entity, productService.get());

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    @SneakyThrows(EntityPersistenceException.class)
    private void editManufacturer(Manufacturer manufacturer) throws IOException {
        manufacturerService.edit(manufacturer);

        Entity entity = new Entity();
        entity.setType(EntityType.MANUFACTURER);
        ManufacturerConverter.setList(entity, manufacturerService.get());

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    private void removeEntity(Entity entity) throws IOException {
        switch (entity.getType()) {
            case USER -> removeUser(UserConverter.get(entity));
            case PERSON -> removePerson(PersonConverter.get(entity));
            case SUPPLY -> removeSupply(SupplyConverter.get(entity));
            case SUPPLIER -> removeSupplier(SupplierConverter.get(entity));
            case PRODUCT -> removeProduct(ProductConverter.get(entity));
            case MANUFACTURER -> removeManufacturer(ManufacturerConverter.get(entity));
        }
    }

    private void removeUser(User user) throws IOException {
        userService.remove(user);

        List<User> users;
        try {
            users = userService.get();
        } catch (EntityPersistenceException e) {
            users = new ArrayList<>();
        }

        Entity entity = new Entity();
        entity.setType(EntityType.USER);
        UserConverter.setList(entity, users);

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    private void removePerson(Person person) throws IOException {
        personService.remove(person);

        List<Person> persons;
        try {
            persons = personService.get();
        } catch (EntityPersistenceException e) {
            persons = new ArrayList<>();
        }

        Entity entity = new Entity();
        entity.setType(EntityType.PERSON);
        PersonConverter.setList(entity, persons);

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    private void removeSupply(Supply supply) throws IOException {
        supplyService.remove(supply);

        List<Supply> supplies;
        try {
            supplies = supplyService.get();
        } catch (EntityPersistenceException e) {
            supplies = new ArrayList<>();
        }

        Entity entity = new Entity();
        entity.setType(EntityType.SUPPLY);
        SupplyConverter.setList(entity, supplies);

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    private void removeSupplier(Supplier supplier) throws IOException {
        supplierService.remove(supplier);

        List<Supplier> suppliers;
        try {
            suppliers = supplierService.get();
        } catch (EntityPersistenceException e) {
            suppliers = new ArrayList<>();
        }

        Entity entity = new Entity();
        entity.setType(EntityType.SUPPLIER);
        SupplierConverter.setList(entity, suppliers);

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    private void removeProduct(Product product) throws IOException {
        productService.remove(product);

        List<Product> products;
        try {
            products = productService.get();
        } catch (EntityPersistenceException e) {
            products = new ArrayList<>();
        }

        Entity entity = new Entity();
        entity.setType(EntityType.PRODUCT);
        ProductConverter.setList(entity, products);

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }

    private void removeManufacturer(Manufacturer manufacturer) throws IOException {
        manufacturerService.remove(manufacturer);

        List<Manufacturer> manufacturers;
        try {
            manufacturers = manufacturerService.get();
        } catch (EntityPersistenceException e) {
            manufacturers = new ArrayList<>();
        }

        Entity entity = new Entity();
        entity.setType(EntityType.MANUFACTURER);
        ManufacturerConverter.setList(entity, manufacturers);

        ServerResponse response = new ServerResponse();
        response.setStatus(ServerResponseStatus.OK);
        response.setEntity(entity);
        ServerResponse.writeJson(response, out);
    }
}
