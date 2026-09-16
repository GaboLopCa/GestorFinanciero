package com.gabriel.gestorfinanciero.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
@Table(name = "ingresos")
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private int monto; //INT  PARA MONTO EN $CLP

    @NotBlank(message = "La fuente es obligatoria")
    private String fuente;
    private LocalDate fecha;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Ingreso(){
        this.fecha = LocalDate.now();
    }

    public Ingreso(String descripcion, int monto, String fuente, Usuario usuario) {
        this.descripcion = descripcion;
        this.monto = monto;
        this.fuente = fuente;
        this.usuario = usuario;
        this.fecha = LocalDate.now();
    }


    //Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getMonto() {
        return monto;
    }
    public void setMonto(int monto) {
        this.monto = monto;
    }

    public String getFuente() {
        return fuente;
    }
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


}
