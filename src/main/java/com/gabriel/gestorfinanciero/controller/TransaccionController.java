package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.exception.ResourceNotFoundException;
import com.gabriel.gestorfinanciero.model.Transaccion;
import com.gabriel.gestorfinanciero.model.TipoTransaccion;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.TransaccionRepository;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;

    public TransaccionController(TransaccionRepository transaccionRepository, UsuarioRepository usuarioRepository) {
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public Transaccion crearTransaccion(@RequestBody @Valid Transaccion transaccion, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        transaccion.setUsuario(usuario);
        return transaccionRepository.save(transaccion);
    }

    @GetMapping("/busqueda")
    public List<Transaccion> buscarTransacciones(
            @RequestParam(required = false) Integer montoMin,
            @RequestParam(required = false) Integer montoMax,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) TipoTransaccion tipo,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            Authentication authentication) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        return transaccionRepository.findByUsuarioId(usuario.getId()).stream()
                .filter(t -> montoMin == null || t.getMonto() >= montoMin)
                .filter(t -> montoMax == null || t.getMonto() <= montoMax)
                .filter(t -> categoria == null || categoria.isBlank() || t.getCategoria().equalsIgnoreCase(categoria))
                .filter(t -> tipo == null || t.getTipo() == tipo)
                .filter(t -> fechaDesde == null || (t.getFecha() != null && !t.getFecha().isBefore(fechaDesde)))
                .filter(t -> fechaHasta == null || (t.getFecha() != null && !t.getFecha().isAfter(fechaHasta)))
                .collect(Collectors.toList());
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}