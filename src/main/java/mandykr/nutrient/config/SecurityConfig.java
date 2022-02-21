package mandykr.nutrient.config;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.security.*;
import mandykr.nutrient.service.member.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final Jwt jwt;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtUnauthorizedHandler jwtUnauthorizedHandler;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberService memberService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().disable()
                //.and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //로그인, 회원가입
                .antMatchers("/api/member/login","/api/member/join").permitAll()
                //조회
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                //관리자 권한 화면
                .antMatchers("/api/supplement/**").hasRole("ADMIN")
                .antMatchers("/api/categories/**").hasRole("ADMIN")
                .anyRequest().authenticated()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtUnauthorizedHandler)  //handler 수정
                .accessDeniedHandler(jwtAccessDeniedHandler);

        http
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() throws Exception {
        JwtAuthenticationTokenFilter authenticationFilter = new JwtAuthenticationTokenFilter(jwt);
        return authenticationFilter;
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(MemberService memberService) {
        return new JwtAuthenticationProvider(memberService);
    }
}

