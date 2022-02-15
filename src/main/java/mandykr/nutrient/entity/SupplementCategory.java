package mandykr.nutrient.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class SupplementCategory {
    public static final int MIN_DEPTH = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;

    private String name;
    private int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CATEGORY_ID")
    private SupplementCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<SupplementCategory> childCategories = new ArrayList<>();

    @Builder
    private SupplementCategory(Long id, String name, int depth, SupplementCategory parentCategory) {
        this.id = id;
        this.name = name;
        this.depth = depth;
        this.parentCategory = parentCategory;
    }

    public static SupplementCategory toEntity(Long id, String name, int depth) {
        return SupplementCategory.builder().id(id).name(name).depth(depth).build();
    }

    public static SupplementCategory toEntity(Long id, String name, int depth, SupplementCategory parentCategory) {
        return new SupplementCategory(id, name, depth, parentCategory);
    }

    public void changeParent(SupplementCategory parentCategory) {
        if (parentCategory == null) {
            return;
        }
        if (this.parentCategory != null &&
                this.parentCategory.getId() == parentCategory.getId()) {
            return;
        }
        this.changeDepth(parentCategory.getDepth() + 1);
        this.parentCategory = parentCategory;
    }

    private void changeDepth(int depth) {
        if (depth < MIN_DEPTH) {
            throw new IllegalArgumentException("변경할 레벨은 0 이상의 정수이어야 합니다.");
        }
        if (this.depth == depth) {
            return;
        }
        this.depth = depth;

        if (childCategories.isEmpty()) {
            return;
        }
        this.childCategories.forEach(c -> c.changeDepth(depth + 1));
    }

    public void rename(String name) {
        this.name = name;
    }

}
