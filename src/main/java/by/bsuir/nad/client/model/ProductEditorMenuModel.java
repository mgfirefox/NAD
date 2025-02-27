package by.bsuir.nad.client.model;

import by.bsuir.nad.server.db.entity.Manufacturer;
import by.bsuir.nad.server.db.entity.Product;
import by.bsuir.nad.tcp.Entity;
import by.bsuir.nad.tcp.Entity.EntityType;
import by.bsuir.nad.tcp.Entity.ManufacturerConverter;
import by.bsuir.nad.tcp.Entity.PersonConverter;
import by.bsuir.nad.tcp.Entity.ProductConverter;
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
public class ProductEditorMenuModel extends UserModel {
    @Setter
    private Product product;

    @Getter
    private List<Product> products;
    @Getter
    private List<Manufacturer> manufacturers;
    @Getter
    private Error error;

    public void sendProduct(ServerRequestType type) throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.PRODUCT);
        ProductConverter.set(entity, product);

        ServerRequest request = new ServerRequest();
        request.setType(type);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receiveProducts() throws IOException {
        ServerResponse response = receiveServerResponse();
        if (response.getStatus() == ServerResponseStatus.OK) {
            products = ProductConverter.getList(response.getEntity());
            error = null;
        } else {
            products = null;
            error = response.getError();
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
}
