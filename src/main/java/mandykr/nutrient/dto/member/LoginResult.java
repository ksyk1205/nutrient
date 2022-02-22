package mandykr.nutrient.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResult {
    private final String token;

    private final MemberResponse memberResponse;


}
