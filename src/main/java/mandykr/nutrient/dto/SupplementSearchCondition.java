package mandykr.nutrient.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SupplementSearchCondition {
    private Long id;
    private String name;

    public SupplementSearchCondition(Long id) {
        this.id = id;
    }

    public SupplementSearchCondition(String name) {
        this.name = name;
    }

    public SupplementSearchCondition(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
