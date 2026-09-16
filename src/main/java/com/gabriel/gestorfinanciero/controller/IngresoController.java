package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.exception.ResourceNotFoundException;
import com.gabriel.gestorfinanciero.model.Ingreso;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.IngresoRepository;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import jakarta.validation.Valid;
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

    @GetMapping
    public List<Ingreso> obtenerMisIngresos(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ingresoRepository.findByUsuarioId(usuario.getId());
    }

    @PostMapping
    public Ingreso crearIngreso(@RequestBody @Valid Ingreso ingreso, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        ingreso.setUsuario(usuario);
        return ingresoRepository.save(ingreso);
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}