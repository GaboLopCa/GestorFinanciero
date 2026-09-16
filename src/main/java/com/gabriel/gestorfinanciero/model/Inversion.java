package com.gabriel.gestorfinanciero.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inversiones")
public class Inversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoInversion tipo; // DAP / FONDO_MUTUO

    @Min(value = 1, message = "El monto inicial debe ser mayor a 0")
    private int montoInicial; // INT PARA MONTO EN $CLP

    private Double tasaInteres; // Solo para DAP (porcentaje anual, ej: 5.5)

    @NotNull(message = "La fecha de inicio es obligatoria")
    private java.time.LocalDate fechaInicio;

    private java.time.LocalDate fechaVencimiento; // Solo para DAP

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "inversion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FondoMutuoDetalle> fondoMutuoDetalles = new ArrayList<>();

    public Inversion() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoInversion getTipo() { return tipo; }
    public void setTipo(TipoInversion tipo) { this.tipo = tipo; }

    public int getMontoInicial() { return montoInicial; }
    public void setMontoInicial(int montoInicial) { this.montoInicial = montoInicial; }

    public Double getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(Double tasaInteres) { this.tasaInteres = tasaInteres; }

    public java.time.LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(java.time.LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public java.time.LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(java.time.LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<FondoMutuoDetalle> getFondoMutuoDetalles() { return fondoMutuoDetalles; }
    public void setFondoMutuoDetalles(List<FondoMutuoDetalle> fondoMutuoDetalles) { this.fondoMutuoDetalles = fondoMutuoDetalles; }
}