package com.tfc.v1.modelo.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Movimiento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int id;
	@Column
	private String tipo;
	@Column(columnDefinition = "TIMESTAMP")
	private LocalDateTime fecha_alta;
	public Movimiento(int id, String tipo, LocalDateTime fecha_alta) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.fecha_alta = fecha_alta;
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
	
	
}
