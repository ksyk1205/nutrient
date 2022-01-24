package mandykr.nutrient.dto.combination;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CombinationCreateDto {

    String caption;

    List<Long> supplementIds = new ArrayList<>();


    public CombinationCreateDto(String caption, List<Long> supplementIds) {
        this.caption = caption;
        this.supplementIds = supplementIds;
    }
}
