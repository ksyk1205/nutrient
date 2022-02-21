package mandykr.nutrient;

import com.querydsl.jpa.impl.JPAQueryFactory;
import mandykr.nutrient.config.JwtTokenConfig;
import mandykr.nutrient.security.Jwt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class NutrientApplication {

	public static void main(String[] args) {
		SpringApplication.run(NutrientApplication.class, args);
	}

}
