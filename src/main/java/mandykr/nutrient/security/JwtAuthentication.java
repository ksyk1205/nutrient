package mandykr.nutrient.security;


import mandykr.nutrient.errors.UnauthorizedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class JwtAuthentication extends User {

    public final String memberId;

    public final String password;

    private List<GrantedAuthority> authenticates;

    JwtAuthentication(String memberId, String password, List<GrantedAuthority> authenticates) {
        super(memberId,password, authenticates);
        if(memberId.isEmpty()){
            throw new UnauthorizedException("memberId must be provided");
        }else if(password.isEmpty()){
            throw new UnauthorizedException("password must be provided");
        }else if(authenticates.size() <= 0){
            throw new UnauthorizedException("authenticates size not zero");
        }
        this.authenticates = authenticates;
        this.memberId = memberId;
        this.password = password;
    }


}
