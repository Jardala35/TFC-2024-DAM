package com.tfc.v1.modelo.entidades;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
 
@Entity
@Table(name = "seccion")
public class Seccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seccion")
    private Integer idSeccion;

    @Column(nullable = false)
    private String columna;

    @Column(nullable = false)
    private String fila;

    @ManyToMany(mappedBy = "secciones")
    private Set<Producto> productos = new HashSet<>();

    // Constructor, getters y setters

    public Seccion() {
        super();
    }

    public Seccion(String columna, String fila) {
        super();
        this.columna = columna;
        this.fila = fila;
    }

    public Integer getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(Integer idSeccion) {
        this.idSeccion = idSeccion;
    }

    public String getColumna() {
        return columna;
    }

    public void setColumna(String columna) {
        this.columna = columna;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public Set<Producto> getProductos() {
        return productos;
    }

    public void setProductos(Set<Producto> productos) {
        this.productos = productos;
    }

    public void addProducto(Producto producto) {
        this.productos.add(producto);
        producto.getSecciones().add(this);
    }

    public void removeProducto(Producto producto) {
        this.productos.remove(producto);
        producto.getSecciones().remove(this);
    }
}
