package com.gabriel.gestorfinanciero.repository;

import com.gabriel.gestorfinanciero.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
}