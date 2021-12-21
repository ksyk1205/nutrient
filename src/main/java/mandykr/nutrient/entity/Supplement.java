package mandykr.nutrient.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Supplement {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Double ranking; //별점


}
