package mandykr.nutrient.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mandykr.nutrient.dto.StarRateDto;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class StarRate {
    @Id
    @GeneratedValue
    @Column(name = "STAR_RATE_ID")
    private Long id; //영양제 별점 번호

    private int starNumber; //별점 갯수

    @ManyToOne
    @JoinColumn(name = "SUPPLEMENT_ID") //외래키
    private Supplement supplement; //영양제 번호

    // 회원 번호
    //private Member member;

    //등록을 위한 생성자
    public StarRate(int starNumber, Supplement supplement) {
        this.starNumber = starNumber;
        this.supplement = supplement;
    }

    //수정을 위한 생성자
    public StarRate(Long id, int starNumber, Supplement supplement) {
        this.id = id;
        this.starNumber = starNumber;
        this.supplement = supplement;
    }
}
