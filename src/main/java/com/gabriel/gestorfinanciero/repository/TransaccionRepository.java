package com.gabriel.gestorfinanciero.repository;

import com.gabriel.gestorfinanciero.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByUsuarioId(Long usuarioId);
}