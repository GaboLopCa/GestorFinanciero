package com.gabriel.gestorfinanciero.service;

import com.gabriel.gestorfinanciero.config.WebhookConfigProperties;
import com.gabriel.gestorfinanciero.exception.UnauthorizedException;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import com.gabriel.gestorfinanciero.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class WebhookAuthService {

    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final WebhookConfigProperties config;

    public WebhookAuthService(JwtUtils jwtUtils,
                              UsuarioRepository usuarioRepository,
                              WebhookConfigProperties config) {
        this.jwtUtils = jwtUtils;
        this.usuarioRepository = usuarioRepository;
        this.config = config;
    }

    public Usuario resolverUsuario(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtils.validarToken(token)) {
                String email = jwtUtils.obtenerEmailDelToken(token);
                return usuarioRepository.findByEmail(email)
                        .orElseThrow(() -> new UnauthorizedException("Usuario del token no encontrado"));
            }
        }

        String apiKey = request.getHeader("X-Webhook-Token");
        if (apiKey != null && !apiKey.isBlank() && apiKey.equals(config.getSecret())) {
            return usuarioRepository.findByEmail(config.getDefaultEmail())
                    .orElseThrow(() -> new UnauthorizedException("Usuario por defecto del webhook no encontrado"));
        }

        throw new UnauthorizedException("Autenticación inválida: se requiere Bearer token o X-Webhook-Token válido");
    }
}
