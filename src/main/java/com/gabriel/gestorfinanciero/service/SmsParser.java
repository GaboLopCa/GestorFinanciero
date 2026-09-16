package com.gabriel.gestorfinanciero.service;

public interface SmsParser {
    ParsedSms parse(String mensaje);
}
