package mandykr.nutrient.dto.combination.reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.combination.CombinationReply;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinationReplyUpdateFormDto {

    @Min(1)
    private Long id;

    @NotBlank
    private String content;

    public CombinationReply createReply() {
        return CombinationReply.builder()
                .id(id)
                .content(content)
                .build();
    }
}
