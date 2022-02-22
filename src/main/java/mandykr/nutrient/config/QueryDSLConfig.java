package mandykr.nutrient.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import mandykr.nutrient.security.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class QueryDSLConfig {
    @PersistenceContext
    private EntityManager entityManager;
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){ //해당 어플리케이션이 기동되면서 해당 클래스 내부에 존재하는 빈을 등록시켜준다
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Jwt jwt(JwtTokenConfig jwtTokenConfig) {
        return new Jwt(jwtTokenConfig.getSecret(), jwtTokenConfig.getExpirySeconds());
    }
}
