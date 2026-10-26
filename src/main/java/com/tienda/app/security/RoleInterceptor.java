package com.tienda.app.security;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.tienda.app.enums.RoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class RoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) return true;
        HandlerMethod method = (HandlerMethod) handler;
        RequiresRole annotation = method.getMethodAnnotation(RequiresRole.class);
        if (annotation == null) return true;
        Object roleValue = request.getAttribute("rolId");
        if (!(roleValue instanceof Integer)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Usuario no autenticado\",\"data\":null}");
            return false;
        }
        Integer roleId = (Integer) roleValue;
        for (RoleEnum role : annotation.value()) {
            if (role.getId().equals(roleId)) return true;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"No tienes permiso para esta acción\",\"data\":null}");
        return false;
    }
}