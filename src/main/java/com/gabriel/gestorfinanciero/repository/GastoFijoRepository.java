package com.gabriel.gestorfinanciero.repository;

import com.gabriel.gestorfinanciero.model.GastoFijo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GastoFijoRepository extends JpaRepository<GastoFijo, Long> {
    List<GastoFijo> findByUsuarioId(Long usuarioId);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM GastoFijo g WHERE g.usuario.id = :usuarioId AND g.pagado = false")
    int sumMontosPendientesByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT gf FROM GastoFijo gf JOIN FETCH gf.usuario WHERE gf.id = :id")
    Optional<GastoFijo> findByIdConUsuario(@Param("id") Long id);
}