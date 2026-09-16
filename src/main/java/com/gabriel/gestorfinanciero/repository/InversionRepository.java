package com.gabriel.gestorfinanciero.repository;

import com.gabriel.gestorfinanciero.model.Inversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InversionRepository extends JpaRepository<Inversion, Long> {
    List<Inversion> findByUsuarioId(Long usuarioId);

    @Query("SELECT DISTINCT i FROM Inversion i LEFT JOIN FETCH i.fondoMutuoDetalles WHERE i.usuario.id = :usuarioId")
    List<Inversion> findByUsuarioIdConDetalles(@Param("usuarioId") Long usuarioId);
}