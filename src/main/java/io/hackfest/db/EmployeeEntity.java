package io.hackfest.db;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Optional;

@Entity
@Table(name = "employees")
public class EmployeeEntity extends PanacheEntityBase {
    @Id
    public Long id;

    public Long primaryShopId;

    public String name;

    public Long cardId;

    public static Optional<EmployeeEntity> findByCardId(Long cardId){
        return find("cardId", cardId).singleResultOptional();
    }
}
