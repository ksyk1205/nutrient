package mandykr.nutrient.dto.member;

import lombok.Data;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.security.Jwt;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;



@Data
public class MemberResponse {

    private Long id;
    private String memberId;
    private String name;
    private String password;
    private List<String> roles = new ArrayList<>();

    //Entity -> Dto
    public MemberResponse(Member member){
        BeanUtils.copyProperties(member, this);
    }

    //Entity -> Dto
    public MemberResponse(Member member, String password){
        BeanUtils.copyProperties(member, this);
        this.password = password;
    }

    public String newJwt(Jwt jwt, String[] roles) {
        Jwt.Claims claims = Jwt.Claims.of(memberId, password, roles);
        return jwt.create(claims);
    }
}
