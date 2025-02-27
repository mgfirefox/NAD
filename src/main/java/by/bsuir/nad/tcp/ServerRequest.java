package by.bsuir.nad.tcp;

import by.bsuir.nad.gson.GsonHelper;
import lombok.*;

import java.io.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerRequest implements Serializable {
    public enum ServerRequestType {
        UNDEFINED, SIGN_UP, SIGN_IN, GET, ADD, EDIT, REMOVE, FIND,
    }

    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;

    private ServerRequestType type;
    private String data;

    public Entity getEntity() {
        return Entity.fromJson(data);
    }

    public void setEntity(Entity entity) {
        data = Entity.toJson(entity);
    }

    public static ServerRequest readJson(InputStream in) throws IOException {
        return new GsonHelper(GSON_VERSION).fromJson(in, ServerRequest.class);
    }

    public static ServerRequest readJson(Reader in) throws IOException {
        return new GsonHelper(GSON_VERSION).fromJson(in, ServerRequest.class);
    }

    public static void writeJson(ServerRequest request, OutputStream out) throws IOException {
        new GsonHelper(GSON_VERSION).toJson(request, out);
    }

    public static void writeJson(ServerRequest request, Writer out) throws IOException {
        new GsonHelper(GSON_VERSION).toJson(request, out);
    }
}
