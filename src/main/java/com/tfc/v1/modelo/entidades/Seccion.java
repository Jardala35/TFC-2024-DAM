package com.tfc.v1.modelo.entidades;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nombre"})})
public class Seccion implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int id;
	@Column
	private String nombre;
	@OneToMany(mappedBy = "seccion", cascade=CascadeType.ALL)
	private List<Producto> productos;
	public Seccion() {
		
		
	}
	public Seccion(String nom) {
		super();
		this.nombre = nom;
	}
	public Seccion(int id, String nom,  List<Producto> productos) {
		super();
		this.id = id;
		this.nombre = nom;
		this.productos = productos;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Producto> getProductos() {
		return productos;
	}
	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}
	public String getNombre_seccion() {
		return nombre;
	}
	public void setNombre_seccion(String nombre_seccion) {
		this.nombre = nombre_seccion;
	}	
	
	

}
