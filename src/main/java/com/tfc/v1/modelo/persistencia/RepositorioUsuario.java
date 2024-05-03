package com.tfc.v1.modelo.persistencia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfc.v1.modelo.entidades.usuario.Usuario;

@Repository
public interface RepositorioUsuario extends JpaRepository<Usuario, Integer>{
	Optional<Usuario> findByUsername(String username); 
}
