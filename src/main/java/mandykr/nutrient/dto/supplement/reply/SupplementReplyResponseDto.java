package mandykr.nutrient.dto.supplement.reply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mandykr.nutrient.dto.supplement.reply.request.SupplementReplyRequest;
import mandykr.nutrient.entity.supplement.SupplementReply;
import static org.springframework.beans.BeanUtils.copyProperties;

@Getter
@Setter
@NoArgsConstructor
public class SupplementReplyResponseDto {
    private Long id;

    private String content;

    private Long groups;

    private Long groupOrder;

    private Boolean deleteFlag;

    public SupplementReplyResponseDto(SupplementReply source) {
        copyProperties(source, this);
    }


}