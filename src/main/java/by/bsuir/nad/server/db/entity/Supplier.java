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
public class Supplier implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;
    private String name;

    public static Supplier fromJson(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, Supplier.class);
    }

    public static List<Supplier> fromJsonList(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, new TypeToken<List<Supplier>>(){});
    }

    public static String toJson(Supplier supplier) {
        return new GsonHelper(GSON_VERSION).toJson(supplier);
    }

    public static String toJsonList(List<Supplier> suppliers) {
        return new GsonHelper(GSON_VERSION).toJson(suppliers, new TypeToken<List<Supplier>>(){});
    }
}
