package com.evtech.taskmanager.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component("customAuthFilter")
public class AuthFilter implements Filter {

    // Páginas que não precisam de autenticação
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login", "/register", "/css/", "/js/", "/images/", "/error"
    );

    // Páginas que precisam de permissão de administrador
    private static final List<String> ADMIN_PATHS = Arrays.asList(
            "/admin/"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI();

        // Verifica se o caminho é público
        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(path::startsWith);

        // Verifica se o caminho é de administrador
        boolean isAdminPath = ADMIN_PATHS.stream().anyMatch(path::startsWith);

        // Se o caminho for público, permite o acesso
        if (isPublicPath) {
            chain.doFilter(request, response);
            return;
        }

        // Verifica se o usuário está autenticado
        boolean isAuthenticated = (session != null && session.getAttribute("userId") != null);

        // Se não estiver autenticado, redireciona para o login
        if (!isAuthenticated) {
            httpResponse.sendRedirect("/login");
            return;
        }

        // Verifica se o caminho requer permissão de administrador
        if (isAdminPath) {
            String userRole = (String) session.getAttribute("userRole");

            // Se não for administrador, redireciona para a página de tarefas
            if (!"ROLE_ADMIN".equals(userRole)) {
                httpResponse.sendRedirect("/tasks");
                return;
            }
        }

        // Permite o acesso
        chain.doFilter(request, response);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
