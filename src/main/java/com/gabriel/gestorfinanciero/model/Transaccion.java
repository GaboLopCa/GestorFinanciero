package com.gabriel.gestorfinanciero.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private int monto; // INT PARA MONTO EN $CLP

    private LocalDate fecha;

    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipo; // GASTO / INGRESO

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria; // Ej: "Supermercado", "Sueldo", "Inversión", "Venta"

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Transaccion() {
        this.fecha = LocalDate.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getMonto() { return monto; }
    public void setMonto(int monto) { this.monto = monto; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public TipoTransaccion getTipo() { return tipo; }
    public void setTipo(TipoTransaccion tipo) { this.tipo = tipo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}