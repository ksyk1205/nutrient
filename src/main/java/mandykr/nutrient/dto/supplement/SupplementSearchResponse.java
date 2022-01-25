package mandykr.nutrient.dto.supplement;

import lombok.AllArgsConstructor;
import lombok.Data;
import mandykr.nutrient.entity.SupplementCategory;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class SupplementSearchResponse {
    private Long id;
    private String name;
    private String prdlstReportNo;
    private Double ranking;
    private boolean deleteFlag;
    private SupplementCategoryDto supplementCategoryDto;

    @Data
    @AllArgsConstructor
    public static class SupplementCategoryDto{
        private Long id;
        private String name;
    }

}

