package com.gabriel.gestorfinanciero.service.parser;

import com.gabriel.gestorfinanciero.model.TipoTransaccion;
import com.gabriel.gestorfinanciero.service.ParsedSms;
import com.gabriel.gestorfinanciero.service.SmsParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SantanderSmsParser implements SmsParser {

    private final Pattern patronCompra;
    private final Pattern patronTransferencia;
    private final Pattern patronDeposito;

    public SantanderSmsParser(
            @Value("${webhook.parsers.santander.compra-pattern:}") String patronCompraStr,
            @Value("${webhook.parsers.santander.transferencia-pattern:}") String patronTransferenciaStr,
            @Value("${webhook.parsers.santander.deposito-pattern:}") String patronDepositoStr
    ) {
        this.patronCompra = compileOrDefault(patronCompraStr,
                "(?:Tu\\s+cuenta\\s+\\S+\\s+)?(?:\\S+:\\s+)?(?:Compra|deb[ií]to)\\s+por\\s+\\$([\\d.]+)\\s+(?:en|de|con|@)\\s+(.+?)(?:[\\.:!]|$)");
        this.patronTransferencia = compileOrDefault(patronTransferenciaStr,
                "(?:Tu\\s+cuenta\\s+\\S+\\s+)?(?:\\S+:\\s+)?(?:Transferiste|Enviaste)\\s+\\$([\\d.]+)\\s+a\\s+(.+?)(?:[\\.:!]|$)");
        this.patronDeposito = compileOrDefault(patronDepositoStr,
                "(?:\\S+:\\s+)?(?:Recibiste|Abon(?:aste|o))\\s+\\$([\\d.]+)");
    }

    @Override
    public ParsedSms parse(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return null;
        }

        Matcher mCompra = patronCompra.matcher(mensaje);
        if (mCompra.find()) {
            return new ParsedSms(
                    parseMonto(mCompra.group(1)),
                    "Compra en " + mCompra.group(2).trim(),
                    "Compras",
                    TipoTransaccion.GASTO
            );
        }

        Matcher mTransferencia = patronTransferencia.matcher(mensaje);
        if (mTransferencia.find()) {
            return new ParsedSms(
                    parseMonto(mTransferencia.group(1)),
                    "Transferencia a " + mTransferencia.group(2).trim(),
                    "Transferencias",
                    TipoTransaccion.GASTO
            );
        }

        Matcher mDeposito = patronDeposito.matcher(mensaje);
        if (mDeposito.find()) {
            return new ParsedSms(
                    parseMonto(mDeposito.group(1)),
                    "Ingreso por transferencia",
                    "Ingresos",
                    TipoTransaccion.INGRESO
            );
        }

        return null;
    }

    private Pattern compileOrDefault(String patronStr, String fallbackRegex) {
        if (patronStr != null && !patronStr.isBlank()) {
            return Pattern.compile(patronStr, Pattern.CASE_INSENSITIVE);
        }
        return Pattern.compile(fallbackRegex, Pattern.CASE_INSENSITIVE);
    }

    private int parseMonto(String montoStr) {
        String limpio = montoStr.replace(".", "").replace(",", "");
        try {
            return Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
