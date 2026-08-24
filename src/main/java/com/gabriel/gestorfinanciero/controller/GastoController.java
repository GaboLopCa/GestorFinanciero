package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.model.Gasto;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.GastoRepository;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
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

    // 1. Obtener solo los gastos pertenecientes al usuario del Token JWT
    @GetMapping
    public List<Gasto> obtenerMisGastos(Authentication authentication) {
        String email = authentication.getName(); // Extrae el email del JWT procesado por JwtFilter
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.findByUsuarioId(usuario.getId());
    }

    // 2. Crear un gasto y asignarlo automáticamente al usuario autenticado
    @PostMapping
    public Gasto crearGasto(@RequestBody Gasto gasto, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        gasto.setUsuario(usuario); // Asignación automática usando el token
        return gastoRepository.save(gasto);
    }
}