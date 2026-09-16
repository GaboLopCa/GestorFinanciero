package com.gabriel.gestorfinanciero.repository;

import com.gabriel.gestorfinanciero.model.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findByUsuarioId(Long usuarioId);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.usuario.id = :usuarioId")
    int sumMontosByUsuarioId(@Param("usuarioId") Long usuarioId);
}