package by.bsuir.nad.server.db.entity;

import by.bsuir.nad.gson.GsonHelper;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Supply implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supply_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    private Date deliveryDate;
    private Date paymentDate;
    @Transient
    private List<SupplyProduct> supplyProducts;
    private BigDecimal cost;

    public static Supply fromJson(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, Supply.class);
    }

    public static List<Supply> fromJsonList(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, new TypeToken<List<Supply>>(){});
    }

    public static String toJson(Supply supply) {
        return new GsonHelper(GSON_VERSION).toJson(supply);
    }

    public static String toJsonList(List<Supply> supplies) {
        return new GsonHelper(GSON_VERSION).toJson(supplies, new TypeToken<List<Supply>>(){});
    }
}
