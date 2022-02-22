package mandykr.nutrient.security;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.member.MemberResponse;
import mandykr.nutrient.errors.NotFoundException;
import mandykr.nutrient.service.member.MemberService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ClassUtils.isAssignable;


@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final MemberService memberService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        return processUserAuthentication(
                String.valueOf(authenticationToken.getPrincipal()),
                String.valueOf(authenticationToken.getCredentials())
        );
    }

    private Authentication processUserAuthentication(String memberId, String password) {
        try {
            MemberResponse member = memberService.getByMemberIdAndPassword(memberId, password);
            List<GrantedAuthority> authorities = obtainAuthorities(member.getRoles());
            JwtAuthenticationToken authenticated =
                    new JwtAuthenticationToken(
                            new JwtAuthentication( member.getMemberId(), member.getPassword(), authorities),
                            null,
                            authorities
                    );
            authenticated.setDetails(member);
            return authenticated;
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (DataAccessException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    private List<GrantedAuthority> obtainAuthorities(List<String> role) {
        String[] roles = role.toArray(new String[role.size()]);
        return roles == null || roles.length == 0 ?
                Collections.emptyList() :
                Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(toList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return isAssignable(JwtAuthenticationToken.class, authentication);
    }

}
