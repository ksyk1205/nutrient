package mandykr.nutrient.dto.combination.reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationReply;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ReplyParentIdConstraint
public class CombinationReplyCreateFormDto extends CombinationReplyFormDto {

    @NotBlank
    private String content;

    @Min(1)
    @Max(2)
    private int orders;
    private boolean deleteFlag;

    @Positive
    private Long combinationId;
    private Long parentId;

    public CombinationReply createReply() {
        return CombinationReply.builder()
                .content(content)
                .orders(orders)
                .deleteFlag(true)
                .combination(new Combination(combinationId))
                .parent(new CombinationReply(parentId))
                .build();
    }
}
