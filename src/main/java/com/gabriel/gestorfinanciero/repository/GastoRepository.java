package com.gabriel.gestorfinanciero.repository;

import com.gabriel.gestorfinanciero.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByUsuarioId(Long usuarioId);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.usuario.id = :usuarioId")
    int sumMontosByUsuarioId(@Param("usuarioId") Long usuarioId);
}