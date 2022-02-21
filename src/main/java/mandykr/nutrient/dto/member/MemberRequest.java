package mandykr.nutrient.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.member.Role;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {
    @NotEmpty
    private String memberId;
    @NotEmpty
    private String password;
    @NotEmpty
    private String name;
    @NotEmpty
    private List<String> roles = new ArrayList<>();
}
