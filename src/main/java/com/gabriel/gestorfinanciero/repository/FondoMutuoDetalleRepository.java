package com.gabriel.gestorfinanciero.repository;

import com.gabriel.gestorfinanciero.model.FondoMutuoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FondoMutuoDetalleRepository extends JpaRepository<FondoMutuoDetalle, Long> {
    List<FondoMutuoDetalle> findByInversionId(Long inversionId);

    @Query("SELECT d FROM FondoMutuoDetalle d JOIN FETCH d.inversion i JOIN FETCH i.usuario u WHERE d.id = :id")
    List<FondoMutuoDetalle> findByIdConInversionYUsuario(@Param("id") Long id);
}