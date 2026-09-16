package com.gabriel.gestorfinanciero.service.parser;

import com.gabriel.gestorfinanciero.model.TipoTransaccion;
import com.gabriel.gestorfinanciero.service.ParsedSms;
import com.gabriel.gestorfinanciero.service.SmsParser;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GenericSmsParser implements SmsParser {

    private static final String MONTO_REGEX = "\\$([\\d.]+)";

    // Compra: "Compra por $25.000 en LIDER" o "Compra en LIDER por $25.000"
    private static final Pattern PATRON_COMPRA = Pattern.compile(
            "Compra\\s+por\\s+" + MONTO_REGEX + "\\s+(?:en|de|con|@)\\s+(.+?)(?:[\\.:!]|$)",
            Pattern.CASE_INSENSITIVE
    );

    // Transferencia saliente: "Transferiste $15.000 a Juan Perez"
    private static final Pattern PATRON_TRANSFERENCIA_SALIENTE = Pattern.compile(
            "(?:Transferiste|Enviaste|Pagaste)\\s+" + MONTO_REGEX + "\\s+a\\s+(.+?)(?:[\\.:!]|$)",
            Pattern.CASE_INSENSITIVE
    );

    // Transferencia entrante / depósito / abono: "Recibiste $800000 de Sueldo" o "Abonaste $15000" o "Depósito de $100000"
    private static final Pattern PATRON_TRANSFERENCIA_ENTRANTE = Pattern.compile(
            "(?:Recibiste|Abon(?:aste|o)|Dep[oó]sito\\s+de|Cr[eé]dito\\s+de)\\s+" + MONTO_REGEX,
            Pattern.CASE_INSENSITIVE
    );

    // Débito: "Débito de $12000"
    private static final Pattern PATRON_DEBITO = Pattern.compile(
            "(?:D[eé]bito|Carg(?:o|o\\s+a\\s+tu\\s+cuenta))\\s+(?:de\\s+)?" + MONTO_REGEX,
            Pattern.CASE_INSENSITIVE
    );

    // Monto simple como fallback
    private static final Pattern PATRON_MONTO_SIMPLE = Pattern.compile(
            MONTO_REGEX
    );

    @Override
    public ParsedSms parse(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return null;
        }

        // Intentar compra
        Matcher mCompra = PATRON_COMPRA.matcher(mensaje);
        if (mCompra.find()) {
            return new ParsedSms(
                    parseMonto(mCompra.group(1)),
                    "Compra en " + mCompra.group(2).trim(),
                    "Compras",
                    TipoTransaccion.GASTO
            );
        }

        // Intentar transferencia saliente
        Matcher mSaliente = PATRON_TRANSFERENCIA_SALIENTE.matcher(mensaje);
        if (mSaliente.find()) {
            return new ParsedSms(
                    parseMonto(mSaliente.group(1)),
                    "Transferencia a " + mSaliente.group(2).trim(),
                    "Transferencias",
                    TipoTransaccion.GASTO
            );
        }

        // Intentar transferencia entrante / depósito
        Matcher mEntrante = PATRON_TRANSFERENCIA_ENTRANTE.matcher(mensaje);
        if (mEntrante.find()) {
            return new ParsedSms(
                    parseMonto(mEntrante.group(1)),
                    "Ingreso por transferencia",
                    "Ingresos",
                    TipoTransaccion.INGRESO
            );
        }

        // Intentar débito
        Matcher mDebito = PATRON_DEBITO.matcher(mensaje);
        if (mDebito.find()) {
            return new ParsedSms(
                    parseMonto(mDebito.group(1)),
                    "Débito automático",
                    "Gastos Fijos",
                    TipoTransaccion.GASTO
            );
        }

        // Fallback: extraer monto simple y marcar como gasto
        Matcher mSimple = PATRON_MONTO_SIMPLE.matcher(mensaje);
        if (mSimple.find()) {
            return new ParsedSms(
                    parseMonto(mSimple.group(1)),
                    truncar(mensaje, 100),
                    "Sin categoría",
                    TipoTransaccion.GASTO
            );
        }

        return null;
    }

    // Parsea "$25.000" o "$2.500.000" → 25000
    protected int parseMonto(String montoStr) {
        String limpio = montoStr.replace(".", "").replace(",", "");
        try {
            return Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String truncar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max) + "...";
    }
}
