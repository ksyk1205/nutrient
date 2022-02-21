package mandykr.nutrient.dto.member;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequest {
    @NotEmpty(message = "memberID must be provided")
    private String memberId;
    @NotEmpty(message = "password must be provided")
    private String password;
}
