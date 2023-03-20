package io.hackfest.dbmodel;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
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

    public static Optional<ProductEntity> findByCode(String code){
        return find("code", code).singleResultOptional();
    }
}
