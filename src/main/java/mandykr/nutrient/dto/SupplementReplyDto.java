package mandykr.nutrient.dto;

import lombok.Getter;
import lombok.Setter;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import static org.springframework.beans.BeanUtils.copyProperties;

@Getter
@Setter
public class SupplementReplyDto {
    private Long id;

    private String content;

    private Integer orders;

    public SupplementReplyDto(SupplementReply source) {
        copyProperties(source, this);
    }
}