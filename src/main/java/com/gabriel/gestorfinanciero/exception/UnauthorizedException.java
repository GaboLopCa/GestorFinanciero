package com.gabriel.gestorfinanciero.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String mensaje) {
        super(mensaje);
    }
}
