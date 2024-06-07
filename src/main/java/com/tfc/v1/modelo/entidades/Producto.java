package com.tfc.v1.modelo.entidades;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
/**
 * Entidad que representa un producto en el almacén.
 * 
 * <p>Esta clase define los atributos y métodos asociados con un producto
 * en el almacén, como el nombre, el valor por unidad, la cantidad disponible,
 * el peso, la descripción y la sección a la que pertenece.</p>
 * 
 * @author Pablo Navarro Duro 
 */
@Entity
public class Producto implements Serializable {
	 private static final long serialVersionUID = 7772400257406297643L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column
	private String nombre_producto;		
	private double valor_producto_unidad;
	private int cantidad;
	private double peso;
	private String descripcion;

	@ManyToOne
	@JoinColumn(name="fk_seccion", referencedColumnName="id")
	private Seccion seccion;
	
	
	/**
	 * Constructor por defecto
	 */
	public Producto() {
		
	}
	/**
     * Constructor con parámetros.
     * 
     * @param id El ID del producto.
     * @param nombre_producto El nombre del producto.
     * @param valor_producto_unidad El valor del producto por unidad.
     * @param cantidad La cantidad disponible del producto.
     * @param peso El peso del producto.
     * @param descripcion La descripción del producto.
     * @param seccion La sección a la que pertenece el producto.
     */
	public Producto(int id, String nombre_producto, double valor_producto_unidad, int cantidad, double peso,
			String descripcion, Seccion seccion) {
		super();
		this.id = id;
		this.nombre_producto = nombre_producto;
		this.valor_producto_unidad = valor_producto_unidad;
		this.cantidad = cantidad;
		this.peso = peso;
		this.descripcion = descripcion;
		this.seccion = seccion;
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

	public double getValor_producto_unidad() {
		return valor_producto_unidad;
	}

	public void setValor_producto_unidad(double valor_producto_unidad) {
		this.valor_producto_unidad = valor_producto_unidad;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Seccion getSeccion() {
		return seccion;
	}

	public void setSeccion(Seccion seccion) {
		this.seccion = seccion;
	}
	
	@Override
    public String toString() {
		return "Producto{" + "id=" + id + ", nombre_producto='" + nombre_producto + '\'' + ", valor_producto_unidad="
				+ valor_producto_unidad + ", cantidad=" + cantidad + ", peso=" + peso + ", descripcion='" + descripcion
				+ '\'' + ", seccion=" + seccion + '}';
    }
	
	
}
