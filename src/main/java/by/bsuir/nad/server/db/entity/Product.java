package by.bsuir.nad.server.db.entity;

import by.bsuir.nad.gson.GsonHelper;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Product implements Serializable {
    public enum Unit {
        UNDEFINED, PIECE, GRAM, KILOGRAM, TONNE,
        ;

        public static String toString(Unit unit) {
            if (unit == null) {
                unit = UNDEFINED;
            }
            return switch (unit) {
                case PIECE -> "штука";
                case GRAM -> "грамм";
                case KILOGRAM -> "килограмм";
                case TONNE -> "тонна";
                default -> "неопределенная";
            };
        }

        public static Unit fromString(String s) {
            return switch (s) {
                case "штука" -> PIECE;
                case "грамм" -> GRAM;
                case "килограмм" -> KILOGRAM;
                case "тонна" -> TONNE;
                default -> UNDEFINED;
            };
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;
    private Unit unit;

    public static Product fromJson(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, Product.class);
    }

    public static List<Product> fromJsonList(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, new TypeToken<List<Product>>(){});
    }

    public static String toJson(Product product) {
        return new GsonHelper(GSON_VERSION).toJson(product);
    }

    public static String toJsonList(List<Product> products) {
        return new GsonHelper(GSON_VERSION).toJson(products, new TypeToken<List<Product>>(){});
    }
}
