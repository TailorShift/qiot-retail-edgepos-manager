package io.hackfest.dbmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipt_positions")
public class ReceiptPositionEntity extends PanacheEntityBase {

    @Id
    public Long id;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    @JsonIgnore
    public ReceiptEntity receipt;

    public Long position;

    public Long productId;

    public String size;

    public String color;

    public Integer quantity;

    @Transient
    public Integer returnedQuantity;

    public Double price;

    public Integer discount;

    public String discountReason;

    public Double taxRate;
}
