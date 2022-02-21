package mandykr.nutrient.security;

import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private static final String ERROR403 =
            "{\"success\":false,\"response\":null,\"error\":{\"message\":\"Forbidden\",\"status\":403}}";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(SC_FORBIDDEN);
        response.setHeader("content-type", "application/json");
        response.getWriter().write(ERROR403);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
