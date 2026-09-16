package com.gabriel.gestorfinanciero.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "fondo_mutuo_detalles")
public class FondoMutuoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del fondo es obligatorio")
    private String nombreFondo;

    @Min(value = 1, message = "La cantidad de cuotas debe ser mayor a 0")
    private double cantidadCuotas;

    @Min(value = 0, message = "El valor de compra no puede ser negativo")
    private double valorCuotaCompra;

    @Min(value = 0, message = "El valor actual no puede ser negativo")
    private double valorCuotaActual;

    private LocalDate fechaUltimoCalculo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inversion_id", nullable = false)
    private Inversion inversion;

    public FondoMutuoDetalle() {
        this.fechaUltimoCalculo = LocalDate.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreFondo() { return nombreFondo; }
    public void setNombreFondo(String nombreFondo) { this.nombreFondo = nombreFondo; }

    public double getCantidadCuotas() { return cantidadCuotas; }
    public void setCantidadCuotas(double cantidadCuotas) { this.cantidadCuotas = cantidadCuotas; }

    public double getValorCuotaCompra() { return valorCuotaCompra; }
    public void setValorCuotaCompra(double valorCuotaCompra) { this.valorCuotaCompra = valorCuotaCompra; }

    public double getValorCuotaActual() { return valorCuotaActual; }
    public void setValorCuotaActual(double valorCuotaActual) { this.valorCuotaActual = valorCuotaActual; }

    public LocalDate getFechaUltimoCalculo() { return fechaUltimoCalculo; }
    public void setFechaUltimoCalculo(LocalDate fechaUltimoCalculo) { this.fechaUltimoCalculo = fechaUltimoCalculo; }

    public Inversion getInversion() { return inversion; }
    public void setInversion(Inversion inversion) { this.inversion = inversion; }
}