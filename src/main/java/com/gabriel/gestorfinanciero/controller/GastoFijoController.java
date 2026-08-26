package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.model.GastoFijo;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.GastoFijoRepository;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gastos-fijos")
public class GastoFijoController {

    private final GastoFijoRepository gastoFijoRepository;
    private final UsuarioRepository usuarioRepository;

    public GastoFijoController(GastoFijoRepository gastoFijoRepository, UsuarioRepository usuarioRepository) {
        this.gastoFijoRepository = gastoFijoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener todos los gastos fijos del usuario
    @GetMapping
    public List<GastoFijo> obtenerMisGastosFijos(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoFijoRepository.findByUsuarioId(usuario.getId());
    }

    // Crear un nuevo gasto fijo
    @PostMapping
    public GastoFijo crearGastoFijo(@RequestBody GastoFijo gastoFijo, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        gastoFijo.setUsuario(usuario);
        return gastoFijoRepository.save(gastoFijo);
    }

    // Cambiar el estado de pago (marcar como pagado o pendiente)
    @PatchMapping("/{id}/toggle-pago")
    public GastoFijo cambiarEstadoPago(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        GastoFijo gastoFijo = gastoFijoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto fijo no encontrado"));

        if (!gastoFijo.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para modificar este recurso");
        }

        gastoFijo.setPagado(!gastoFijo.getPagado());
        return gastoFijoRepository.save(gastoFijo);
    }
}