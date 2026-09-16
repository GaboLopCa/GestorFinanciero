package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.exception.ResourceNotFoundException;
import com.gabriel.gestorfinanciero.model.Gasto;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.GastoRepository;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gastos")
public class GastoController {

    private final GastoRepository gastoRepository;
    private final UsuarioRepository usuarioRepository;

    public GastoController(GastoRepository gastoRepository, UsuarioRepository usuarioRepository) {
        this.gastoRepository = gastoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Gasto> obtenerMisGastos(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return gastoRepository.findByUsuarioId(usuario.getId());
    }

    @PostMapping
    public Gasto crearGasto(@RequestBody @Valid Gasto gasto, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        gasto.setUsuario(usuario);
        return gastoRepository.save(gasto);
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}