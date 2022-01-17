package mandykr.nutrient.dto.supplement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.supplement.SupplementStarRate;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplementDto {
    private Long id;
    private String name;
    private String prdlstReportNo; //품목제조번호
    private Double ranking; //별점

    //Entity -> Dto
    public SupplementDto(Supplement supplement){
        BeanUtils.copyProperties(supplement,this);
    }

}
