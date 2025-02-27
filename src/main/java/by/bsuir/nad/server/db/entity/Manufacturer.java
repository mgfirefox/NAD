package by.bsuir.nad.server.db.entity;

import by.bsuir.nad.gson.GsonHelper;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Manufacturer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manufacturer_id")
    private long id;
    private String name;

    public static Manufacturer fromJson(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, Manufacturer.class);
    }

    public static List<Manufacturer> fromJsonList(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, new TypeToken<List<Manufacturer>>(){});
    }

    public static String toJson(Manufacturer manufacturer) {
        return new GsonHelper(GSON_VERSION).toJson(manufacturer);
    }

    public static String toJsonList(List<Manufacturer> manufacturers) {
        return new GsonHelper(GSON_VERSION).toJson(manufacturers, new TypeToken<List<Manufacturer>>(){});
    }
}

