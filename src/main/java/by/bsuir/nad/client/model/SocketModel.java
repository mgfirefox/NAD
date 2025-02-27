package by.bsuir.nad.client.model;

import by.bsuir.nad.client.ClientSocket;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.tcp.ServerRequest;
import by.bsuir.nad.tcp.ServerResponse;
import lombok.*;

import java.io.IOException;

@Getter
public abstract class SocketModel extends Model {
    @NonNull
    private final ClientSocket clientSocket;

    public SocketModel() {
        this(true);
    }

    public SocketModel(boolean shouldOpenSocketIfClosed) {
        clientSocket = ClientApplication.getContext().getBean("clientSocket", ClientSocket.class);

        if (shouldOpenSocketIfClosed) {
            try {
                clientSocket.openIfClosed();
            } catch (IOException e) {
                throw new RuntimeException("Failed to connect to the Server", e);
            }
        }
    }

    protected void sendServerRequest(ServerRequest request) throws IOException {
        ServerRequest.writeJson(request, clientSocket.getPrintWriter());
    }

    protected ServerResponse receiveServerResponse() throws IOException {
        return ServerResponse.readJson(clientSocket.getBufferedReader());
    }
}
