package by.bsuir.nad.client.model;

import by.bsuir.nad.server.db.entity.Person;
import by.bsuir.nad.tcp.Entity;
import by.bsuir.nad.tcp.Entity.EntityType;
import by.bsuir.nad.tcp.Entity.PersonConverter;
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
import java.util.List;

@NoArgsConstructor
public class PersonEditorMenuModel extends UserModel {
    @Setter
    private Person person;

    @Getter
    private List<Person> persons;
    @Getter
    private Error error;

    public void sendPerson(ServerRequestType type) throws IOException {
        Entity entity = new Entity();
        entity.setType(EntityType.PERSON);
        PersonConverter.set(entity, person);

        ServerRequest request = new ServerRequest();
        request.setType(type);
        request.setEntity(entity);
        sendServerRequest(request);
    }

    public void receivePersons() throws IOException {
        ServerResponse response = receiveServerResponse();
        if (response.getStatus() == ServerResponseStatus.OK) {
            persons = PersonConverter.getList(response.getEntity());
            error = null;
        } else {
            persons = null;
            error = response.getError();
        }
    }
}
