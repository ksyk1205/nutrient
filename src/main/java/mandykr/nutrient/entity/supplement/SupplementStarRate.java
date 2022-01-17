package mandykr.nutrient.entity.supplement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.util.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class SupplementStarRate extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "STARRATE_ID")
    private Long id; //영양제 별점 번호

    private int starNumber; //별점 갯수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLEMENT_ID") //외래키
    private Supplement supplement; //영양제 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID") //외래키
    private Member member;// 회원 번호

    //등록을 위한 생성자
    public SupplementStarRate(int starNumber, Supplement supplement, Member member) {
        this.starNumber = starNumber;
        this.supplement = supplement;
        this.member = member;
    }

    //수정을 위한 생성자
    public SupplementStarRate(Long id, int starNumber, Supplement supplement, Member member) {
        this.id = id;
        this.starNumber = starNumber;
        this.supplement = supplement;
        this.member = member;
    }

    //별점 수정을 위한 메서드
    public void updateStarRate(Long id,int starNumber){
        if(id!=null) {
            this.id = id;
        }
        this.starNumber = starNumber;
    }

}
