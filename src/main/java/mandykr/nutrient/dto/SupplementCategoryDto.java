package mandykr.nutrient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplementCategoryDto {
    private Long id;
    private String name;
    private int level;
    private SupplementCategoryDto parentCategory;

    public SupplementCategoryDto(mandykr.nutrient.entity.SupplementCategory category) {
        this.id = category.getId();
        this.name = category.getName();
        this.level = category.getLevel();
        this.parentCategory = category.getParentCategory() == null ?
                null : new SupplementCategoryDto(category.getParentCategory());
    }
}
