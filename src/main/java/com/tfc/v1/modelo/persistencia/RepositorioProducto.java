package com.tfc.v1.modelo.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tfc.v1.modelo.entidades.Producto;

import jakarta.transaction.Transactional;
/**
 * Interfaz para la persistencia de productos en la base de datos.
 * 
 * @author Pablo Navarro Duro 
 */
@Repository
public interface RepositorioProducto extends JpaRepository<Producto, Integer> {
	@Modifying
    @Transactional
    @Query(value = "DELETE FROM producto", nativeQuery = true)
    public void deleteAllProductos();
}
