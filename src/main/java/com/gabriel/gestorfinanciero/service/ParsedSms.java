package com.gabriel.gestorfinanciero.service;

import com.gabriel.gestorfinanciero.model.TipoTransaccion;

public class ParsedSms {

    private final int monto;
    private final String descripcion;
    private final String categoria;
    private final TipoTransaccion tipo;

    public ParsedSms(int monto, String descripcion, String categoria, TipoTransaccion tipo) {
        this.monto = monto;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.tipo = tipo;
    }

    public int getMonto() { return monto; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public TipoTransaccion getTipo() { return tipo; }
}
