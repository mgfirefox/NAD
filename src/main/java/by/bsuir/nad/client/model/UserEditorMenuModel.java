package by.bsuir.nad.client.model;

import by.bsuir.nad.server.db.entity.User;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import by.bsuir.nad.tcp.Entity;
import by.bsuir.nad.tcp.Entity.EntityType;
import by.bsuir.nad.tcp.Entity.UnauthorizedUserConverter;
import by.bsuir.nad.tcp.Entity.UserConverter;
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
public class UserEditorMenuModel extends UserModel {
    @Setter
    private UnauthorizedUser unauthorizedUser;
    @Setter
    private User user;

    @Getter
    private List<User> users;
    @Getter
    private Error error;

    public void sendUnauthorizedUser(ServerRequestType type) throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.UNAUTHORIZED_USER);
        UnauthorizedUserConverter.set(entity, unauthorizedUser);

        ServerRequest request = new ServerRequest();
        request.setType(type);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void sendUser(ServerRequestType type) throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.USER);
        UserConverter.set(entity, user);

        ServerRequest request = new ServerRequest();
        request.setType(type);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receiveUsers() throws IOException {
        ServerResponse response = receiveServerResponse();
        if (response.getStatus() == ServerResponseStatus.OK) {
            users = UserConverter.getList(response.getEntity());
            error = null;
        } else {
            users = null;
            error = response.getError();
        }
    }
}
