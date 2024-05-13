package com.tfc.v1.modelo.entidades;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Seccion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int id;
	@Column
	
	@OneToMany(mappedBy = "seccion", cascade=CascadeType.ALL)
	private List<Producto> productos;
	public Seccion(int id, List<Producto> productos) {
		super();
		this.id = id;
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
	
	

}
