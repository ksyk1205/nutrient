package mandykr.nutrient.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import mandykr.nutrient.dto.member.MemberResponse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Date;

public final class Jwt {

    private final String secret;

    private int expirySeconds;

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier;

    public Jwt( String secret, int expirySeconds) {
        this.secret = secret;
        this.expirySeconds = expirySeconds;
        this.algorithm = Algorithm.HMAC512(secret);
        this.jwtVerifier = JWT.require(algorithm)
                .build();
    }

    public String create(Claims claims) {
        Date now = new Date();
        JWTCreator.Builder builder = JWT.create();
        builder.withIssuedAt(now);
        if (expirySeconds > 0) {
            builder.withExpiresAt(new Date(now.getTime() + expirySeconds * 1_000L));
        }
        builder.withClaim("memberId", claims.memberId);
        builder.withClaim("password", claims.password);
        builder.withArrayClaim("roles", claims.roles);
        return builder.sign(algorithm);
    }

    public Claims verify(String token) throws JWTVerificationException {
        return new Claims(jwtVerifier.verify(token));
    }

    static public class Claims {
        String memberId;
        String password;
        String[] roles;
        Date createdAt;
        Date expiresAt;

        private Claims() {/*empty*/}

        Claims(DecodedJWT decodedJWT) {
            Claim memberId = decodedJWT.getClaim("memberId");
            if (!memberId.isNull()) {
                this.memberId = memberId.asString();
            }
            Claim password = decodedJWT.getClaim("password");
            if (!password.isNull()) {
                this.password = password.asString();
            }
            Claim roles = decodedJWT.getClaim("roles");
            if (!roles.isNull()) {
                this.roles = roles.asArray(String.class);
            }
            this.createdAt = decodedJWT.getIssuedAt();
            this.expiresAt = decodedJWT.getExpiresAt();
        }

        public static Claims of(String memberId, String password, String[] roles) {
            Claims claims = new Claims();
            claims.password = password;
            claims.memberId = memberId;
            claims.roles = roles;
            return claims;
        }

        long iat() {
            return createdAt != null ? createdAt.getTime() : -1;
        }

        long exp() {
            return expiresAt != null ? expiresAt.getTime() : -1;
        }

        void eraseIat() {
            createdAt = null;
        }

        void eraseExp() {
            expiresAt = null;
        }

    }
}
