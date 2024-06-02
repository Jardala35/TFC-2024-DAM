package com.tfc.v1.modelo.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Movimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;
    @Column
    private String tipo;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime fecha_alta;
    @Column
    private Boolean pendiente;
    
    @Transient
    private List<Producto> productos;
    
    public Movimiento() {
        
    }
    
    public Movimiento(int id, String tipo, LocalDateTime fecha_alta) {
        super();
        this.id = id;
        this.tipo = tipo;
        this.fecha_alta = fecha_alta;
    }
    
    public Movimiento(String tipo, Boolean pendiente, LocalDateTime fecha_alta, List<Producto> listaProd) {
        super();        
        this.tipo = tipo;
        this.fecha_alta = fecha_alta;
        this.pendiente = pendiente;
        this.productos = listaProd;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public LocalDateTime getFecha_alta() {
        return fecha_alta;
    }
    public void setFecha_alta(LocalDateTime fecha_alta) {
        this.fecha_alta = fecha_alta;
    }
    
    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    
    
    
    public Boolean getPendiente() {
		return pendiente;
	}

	public void setPendiente(Boolean pendiente) {
		this.pendiente = pendiente;
	}

	@Override
    public String toString() {
        return "Movimiento{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", fecha_alta=" + fecha_alta +
                '}';
    }
    
}
