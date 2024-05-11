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
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(nullable = false)
    private String nombre;

    private String caracteristicas;

    private String observaciones;

    @ManyToMany
    private Set<Seccion> secciones = new HashSet<>();

    // Constructor, getters y setters

    public Producto() {
        super();
    }

    public Producto(String nombre, String caracteristicas, String observaciones) {
        super();
        this.nombre = nombre;
        this.caracteristicas = caracteristicas;
        this.observaciones = observaciones;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Set<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(Set<Seccion> secciones) {
        this.secciones = secciones;
    }

    public void addSeccion(Seccion seccion) {
        this.secciones.add(seccion);
        seccion.getProductos().add(this);
    }

    public void removeSeccion(Seccion seccion) {
        this.secciones.remove(seccion);
        seccion.getProductos().remove(this);
    }
}
