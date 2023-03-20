package io.hackfest.dbmodel;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "receipts")
public class ReceiptEntity extends PanacheEntityBase {

    @Id
    public Long id;

    public Long shopId;

    public Long posDeviceId;

    public Long customerId;

    public LocalDateTime createdAt;

    public Double discountTotal;

    public Double taxTotal;

    public Double amountTotal;

    public Long employeeId;

    public Long deliveryShopId;

    @OneToMany(mappedBy = "receipt")
    public List<ReceiptPositionEntity> positions;
}
