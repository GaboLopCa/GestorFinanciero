package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.exception.ForbiddenException;
import com.gabriel.gestorfinanciero.exception.ResourceNotFoundException;
import com.gabriel.gestorfinanciero.model.GastoFijo;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.GastoFijoRepository;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import jakarta.validation.Valid;
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

    @GetMapping
    public List<GastoFijo> obtenerMisGastosFijos(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return gastoFijoRepository.findByUsuarioId(usuario.getId());
    }

    @PostMapping
    public GastoFijo crearGastoFijo(@RequestBody @Valid GastoFijo gastoFijo, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        gastoFijo.setUsuario(usuario);
        return gastoFijoRepository.save(gastoFijo);
    }

    @PatchMapping("/{id}/toggle-pago")
    public GastoFijo cambiarEstadoPago(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        GastoFijo gastoFijo = gastoFijoRepository.findByIdConUsuario(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto fijo no encontrado"));

        if (!gastoFijo.getUsuario().getId().equals(usuario.getId())) {
            throw new ForbiddenException("No tienes permisos para modificar este recurso");
        }

        gastoFijo.setPagado(!gastoFijo.getPagado());
        return gastoFijoRepository.save(gastoFijo);
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}