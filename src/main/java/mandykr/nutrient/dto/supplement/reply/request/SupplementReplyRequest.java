package mandykr.nutrient.dto.supplement.reply.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplementReplyRequest {
    @NotEmpty
    @Size(min = 1, max = 50)
    private String content;
}
