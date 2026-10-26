package com.tienda.app.filter;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.tienda.app.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (path.startsWith(contextPath)) path = path.substring(contextPath.length());
        if (path.startsWith("/auth/")) return true;
        if (!request.getMethod().equals("GET")) return false;
        if (path.equals("/products")) return true;
        return path.matches("/products/[0-9]+");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null) { unauthorized(response, "Falta el token"); return; }
        if (!header.startsWith("Bearer ")) { unauthorized(response, "El token debe usar Bearer"); return; }
        String token = header.substring(7);
        if (!jwtService.isTokenValid(token)) { unauthorized(response, "El token no es válido o expiró"); return; }
        request.setAttribute("username", jwtService.extractUsername(token));
        request.setAttribute("userId", jwtService.extractUserId(token));
        request.setAttribute("rolId", jwtService.extractRoleId(token));
        chain.doFilter(request, response);
    }
    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\",\"data\":null}");
    }
}