package io.hackfest.db;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shop_discounts")
public class ShopDiscountEntity extends PanacheEntityBase {
    @Id
    public Long id;

    public Long shopId;

    public Long productId;

    public Integer discount;
}
