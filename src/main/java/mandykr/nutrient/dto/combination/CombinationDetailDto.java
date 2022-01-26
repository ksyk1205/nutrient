package mandykr.nutrient.dto.combination;

import lombok.*;
import mandykr.nutrient.entity.combination.Combination;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinationDetailDto {
    private Long id;
    private String caption;
    private double rating;
    @Builder.Default
    private List<CombinationDetailDto.SupplementDto> supplementDtoList = new ArrayList<>();

    public void addSupplement(CombinationDetailDto.SupplementDto dto) {
        supplementDtoList.add(dto);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupplementDto {
        private Long id;
        private String name;
        private Long categoryId;
        private String categoryName;
    }
}
