package mandykr.nutrient.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class SupplementCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

    private int level;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private SupplementCategory parentCategory;

    public SupplementCategory(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public SupplementCategory(String name, int level, SupplementCategory parentCategory) {
        this.name = name;
        this.level = level;
        this.parentCategory = parentCategory;
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
