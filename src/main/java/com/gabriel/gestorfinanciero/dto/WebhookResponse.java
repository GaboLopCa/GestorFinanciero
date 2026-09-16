package com.gabriel.gestorfinanciero.dto;

import com.gabriel.gestorfinanciero.model.Transaccion;
import com.gabriel.gestorfinanciero.service.ParsedSms;

public class WebhookResponse {

    private Transaccion transaccion;
    private ParsedSms parsed;

    public WebhookResponse(Transaccion transaccion, ParsedSms parsed) {
        this.transaccion = transaccion;
        this.parsed = parsed;
    }

    public Transaccion getTransaccion() { return transaccion; }
    public ParsedSms getParsed() { return parsed; }
}
