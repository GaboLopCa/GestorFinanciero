package com.gabriel.gestorfinanciero.repository;

import com.gabriel.gestorfinanciero.model.GastoFijo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GastoFijoRepository extends JpaRepository<GastoFijo, Long> {
    List<GastoFijo> findByUsuarioId(Long usuarioId);
}