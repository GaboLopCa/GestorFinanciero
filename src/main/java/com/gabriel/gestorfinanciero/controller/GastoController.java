package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.model.Gasto;
import com.gabriel.gestorfinanciero.repository.GastoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Super-poder 1: Le dice a Spring "Soy un Controller (el mesero), atiende peticiones web aquí".
public class GastoController {

        private final GastoRepository gastoRepository;

        //Constructor: Spring lo inyecta en la DB
        public GastoController(GastoRepository gastoRepository){
            this.gastoRepository = gastoRepository;
        }

        @PostMapping("/gastos")
        public String registrarGasto(@RequestBody Gasto gasto) {
            gastoRepository.save(gasto);
            return "Gasto registrado: "+gasto.getDescripcion() + " $"+gasto.getMonto();
        }

        @GetMapping("/gastos")
        public List<Gasto> retornarGastos(){
             return gastoRepository.findAll();
        }

}