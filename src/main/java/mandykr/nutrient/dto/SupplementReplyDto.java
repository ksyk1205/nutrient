package mandykr.nutrient.dto;

import lombok.Getter;
import lombok.Setter;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;

@Getter
@Setter
public class SupplementReplyDto {
    private Long id;

    private String content;

    private Long groupId;

    private Long orders;

    private Supplement supplement;

    public SupplementReplyDto(SupplementReply supplementReply) {
        this.id = supplementReply.getId();
        this.content = supplementReply.getContent();
        this.groupId = supplementReply.getGroupId();
        this.orders = supplementReply.getOrders();
        this.supplement = supplementReply.getSupplement();
    }

    public SupplementReplyDto() {

    }

    public SupplementReply makeSupplementReply(Long id,SupplementReplyDto supplementReplyDto){
        SupplementReply supplementReply = SupplementReply.makeSupplementReply(id,supplementReplyDto.content
                , supplementReplyDto.supplement);
        return supplementReply;
    }
}