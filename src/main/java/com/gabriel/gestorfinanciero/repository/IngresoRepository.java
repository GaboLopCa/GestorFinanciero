package com.gabriel.gestorfinanciero.repository;

import com.gabriel.gestorfinanciero.model.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findByUsuarioId(Long usuarioId);
}