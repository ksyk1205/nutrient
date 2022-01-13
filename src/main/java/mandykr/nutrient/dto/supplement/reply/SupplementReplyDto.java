package mandykr.nutrient.dto.supplement.reply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mandykr.nutrient.entity.supplement.SupplementReply;
import static org.springframework.beans.BeanUtils.copyProperties;

@Getter
@Setter
@NoArgsConstructor
public class SupplementReplyDto {
    private Long id;

    private String content;

    private Long groups;

    private Long groupOrder;

    private Boolean deleteFlag;

    public SupplementReplyDto(SupplementReply source) {
        copyProperties(source, this);
    }

    public SupplementReplyDto(String content) {
        this.content = content;
    }

    public static SupplementReplyDto toSupplementReplyDto(String content) {
        return new SupplementReplyDto(content);
    }
}