package mandykr.nutrient.dto.supplement.reply.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SupplementReplyRequest {
    @NotEmpty
    @Size(min = 1, max = 1000)
    private String content;
}
