package by.bsuir.nad.tcp;

import by.bsuir.nad.gson.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerResponse implements Serializable {
    public enum ServerResponseStatus {
        UNDEFINED, OK, ERROR,
    }

    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;
    
    private ServerResponseStatus status;
    private String data;

    public Entity getEntity() {
        return Entity.fromJson(data);
    }

    public void setEntity(Entity entity) {
        data = Entity.toJson(entity);
    }

    public Error getError() {
        return Error.fromJson(data);
    }

    public void setError(Error message) {
        data = Error.toJson(message);
    }

    public static ServerResponse readJson(InputStream in) throws IOException {
        return new GsonHelper(GSON_VERSION).fromJson(in, ServerResponse.class);
    }

    public static ServerResponse readJson(Reader in) throws IOException {
        return new GsonHelper(GSON_VERSION).fromJson(in, ServerResponse.class);
    }

    public static void writeJson(ServerResponse response, OutputStream out) throws IOException {
        new GsonHelper(GSON_VERSION).toJson(response, out);
    }

    public static void writeJson(ServerResponse response, Writer out) throws IOException {
        new GsonHelper(GSON_VERSION).toJson(response, out);
    }
}
