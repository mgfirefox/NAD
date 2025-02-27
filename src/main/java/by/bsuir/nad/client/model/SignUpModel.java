package by.bsuir.nad.client.model;

import by.bsuir.nad.server.db.entity.User;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import by.bsuir.nad.tcp.Entity;
import by.bsuir.nad.tcp.Entity.EntityType;
import by.bsuir.nad.tcp.Entity.UnauthorizedUserConverter;
import by.bsuir.nad.tcp.Error;
import by.bsuir.nad.tcp.ServerRequest;
import by.bsuir.nad.tcp.ServerRequest.ServerRequestType;
import by.bsuir.nad.tcp.ServerResponse;
import by.bsuir.nad.tcp.ServerResponse.ServerResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;

@NoArgsConstructor
public class SignUpModel extends SocketModel {
    @Setter
    @NonNull
    private UnauthorizedUser unauthorizedUser;

    @Getter
    private User user;
    @Getter
    private Error error;

    public void sendUnauthorizedUser() throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.UNAUTHORIZED_USER);
        UnauthorizedUserConverter.set(entity, unauthorizedUser);

        ServerRequest request = new ServerRequest();
        request.setType(ServerRequestType.SIGN_UP);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receiveUser() throws IOException {
        ServerResponse response = receiveServerResponse();
        if (response.getStatus() == ServerResponseStatus.OK) {
            user = Entity.UserConverter.get(response.getEntity());
            error = null;
        } else {
            user = null;
            error = response.getError();
        }
    }
}
