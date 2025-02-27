package by.bsuir.nad.server.db.entity;

import by.bsuir.nad.gson.GsonHelper;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class SupplyProduct implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;
    
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "supply_id")
        private Long supplyId;
        @Column(name = "product_id")
        private Long productId;
        @Transient
        private Product product;
    }
    
    @EmbeddedId
    private Id id;
    private Integer amount;
    private BigDecimal pricePerUnit;

    public static SupplyProduct fromJson(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, SupplyProduct.class);
    }

    public static List<SupplyProduct> fromJsonList(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, new TypeToken<List<SupplyProduct>>(){});
    }

    public static String toJson(SupplyProduct supplyProduct) {
        return new GsonHelper(GSON_VERSION).toJson(supplyProduct);
    }

    public static String toJsonList(List<SupplyProduct> supplyProducts) {
        return new GsonHelper(GSON_VERSION).toJson(supplyProducts, new TypeToken<List<SupplyProduct>>(){});
    }
}
