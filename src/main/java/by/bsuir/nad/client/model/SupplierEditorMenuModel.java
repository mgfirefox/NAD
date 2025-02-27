package by.bsuir.nad.client.model;

import by.bsuir.nad.server.db.entity.Supplier;
import by.bsuir.nad.tcp.Entity;
import by.bsuir.nad.tcp.Entity.EntityType;
import by.bsuir.nad.tcp.Entity.PersonConverter;
import by.bsuir.nad.tcp.Entity.SupplierConverter;
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
public class SupplierEditorMenuModel extends UserModel {
    @Setter
    private Supplier supplier;

    @Getter
    private List<Supplier> suppliers;
    @Getter
    private Error error;

    public void sendSupplier(ServerRequestType type) throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.SUPPLIER);
        SupplierConverter.set(entity, supplier);

        ServerRequest request = new ServerRequest();
        request.setType(type);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receiveSuppliers() throws IOException {
        ServerResponse response = receiveServerResponse();
        if (response.getStatus() == ServerResponseStatus.OK) {
            suppliers = SupplierConverter.getList(response.getEntity());
            error = null;
        } else {
            suppliers = null;
            error = response.getError();
        }
    }
}
