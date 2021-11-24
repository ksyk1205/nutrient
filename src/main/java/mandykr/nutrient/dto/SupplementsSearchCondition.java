package mandykr.nutrient.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SupplementsSearchCondition {
    private Long id;
    private String name;

    public SupplementsSearchCondition(Long id) {
        this.id = id;
    }

    public SupplementsSearchCondition(String name) {
        this.name = name;
    }

    public SupplementsSearchCondition(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
