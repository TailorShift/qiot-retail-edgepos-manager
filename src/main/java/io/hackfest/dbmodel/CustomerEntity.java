package io.hackfest.dbmodel;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Optional;

@Entity
@Table(name = "customers")
public class CustomerEntity extends PanacheEntityBase {
    @Id
    public Long id;

    public String name;

    public Long cardId;

    @Column(name = "street1")
    public String street1;

    @Column(name = "street2")
    public String street2;

    public String postcode;

    public String city;

    public Integer discount;

    public static Optional<CustomerEntity> findByCardId(Long cardId){
        return find("cardId", cardId).singleResultOptional();
    }
}
