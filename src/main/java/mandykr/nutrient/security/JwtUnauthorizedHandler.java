package mandykr.nutrient.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Component
public class JwtUnauthorizedHandler implements AuthenticationEntryPoint {

    private static final String ERROR401 = "{\"success\":false,\"response\":null,\"error\":{\"message\":\"Unauthorized\",\"status\":401}}";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(SC_UNAUTHORIZED);
        response.setHeader("content-type", "application/json");
        response.getWriter().write(ERROR401);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
