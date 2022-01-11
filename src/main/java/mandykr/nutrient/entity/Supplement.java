package mandykr.nutrient.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Supplement {
    @Id @GeneratedValue
    @Column(name = "SUPPLEMENT")
    private Long id;

    private String name;
    private String prdlstReportNo; //품목제조번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLEMENT_CATEGORY_ID")
    private SupplementCategory supplementCategory;

    private Double ranking; //별점

    @OneToMany(mappedBy = "supplement")
    private List<StarRate> starRateList = new ArrayList<>();

    //수정을 위한 메서드
    public void updateNameAndPrdlst(String name, String prdlstReportNo) {
        if(name != null)
            this.name = name;
        if(prdlstReportNo != null)
            this.prdlstReportNo = prdlstReportNo;
    }

    //평점 수정을 위한 메서드
    public void updateRanking(){
        double ranking = starRateList.stream()
                .mapToInt(StarRate::getStarNumber)
                .average()
                .getAsDouble();

        this.ranking = ranking;
    }

    public void insertList(Long id, int starNumber) {
        StarRate starRate = new StarRate();
        starRate.updateStarRate(id,starNumber);
        this.starRateList.add(starRate);
    }

    public void updateList(Long starRateId, int starNumber) {
        for(int i=0;i < starRateList.size(); i++) {
            if (starRateList.get(i).getId() == starRateId) {
                StarRate starRate = new StarRate();
                starRate.updateStarRate(null,starNumber);
                starRateList.set(i, starRate);
                return;
            }
        }
    }
}
