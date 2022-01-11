package mandykr.nutrient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.SupplementCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplementCategoryDto {
    private Long id;
    private String name;
    private int level;
    private SupplementParentCategoryDto parentCategory;

    public SupplementCategoryDto(Long id, String name, int level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public static SupplementCategoryDto toCategoryDto(SupplementCategory category) {
        SupplementCategoryDto categoryDto = new SupplementCategoryDto(category.getId(), category.getName(), category.getLevel());
        categoryDto.setParentCategory(category.getParentCategory() == null ?
                null : new SupplementParentCategoryDto(category.getParentCategory().getId()));
        return categoryDto;
    }

    public static SupplementCategoryDto toCategoryDto(Long id, String name, int level, Long parentId) {
        SupplementCategoryDto categoryDto = new SupplementCategoryDto(id, name, level);
        categoryDto.setParentCategory(new SupplementParentCategoryDto(parentId));
        return categoryDto;
    }

    public void setParentCategoryById(Long parentId) {
        setParentCategory(new SupplementParentCategoryDto(parentId));
    }

    @Data
    @AllArgsConstructor
    public static class SupplementParentCategoryDto {
        private Long id;
    }
}
