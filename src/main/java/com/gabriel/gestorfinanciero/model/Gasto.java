package com.gabriel.gestorfinanciero.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private int monto; // MONTO EN INT YA QUE ESTÁ PENNSADO PARA CLP

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    //Constructor
    public Gasto() {
    }

    public Gasto(int monto, String descripcion) {
        this.monto = monto;
        this.descripcion = descripcion;

    }

    //Getters y Setters
    public Long getId() {return id;}
    public int getMonto() {return monto;}

    public void setMonto(int monto) {this.monto = monto;}

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public Usuario getUsuario(){return usuario;}
    public void setUsuario(Usuario usuario){this.usuario = usuario;}
}
