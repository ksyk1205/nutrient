package mandykr.nutrient.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue //자동증가
    @Column(name = "MEMBER_ID")
    private Long id;
    private String name;
}
