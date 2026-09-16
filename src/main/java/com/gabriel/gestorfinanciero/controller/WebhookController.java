package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.dto.WebhookRequest;
import com.gabriel.gestorfinanciero.dto.WebhookResponse;
import com.gabriel.gestorfinanciero.exception.BadRequestException;
import com.gabriel.gestorfinanciero.model.Transaccion;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.TransaccionRepository;
import com.gabriel.gestorfinanciero.service.ParsedSms;
import com.gabriel.gestorfinanciero.service.SmsParserService;
import com.gabriel.gestorfinanciero.service.WebhookAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final SmsParserService smsParserService;
    private final WebhookAuthService webhookAuthService;
    private final TransaccionRepository transaccionRepository;

    public WebhookController(SmsParserService smsParserService,
                             WebhookAuthService webhookAuthService,
                             TransaccionRepository transaccionRepository) {
        this.smsParserService = smsParserService;
        this.webhookAuthService = webhookAuthService;
        this.transaccionRepository = transaccionRepository;
    }

    @PostMapping("/transacciones")
    public WebhookResponse recibir(@RequestBody @Valid WebhookRequest req, HttpServletRequest request) {
        Usuario usuario = webhookAuthService.resolverUsuario(request);

        ParsedSms parsed = smsParserService.parse(req.getMensaje(), req.getBanco());
        if (parsed == null) {
            throw new BadRequestException("No se pudo interpretar el mensaje bancario");
        }

        Transaccion transaccion = new Transaccion();
        transaccion.setDescripcion(parsed.getDescripcion());
        transaccion.setMonto(parsed.getMonto());
        transaccion.setTipo(parsed.getTipo());
        transaccion.setCategoria(parsed.getCategoria());
        transaccion.setFecha(req.getFecha() != null ? req.getFecha() : LocalDate.now());
        transaccion.setUsuario(usuario);

        Transaccion guardada = transaccionRepository.save(transaccion);

        return new WebhookResponse(guardada, parsed);
    }
}
