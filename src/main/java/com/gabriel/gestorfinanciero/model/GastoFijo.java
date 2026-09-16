package com.gabriel.gestorfinanciero.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "gastos_fijos")
public class GastoFijo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El concepto es obligatorio")
    private String concepto; // Ej: "Arriendo", "Internet", "Subscripción Streaming"

    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private int monto; // INT PARA MONTO EN $CLP

    @NotNull(message = "La frecuencia es obligatoria")
    @Enumerated(EnumType.STRING)
    private Frecuencia frecuencia;

    @Min(value = 1, message = "El día de cobro debe ser mayor a 0")
    @Max(value = 31, message = "El día de cobro debe ser menor o igual a 31")
    private Integer diaCobro; // Día del mes o de la semana en que se ejecuta el cobro
    private Boolean pagado = false; // Permite saber si ya se cubrió este mes/semana

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public GastoFijo() {}

    public GastoFijo(String concepto, int monto, Frecuencia frecuencia, Integer diaCobro, Usuario usuario) {
        this.concepto = concepto;
        this.monto = monto;
        this.frecuencia = frecuencia;
        this.diaCobro = diaCobro;
        this.usuario = usuario;
        this.pagado = false;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public int getMonto() { return monto; }
    public void setMonto(int monto) { this.monto = monto; }

    public Frecuencia getFrecuencia() { return frecuencia; }
    public void setFrecuencia(Frecuencia frecuencia) { this.frecuencia = frecuencia; }

    public Integer getDiaCobro() { return diaCobro; }
    public void setDiaCobro(Integer diaCobro) { this.diaCobro = diaCobro; }

    public Boolean getPagado() { return pagado; }
    public void setPagado(Boolean pagado) { this.pagado = pagado; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}