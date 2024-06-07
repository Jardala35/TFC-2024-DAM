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

/**
 * Entidad que representa una sección de productos en el almacén.
 * 
 * <p>Esta clase define los atributos y métodos asociados con una sección
 * de productos en el almacén, como el nombre de la sección y la lista de productos
 * asociados a ella.</p>
 * 
 * @author Pablo Navarro Duro 
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nombre"})})
public class Seccion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int id;
	@Column
	private String nombre;
	@OneToMany(mappedBy = "seccion", cascade=CascadeType.ALL)
	private List<Producto> productos;
	/**
     * Constructor por defecto.
     *     
     */
	public Seccion() {
		
		
	}
	/**
     * Constructor con parámetros.
     * 
     * @param nom El nombre de la sección.
     */
	public Seccion(String nom) {
		super();
		this.nombre = nom;
	}
	/**
     * Constructor con parámetros.
     * 
     * @param id El ID de la sección.
     * @param nom El nombre de la sección.
     * @param productos La lista de productos asociados a la sección.
     */
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
