package io.hackfest.db;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "debezium_receipt_export")
@TypeDef(name = "json", typeClass = JsonType.class)
public class DebeziumReceiptExport extends PanacheEntityBase {

    @Id
    public Long id;

    public LocalDateTime timestamp;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    public String payload;
}
