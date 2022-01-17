package mandykr.nutrient.dto.combination;

import lombok.*;
import mandykr.nutrient.entity.combination.Combination;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinationDto {
    private Long id;
    private String caption;
    private double rating;
    private List<SupplementDto> supplementDtoList;

    public static CombinationDto of(Combination entity) {
        return CombinationDto.builder()
                .id(entity.getId())
                .caption(entity.getCaption())
                .rating(entity.getRating())
                .build();
    }

    public void addSupplement(SupplementDto dto) {
        supplementDtoList.add(dto);
    }

//    public Combination createEntity() {
//        return Combination.builder()
//                .id(id)
//                .caption(caption)
//                .rating(rating)
//                .build();
//    }

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
