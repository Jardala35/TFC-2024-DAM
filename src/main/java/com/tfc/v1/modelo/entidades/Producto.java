package com.tfc.v1.modelo.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Producto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column
	private String nombre_producto;	
	private double peso;
	private double valor_producto_unidad;
	private String descripcion;
	@ManyToOne
	@JoinColumn(name="fk_seccion", referencedColumnName="id")
	private Seccion seccion;
	
	public Producto(int id, String nombre_producto, double peso, double valor_producto_unidad,
			String descripcion) {
		super();
		this.id = id;
		this.nombre_producto = nombre_producto;		
		this.peso = peso;
		this.valor_producto_unidad = valor_producto_unidad;
		this.descripcion = descripcion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre_producto() {
		return nombre_producto;
	}

	public void setNombre_producto(String nombre_producto) {
		this.nombre_producto = nombre_producto;
	}
	
	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	public double getValor_producto_unidad() {
		return valor_producto_unidad;
	}

	public void setValor_producto_unidad(double valor_producto_unidad) {
		this.valor_producto_unidad = valor_producto_unidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	
	
}
