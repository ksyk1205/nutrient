package mandykr.nutrient.entity.supplement;

import lombok.*;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.entity.util.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Supplement extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "SUPPLEMENT_ID")
    private Long id;

    private String name;
    private String prdlstReportNo; //품목제조번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLEMENT_CATEGORY_ID")
    private SupplementCategory supplementCategory;

    private double ranking; //별점

    @OneToMany(mappedBy = "supplement")
    @Builder.Default
    private List<SupplementStarRate> starRateList = new ArrayList<>();

    private boolean deleteFlag;

    //수정을 위한 메서드
    public void updateNameAndPrdlstAndCategory(String name, String prdlstReportNo, SupplementCategory supplementCategory) {
        if(name != null) {
            this.name = name;
        }
        if(prdlstReportNo != null) {
            this.prdlstReportNo = prdlstReportNo;
        }
        if(supplementCategory.getId()!=this.supplementCategory.getId()){
            this.supplementCategory = supplementCategory;
        }
    }

    //평점 수정을 위한 메서드
    public void updateRanking(){
        double ranking = starRateList.stream()
                .mapToInt(SupplementStarRate::getStarNumber)
                .average()
                .getAsDouble();

        this.ranking = ranking;
    }

    public void insertList(Long id, int starNumber) {
        SupplementStarRate starRate = new SupplementStarRate();
        starRate.updateStarRate(id,starNumber);
        this.starRateList.add(starRate);
    }

    public void updateList(Long starRateId, int starNumber) {
        for(int i=0;i < starRateList.size(); i++) {
            if (starRateList.get(i).getId() == starRateId) {
                SupplementStarRate starRate = new SupplementStarRate();
                starRate.updateStarRate(null,starNumber);
                starRateList.set(i, starRate);
                return;
            }
        }
    }

    public void updateDeleteFlag() {
        this.deleteFlag = true;
    }
}
