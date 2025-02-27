package by.bsuir.nad.tcp;

import by.bsuir.nad.gson.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Error implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;

    private String message;

    public static Error fromJson(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, Error.class);
    }

    public static String toJson(Error message) {
        return new GsonHelper(GSON_VERSION).toJson(message);
    }
}
