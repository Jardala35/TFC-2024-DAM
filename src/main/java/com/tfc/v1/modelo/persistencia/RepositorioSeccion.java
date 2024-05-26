package com.tfc.v1.modelo.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tfc.v1.modelo.entidades.Seccion;

import jakarta.transaction.Transactional;

@Repository
public interface RepositorioSeccion extends JpaRepository<Seccion, Integer> {
		public Seccion findByNombre(String nombre);
		@Modifying
	    @Transactional
	    @Query(value = "DELETE FROM seccion", nativeQuery = true)
	    public void deleteAllSecciones();

}
