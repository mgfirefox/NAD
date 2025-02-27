package by.bsuir.nad.client.model;

import by.bsuir.nad.server.db.entity.Manufacturer;
import by.bsuir.nad.server.db.entity.Product;
import by.bsuir.nad.server.db.entity.Supplier;
import by.bsuir.nad.server.db.entity.Supply;
import by.bsuir.nad.tcp.Entity;
import by.bsuir.nad.tcp.Entity.*;
import by.bsuir.nad.tcp.Error;
import by.bsuir.nad.tcp.ServerRequest;
import by.bsuir.nad.tcp.ServerRequest.ServerRequestType;
import by.bsuir.nad.tcp.ServerResponse;
import by.bsuir.nad.tcp.ServerResponse.ServerResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class SupplyEditorMenuModel extends UserModel {
    @Setter
    private Supply supply;

    @Getter
    private List<Supply> supplies;
    @Getter
    private List<Supplier> suppliers;
    @Getter
    private List<Manufacturer> manufacturers;
    @Getter
    private List<Product> manufacturerProducts;
    @Getter
    private Error error;

    public void sendSupply(ServerRequestType type) throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.SUPPLY);
        SupplyConverter.set(entity, supply);

        ServerRequest request = new ServerRequest();
        request.setType(type);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receiveSupplies() throws IOException {
        ServerResponse response = receiveServerResponse();
        if (response.getStatus() == ServerResponseStatus.OK) {
            supplies = SupplyConverter.getList(response.getEntity());
            error = null;
        } else {
            supplies = null;
            error = response.getError();
        }
    }

    private void sendSupplier() throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.SUPPLIER);
        SupplierConverter.set(entity, null);

        ServerRequest request = new ServerRequest();
        request.setType(ServerRequestType.GET);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receiveSuppliers() {
        try {
            sendSupplier();

            ServerResponse response = receiveServerResponse();
            if (response.getStatus() == ServerResponseStatus.OK) {
                suppliers = SupplierConverter.getList(response.getEntity());
            } else {
                suppliers = new ArrayList<>();
            }
        } catch (IOException e) {
            suppliers = new ArrayList<>();
        }
    }

    private void sendManufacturer() throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.MANUFACTURER);
        ManufacturerConverter.set(entity, null);

        ServerRequest request = new ServerRequest();
        request.setType(ServerRequestType.GET);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receiveManufacturers() {
        try {
            sendManufacturer();

            ServerResponse response = receiveServerResponse();
            if (response.getStatus() == ServerResponseStatus.OK) {
                manufacturers = ManufacturerConverter.getList(response.getEntity());
            } else {
                manufacturers = new ArrayList<>();
            }
        } catch (IOException e) {
            manufacturers = new ArrayList<>();
        }
    }

    private void sendManufacturerProduct() throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.PRODUCT);
        ProductConverter.set(entity, null);

        ServerRequest request = new ServerRequest();
        request.setType(ServerRequestType.GET);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receiveManufacturerProducts() {
        try {
            sendManufacturerProduct();

            ServerResponse response = receiveServerResponse();
            if (response.getStatus() == ServerResponseStatus.OK) {
                manufacturerProducts = ProductConverter.getList(response.getEntity());
            } else {
                manufacturerProducts = new ArrayList<>();
            }
        } catch (IOException e) {
            manufacturerProducts = new ArrayList<>();
        }
    }
}
