package com.tfc.v1.modelo.entidades.usuario;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Clase que representa a un usuario.
 * 
 * <p>Esta clase define los atributos y métodos asociados con un usuario
 * , incluyendo su nombre de usuario, contraseña, nombre, apellido,
 * correo electrónico, fecha de alta y rol.</p>
 * 
 * @author Pablo Navarro Duro 
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nomUsr"})})
public class Usuario implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer id;
	@Column(nullable = false)
	private String nomUsr;
	private String pass;
	private String nombre;
	private String apellido;
	private String email;
	@Column(columnDefinition = "TIMESTAMP")
	private LocalDateTime fecha_alta;
	@Enumerated(EnumType.STRING)
	private Rol rol;
	/**
	 * Constructor por defecto.
	 */
	public Usuario() {
		super();
	}
	
	/**
     * Constructor con parámetros.
     * 
     * @param nomUsr El nombre de usuario.
     * @param pass La contraseña.
     * @param nombre El nombre del usuario.
     * @param apellido El apellido del usuario.
     * @param email El correo electrónico del usuario.
     * @param rol El rol del usuario.
     */
	public Usuario(String nomUsr, String pass, String nombre, String apellido, String email, Rol rol) {
		super();
		this.nomUsr = nomUsr;
		this.pass = pass;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.rol = rol;
		this.fecha_alta = LocalDateTime.now();
	}

	// Métodos de UserDetails
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority((rol.name())));
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getPassword() {
		return this.pass;
	}

	@Override
	public String getUsername() {
		return this.nomUsr;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getNomUsr() {
		return nomUsr;
	}


	public void setNomUsr(String nomUsr) {
		this.nomUsr = nomUsr;
	}


	public String getPass() {
		return pass;
	}


	public void setPass(String pass) {
		this.pass = pass;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getApellido() {
		return apellido;
	}


	public void setApellido(String apellido) {
		this.apellido = apellido;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public LocalDateTime getFecha_alta() {
		return fecha_alta;
	}


	public void setFecha_alta(LocalDateTime fecha_alta) {
		this.fecha_alta = fecha_alta;
	}


	public Rol getRol() {
		return rol;
	}


	public void setRol(Rol rol) {
		this.rol = rol;
	}
	
	
	
	
	
}
