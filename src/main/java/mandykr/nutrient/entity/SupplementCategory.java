package mandykr.nutrient.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mandykr.nutrient.dto.SupplementCategoryDto;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@NoArgsConstructor
public class SupplementCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @NotEmpty
    private String name;

    private int level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private SupplementCategory parentCategory;

    @Builder
    private SupplementCategory(Long id, String name, int level, SupplementCategory parentCategory) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.parentCategory = parentCategory;
    }

    public static SupplementCategory toEntity(Long id, String name, int level) {
        return SupplementCategory.builder().id(id).name(name).level(level).build();
    }

    public static SupplementCategory toEntity(Long id, String name, int level, SupplementCategory parentCategory) {
        return new SupplementCategory(id, name, level, parentCategory);
    }

    public void rename(String name) {
        this.name = name;
    }

    public void moveToAnotherParent(SupplementCategory parentCategory) {
        this.changeLevel(parentCategory.getLevel() + 1);
        this.parentCategory = parentCategory;
    }

    public void changeLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("변경할 레벨은 0 이상의 정수이어야 합니다.");
        }
        this.level = level;
    }
}
