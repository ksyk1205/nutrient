package mandykr.nutrient.dto.supplement.reply;

import lombok.Data;
import mandykr.nutrient.dto.supplement.reply.request.SupplementReplyRequest;

import static org.springframework.beans.BeanUtils.copyProperties;


@Data
public class SupplementReplyRequestDto {
    private String content;

    public SupplementReplyRequestDto(SupplementReplyRequest source) {
        copyProperties(source, this);
    }
}
