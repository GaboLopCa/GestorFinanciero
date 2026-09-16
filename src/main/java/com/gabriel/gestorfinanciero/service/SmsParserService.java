package com.gabriel.gestorfinanciero.service;

import com.gabriel.gestorfinanciero.service.parser.BciSmsParser;
import com.gabriel.gestorfinanciero.service.parser.GenericSmsParser;
import com.gabriel.gestorfinanciero.service.parser.SantanderSmsParser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsParserService {

    private final GenericSmsParser genericParser;
    private final Map<String, SmsParser> parsersBanco;

    public SmsParserService(GenericSmsParser genericParser,
                            SantanderSmsParser santanderParser,
                            BciSmsParser bciParser) {
        this.genericParser = genericParser;
        this.parsersBanco = Map.of(
                "SANTANDER", santanderParser,
                "BCI", bciParser
        );
    }

    public ParsedSms parse(String mensaje, String banco) {
        if (banco != null && !banco.isBlank()) {
            String bancoUpper = banco.trim().toUpperCase();
            SmsParser parser = parsersBanco.get(bancoUpper);
            if (parser != null) {
                ParsedSms resultado = parser.parse(mensaje);
                if (resultado != null) {
                    return resultado;
                }
            }
        }
        return genericParser.parse(mensaje);
    }
}
