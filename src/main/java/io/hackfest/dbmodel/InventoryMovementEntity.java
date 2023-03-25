package io.hackfest.dbmodel;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovementEntity extends PanacheEntityBase {
    @Id
    public Long id;

    public Long shopId;

    public Long productId;

    public Long receiptId;

    public Long returnId;

    @Enumerated(EnumType.STRING)
    public Size size;

    public String color;

    public Integer quantity;

    public static Optional<InventoryQuantity> getCurrentQuantity(long productId, long shopId, Size size, String color) {
        return find("""
                SELECT productId, shopId, size, color, SUM(quantity) as quantity
                FROM InventoryMovementEntity e
                WHERE productId = ?1
                AND shopId = ?2
                AND size = ?3
                AND color = ?4
                GROUP BY productId, shopId, size, color
                """, productId, shopId, size, color)
                .project(InventoryQuantity.class)
                .singleResultOptional();
    }

    public static List<AvailableItem> getAvailableItems(long productId, long shopId) {
        return find("""
                SELECT e.shopId, e.size, e.color, SUM(e.quantity) as quantity, sde.discount
                FROM InventoryMovementEntity e
                LEFT JOIN ShopDiscountEntity sde ON e.productId = sde.productId AND e.shopId = sde.shopId
                WHERE e.productId = ?1
                AND e.shopId = ?2
                GROUP BY e.shopId, e.size, e.color, sde.discount
                HAVING SUM(e.quantity) > 0
                """, productId, shopId)
                .project(AvailableItem.class)
                .stream().toList();
    }
}
