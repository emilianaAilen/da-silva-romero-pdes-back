package pdes.unq.com.APC.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pdes.unq.com.APC.services.AuthService;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    public JwtAuthenticationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            try {
                token = token.substring(7);
                var userDetails = authService.validateToken(token);

                String roleType = userDetails.getRoleType(); // Ajusta según tu implementación

                // Verifica la URL y aplica restricciones basadas en el roleType
                String requestURI = request.getRequestURI();
                if (isAccessDenied(roleType, requestURI)) {
                    System.out.println("Access Denied");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Access Denied");
                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
    * Verifica si el acceso está denegado según el roleType y la URL.
    */
    private boolean isAccessDenied(String roleType, String requestURI) {
        if (roleType.equals("admin")) {
            return false; // Admin puede acceder a todo
        }
        if (roleType.equals("common")) {
            return requestURI.contains("admin"); // Bloquea rutas de admin
        }
        return true; 
    }
}