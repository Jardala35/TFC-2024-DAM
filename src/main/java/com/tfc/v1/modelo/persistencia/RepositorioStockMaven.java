package com.tfc.v1.modelo.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfc.v1.modelo.entidades.Producto;

@Repository
public interface RepositorioStockMaven extends JpaRepository<Producto, Integer> {

}
