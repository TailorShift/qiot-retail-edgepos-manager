package io.hackfest.dbmodel;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "products")
@TypeDef(name = "json", typeClass = JsonType.class)
public class ProductEntity extends PanacheEntityBase {
    @Id
    public Long id;

    public String manufacturer;

    public String name;

    public String code;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    public List<String> colors;

    public Double price;

    public Double taxRate;

    @Transient
    public List<AvailableItem> available;

    public static Optional<ProductEntity> findByCode(String code){
        return find("code", code).singleResultOptional();
    }
}
