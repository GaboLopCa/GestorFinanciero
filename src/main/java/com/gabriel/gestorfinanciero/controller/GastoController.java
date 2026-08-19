package com.gabriel.gestorfinanciero.controller;

import com.gabriel.gestorfinanciero.model.Gasto;
import com.gabriel.gestorfinanciero.model.Usuario;
import com.gabriel.gestorfinanciero.repository.GastoRepository;
import com.gabriel.gestorfinanciero.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Super-poder 1: Le dice a Spring "Soy un Controller (el mesero), atiende peticiones web aquí".
public class GastoController {

        private final GastoRepository gastoRepository;
        private final UsuarioRepository usuarioRepository;

        //Constructor: Spring lo inyecta en la DB
        public GastoController(GastoRepository gastoRepository, UsuarioRepository usuarioRepository){
            this.gastoRepository = gastoRepository;
            this.usuarioRepository = usuarioRepository;
        }

        @PostMapping("/gastos")
        public String registrarGasto(@RequestBody Gasto gasto, @RequestParam Long usuarioId) {
            //1. Buscamos al usuario en la BD. Si no existe, lanzamos un error 404.
            Usuario usuario = usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encotrado con ID: "+ usuarioId));

            //2. Asociamos el usuario al gasto
            gasto.setUsuario(usuario);

            //3. Guardamos el gasto asociado a su dueño en la DB
            gastoRepository.save(gasto);
            return "Gasto registrado para: "+usuario.getNombre() + ": "+gasto.getDescripcion()+ " $"+ gasto.getMonto();
        }

        @GetMapping("/gastos")
        public List<Gasto> retornarGastos(@RequestParam Long usuarioId){
             return gastoRepository.findByUsuarioId(usuarioId);
        }

}