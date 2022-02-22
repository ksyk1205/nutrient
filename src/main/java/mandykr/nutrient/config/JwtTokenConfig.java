package mandykr.nutrient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtTokenConfig {
    //private String issuer;
    private String secret;
    private int expirySeconds;
}
