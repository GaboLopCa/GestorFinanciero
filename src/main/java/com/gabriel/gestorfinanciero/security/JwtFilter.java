package com.gabriel.gestorfinanciero.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Buscamos la cabecera "Authorization" en la petición HTTP
        String authHeader = request.getHeader("Authorization");

        // 2. Verificamos si la cabecera existe y empieza con "Bearer" (formato estándar de tokens)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Extraemos el token quitando la palabra "Bearer"

            // 3. Validamos si el token es legítimo
            if (jwtUtils.validarToken(token)) {
                String email = jwtUtils.obtenerEmailDelToken(token);

                // 4. Creamos la credencial de autenticación para Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, new ArrayList<>()
                );

                // 5. Registramos al usuario como autenticado en el contexto actual de la petición
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Dejamos que la petición continúe su camino hacia el controlador
        filterChain.doFilter(request, response);
    }
}