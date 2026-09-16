package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.exception.BadRequestException;
import com.gabriel.gestorfinanciero.exception.ForbiddenException;
import com.gabriel.gestorfinanciero.exception.ResourceNotFoundException;
import com.gabriel.gestorfinanciero.model.FondoMutuoDetalle;
import com.gabriel.gestorfinanciero.model.Inversion;
import com.gabriel.gestorfinanciero.model.TipoInversion;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.FondoMutuoDetalleRepository;
import com.gabriel.gestorfinanciero.repository.GastoFijoRepository;
import com.gabriel.gestorfinanciero.repository.GastoRepository;
import com.gabriel.gestorfinanciero.repository.IngresoRepository;
import com.gabriel.gestorfinanciero.repository.InversionRepository;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/finanzas")
public class FinanzasController {

    private final IngresoRepository ingresoRepository;
    private final GastoFijoRepository gastoFijoRepository;
    private final GastoRepository gastoRepository;
    private final InversionRepository inversionRepository;
    private final FondoMutuoDetalleRepository fondoMutuoDetalleRepository;
    private final UsuarioRepository usuarioRepository;

    public FinanzasController(IngresoRepository ingresoRepository,
                              GastoFijoRepository gastoFijoRepository,
                              GastoRepository gastoRepository,
                              InversionRepository inversionRepository,
                              FondoMutuoDetalleRepository fondoMutuoDetalleRepository,
                              UsuarioRepository usuarioRepository) {
        this.ingresoRepository = ingresoRepository;
        this.gastoFijoRepository = gastoFijoRepository;
        this.gastoRepository = gastoRepository;
        this.inversionRepository = inversionRepository;
        this.fondoMutuoDetalleRepository = fondoMutuoDetalleRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/resumen-liquidez")
    public Map<String, Object> resumenLiquidez(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        int totalIngresos = ingresoRepository.sumMontosByUsuarioId(usuario.getId());

        int gastosFijosPendientes = gastoFijoRepository.sumMontosPendientesByUsuarioId(usuario.getId());

        int gastosVariablesEjecutados = gastoRepository.sumMontosByUsuarioId(usuario.getId());

        int dineroDisponible = totalIngresos - gastosFijosPendientes - gastosVariablesEjecutados;

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("totalIngresos", totalIngresos);
        respuesta.put("gastosFijosPendientes", gastosFijosPendientes);
        respuesta.put("gastosVariablesEjecutados", gastosVariablesEjecutados);
        respuesta.put("dineroDisponibleReal", dineroDisponible);
        return respuesta;
    }

    @PostMapping("/inversiones/dap")
    public Map<String, Object> crearDap(@RequestBody @Valid Inversion inversion, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        if (inversion.getTipo() != TipoInversion.DAP) {
            throw new BadRequestException("El tipo de inversión debe ser DAP");
        }
        if (inversion.getTasaInteres() == null) {
            throw new BadRequestException("La tasa de interés es obligatoria para un DAP");
        }
        if (inversion.getFechaVencimiento() == null) {
            throw new BadRequestException("La fecha de vencimiento es obligatoria para un DAP");
        }

        inversion.setUsuario(usuario);
        inversionRepository.save(inversion);

        long dias = ChronoUnit.DAYS.between(inversion.getFechaInicio(), inversion.getFechaVencimiento());
        double ganancia = inversion.getMontoInicial() * (inversion.getTasaInteres() / 100.0) * (dias / 365.0);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("inversion", inversion);
        respuesta.put("gananciaProyectada", Math.round(ganancia));
        return respuesta;
    }

    @PostMapping("/inversiones/fondo-mutuo")
    public Inversion crearFondoMutuo(@RequestBody @Valid Inversion inversion, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        if (inversion.getTipo() != TipoInversion.FONDO_MUTUO) {
            throw new BadRequestException("El tipo de inversión debe ser FONDO_MUTUO");
        }
        if (inversion.getFondoMutuoDetalles() == null || inversion.getFondoMutuoDetalles().isEmpty()) {
            throw new BadRequestException("Debe indicar al menos un detalle de fondo mutuo");
        }

        inversion.setUsuario(usuario);
        for (FondoMutuoDetalle detalle : inversion.getFondoMutuoDetalles()) {
            detalle.setInversion(inversion);
            if (detalle.getValorCuotaActual() == 0) {
                detalle.setValorCuotaActual(detalle.getValorCuotaCompra());
            }
            if (detalle.getFechaUltimoCalculo() == null) {
                detalle.setFechaUltimoCalculo(LocalDate.now());
            }
        }
        return inversionRepository.save(inversion);
    }

    @PatchMapping("/inversiones/fondo-mutuo/{id}/actualizar-cuota")
    public Map<String, Object> actualizarValorCuota(@PathVariable Long id,
                                                    @RequestBody Map<String, Object> body,
                                                    Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        FondoMutuoDetalle detalle = fondoMutuoDetalleRepository.findByIdConInversionYUsuario(id).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de fondo mutuo no encontrado"));

        if (!detalle.getInversion().getUsuario().getId().equals(usuario.getId())) {
            throw new ForbiddenException("No tienes permisos para modificar este recurso");
        }

        Object valor = body.get("valorCuotaActual");
        if (valor == null) {
            throw new BadRequestException("El campo valorCuotaActual es obligatorio");
        }
        double valorActual = Double.parseDouble(valor.toString());
        if (valorActual < 0) {
            throw new BadRequestException("El valor de la cuota no puede ser negativo");
        }

        detalle.setValorCuotaActual(valorActual);
        detalle.setFechaUltimoCalculo(LocalDate.now());
        fondoMutuoDetalleRepository.save(detalle);

        double ganancia = (detalle.getValorCuotaActual() - detalle.getValorCuotaCompra()) * detalle.getCantidadCuotas();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("detalle", detalle);
        respuesta.put("gananciaTotal", Math.round(ganancia));
        return respuesta;
    }

    @GetMapping("/inversiones/fondo-mutuo/rendimiento")
    public Map<String, Object> rendimientoFondoMutuo(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        List<Inversion> fondos = inversionRepository.findByUsuarioIdConDetalles(usuario.getId()).stream()
                .filter(i -> i.getTipo() == TipoInversion.FONDO_MUTUO)
                .collect(Collectors.toList());

        List<Map<String, Object>> historial = new ArrayList<>();
        double gananciaAcumulada = 0;
        double totalInvertido = 0;

        for (Inversion fondo : fondos) {
            for (FondoMutuoDetalle detalle : fondo.getFondoMutuoDetalles()) {
                double invertido = detalle.getValorCuotaCompra() * detalle.getCantidadCuotas();
                double valorActual = detalle.getValorCuotaActual() * detalle.getCantidadCuotas();
                double ganancia = valorActual - invertido;

                totalInvertido += invertido;
                gananciaAcumulada += ganancia;

                Map<String, Object> item = new LinkedHashMap<>();
                item.put("inversionId", fondo.getId());
                item.put("detalleId", detalle.getId());
                item.put("nombreFondo", detalle.getNombreFondo());
                item.put("cantidadCuotas", detalle.getCantidadCuotas());
                item.put("valorCuotaCompra", detalle.getValorCuotaCompra());
                item.put("valorCuotaActual", detalle.getValorCuotaActual());
                item.put("valorTotalInvertido", Math.round(invertido));
                item.put("valorTotalActual", Math.round(valorActual));
                item.put("ganancia", Math.round(ganancia));
                item.put("fechaUltimoCalculo", detalle.getFechaUltimoCalculo());
                historial.add(item);
            }
        }

        double rentabilidadAcumulada = totalInvertido == 0 ? 0.0 : (gananciaAcumulada / totalInvertido) * 100;

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("historial", historial);
        respuesta.put("totalInvertido", Math.round(totalInvertido));
        respuesta.put("gananciaAcumulada", Math.round(gananciaAcumulada));
        respuesta.put("rentabilidadAcumuladaPorcentual", Math.round(rentabilidadAcumulada * 100.0) / 100.0);
        return respuesta;
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}