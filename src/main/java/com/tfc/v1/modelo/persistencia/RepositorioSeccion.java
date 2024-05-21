package com.tfc.v1.modelo.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfc.v1.modelo.entidades.Seccion;

@Repository
public interface RepositorioSeccion extends JpaRepository<Seccion, Integer> {
		public Seccion findByNombre(String nombre);

}
