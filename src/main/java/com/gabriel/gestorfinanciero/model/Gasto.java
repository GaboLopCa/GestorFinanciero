package com.gabriel.gestorfinanciero.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int monto; // MONTO EN INT YA QUE ESTÁ PENNSADO PARA CLP
    private String descripcion;



    //Constructor
    public Gasto(){}

    public Gasto(int monto, String descripcion) {
        this.monto = monto;
        this.descripcion = descripcion;
    }

    //Getters y Setters
    public Long getId(){
        return id;
    }

    public int getMonto(){
        return monto;
    }

    public void setMonto(int monto){
        this.monto = monto;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
}
