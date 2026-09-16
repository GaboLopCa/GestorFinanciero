package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.exception.UnauthorizedException;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import com.gabriel.gestorfinanciero.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/registro")
    public String registrarUsuario(@RequestBody @Valid Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return "Usuario registrado exitosamente";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody @Valid AuthRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        return Map.of("token", jwtUtils.generarToken(usuario.getEmail()));
    }
}