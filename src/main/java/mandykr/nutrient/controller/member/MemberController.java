package mandykr.nutrient.controller.member;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.member.LoginRequest;
import mandykr.nutrient.dto.member.LoginResult;
import mandykr.nutrient.dto.member.MemberRequest;
import mandykr.nutrient.dto.member.MemberResponse;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.errors.NotFoundException;
import mandykr.nutrient.errors.UnauthorizedException;
import mandykr.nutrient.security.Jwt;
import mandykr.nutrient.security.JwtAuthentication;
import mandykr.nutrient.security.JwtAuthenticationToken;
import mandykr.nutrient.service.member.MemberService;
import mandykr.nutrient.util.ApiUtils;
import mandykr.nutrient.util.ApiUtils.ApiResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final Jwt jwt;
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;

    @PostMapping("/join")
    public void createMember(
            @Valid @RequestBody MemberRequest memberRequest
        ){
        memberService.createMember(memberRequest);
    }


    @PostMapping(path = "/login")
    public ApiResult<LoginResult> login(
            @Valid @RequestBody LoginRequest request
    ) throws UnauthorizedException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new JwtAuthenticationToken(request.getMemberId(), request.getPassword())
            );
            Object object = authentication.getDetails();
            MemberResponse memberResponse = (MemberResponse) object;
            final String token = memberResponse.newJwt(
                    jwt,
                    authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toArray(String[]::new)
            );
            return success(new LoginResult(token, memberResponse));
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(e.getMessage(), e);
        }
    }

    @GetMapping(path = "/me")
    public ApiResult<MemberResponse> me(
            // JwtAuthenticationTokenFilter 에서 JWT 값을 통해 사용자를 인증한다.
            // 사용자 인증이 정상으로 완료됐다면 @AuthenticationPrincipal 어노테이션을 사용하여 인증된 사용자 정보(JwtAuthentication)에 접근할 수 있다.
            @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        return success(
                memberService.getByMemberIdAndPassword(authentication.memberId, authentication.password)
        );
    }
}
