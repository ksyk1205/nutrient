package mandykr.nutrient.dto.combination.reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationReply;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinationReplyDto {

    private Long id;
    private String content;
    private long orders;
    private boolean deleteFlag;
    private Long combinationId;
    private Long parentId;

    public static CombinationReplyDto of(CombinationReply entity) {
        return CombinationReplyDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .orders(entity.getOrders())
                .deleteFlag(entity.isDeleteFlag())
                .combinationId(entity.getCombination().getId())
                .parentId(entity.getParent().getId())
                .build();
    }

    public static CombinationReply createReply(CombinationReplyDto dto) {
        return CombinationReply.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .orders(dto.getOrders())
                .deleteFlag(dto.isDeleteFlag())
                .combination(new Combination(dto.getCombinationId()))
                .parent(new CombinationReply(dto.getParentId()))
                .build();
    }
}
