package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.model.Ingreso;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.IngresoRepository;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingresos")
public class IngresoController {

    private final IngresoRepository ingresoRepository;
    private final UsuarioRepository usuarioRepository;

    public IngresoController(IngresoRepository ingresoRepository, UsuarioRepository usuarioRepository) {
        this.ingresoRepository = ingresoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener los ingresos del usuario autenticado
    @GetMapping
    public List<Ingreso> obtenerMisIngresos(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ingresoRepository.findByUsuarioId(usuario.getId());
    }

    // Registrar un nuevo ingreso
    @PostMapping
    public Ingreso crearIngreso(@RequestBody Ingreso ingreso, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ingreso.setUsuario(usuario);
        return ingresoRepository.save(ingreso);
    }
}