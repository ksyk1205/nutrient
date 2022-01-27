package mandykr.nutrient.dto.supplement.reply;

import lombok.Getter;
import lombok.Setter;
import mandykr.nutrient.entity.supplement.SupplementReply;

import static org.springframework.beans.BeanUtils.copyProperties;

@Getter
@Setter
public class SupplementReplyDto {
    private Long id;

    private String content;

    private Long groups;

    private Long groupOrder;

    private Boolean deleted;

    private Long parent;

    private Long supplement;

    public SupplementReplyDto(SupplementReply source) {
        copyProperties(source, this);
        this.parent = source.getParent() == null ? null : source.getParent().getId();
        this.supplement = source.getSupplement().getId(); //영양제는 반드시 있다.
    }



}