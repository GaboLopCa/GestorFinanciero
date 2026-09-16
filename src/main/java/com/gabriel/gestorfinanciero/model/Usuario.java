package com.gabriel.gestorfinanciero.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    //ATRIBUTOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gasto> gastos = new ArrayList<>();

    //CONSTRUCTOR
    public Usuario(){}

    //GETTERS Y SETTERS
    public Long getId() { return id; }

    public void setNombre(String nombre){this.nombre = nombre;}
    public String getNombre(){return nombre;}

    public void setEmail(String email){this.email = email;}
    public String getEmail(){return email;}

    public void setPassword(String password){this.password = password;}
    public String getPassword(){return password;}

    public void setGastos(List<Gasto> gastos){this.gastos = gastos;}
    public List<Gasto> getGastos(){return gastos;}
}