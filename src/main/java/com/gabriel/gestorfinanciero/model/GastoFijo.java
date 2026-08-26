package com.gabriel.gestorfinanciero.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gastos_fijos")
public class GastoFijo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String concepto; // Ej: "Arriendo", "Internet", "Subscripción Streaming"
    private Double monto;

    @Enumerated(EnumType.STRING)
    private Frecuencia frecuencia;

    private Integer diaCobro; // Día del mes o de la semana en que se ejecuta el cobro
    private Boolean pagado = false; // Permite saber si ya se cubrió este mes/semana

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public GastoFijo() {}

    public GastoFijo(String concepto, Double monto, Frecuencia frecuencia, Integer diaCobro, Usuario usuario) {
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

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public Frecuencia getFrecuencia() { return frecuencia; }
    public void setFrecuencia(Frecuencia frecuencia) { this.frecuencia = frecuencia; }

    public Integer getDiaCobro() { return diaCobro; }
    public void setDiaCobro(Integer diaCobro) { this.diaCobro = diaCobro; }

    public Boolean getPagado() { return pagado; }
    public void setPagado(Boolean pagado) { this.pagado = pagado; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}