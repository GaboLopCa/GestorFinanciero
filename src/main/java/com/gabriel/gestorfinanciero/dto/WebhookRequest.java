package com.gabriel.gestorfinanciero.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class WebhookRequest {

    @NotBlank(message = "El mensaje del SMS es obligatorio")
    private String mensaje;

    @NotBlank(message = "El banco es obligatorio")
    private String banco;

    private LocalDate fecha;

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
