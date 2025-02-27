package by.bsuir.nad.client.model;

import by.bsuir.nad.server.db.entity.Manufacturer;
import by.bsuir.nad.tcp.Entity;
import by.bsuir.nad.tcp.Entity.EntityType;
import by.bsuir.nad.tcp.Entity.ManufacturerConverter;
import by.bsuir.nad.tcp.Error;
import by.bsuir.nad.tcp.ServerRequest;
import by.bsuir.nad.tcp.ServerRequest.ServerRequestType;
import by.bsuir.nad.tcp.ServerResponse;
import by.bsuir.nad.tcp.ServerResponse.ServerResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

@NoArgsConstructor
public class ManufacturerEditorMenuModel extends UserModel {
    @Setter
    private Manufacturer manufacturer;

    @Getter
    private List<Manufacturer> manufacturers;
    @Getter
    private Error error;

    public void sendManufacturer(ServerRequestType type) throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.MANUFACTURER);
        ManufacturerConverter.set(entity, manufacturer);

        ServerRequest request = new ServerRequest();
        request.setType(type);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receiveManufacturers() throws IOException {
        ServerResponse response = receiveServerResponse();
        if (response.getStatus() == ServerResponseStatus.OK) {
            manufacturers = ManufacturerConverter.getList(response.getEntity());
            error = null;
        } else {
            manufacturers = null;
            error = response.getError();
        }
    }
}
