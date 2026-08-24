package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import com.gabriel.gestorfinanciero.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    // 1. Registro (Encripta la clave antes de guardar)
    @PostMapping("/registro")
    public String registrarUsuario(@RequestBody Usuario usuario) {
        // Encriptamos la contraseña en texto plano a formato BCrypt hash
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return "Usuario registrado exitosamente";
    }

    // 2. LOGIN (Valida la clave y genera el JWT)
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        // Buscamos el usuario por su email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // Comparamos la contraseña enviada con el hash guardado en PostgreSQL
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Si todo coincide, generamos y retornamos el token JWT
        return jwtUtils.generarToken(usuario.getEmail());
    }
}