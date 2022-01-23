package mandykr.nutrient.dto.supplement;

import lombok.Data;
import mandykr.nutrient.dto.supplement.reply.request.SupplementReplyRequest;

import static org.springframework.beans.BeanUtils.copyProperties;

@Data
public class SupplementRequestDto {
    private String name;
    private String prdlstReportNo;

    public SupplementRequestDto(SupplementRequest source) {
        copyProperties(source, this);
    }
}
